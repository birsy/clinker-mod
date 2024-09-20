package birsy.clinker.common.world.entity.rope;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundPushPacket;
import birsy.clinker.common.networking.packet.ClientboundRopeEntitySegmentAddPacket;
import birsy.clinker.common.world.entity.ColliderEntity;
import birsy.clinker.common.world.entity.CollisionParent;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RopeEntity<T extends RopeEntitySegment> extends PathfinderMob implements CollisionParent {
    public List<T> segments;
    public Map<ColliderEntity<?>, T> segmentByCollider;
    protected RopeEntitySegment lastHitSegment = null;

    public RopeEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        //this.lookControl = new RopeLookController(this);
        this.moveControl = new RopeMoveController(this);
        this.setNoGravity(true);
        this.segments = new ArrayList<>();
        this.segmentByCollider = new HashMap<>();
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new RopeLookController.RopeBodyRotationControl(this);
    }

    protected abstract T createSegment(int segmentType, int index);

    @Override
    public void tick() {
        super.tick();
        if (!this.segments.isEmpty()) {
            Vector3dc headPos = segments.get(0).getPosition();
            Vector3dc headOffset = segments.get(0).getAttachmentDirection(1.0F);
            float radius = segments.get(0).radius;
            this.setPos(headPos.x() + headOffset.x()*radius, headPos.y() - this.getEyeHeight() + headOffset.y()*radius, headPos.z() + headOffset.z()*radius);
        }
        this.updateSegments();
    }

    @Override
    public boolean isInWall() {
        return this.segments.get(0).collider.isInWall();
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
        T segment = createSegment(type, this.segments.size());
        this.segments.add(segment);
        this.segmentByCollider.put(segment.collider, segment);

        if (network && this.level().isClientSide()) {
            ClinkerPacketHandler.sendToClientsTrackingEntity(this, new ClientboundRopeEntitySegmentAddPacket(this, type));
        }
    }

    private final Vector3d center = new Vector3d();
    private final Vector3d direction = new Vector3d();
    private void updateSegments() {
        for (int i = 0; i < this.segments.size(); i++) segments.get(i).index = i;

        if (this.level().isClientSide()) {
            for (T segment : this.segments) segment.updateClient();
            return;
        }

        // if there's something between two segments, turn off horizontal collision for one of the segments
        for (int i = 1; i < segments.size(); i++) {
            RopeEntitySegment behindSegment = segments.get(i);
            RopeEntitySegment aheadSegment = segments.get(i - 1);
            if (behindSegment.position.distance(aheadSegment.position) > behindSegment.length * 1.5) {
                //behindSegment.canCollideHorizontally = false;
                behindSegment.accelerate(0, 1,  0);
                continue;
            }
            BlockHitResult raycast = this.level().clip(new ClipContext(
                    VectorUtils.toMoj(behindSegment.position), VectorUtils.toMoj(aheadSegment.position), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.of(this)
            ));
            if (raycast.isInside() || raycast.getType() == HitResult.Type.BLOCK) behindSegment.accelerate(0, 0.1,  0);

            behindSegment.canCollideHorizontally = true;
        }

        for (T segment : this.segments) {
            BlockPos segmentPos = BlockPos.containing(segment.position.x, segment.position.y, segment.position.z);
            FluidState fluidAtPos = this.level().getFluidState(segmentPos);
            if (fluidAtPos.isEmpty()) {
                segment.accelerate(0, -0.08, 0);
            } else {
                Vec3 flowVector = fluidAtPos.getFlow(this.level(), segmentPos);
                float flowSpeed = 0.08F;
                segment.accelerate(flowVector.x() * flowSpeed, flowVector.y() * flowSpeed, flowVector.z() * flowSpeed);
            }

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

            if (i % 2 == 0) {
                for (T segment : this.segments) {
                    // self collisions
                    for (T segmentB : this.segments) {
                        segment.collideWithOtherSegment(segmentB);
                    }
                    segment.collideWithLevel();
                }
            }
        }

        for (T segment : this.segments) {
            segment.collideWithLevel();
            segment.finalizeSim();
        }
    }



    private boolean wasSegmentHit = false;
    private final Vector3d normalizedKnockback = new Vector3d();
    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        if (lastHitSegment == null || !this.wasSegmentHit) {
            lastHitSegment = this.segments.get(0);
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
                    this.lastHitSegment.isOnGround() ? Math.min(0.4, movement.y() / 2.0 + pStrength) : movement.y(),
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
}
