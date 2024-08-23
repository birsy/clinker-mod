package birsy.clinker.common.world.entity;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.common.world.entity.proceduralanimation.IKLegSystem;
import birsy.clinker.common.world.entity.proceduralanimation.IKLocomotionEntity;
import birsy.clinker.common.world.physics.particle.CollidingParticle;
import birsy.clinker.common.world.physics.particle.Constraint;
import birsy.clinker.common.world.physics.particle.ParticleParent;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FrogNoMoreEntity extends Monster implements IKLocomotionEntity, InterpolatedSkeletonParent, ParticleParent {
    @OnlyIn(Dist.CLIENT)
    InterpolatedSkeleton skeleton;
    private final IKLegSystem ikSystem;

    private final Vector3d terrainNormal = new Vector3d();

    @OnlyIn(Dist.CLIENT)
    public CollidingParticle tail;
    @OnlyIn(Dist.CLIENT)
    private TailConstraint tailConstraint;
    private List<CollidingParticle> particles = new ArrayList<>();
    private List<Constraint> constraints = new ArrayList<>();

    public FrogNoMoreEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.ikSystem = IKLegSystem.SymmetricalBuilder.create()
                .baseOffset(-0.5625F, 0.09375F)
                .addLeg(0.40625F, -0.5F, 0.2F)
                .addLeg(-0.46875F, -0.5F, -0.1F)
                .build(this);
        this.tail = new CollidingParticle(this, 0.5F);
        this.tailConstraint = new TailConstraint(this, 1.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    private final Vector3d tDif = new Vector3d();
    private void updateTerrainSlope(int range) {
        boolean hasSurroundingBlocks = false;
        this.terrainNormal.set(0, 0, 0);
        for (BlockPos blockPos : BlockPos.betweenClosed(this.blockPosition().offset(range, range, range), this.blockPosition().offset(-range, -range, -range))) {
            if (this.level().getBlockState(blockPos).isCollisionShapeFullBlock(this.level(), blockPos)) {
                tDif.set(this.getX() - blockPos.getX(), this.getY() - blockPos.getY(), this.getZ() - blockPos.getZ()).normalize();
                this.terrainNormal.add(tDif);
                hasSurroundingBlocks = true;
            }
        }

        if (!hasSurroundingBlocks) {
            this.terrainNormal.set(0, 1, 0);
        }
        this.terrainNormal.normalize();
    }

    @Override
    public void tick() {
        super.tick();
        this.updateTerrainSlope(2);
        //this.ikSystem.setAttachmentDirection(this.terrainNormal);
        this.ikSystem.update();
        this.debugMove();
        //this.tail.position = this.position();
        //Clinker.LOGGER.info(this.tail.position);
        if (this.tail.position.distanceTo(this.position()) > 10.0F) this.tail.position = this.position();
        this.updatePhysics(new Vec3(0, -0.1, 0), 1.0F, 1);
    }

    private void debugMove() {
        float maxSpeed = 0.2F;
        for (int i = 0; i < 4; i++) {
            if (this.getLeg(i).stepping) {
                maxSpeed *= 0.5F;
            }
        }
        float acceleration = 0.2F;
        Player target = EntityRetrievalUtil.getNearestEntity(this, 16.0F, (entity -> entity instanceof Player));

        if (target != null) {
            if (target.getMainHandItem().is(Items.CARROT_ON_A_STICK)) {
                this.moveTowardsPosition(target.getX(), target.getY(), target.getZ(), acceleration, maxSpeed, 5.0, !this.level().isClientSide());
                return;
            }
            if (target.getMainHandItem().is(Items.ARROW)) {
                BlockHitResult result = this.level().clip(new ClipContext(
                        target.getEyePosition(),
                        target.getEyePosition().add(target.getLookAngle().scale(16.0F)),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        CollisionContext.empty()
                ));
                if (result.getType() == HitResult.Type.BLOCK) {
                    this.moveTowardsPosition(result.getLocation().x(), result.getLocation().y(), result.getLocation().z(), acceleration, maxSpeed, 1.0, !this.level().isClientSide());
                    return;
                }
            }
        }

        this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), 0.01F, maxSpeed, 1.0, false);
    }
    private void moveTowardsPosition(double x, double y, double z, double acceleration, double maxSpeed, double completionRadius, boolean setLook) {
        double desiredXVelocity = 0;
        double desiredZVelocity = 0;
        double acc = 0.0;
        if (this.position().distanceToSqr(x, this.position().y, z) > completionRadius*completionRadius) {
            desiredXVelocity = (x - this.position().x);
            desiredZVelocity = (z - this.position().z);

            double invMagnitude = 1.0 / Math.sqrt(desiredXVelocity*desiredXVelocity + desiredZVelocity*desiredZVelocity);

            desiredXVelocity *= invMagnitude * maxSpeed;
            desiredZVelocity *= invMagnitude * maxSpeed;
            acc = acceleration;
        }

        Vec3 deltaMovement = this.getDeltaMovement();
        this.setDeltaMovement(MathUtils.approach(deltaMovement.x, desiredXVelocity, acc), deltaMovement.y, MathUtils.approach(deltaMovement.z, desiredZVelocity, acc));
        if (setLook) {
            this.getLookControl().setLookAt(x, y, z);
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
        }
    }

    @Override
    public IKLegSystem getLegSystem() { return this.ikSystem; }
    @Override
    public void setSkeleton(InterpolatedSkeleton skeleton) { this.skeleton = skeleton; }
    @Override
    public InterpolatedSkeleton getSkeleton() { return this.skeleton; }

    @Nullable
    @Override
    public Level getLevel() {
        return this.level();
    }

    @Nullable
    @Override
    public Vec3 getPosition() {
        return this.position();
    }

    @Override
    public List getParticles() {
        return particles;
    }

    @Override
    public List getConstraints() {
        return constraints;
    }

    private static class TailConstraint extends Constraint {
        private final FrogNoMoreEntity entity;
        private final CollidingParticle particle;
        private final float length;
        public TailConstraint(FrogNoMoreEntity entity, float length) {
            super(entity);
            this.entity = entity;
            this.particle = entity.tail;
            this.length = length;
        }

        @Override
        protected void apply() {
            float rotation = Mth.DEG_TO_RAD * -entity.yBodyRot;
            Vec3 assPosition = entity.position().add(-Mth.sin(rotation) * entity.getDimensions(entity.getPose()).width * 0.5F, entity.getDimensions(entity.getPose()).height * 0.5F, -Mth.cos(rotation) * entity.getDimensions(entity.getPose()).width * 0.5F);
            Vec3 towardParticle = assPosition.subtract(particle.physicsNextPosition).normalize().scale(this.length);
            particle.physicsNextPosition = assPosition.subtract(towardParticle);
        }
    }
}
