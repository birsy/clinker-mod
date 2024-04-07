package birsy.clinker.common.world.entity;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundUrnPathPacket;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class UrnEntity extends AbstractGolem implements InterpolatedSkeletonParent {
    private Vec3 desiredPosition, targetDesiredPosition;
    public UrnFoot[] legs = new UrnFoot[5];

    @OnlyIn(Dist.CLIENT)
    private Vec3 pVelocity = Vec3.ZERO, velocity = Vec3.ZERO;
    @OnlyIn(Dist.CLIENT)
    public Path clientPath;

    int timeSinceLastStep = 0;

    private Vec3 knockbackVector = Vec3.ZERO;

    public UrnEntity(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
        this.moveControl = new UrnMoveController(this);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        resetDesiredPosition();

        for (int i = 0; i < this.legs.length; i++) {
            float rotation = (i + 0.5F) * (Mth.TWO_PI / this.legs.length);
            this.legs[i] = new UrnFoot(this,
                    new Vec3(0, 0, 3.0F / 16.0F).yRot(rotation),
                    new Vec3(0, 0, 1).yRot(rotation), 1.5F);
        }
    }

    protected void registerGoals() {
        //this.goalSelector.addGoal(1, new ApproachPlayerGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new UrnWanderGoal(this, 0.7D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected AABB makeBoundingBox() {
        //if (this.potPosition != null) return this.getDimensions(Pose.STANDING).makeBoundingBox(this.potPosition);
        return super.makeBoundingBox();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        resetDesiredPosition();
        if (this.legs != null) {
            for (UrnFoot leg : this.legs) {
                leg.setNeutralFootPos();
            }
        }
    }

    public void resetDesiredPosition() {
        this.desiredPosition = this.position();
        this.targetDesiredPosition = this.position();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader level) {
        if (!level.getBlockState(blockPos).isAir()) return 0.0F;

        int blocksAboveGround = 10000;
        boolean isNearGround = false;
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        for (int yOffset = 0; yOffset < 5; yOffset++) {
            mutable.setY(blockPos.getY() - yOffset);
            if (level.getBlockState(mutable).isSolid()) {
                blocksAboveGround = yOffset;
                isNearGround = true;
                break;
            }
        }

        if (!isNearGround) return 0.0F;

        // prefer 3 blocks above the ground.
        return 10.0F - Math.abs(blocksAboveGround - 3);
    }

    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            @Override
            // stable if there's a block at least two blocks below it.
            public boolean isStableDestination(BlockPos pos) {
                BlockPos.MutableBlockPos mutable = pos.mutable();
                for (int yOffset = 0; yOffset < 2; yOffset++) {
                    mutable.setY(pos.getY() - yOffset);
                    if (level.getBlockState(mutable).isSolid()) {
                        return true;
                    }
                }

                return false;
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void knockback(double knockbackStrength, double knockbackX, double knockbackZ) {
        net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent event = net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack(this, (float) knockbackStrength, knockbackX, knockbackZ);
        if(event.isCanceled()) return;
        knockbackStrength = event.getStrength();
        knockbackX = event.getRatioX();
        knockbackZ = event.getRatioZ();
        knockbackStrength *= 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(knockbackStrength <= 0.0)) {
            this.hasImpulse = true;
            Vec3 deltaMovement = this.getDeltaMovement();
            Vec3 knockBack = new Vec3(knockbackX, 0.0, knockbackZ).normalize().scale(knockbackStrength);
            Vec3 vector = new Vec3(deltaMovement.x / 2.0 - knockBack.x,
                    this.onGround() ? Math.min(0.4, deltaMovement.y / 2.0 + knockbackStrength) : deltaMovement.y,
                    deltaMovement.z / 2.0 - knockBack.z).subtract(deltaMovement);
            this.knockbackVector = this.knockbackVector.add(vector);
        }
    }

    public int nameTicks = 0;
    public int openTime = 0;
    @Override
    public void tick() {
        super.tick();



        this.pVelocity = this.velocity;
        this.velocity = this.velocity.lerp(this.getPosition(0).subtract(this.getPosition(1)), 0.1F);


        this.setNoGravity(true);

        if (this.navigation.getPath() != null) {
            ClinkerPacketHandler.sendToAllClients(new ClientboundUrnPathPacket(this, this.navigation.getPath()));
        }

        if (this.navigation.getPath() != null) {
            if (!this.navigation.isDone()) {
                this.targetDesiredPosition = this.navigation.getTargetPos().getCenter();
            }
        }
        Vec3 targetPos = this.targetDesiredPosition;
        ClipContext clipContext = new ClipContext(targetPos, targetPos.subtract(0, 1.0, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult targetFloorRaycast = this.level().clip(clipContext);
        if (targetFloorRaycast.getType() == HitResult.Type.BLOCK) targetPos = targetFloorRaycast.getLocation().add(0, 1.0, 0);

        this.desiredPosition = this.desiredPosition.lerp(targetPos, 0.1F);
        Vec3 desiredPositionGroundAdjusted = this.desiredPosition;
        ClipContext clipContext2 = new ClipContext(this.desiredPosition, this.desiredPosition.subtract(0, 1.0, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult dPosFloorRaycast = this.level().clip(clipContext2);
        if (dPosFloorRaycast.getType() == HitResult.Type.BLOCK) desiredPositionGroundAdjusted = dPosFloorRaycast.getLocation().add(0, 1.0, 0);

        float controlFactor = this.getControlFactor();

        Vec3 deltaMovement = this.getDeltaMovement().scale(0.5F).add(0, -0.04 * (1 - controlFactor), 0);
        Vec3 desiredVelocity = desiredPositionGroundAdjusted.subtract(this.position()).normalize().scale(this.moveControl.getSpeedModifier() * 0.3F); //this.getAttributeValue(Attributes.MOVEMENT_SPEED)
        Vec3 velocityCorrection = desiredVelocity.subtract(this.getDeltaMovement());
        this.knockbackVector = this.knockbackVector.scale(0.9);
        this.setDeltaMovement(deltaMovement.add(velocityCorrection.scale(Math.min(controlFactor, 0.5))).add(knockbackVector)); // Math.min(controlFactor, 0.5)
        Vec3 rotationVector = desiredVelocity.multiply(1, 0, 1).normalize();
        float rotation = (float) Mth.atan2(rotationVector.x, rotationVector.z);
        this.setYRot((float) Math.toDegrees(rotation));

        this.timeSinceLastStep++;
        int attachedLegs = 0;
        for (UrnFoot leg : this.legs) {
            if (leg.isAttached()) attachedLegs++;
        }
        boolean canStep = attachedLegs >= 3 || attachedLegs == 0 || timeSinceLastStep > 20;
        int legsMoved = 0;
        for (UrnFoot leg : this.legs) {
            if (leg.update(canStep && legsMoved < 3)) {
                this.timeSinceLastStep = 0;
                legsMoved++;

                if (this.level().isClientSide) {
                    this.level().playLocalSound(this, SoundEvents.STEM_STEP, SoundSource.HOSTILE, 0.1F, this.random.nextFloat() + this.random.nextFloat());
                }

            }
        }
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public void onPathfindingDone() {
        super.onPathfindingDone();
        this.targetDesiredPosition = this.position();
    }

    // in meters per second
    public Vec3 getAnimationVelocity(float partialTicks) {
        return this.pVelocity.lerp(this.velocity, partialTicks).scale(20.0F);
    }

    public float getControlFactor() {
        float factor = 0.0F;
        for (UrnFoot leg : this.legs) {
            if (leg.isAttached()) factor += 1.0F / this.legs.length;
        }
        return factor;
    }

    public InterpolatedSkeleton skeleton;

    @Override
    public void setSkeleton(InterpolatedSkeleton skeleton) {
        this.skeleton = skeleton;
    }

    @Override
    public InterpolatedSkeleton getSkeleton() {
        return skeleton;
    }

    public static class UrnMoveController extends MoveControl {
        public UrnMoveController(Mob p_24983_) {
            super(p_24983_);
        }

        // don't do anything LOL
        @Override
        public void tick() {
            super.tick();
        }
    }

    public static class UrnWanderGoal extends WaterAvoidingRandomStrollGoal {

        public UrnWanderGoal(PathfinderMob mob, double speedModifier) {
            super(mob, speedModifier);
        }

        public UrnWanderGoal(PathfinderMob mob, double speedModifier, float probability) {
            super(mob, speedModifier, probability);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            return super.getPosition().add(0, 0.8, 0);
        }
    }

    public static class UrnFoot {
        final UrnEntity parent;
        public final Vec3 startPos;
        final Vec3 castOffset;
        final float castLength;
        boolean hasDesiredPlacement = false, isMovingFromStep = false;

        float stepProgress;
        Vec3 currentPosition = Vec3.ZERO, previousCurrentPosition = Vec3.ZERO,
                desiredPosition = Vec3.ZERO,
                previousPosition = Vec3.ZERO;
        Vec3 currentNormal = new Vec3(0, 1, 0), previousCurrentNormal = new Vec3(0, 1, 0),
                desiredNormal = new Vec3(0, 1, 0),
                previousNormal = new Vec3(0, 1, 0);

        BlockState attachedState;
        BlockPos attachedPosition;

        double legLength;
        float stepSpeed = 0.2F;

        public UrnFoot(UrnEntity parent, Vec3 startPos, Vec3 castOffset, float castLength) {
            this.parent = parent;
            this.startPos = startPos;
            this.castOffset = castOffset;
            this.castLength = castLength;

            this.legLength = castOffset.add(0, this.castLength * 1.1F, 0).length();

            //this.setNeutralFootPos();
        }

        public boolean update(boolean canStep) {
            boolean hasStepped = false;
            this.previousCurrentPosition = this.currentPosition;
            this.previousCurrentNormal = this.currentNormal;

            boolean isFootPlacementValid = this.isDesiredFootPlacementValid();
            boolean attemptNewStep = canStep && (this.stepProgress < 0.1 || this.stepProgress > 0.9) && !isFootPlacementValid;
            // if our current foot placement is invalid,
            if (attemptNewStep) {
                // then search for a new foot placement.
                boolean success = this.findNewFootPosition();
                hasStepped = success;
            }

            this.stepSpeed = 0.2F;

            float rotation = (float) Math.toRadians(parent.getYRot());
            Vec3 castO = castOffset.yRot(rotation);
            if (this.hasDesiredPlacement) {
                // interpolate between the step positions
                this.stepProgress = Math.min(this.stepProgress + ((1 / this.stepSpeed) * 0.05F), 1.0F);
                float footLiftHeight = this.isMovingFromStep ? 0.6F : 0.0F;
                float footLiftFactor = Mth.sin(this.stepProgress * Mth.PI);
                this.currentNormal = stepProgress > 0.99 ? this.desiredNormal : VectorUtils.slerp(this.previousNormal, this.desiredNormal, this.stepProgress).normalize();
                this.currentPosition = this.previousPosition.lerp(this.desiredPosition, this.stepProgress).add(0, footLiftFactor * footLiftHeight, 0);
            } else {
                // assume the default position.
                Vec3 worldspaceCastOffset = castO.add(parent.position());
                Vec3 castPosition = worldspaceCastOffset.subtract(0, this.castLength, 0);
                this.currentPosition = this.currentPosition.lerp(castPosition, 0.1F);
                this.currentNormal = this.currentNormal.normalize().lerp(new Vec3(0, 1, 0).add(castO.normalize().scale(1)).normalize(), 0.1F).normalize();
            }

            return hasStepped;
        }

        public boolean isAttached() {
            return this.hasDesiredPlacement && (this.stepProgress < 0.1 || this.stepProgress > 0.9);
        }

        private boolean findNewFootPosition() {
            // find cast position, relative to current parent orientation
            float rotation = (float) Math.toRadians(parent.getYRot());
            Vec3 worldspaceCastOffset = castOffset.yRot(rotation).add(parent.position());
            Vec3 worldspaceStartPos = startPos.yRot(rotation).add(parent.position());
            Vec3 deadReckoning = this.parent.getPosition(1.0F).subtract(this.parent.getPosition(0.0F)).scale(20).scale(1.0 / stepSpeed).scale(0.18F);
            Vec3 castPosition = worldspaceCastOffset.subtract(0, this.castLength, 0).add(deadReckoning);

            ClipContext clipContext = new ClipContext(worldspaceStartPos, castPosition, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, parent);
            BlockHitResult result = parent.level().clip(clipContext);

            if (result.getType() == HitResult.Type.BLOCK) {
                this.attachedState = parent.level().getBlockState(result.getBlockPos());
                this.attachedPosition = result.getBlockPos();

                // reset step progress
                this.stepProgress = 0.0F;

                // if it didn't have a valid foot position before this one (e.g. if it was too far away from the ground),
                // then don't do the whole step animation thing.
                this.isMovingFromStep = hasDesiredPlacement;
                this.hasDesiredPlacement = true;

                this.previousPosition = this.currentPosition;
                this.previousNormal = this.currentNormal;
                this.desiredPosition = result.getLocation();
                this.desiredNormal = new Vec3(result.getDirection().step()).add(this.castOffset.yRot(rotation).normalize().scale(1)).normalize();
                return true;
            } else {
                this.hasDesiredPlacement = false;
                this.attachedPosition = null;
                this.attachedState = null;
                return false;
            }
        }

        private boolean isDesiredFootPlacementValid() {
            // if we don't currently have a desired foot placement, the desired foot placement is invalid.
            if (this.hasDesiredPlacement == false) return false;

            // if you're currently moving, always return true.
            if (this.stepProgress > 0.01 && this.stepProgress < 0.99) return true;

            // find cast position, relative to current parent orientation
            float rotation = (float) Math.toRadians(parent.getYRot());
            Vec3 worldspaceCastOffset = castOffset.yRot(rotation).add(parent.position());
            Vec3 worldspaceStartPos = startPos.yRot(rotation).add(parent.position());

            // rule: if it's too far away, it's invalid.
            Vec3 deadReckoning = this.parent.getPosition(1.0F).subtract(this.parent.getPosition(0.0F)).scale(20).scale(1.0 / stepSpeed).scale(0.1F);
            if (this.desiredPosition.distanceTo(this.parent.position().add(deadReckoning)) > legLength) return false;

            // rule - if it's too far away horizontally from the cast position, it's invalid. this is dependent on the current velocity.
            double velocityFactor = this.parent.getPosition(0.0F).subtract(this.parent.getPosition(1.0F)).scale(20).length();
            velocityFactor = MathUtils.mapRange(0, 0.1, 0, 1, velocityFactor);
            velocityFactor *= velocityFactor * velocityFactor * velocityFactor;
            velocityFactor = Mth.clamp(velocityFactor, 0.3, 1);
            if (this.desiredPosition.multiply(1, 0, 1)
                    .distanceTo(worldspaceCastOffset.multiply(1, 0, 1))
                    > legLength * velocityFactor) return false;

            // rule: if the blockstate changes, the placement is invalid.
            if (this.parent.level().getBlockState(this.attachedPosition) != attachedState) return false;

            // rule: if there's a block in the way, the placement is invalid.
            // todo: do this as a curve rather than a straight line.
            // offset it by a little bit, so it doesnt treat the floor as a hit.
            Vec3 castPosition = this.desiredPosition.add(this.desiredNormal.scale(0.1));
            ClipContext clipContext = new ClipContext(worldspaceStartPos, castPosition, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, parent);
            BlockHitResult result = parent.level().clip(clipContext);
            if (result.getType() == HitResult.Type.BLOCK) {
                return false;
            }

            // if no rules are satisfied, the foot placement is valid.
            return true;
        }

        private void setFootPos(Vec3 newPos, Vec3 newNormal) {
            // reset step progress
            this.stepProgress = 0.0F;

            // if it didn't have a valid foot position before this one (e.g. if it was too far away from the ground),
            // then don't do the whole step animation thing.
            this.isMovingFromStep = hasDesiredPlacement;

            this.hasDesiredPlacement = true;

            this.previousPosition = this.currentPosition;
            this.desiredPosition = newPos;

            this.previousNormal = this.currentNormal;
            this.desiredNormal = newNormal;
        }

        private void setNeutralFootPos() {
            float rotation = (float) Math.toRadians(parent.getYRot());
            Vec3 worldspaceCastOffset = castOffset.yRot(rotation);
            Vec3 pos = worldspaceCastOffset.scale(0.5).add(0, -this.castLength * 0.8, 0);
            this.desiredPosition = this.parent.position().add(pos);
            this.desiredNormal = pos.subtract(worldspaceCastOffset).normalize();
        }

        private void interpolateFoot() {
            float footLiftHeight = (float) (Math.abs(this.previousPosition.y - this.desiredPosition.y) + (0.6F));
            float footLiftFactor = this.isMovingFromStep ? Mth.sin(this.stepProgress * Mth.PI) : 0.0F;
            this.currentNormal = VectorUtils.slerp(this.previousNormal, this.currentNormal, this.stepProgress);
            this.currentPosition = this.previousPosition.lerp(this.desiredPosition, this.stepProgress).add(this.currentNormal.scale(footLiftFactor * footLiftHeight));
        }

        public Vec3 getStartPos(float partialTicks) {
            return startPos.yRot((float) Math.toRadians(Mth.rotLerp(partialTicks, parent.yRotO, parent.getYRot())));
        }
        public Vec3 getCurrentPosition(float partialTicks) {
            return this.previousCurrentPosition.lerp(this.currentPosition, partialTicks);
        }
        public Vec3 getCurrentNormal(float partialTicks) {
            return this.previousCurrentNormal.lerp(this.currentNormal, partialTicks).normalize();
        }
    }
}
