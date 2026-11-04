package birsy.clinker.common.entity;

import birsy.clinker.common.networking.packet.ClientboundSegmentedEntitySyncPacket;
import birsy.clinker.common.networking.packet.ClientboundSegmentedEntityPositionPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SegmentedEntity extends PathfinderMob implements IEntityWithComplexSpawn {
    final int maxSegmentCount;
    public final List<Segment> segments;
    final SegmentPartEntity[] parts;

    // networking stuff
    private static final int FULL_SYNC_INTERVAL = 40; // todo
    private static final double POS_SCALE = 4096.0;
    private final short[] positionBuffer;
    private final double[] estimatedClientPositionBuffer;

    @Nullable Segment lastHurtSegment;


    public SegmentedEntity(EntityType<SegmentedEntity> entityType, Level level) { //, int maxSegmentCount) {
        super(entityType, level);

        this.maxSegmentCount = 24;
        this.segments = new ArrayList<>(maxSegmentCount);
        this.parts = new SegmentPartEntity[maxSegmentCount];

        this.positionBuffer = new short[maxSegmentCount * 3];
        this.estimatedClientPositionBuffer = new double[maxSegmentCount * 3];
        this.noPhysics = true;

        // allocate all hitboxes in advance!
        SegmentPartEntity[] segmentPartEntities = this.parts;
        for (int i = 0; i < segmentPartEntities.length; i++) {
            segmentPartEntities[i] = new SegmentPartEntity(this);
        }
        this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);

    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.parts.length; i++)
            this.parts[i].setId(id + i + 1);
    }

    // segment stuff
    protected Segment createSegment(int index) {
        return new Segment(this, this.parts[index]);
    }
    protected Segment addSegment(Segment segment) {
        segments.add(segment);
        parts[segments.size() - 1] = segment.part;
        segment.initialize();
        return segment;
    }
    protected void tickSegmentsServer() {
        for (Segment segment : this.segments) segment.tick();

        // FaBRIK rope constraint solver
        // need to work out some way of updating velocity...
        for (int iteration = 0; iteration < 6; iteration++) {
            boolean forwards = iteration % 2 == 0;
            for (int i = 0; i < segments.size() - 1; i++) {
                int segmentIndex = forwards ? i : segments.size()  - 2 - i;
                Segment a = this.segments.get(segmentIndex),
                        b = this.segments.get(segmentIndex + 1);
                // length of the leading segment
                double segmentLength = a.length;

                double sX = (a.x + a.dX) - (b.x + b.dX),
                       sY = (a.y + a.dY) - (b.y + b.dY),
                       sZ = (a.z + a.dZ) - (b.z + b.dZ);
                double offset = Mth.length(sX, sY, sZ);
                if (offset < 0.0001) {
                    sX = 0; sY = 0; sZ = segmentLength;
                    offset = segmentLength;
                }

                double error = offset - a.length;
                double k = (error / offset) * 0.5;

                a.dX -= sX * k; a.dY -= sY * k; a.dZ -= sZ * k;
                b.dX += sX * k; b.dY += sY * k; b.dZ += sZ * k;
            }
        }

        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            segment.move();
        }
    }
    protected void tickSegmentsClient() {
        for (Segment segment : this.segments) {
            segment.tick();
            segment.move();
        }
    }

    // packet handling
    private final void sendPositionPackets() {
        for (int i = 0; i < this.segments.size(); i++) {
            Segment segment = this.segments.get(i);
            double dX = Math.round((segment.x - estimatedClientPositionBuffer[i * 3 + 0]) * POS_SCALE),
                   dY = Math.round((segment.y - estimatedClientPositionBuffer[i * 3 + 1]) * POS_SCALE),
                   dZ = Math.round((segment.z - estimatedClientPositionBuffer[i * 3 + 2]) * POS_SCALE);

            if (dX >= Short.MAX_VALUE || dX <= Short.MIN_VALUE ||
                dY >= Short.MAX_VALUE || dY <= Short.MIN_VALUE ||
                dZ >= Short.MAX_VALUE || dZ <= Short.MIN_VALUE) {
                sendFullSyncPackets();
                return;
            }

            positionBuffer[i * 3 + 0] = (short) dX;
            positionBuffer[i * 3 + 1] = (short) dY;
            positionBuffer[i * 3 + 2] = (short) dZ;

            // recreate client reconstruction so it stays more accurate
            estimatedClientPositionBuffer[i * 3 + 0] += dX / POS_SCALE;
            estimatedClientPositionBuffer[i * 3 + 1] += dY / POS_SCALE;
            estimatedClientPositionBuffer[i * 3 + 2] += dZ / POS_SCALE;
        }
        PacketDistributor.sendToPlayersTrackingEntity(this,
                new ClientboundSegmentedEntityPositionPacket(this.getId(), this.positionBuffer)
        );
    }
    public final void updateFromPositionPacket(short[] offsets) {
        for (int i = 0; i < this.segments.size(); i++) {
            double dX = offsets[i * 3 + 0] / POS_SCALE,
                   dY = offsets[i * 3 + 1] / POS_SCALE,
                   dZ = offsets[i * 3 + 2] / POS_SCALE;
            Segment segment = this.segments.get(i);
            segment.x += dX; segment.dX = 0;
            segment.y += dY; segment.dY = 0;
            segment.z += dZ; segment.dZ = 0;
        }
    }

    private final void sendFullSyncPackets() {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
        writeSegmentSyncDataToBuffer(buf);

        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundSegmentedEntitySyncPacket(getId(), data));
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            segment.dataDirtyExceptPosition = false;
            // reset client reconstruction to real values
            estimatedClientPositionBuffer[i * 3 + 0] = segment.x;
            estimatedClientPositionBuffer[i * 3 + 1] = segment.y;
            estimatedClientPositionBuffer[i * 3 + 2] = segment.z;
        }
    }
    public final void updateFromFullSyncPacket(RegistryFriendlyByteBuf buf) {
        int newSegmentCount = buf.readInt(), existingSegmentCount = this.segments.size();
        for (int i = 0; i < existingSegmentCount; i++) {
            Segment segment = this.segments.get(i);
            segment.readFromBuffer(buf);
        }
        if (newSegmentCount <= existingSegmentCount) return;
        for (int i = existingSegmentCount; i < newSegmentCount; i++) {
            Segment segment = this.createSegment(i);
            segment.readFromBuffer(buf);
            this.addSegment(segment);
        }
    }

    // data
    public void writeSegmentSyncDataToBuffer(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.segments.size());
        for (int i = 0; i < this.segments.size(); i++)
            this.segments.get(i).writeToBuffer(buf);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag segmentsTag = new ListTag(this.segments.size());
        for (int i = 0; i < this.segments.size(); i++)
            segmentsTag.add(this.segments.get(i).serialize(new CompoundTag()));
        tag.put("segments", segmentsTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag segmentsTag = tag.getList("segments", Tag.TAG_COMPOUND);
        for (int i = 0; i < segmentsTag.size(); i++) {
            Segment segment = this.createSegment(i);
            segment.deserialize(segmentsTag.getCompound(i));
            this.addSegment(segment);
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        writeSegmentSyncDataToBuffer(buf);
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        int segmentCount = buf.readInt();
        for (int i = 0; i < segmentCount; i++) {
            Segment segment = this.createSegment(i);
            segment.readFromBuffer(buf);
            this.addSegment(segment);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.tickSegmentsServer();

            // physics is handled entirely by the segments,
            // so just set our position to our head.
            Segment headSegment = segments.getFirst();
            this.setPos(headSegment.x, headSegment.y, headSegment.z);

            // sync to clients
            if ((this.tickCount + this.getId()) % FULL_SYNC_INTERVAL == 0)
                this.sendFullSyncPackets();
            else this.sendPositionPackets();
        } else {
            this.tickSegmentsClient();

            List<Player> potentiallyCollidingPlayers = level().getEntitiesOfClass(Player.class, this.getBoundingBox(), EntitySelector.pushableBy(this));
            for (Segment s : segments) {
                AABB box = s.part.getBoundingBox();
                for (Player player : potentiallyCollidingPlayers) {
                    if (!player.getBoundingBox().intersects(box)) continue;
                    if (player.noPhysics) continue;
                    double dx = player.getX() - s.x,
                           dz = player.getZ() - s.z;
                    double d = Mth.absMax(dx, dz);
                    if (d < 0.01) continue;
                    d = Math.sqrt(d);
                    double scale = Math.min(1.0 / d, 1.0) * 0.05 / d;
                    player.push(dx * scale, 0, dz * scale);
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data) {
        data = super.finalizeSpawn(level, difficulty, reason, data);
        int count = level.getRandom().nextInt(maxSegmentCount / 2, maxSegmentCount);
        for (int i = 0; i < count; i++) {
            Segment segment = createSegment(i);
            segment.x = this.getX() + Math.sin(i * 0.75) * 1;
            segment.y = this.getY();
            segment.z = this.getZ() - i;
            this.addSegment(segment);
        }
        return data;
    }

    @Override
    protected AABB makeBoundingBox() {
        if (segments == null || segments.isEmpty()) return super.makeBoundingBox();
        Segment segment = this.segments.getFirst();
        double minX = segment.x, minY = segment.y, minZ = segment.z;
        double maxX = segment.x, maxY = segment.y, maxZ = segment.z;
        for (int i = 1; i < segments.size(); i++) {
            segment = segments.get(i);
            minX = Math.min(minX, segment.x); minY = Math.min(minY, segment.y); minZ = Math.min(minZ, segment.z);
            maxX = Math.max(maxX, segment.x); maxY = Math.max(maxY, segment.y); maxZ = Math.max(maxZ, segment.z);
        }
        return new AABB(minX - 1, minY - 0.5, minZ - 1, maxX + 1, maxY + 1.5, maxZ + 1);
    }

    public boolean hurt(DamageSource source, float amount, Segment segment) {
        lastHurtSegment = segment;
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        super.knockback(strength, x, z);
    }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override public boolean isMultipartEntity() { return true; }
    @Override public PartEntity<?>[] getParts() { return parts; }

    // the actual data and behavior for a segment
    public static class Segment {
        final SegmentedEntity parent;
        final SegmentPartEntity part;
        float length;
        double x, y, z; // pos
        double pX, pY, pZ; // previous pos
        double dX, dY, dZ; // delta pos
        boolean onGround = false;

        // is our data (except position) dirty? if so we need to send an update packet.
        // position is updated automatically
        boolean dataDirtyExceptPosition = false;

        protected Segment(SegmentedEntity parent, SegmentPartEntity part) {
            this.parent = parent;
            this.part = part;
            this.length = 1F;
        }

        public void initialize() {
            this.part.segment = this;
            this.part.setSize(length, length);
            this.part.active = true;
        }

        public void tick() {
            if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
                x = parent.getX(); y = parent.getY(); z = parent.getZ();
            }
            pX = x; pY = y; pZ = z;
            float friction = dY == 0 ? 0.8F : 0.95F;

            dX *= friction; dY *= 0.98F; dZ *= friction;
            dY -= (float) parent.getGravity();
        }

        public void move() {
            // collide with blocks
            Vec3 finalDelta = collide(new Vec3(dX, dY, dZ));
            x += finalDelta.x(); y += finalDelta.y(); z += finalDelta.z();

            dX = finalDelta.x(); dY = finalDelta.y(); dZ = finalDelta.z();

            this.part.setOldPosAndRot();
            this.part.setPos(x, y, z);
        }

        public Vec3 collide(Vec3 delta) {
            AABB box = part.getDimensions(Pose.STANDING).makeBoundingBox(x, y, z);
            List<VoxelShape> potentialCollisions = parent.level().getEntityCollisions(parent, box.expandTowards(delta));
            Vec3 levelCollidedDelta = delta.lengthSqr() == 0.0 ? delta : collideBoundingBox(parent, delta, box, parent.level(), potentialCollisions);

            boolean collidedX = delta.x != levelCollidedDelta.x,
                    collidedY = delta.y != levelCollidedDelta.y,
                    collidedZ = delta.z != levelCollidedDelta.z;

            // step up
            this.onGround = collidedY && delta.y < 0.0;
            if (this.parent.maxUpStep() > 0.0F && onGround && (collidedX || collidedZ)) {
                double distanceMoved = levelCollidedDelta.horizontalDistanceSqr();
                AABB onGroundBox = box.move(0.0, levelCollidedDelta.y, 0.0);

                List<VoxelShape> potentialStepCollisions = collectColliders(
                        parent, parent.level(), potentialCollisions,
                        onGroundBox.expandTowards(delta.x, this.parent.maxUpStep(), delta.z)
                );
                float[] stepUpHeights = collectCandidateStepUpHeights(onGroundBox, potentialStepCollisions, parent.maxUpStep(), (float) levelCollidedDelta.y);

                for (float height : stepUpHeights) {
                    Vec3 stepCollidedDelta = collideWithShapes(new Vec3(delta.x, height, delta.z), onGroundBox, potentialStepCollisions);
                    if (stepCollidedDelta.horizontalDistanceSqr() > distanceMoved) {
                        return stepCollidedDelta.add(0.0, onGroundBox.minY - box.minY, 0.0);
                    }
                }
            }

            return levelCollidedDelta;
        }

        public boolean hurt(DamageSource source, float amount) {
            return this.parent.hurt(source, amount);
        }

        public void impulse(double x, double y, double z) {
            this.dX += x; this.dY += y; this.dZ += z;
        }

        public double getX(float partialTick) { return Mth.lerp(partialTick, pX, x); }
        public double getY(float partialTick) { return Mth.lerp(partialTick, pY, y); }
        public double getZ(float partialTick) { return Mth.lerp(partialTick, pZ, z); }

        public CompoundTag serialize(CompoundTag tag) {
            tag.putDouble("x", x);
            tag.putDouble("y", y);
            tag.putDouble("z", z);
            tag.putFloat("length", length);
            return tag;
        }
        public void deserialize(CompoundTag tag) {
            this.x = tag.getDouble("x");
            this.y = tag.getDouble("y");
            this.z = tag.getDouble("z");
            this.length = tag.getFloat("length");
        }

        public void writeToBuffer(RegistryFriendlyByteBuf buf) {
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
            buf.writeFloat(length);
        }
        public void readFromBuffer(RegistryFriendlyByteBuf buf) {
            x = buf.readDouble();
            y = buf.readDouble();
            z = buf.readDouble();
            length = buf.readFloat();
        }
    }

    // just the hitbox for a given segment
    protected static class SegmentPartEntity extends PartEntity<SegmentedEntity> {
        @Nullable SegmentedEntity.Segment segment;
        EntityDimensions size;
        boolean active = false;

        public SegmentPartEntity(SegmentedEntity parent) {
            super(parent);
            this.size = EntityDimensions.scalable(0F, 0F);
        }

        public void setSize(float width, float height) {
            this.size = EntityDimensions.scalable(width, height);
            this.refreshDimensions();
        }

        @Override
        public boolean hurt(DamageSource source, float amount) {
            if (segment == null) return false;
            return this.segment.hurt(source, amount);
        }
        @Override
        public void push(double x, double y, double z) {
            if (segment == null) return;
            this.segment.impulse(x, y, z);
        }

        @Override
        public void push(Entity entity) {
            if (entity.is(getParent())) return;
            super.push(entity);
        }

        @Override public EntityDimensions getDimensions(Pose pose) { return size; }
        @Override public boolean is(Entity other) { return this == other || getParent() == other; }
        @Override public boolean isPickable() { return active; }

        @Override public boolean isPushable() { return true; }
        @Override
        public boolean canCollideWith(Entity entity) {
            if (entity instanceof PartEntity<?> other)
                return this.getParent() != other.getParent();
            return entity != this.getParent() && super.canCollideWith(entity);
        }

        @Override public boolean shouldBeSaved() { return false; }
        @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
        @Override protected void readAdditionalSaveData(CompoundTag tag) {}
        @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    }
}
