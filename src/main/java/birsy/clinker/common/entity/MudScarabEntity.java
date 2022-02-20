package birsy.clinker.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MudScarabEntity extends AbstractBugEntity {

    public MudScarabEntity(EntityType<? extends MudScarabEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 3;
        this.moveControl = new SmoothSwimmingMoveControl(this, 6, 5, 4.0F, 1.0F, false);
        this.maxUpStep = 1.0F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(1, new MudScarabLookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new MudScarabLookAtPlayerGoal(this, MudScarabEntity.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.7D, 0.01F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public class MudScarabLookAtPlayerGoal extends LookAtPlayerGoal {
        public MudScarabLookAtPlayerGoal(MudScarabEntity pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
            super(pMob, pLookAtType, pLookDistance);
        }

        public MudScarabLookAtPlayerGoal(MudScarabEntity pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability) {
            super(pMob, pLookAtType, pLookDistance, pProbability);
        }

        public MudScarabLookAtPlayerGoal(MudScarabEntity pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability, boolean pOnlyHorizontal) {
            super(pMob, pLookAtType, pLookDistance, pProbability, pOnlyHorizontal);
        }

        @Override
        public boolean canUse() {
            if (this.lookAt != null) {
                AABB ridingBox = ((MudScarabEntity)(this.mob)).getRidingBox();

                if (ridingBox.contains(this.lookAt.getPosition(1.0F)) && (!(lookAt instanceof MudScarabEntity) && !lookAt.noPhysics && lookAt.isOnGround() && !lookAt.isPassenger())) {
                    return false;
                }
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.lookAt != null) {
                AABB ridingBox = ((MudScarabEntity)(this.mob)).getRidingBox();

                if (ridingBox.contains(this.lookAt.getPosition(1.0F)) && (!(lookAt instanceof MudScarabEntity) && !lookAt.noPhysics && lookAt.isOnGround() && !lookAt.isPassenger())) {
                    return false;
                }
            }
            return super.canContinueToUse();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void tick() {
        AABB movementBox = this.getRidingBox();
        for(Entity entity : this.level.getEntities(this, movementBox, EntitySelector.NO_SPECTATORS.and((entity) -> !entity.isPassengerOfSameVehicle(this)))) {
            if (!(entity instanceof MudScarabEntity) && !entity.noPhysics && entity.isOnGround() && !entity.isPassenger()) {
                entity.setDeltaMovement(entity.getDeltaMovement().x + this.getDeltaMovement().x, Math.abs(this.getDeltaMovement().y) > Math.abs(entity.getDeltaMovement().y) ? this.getDeltaMovement().y : entity.getDeltaMovement().y, entity.getDeltaMovement().z + this.getDeltaMovement().z);
            }
        }
        super.tick();
    }

    public AABB getRidingBox() {
        return this.getBoundingBox().move(0, this.getBbHeight() / 2, 0).deflate(0, this.getBbHeight() * 0.95F, 0);
    }

    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    public void push(Entity pEntity) {
        if (pEntity instanceof MudScarabEntity) {
            super.push(pEntity);
        } else if (pEntity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(pEntity);
        }

    }

    protected BodyRotationControl createBodyControl() {
        return new MudScarabEntity.MudScarabBodyRotationControl(this);
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.4375F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight();
    }

    @Override
    public int getMaxHeadYRot() {
        return 80;
    }

    @Override
    public int getMaxHeadXRot() {
        return 70;
    }

    @Override
    public int getHeadRotSpeed() {
        return super.getHeadRotSpeed() / 2;
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ENDERMITE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMITE_DEATH;
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 0.5F);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    //Easy solution for body rotation - don't!
    public class MudScarabBodyRotationControl extends BodyRotationControl {
        private final Mob mob;
        private int headStableTime;
        private float lastStableYHeadRot;

        public MudScarabBodyRotationControl(Mob pMob) {
            super(pMob);
            this.mob = pMob;
        }

        public void clientTick() {
            if (this.isMoving()) {
                this.mob.yBodyRot = this.mob.getYRot();
                this.rotateHeadIfNecessary();
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.headStableTime = 0;
            } else {
                if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) < 15.0F) {
                    ++this.headStableTime;
                    if (this.headStableTime > 10) {
                        this.rotateHeadTowardsFront();
                    }
                } else {
                    this.headStableTime = 0;
                    this.lastStableYHeadRot = this.mob.yHeadRot;
                }
            }
        }

        private void rotateHeadTowardsFront() {
            int i = this.headStableTime - 10;
            float f = Mth.clamp((float)i / 10.0F, 0.0F, 1.0F);

            float f1 = (float)this.mob.getMaxHeadYRot() * (1.0F - f);
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, f1);

            float f2 = (float)this.mob.getMaxHeadXRot() * (1.0F - f);
            this.mob.setXRot(Mth.rotateIfNecessary(this.mob.getXRot(), 0.0F, f2));
        }

        private void rotateHeadIfNecessary() {
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
            this.mob.setXRot(Mth.rotateIfNecessary(this.mob.getXRot(), 0.0F, this.mob.getMaxHeadXRot()));
        }

        private boolean isMoving() {
            double d0 = this.mob.getX() - this.mob.xo;
            double d1 = this.mob.getZ() - this.mob.zo;
            return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
        }
    }
}