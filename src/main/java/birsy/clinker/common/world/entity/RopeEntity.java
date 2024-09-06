package birsy.clinker.common.world.entity;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundPushPacket;
import birsy.clinker.common.networking.packet.ClientboundRopeEntitySegmentAddPacket;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RopeEntity<T extends RopeEntity.RopeEntitySegment> extends PathfinderMob implements CollisionParent {
    public List<T> segments;
    public Map<ColliderEntity, T> segmentByCollider;
    protected RopeEntitySegment lastHitSegment = null;

    public RopeEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        this.segments = new ArrayList<>();
        this.segmentByCollider = new HashMap<>();
    }

    protected abstract T createSegment(int segmentType);

    @Override
    public void tick() {
        super.tick();
        if (!this.segments.isEmpty()) {
            Vector3dc headPos = segments.get(0).getPosition();
            this.setPos(headPos.x(), headPos.y() - this.getDimensions(this.getPose()).height*0.5, headPos.z());
        }
        this.updateSegments();
    }

    @Override
    public void makePoofParticles() {
        for (RopeEntitySegment segment : this.segments) {
            for(int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.POOF,
                        segment.getPosition().x() + (((this.random.nextFloat() * 2.0) - 1.0) * segment.radius),
                        segment.getPosition().y() + (((this.random.nextFloat() * 2.0) - 1.0) * segment.radius),
                        segment.getPosition().z() + (((this.random.nextFloat() * 2.0) - 1.0) * segment.radius),
                        d0, d1, d2);
            }
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        for (T segment : this.segments) {
            segment.remove(pReason);
        }
        super.remove(pReason);
    }

    @Override
    protected int calculateFallDamage(float pFallDistance, float pDamageMultiplier) {
        return 0;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY,
               minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        for (T segment : this.segments) {
            maxX = Math.max(segment.position.x + segment.radius, maxX);
            maxY = Math.max(segment.position.y + segment.radius, maxY);
            maxZ = Math.max(segment.position.z + segment.radius, maxZ);
            minX = Math.min(segment.position.x - segment.radius, minX);
            minY = Math.min(segment.position.y - segment.radius, minY);
            minZ = Math.min(segment.position.z - segment.radius, minZ);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(0.5F, 0.5F, 0.5F);
    }

    public void addSegmentToEnd(int type) {
        this.addSegmentToEnd(type, !this.level().isClientSide());
    }
    public void addSegmentToEnd(int type, boolean network) {
        T segment = createSegment(type);
        this.segments.add(segment);
        this.segmentByCollider.put(segment.collider, segment);

        if (network && this.level().isClientSide()) {
            ClinkerPacketHandler.sendToClientsTrackingEntity(this, new ClientboundRopeEntitySegmentAddPacket(this, type));
        }
    }

    private Vector3d center = new Vector3d();
    private Vector3d direction = new Vector3d();
    private void updateSegments() {
        if (this.level().isClientSide()) {
            for (T segment : this.segments) segment.updateClient();
            return;
        }

        for (T segment : this.segments) {
            segment.accelerate(0, -0.08, 0);
            segment.integrate();
        }
        // run the rope simulation
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < segments.size() - 1; j++) {
                T segmentA = segments.get(j + 0);
                T segmentB = segments.get(j + 1);

                Vector3d pos1 = segmentA.nextPosition;
                Vector3d pos2 = segmentB.nextPosition;

                pos1.add(pos2, center).mul(0.5);
                pos1.sub(pos2, direction).normalize().mul(0.5);

                center.add(direction.mul(segmentA.length, segmentA.nextPosition), segmentA.nextPosition);
                center.sub(direction.mul(segmentB.length, segmentB.nextPosition), segmentB.nextPosition);
            }

            // we don't need to run the collision solver for every rope iteration, only a couple will do fine.
            if (true) {
                for (T segment : this.segments) {
                    segment.applyLevelCollisionsAndFinalize(this.level());
                }
            }
        }
        int i = 0;
        for (T segment : this.segments) {
            segment.finalizeSim();
            i++;
        }
    }



    private boolean wasSegmentHit = false;
    private final Vector3d normalizedKnockback = new Vector3d();
    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        if (lastHitSegment == null || !this.wasSegmentHit) {
            super.knockback(pStrength, pX, pZ);
            return;
        }

        net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent event = net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack(this, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(pStrength <= 0.0)) {
            Vector3dc movement = this.lastHitSegment.getDeltaMovement();
            normalizedKnockback.set(pX, 0.0, pZ).normalize().mul(pStrength);

            this.lastHitSegment.accelerate(
                    movement.x() / 2.0 - normalizedKnockback.x,
                    this.lastHitSegment.onGround() ? Math.min(0.4, movement.y() / 2.0 + pStrength) : movement.y(),
                    movement.z() / 2.0 - normalizedKnockback.z);
        }
    }

    @Override
    protected void playHurtSound(DamageSource pSource) {
        if (lastHitSegment == null || !this.wasSegmentHit) {
            super.playHurtSound(pSource);
            return;
        }
        this.ambientSoundTime = -this.getAmbientSoundInterval();

        SoundEvent soundevent = this.getHurtSound(pSource);
        if (soundevent != null) {
            if (!this.isSilent()) this.level().playSound(null,
                    this.lastHitSegment.position.x,
                    this.lastHitSegment.position.y,
                    this.lastHitSegment.position.z, 
                    soundevent, this.getSoundSource(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public boolean hurt(ColliderEntity damageReceiver, DamageSource pSource, float pAmount) {
        if (this.segmentByCollider.containsKey(damageReceiver)) {
            this.wasSegmentHit = true;
            this.lastHitSegment = this.segmentByCollider.get(damageReceiver);
            this.hurt(pSource, pAmount);
            this.wasSegmentHit = false;
        }

        return this.hurt(pSource, pAmount);
    }
    @Override
    public void push(ColliderEntity pushReceiver, double xAmount, double yAmount, double zAmount) {
        if (this.segmentByCollider.containsKey(pushReceiver)) this.segmentByCollider.get(pushReceiver).accelerate(xAmount, yAmount, zAmount);
    }

    @Override
    public void push(ColliderEntity pushReceiver, Entity pusher) {
        // copied from Entity.push()
        if (!this.isPassengerOfSameVehicle(pusher)) {
            if (!pusher.noPhysics) {
                double xMovement = pusher.getX() - pushReceiver.getX();
                double zMovement = pusher.getZ() - pushReceiver.getZ();
                double maxMovement = Mth.absMax(xMovement, zMovement);
                if (maxMovement >= 0.01F) {
                    maxMovement = Math.sqrt(maxMovement);
                    xMovement /= maxMovement;
                    zMovement /= maxMovement;
                    double d3 = 1.0 / maxMovement;
                    if (d3 > 1.0) {
                        d3 = 1.0;
                    }

                    xMovement *= d3;
                    zMovement *= d3;
                    xMovement *= 0.05F;
                    zMovement *= 0.05F;
                    if (!pushReceiver.isVehicle() && pushReceiver.isPushable()) {
                        this.push(pushReceiver, -xMovement, 0.0, -zMovement);
                    }

                    if (!pusher.isVehicle() && pusher.isPushable()) {
                        if (pusher instanceof ServerPlayer client) ClinkerPacketHandler.sendToClient(client, new ClientboundPushPacket(xMovement, 0.0, zMovement));
                        pusher.push(xMovement, 0.0, zMovement);
                    }
                }
            }
        }
    }

    public static class RopeEntitySegment {
        public final RopeEntity parent;
        public final int type;
        protected final Vector3d
                position = new Vector3d(0, 0, 0),
                previousPosition = new Vector3d(0, 0, 0),
                nextPosition = new Vector3d(0, 0, 0),
                acceleration = new Vector3d(0, 0, 0);
        protected ColliderEntity collider;
        protected boolean isOnGround;

        public final float radius, length;

        protected RopeEntitySegment(RopeEntity parent, int type, float radius, float length) {
            this.parent = parent;
            this.type = type;
            this.position.set(parent.getX(), parent.getY(), parent.getZ());
            this.previousPosition.set(position);
            this.nextPosition.set(position);
            this.radius = radius;
            this.length = length;
            if (!this.parent.level().isClientSide()) {
                this.collider = ColliderEntity.create(parent, radius * 2.0F, radius * 2.0F);
                this.parent.level().addFreshEntity(this.collider);
            }
        }

        public void setInitialPosition(double x, double y, double z) {
            this.previousPosition.set(x, y, z);
            this.position.set(x, y, z);
            this.nextPosition.set(x, y, z);
            this.acceleration.set(0);
        }

        private final Vector3d deltaMovement = new Vector3d();
        public Vector3dc getDeltaMovement() {
            return this.getDeltaMovement(deltaMovement);
        }
        public Vector3d getDeltaMovement(Vector3d assignment) {
            return this.position.sub(this.previousPosition, assignment);
        }
        public boolean onGround() {
            return this.isOnGround;
        }

        public void setPosition(double x, double y, double z) {
            this.position.set(x, y, z);
        }

        public void setNextPosition(double x, double y, double z) {
            this.nextPosition.set(x, y, z);
        }

        public Vector3dc getPosition() {
            return this.position;
        }

        @OnlyIn(Dist.CLIENT)
        Vector3d interpolatedPosition = new Vector3d();
        @OnlyIn(Dist.CLIENT)
        public Vector3dc getPosition(float partialTick) {
            return this.previousPosition.lerp(this.position, partialTick, this.interpolatedPosition);
        }

        protected void accelerate(double x, double y, double z) {
            this.acceleration.add(x, y, z);
        }

        protected void walk(double x, double y, double z) {

        }

        private final Vector3d velocity = new Vector3d(0);
        protected void integrate() {
            this.isOnGround = false;

            this.getDeltaMovement(this.velocity).mul(0.95);
            this.previousPosition.set(this.position);

            this.velocity.add(this.acceleration);
            this.acceleration.set(0, 0, 0);

            this.position.add(velocity, this.nextPosition);
        }

        Vector3d velocityWithoutCollision = new Vector3d();
        protected void applyLevelCollisionsAndFinalize(Level level) {
            AABB aabb = new AABB(
                    this.position.x + radius, this.position.y + radius, this.position.z + radius,
                    this.position.x - radius, this.position.y - radius, this.position.z - radius
            );
            this.nextPosition.sub(this.position, this.velocity);
            this.velocityWithoutCollision.set(this.velocity);
            List<VoxelShape> list = level.getEntityCollisions(parent, aabb.expandTowards(this.velocity.x, this.velocity.y, this.velocity.z));
            VectorUtils.toJOML(collideBoundingBox(parent, VectorUtils.toMoj(this.velocity), aabb, level, list), this.velocity);

            // apply friction
            // ...only on y-axis. simplifies + this is actually how vanilla handles it LOL
            // sorry ryan. Will Not Work With Landlord :)
            if (Math.abs(this.velocity.y - this.velocityWithoutCollision.y) > 0.01) {
                // get the block at our feet
                BlockPos feetPosition = BlockPos.containing(this.position.x, this.position.y - (this.radius + 0.01), this.position.z);
                float friction = this.parent.level().getBlockState(feetPosition).getFriction(this.parent.level(), feetPosition, this.parent);
                friction = Mth.lerp(0.5F, friction, 1.0F);
                velocity.mul(friction, 1.0F, friction);

                this.isOnGround = true;
            }

            this.position.add(velocity, this.nextPosition);
        }

        protected void finalizeSim() {
            this.position.set(this.nextPosition);
            this.collider.setPos(this.position.x, this.position.y - (this.radius), this.position.z);
        }

        @OnlyIn(Dist.CLIENT)
        protected void updateClient() {
            this.previousPosition.set(this.position);
            this.position.set(this.nextPosition);
        }

        public void remove(RemovalReason reason) {
            this.collider.remove(reason);
        }
    }
}
