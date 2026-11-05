package birsy.clinker.common.entity;

import birsy.clinker.common.entity.module.ModularPathfindingMob;
import birsy.clinker.common.networking.packet.ClientboundSegmentedEntitySyncPacket;
import birsy.clinker.core.Clinker;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class SegmentedEntity<E extends SegmentedEntity<E, J>, J extends SegmentedEntity.Joint<E, J>> extends ModularPathfindingMob<E> implements IEntityWithComplexSpawn {
    private static final int FULL_SYNC_INTERVAL = 2;

    protected final int maxJointCount;
    protected final SegmentPartEntity[] parts;
    public final List<J> joints;

    protected @Nullable J lastHurtSegment;

    public SegmentedEntity(EntityType<? extends SegmentedEntity<E, J>> entityType, Level level, int maxJointCount) {
        super(entityType, level);

        this.maxJointCount = maxJointCount;
        this.joints = new ArrayList<>(maxJointCount);
        this.parts = new SegmentPartEntity[maxJointCount];

        this.noPhysics = true;
        this.blocksBuilding = false;

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
    protected abstract J createJoint(int index, int length);
    protected J addJoint(J joint) {
        joints.add(joint);
        parts[joints.size() - 1] = joint.part;
        joint.initialize();
        return joint;
    }

    protected void tickSegmentsServer() {
        for (J joint : this.joints) joint.tick();

        // FaBRIK rope constraint solver
        // need to work out some way of updating velocity...
        for (int iteration = 0; iteration < 6; iteration++) {
            boolean forwards = iteration % 2 == 0;
            for (int i = 0; i < joints.size() - 1; i++) {
                int index = forwards ? i : joints.size()  - 2 - i;
                Joint a = this.joints.get(index),
                      b = this.joints.get(index + 1);
                double jointLength = b.length;

                double sX = (a.x + a.dX) - (b.x + b.dX),
                       sY = (a.y + a.dY) - (b.y + b.dY),
                       sZ = (a.z + a.dZ) - (b.z + b.dZ);
                double offset = Mth.length(sX, sY, sZ);
                if (offset < 0.0001) {
                    sX = 0; sY = 0; sZ = jointLength;
                    offset = jointLength;
                }

                double error = offset - a.length;
                double k = (error / offset) * 0.5;

                a.dX -= sX * k; a.dY -= sY * k; a.dZ -= sZ * k;
                b.dX += sX * k; b.dY += sY * k; b.dZ += sZ * k;
            }
        }

        for (Joint joint : joints) {
            joint.applyMovement();
        }
    }
    protected void tickSegmentsClient() {
        for (Joint joint : this.joints) {
            joint.tick();
            joint.applyMovement();
        }
    }

    // networking
    private void sendSyncPackets() {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
        writeJointDataToBuffer(buf);

        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundSegmentedEntitySyncPacket(getId(), data));
    }
    public final void updateFromSyncPacket(RegistryFriendlyByteBuf buf) {
        int newJointCount = buf.readInt(),
            curJointCount = this.joints.size();

        double headX = buf.readDouble(),
               headY = buf.readDouble(),
               headZ = buf.readDouble();

        for (int i = 0; i < curJointCount; i++) {
            J joint = this.joints.get(i);
            joint.x = headX + buf.readFloat();
            joint.y = headY + buf.readFloat();
            joint.z = headZ + buf.readFloat();
            joint.readFromBuffer(buf);
        }
        if (newJointCount <= curJointCount) return;
        for (int i = curJointCount; i < newJointCount; i++) {
            J joint = this.createJoint(i, newJointCount);
            joint.x = headX + buf.readFloat();
            joint.y = headY + buf.readFloat();
            joint.z = headZ + buf.readFloat();
            joint.readFromBuffer(buf);
            this.addJoint(joint);
        }
    }

    // data
    public void writeJointDataToBuffer(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.joints.size());

        Joint head = this.joints.getFirst();
        double headX = head.x, headY = head.y, headZ = head.z;
        buf.writeDouble(headX);
        buf.writeDouble(headY);
        buf.writeDouble(headZ);

        for (int i = 0; i < this.joints.size(); i++) {
            Joint segment = this.joints.get(i);
            buf.writeFloat((float) (segment.x - headX));
            buf.writeFloat((float) (segment.y - headY));
            buf.writeFloat((float) (segment.z - headZ));
            this.joints.get(i).writeToBuffer(buf);
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        writeJointDataToBuffer(buf);
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        int segmentCount = buf.readInt();
        double headX = buf.readDouble(),
                headY = buf.readDouble(),
                headZ = buf.readDouble();
        for (int i = 0; i < segmentCount; i++) {
            J segment = this.createJoint(i, segmentCount);
            segment.x = headX + buf.readFloat();
            segment.y = headY + buf.readFloat();
            segment.z = headZ + buf.readFloat();
            segment.readFromBuffer(buf);
            this.addJoint(segment);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag segmentsTag = new ListTag(this.joints.size());
        for (int i = 0; i < this.joints.size(); i++)
            segmentsTag.add(this.joints.get(i).serialize(new CompoundTag()));
        tag.put("segments", segmentsTag);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag segmentsTag = tag.getList("segments", Tag.TAG_COMPOUND);
        for (int i = 0; i < segmentsTag.size(); i++) {
            J segment = this.createJoint(i, segmentsTag.size());
            segment.deserialize(segmentsTag.getCompound(i));
            this.addJoint(segment);
        }
    }

    @Override
    public void moveTo(double x, double y, double z, float yRot, float xRot) {
        double startX = this.getX(),
               startY = this.getY(),
               startZ = this.getZ();
        super.moveTo(x, y, z, yRot, xRot);
        if (this.joints != null)
            for (J joint : joints) {
                double relativeX = joint.x - startX,
                       relativeY = joint.y - startY,
                       relativeZ = joint.z - startZ;
                joint.x = relativeX + x;
                joint.y = relativeY + y;
                joint.z = relativeZ + z;
            }
    }

    @Override public Vec3 getDeltaMovement() { return Vec3.ZERO; }
    @Override public void setDeltaMovement(Vec3 movement) {} // no-op
    @Override protected void pushEntities() {}

    // behavior
    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.tickSegmentsServer();

            // physics is handled entirely by the segments,
            // so just set our position to our head.
            Joint headSegment = joints.getFirst();
            this.setPos(headSegment.x, headSegment.y, headSegment.z);

            // sync to clients
            if ((this.tickCount + this.getId()) % FULL_SYNC_INTERVAL == 0)
                this.sendSyncPackets();
        } else {
            this.tickSegmentsClient();

            List<Player> potentiallyCollidingPlayers = level().getEntitiesOfClass(Player.class, this.getBoundingBox(), EntitySelector.pushableBy(this));
            for (Joint joint : joints) {
                AABB box = joint.part.getBoundingBox();
                for (Player player : potentiallyCollidingPlayers) {
                    if (!player.getBoundingBox().intersects(box)) continue;
                    if (player.noPhysics) continue;
                    double dx = player.getX() - joint.x,
                           dz = player.getZ() - joint.z;
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
    protected AABB makeBoundingBox() {
        if (joints == null || joints.isEmpty())
            return super.makeBoundingBox();

        J joint = this.joints.getFirst();
        double minX = joint.x, minY = joint.y, minZ = joint.z;
        double maxX = joint.x, maxY = joint.y, maxZ = joint.z;
        for (int i = 1; i < joints.size(); i++) {
            joint = joints.get(i);
            float rad = joint.length + joint.radius, h = joint.height;
            minX = Math.min(minX, joint.x - rad); minY = Math.min(minY, joint.y + 0); minZ = Math.min(minZ, joint.z - rad);
            maxX = Math.max(maxX, joint.x + rad); maxY = Math.max(maxY, joint.y + h); maxZ = Math.max(maxZ, joint.z + rad);
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean hurt(DamageSource source, float amount, J segment) {
        lastHurtSegment = segment;
        Clinker.LOGGER.info(source.type());
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        super.knockback(strength, x, z);
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean isMultipartEntity() { return true; }
    @Override public PartEntity<?>[] getParts() { return parts; }

    // the actual data and behavior for a segment
    public static class Joint<E extends SegmentedEntity<E, J>, J extends SegmentedEntity.Joint<E, J>> {
        public final float radius, height, length;
        public final E parent;
        public final SegmentPartEntity part;
        public final int index;
        public double x;
        public double y;
        public double z; // pos
        protected double pX, pY, pZ; // previous pos
        public double dX, dY, dZ; // delta pos
        protected boolean onGround = false;
        protected Vector3d segmentPos = new Vector3d();

        protected Joint(E parent, SegmentPartEntity part, int index, float width, float height, float length) {
            this.parent = parent;
            this.part = part;
            this.index = index;

            this.radius = width * 0.5F;
            this.height = height;
            this.length = length;
        }

        public void initialize() {
            this.part.joint = this;
            this.part.setSize(length, height);
            this.part.active = true;
        }

        public void tick() {
            if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
                x = parent.getX(); y = parent.getY(); z = parent.getZ();
            }

            pX = x; pY = y; pZ = z;
            float friction = onGround ? 0.8F : 0.95F;
            dX *= friction; dY *= 0.98F; dZ *= friction;
        }

        public void applyMovement() {
            // collide with blocks
            Vec3 finalDelta = collide(new Vec3(dX, dY, dZ));
            x += finalDelta.x(); y += finalDelta.y(); z += finalDelta.z();

            dX = finalDelta.x(); dY = finalDelta.y(); dZ = finalDelta.z();

            this.part.setOldPosAndRot();
            getSegmentPosAndDirection(segmentPos, null, 1.0F);
            this.part.setPos(segmentPos.x(), segmentPos.y(), segmentPos.z());
        }

        public Vec3 collide(Vec3 delta) {
            AABB box = new AABB(x - radius, y, z - radius, x + radius, y + height, z + radius);
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
            // handle knockback
            if (!source.is(DamageTypeTags.NO_KNOCKBACK)) {
                float xRatio = 0, zRatio = 0;
                if (source.getSourcePosition() != null) {
                    xRatio = (float) (source.getSourcePosition().x() - x);
                    zRatio = (float) (source.getSourcePosition().z() - z);
                }
                float strength = 0.4F;

                strength *= (float) (1.0 - this.parent.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                if (!(strength <= 0.0)) {
                    float length = (float) Mth.lengthSquared(xRatio, zRatio);
                    while (length < 1.0E-5F) {
                        xRatio = (float) (Math.random() - Math.random()) * 0.01F;
                        zRatio = (float) (Math.random() - Math.random()) * 0.01F;
                        length = (float) Mth.lengthSquared(xRatio, zRatio);
                    }

                    xRatio *= strength / length;
                    zRatio *= strength / length;

                    this.dX = dX / 2.0 - xRatio;
                    this.dY = onGround ? Math.min(0.4, dY / 2.0 + strength) : dY;
                    this.dZ = dZ / 2.0 - zRatio;

                    // apply the same impulse to the other attached joint
                    Joint prev = prev();
                    if (prev != null) {
                        prev.dX = prev.dX / 2.0 - xRatio;
                        prev.dY = onGround ? Math.min(0.4, prev.dY / 2.0 + strength) : prev.dY;
                        prev.dZ = prev.dZ / 2.0 - zRatio;
                    }
                }
            }

            return this.parent.hurt(source, amount);
        }

        public void impulse(double x, double y, double z) {
            this.dX += x; this.dY += y; this.dZ += z;
        }

        @Nullable
        public J prev() {
            if (index <= 0) return null;
            return this.parent.joints.get(index - 1);
        }

        @Nullable
        public J next() {
            if (index >= this.parent.joints.size() - 1) return null;
            return this.parent.joints.get(index + 1);
        }

        public void getSegmentPosAndDirection(@Nullable Vector3d pos, @Nullable Vector3d dir, float partialTick) {
            boolean head = index <= 0;
            Joint other = head ? next() : prev();
            double sX = this.getX(partialTick),
                   sY = this.getY(partialTick),
                   sZ = this.getZ(partialTick);
            double oX = other.getX(partialTick) - sX,
                   oY = other.getY(partialTick) - sY,
                   oZ = other.getZ(partialTick) - sZ;
            double sign = head ? -1 : 1;
            if (pos != null) {
                double mult = 0.5 * sign;
                pos.set(sX + oX * mult, sY + oY * mult, sZ + oZ * mult);
            }
            if (dir != null) {
                if (oX != 0 || oY != 0 || oZ != 0)
                    dir.set(oX, oY, oZ).normalize(sign);
                else
                    dir.set(0, 0, -1);
            }
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
        }

        // position is automatically synced!
        public void writeToBuffer(RegistryFriendlyByteBuf buf) {
            buf.writeShort(writeCompressed(dX));
            buf.writeShort(writeCompressed(dY));
            buf.writeShort(writeCompressed(dZ));
        }
        public void readFromBuffer(RegistryFriendlyByteBuf buf) {
            dX = readCompressed(buf.readShort());
            dY = readCompressed(buf.readShort());
            dZ = readCompressed(buf.readShort());
        }
    }

    // just the hitbox for a given segment
    protected static class SegmentPartEntity extends PartEntity<SegmentedEntity<?,?>> {
        @Nullable SegmentedEntity.Joint joint;
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
            if (joint == null) return false;
            return this.joint.hurt(source, amount);
        }
        @Override
        public void push(double x, double y, double z) {
            if (joint == null) return;
            this.joint.impulse(x, y, z);

            Joint prev = this.joint.prev();
            if (prev == null) return;
            prev.impulse(x, y, z);
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

    static final double COMPRESSION_SCALE = 4096.0;
    static short writeCompressed(double num) {
        return (short) Math.round(num * COMPRESSION_SCALE);
    }
    static double readCompressed(short num) {
        return num / COMPRESSION_SCALE;
    }
}
