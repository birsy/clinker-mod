package birsy.clinker.common.entity;

import birsy.clinker.core.Clinker;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class MudScarabEntity extends AbstractBugEntity {
    private float pHeight = 0;
    private float height = 0;
    private Vec3 pNormal = new Vec3(0, 1, 0);
    private Vec3 normal = new Vec3(0, 1, 0);

    public MudScarabEntity(EntityType<? extends MudScarabEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 3;
        this.moveControl = new SmoothSwimmingMoveControl(this, 6, 5, 4.0F, 1.0F, false);
        this.height =  (float) this.position().y;
        this.pHeight = (float) this.position().y;
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
    public boolean onClimbable() {
        BlockHitResult raycast = this.level.clip(new ClipContext(this.position(), this.position().add(0, this.getBbHeight() * -0.5, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return raycast.getType() == HitResult.Type.BLOCK;
    }

    public void tick() {
        // this is utterly fucked but until i get my hands on landlord it's the best i can be bothered to do
        AABB movementBox = this.getRidingBox();
        for(Entity entity : this.level.getEntities(this, movementBox, EntitySelector.NO_SPECTATORS.and((entity) -> !entity.isPassengerOfSameVehicle(this)))) {
            if (!(entity instanceof MudScarabEntity) && !entity.noPhysics && entity.isOnGround() && !entity.isPassenger()) {
                float scale = 0.83F;
                entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement().multiply(scale, 1.0F, scale)));

                // check for jumping. does not work though. try not to make them jump?
                if (this.getPosition(0.0F).y() < this.getPosition(1.0F).y()) {
                    entity.move(MoverType.SELF, new Vec3(0, this.getPosition(1.0F).y() - this.getPosition(0.0F).y(), 0));
                }
            }
        }

        if (this.getLevel().isClientSide()) this.calculateNormalAndHeight(0.2F);
        super.tick();
    }

    public AABB getRidingBox() {
        return this.getBoundingBox().move(0, this.getBbHeight() / 2, 0).deflate(0, this.getBbHeight() * 0.8F, 0).move(0, -this.getBbHeight() * 0.1F, 0);
    }

    public void calculateNormalAndHeight(float interpolationSpeed) {
        AABB boundingBox = this.getBoundingBox();
        Vec3[] corners = {new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
                          new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
                          new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
                          new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ)};
        Vec3[] castPoses = new Vec3[4];

        for (int i = 0; i < corners.length; i++) {
            Vec3 pos = corners[i];
            BlockHitResult raycast = this.level.clip(new ClipContext(pos, pos.add(0, -this.getBbHeight() - 1.0, 0).add(this.getLookAngle()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (raycast.getType() == HitResult.Type.MISS) {
                castPoses[i] = pos;
            } else {
                castPoses[i] = raycast.getLocation();
            }
        }

        Vec3 normal1 = calculateNormalFromTriangle(castPoses[0], castPoses[1], castPoses[2]);
        Vec3 normal2 = calculateNormalFromTriangle(castPoses[1], castPoses[2], castPoses[3]);
        Vec3 normal = normal1.add(normal2).normalize();

        //Clinker.LOGGER.info(normal);

        this.pNormal = this.normal;
        this.normal = this.normal.lerp(normal2, interpolationSpeed);
        this.pHeight = this.height;
        float h = 0; for (Vec3 castPos : castPoses) h += castPos.y; h /= 4.0F;
        this.height = Mth.lerp(interpolationSpeed, this.height, h);
    }

    private Vec3 calculateNormalFromTriangle(Vec3 a, Vec3 b, Vec3 c) {
        Vec3 dir = b.subtract(a).cross(c.subtract(a));
        Vec3 norm = dir.normalize();
        if (new Vec3(0, 1, 0).dot(norm) < 0.0) norm = norm.scale(-1.0F);
        return norm;
    }

    public float getHeight(float partialTick) {
        return Mth.lerp(partialTick, this.pHeight, this.height);
    }
    public Vec3 getNormal(float partialTick) {
        return this.pNormal.lerp(this.normal, partialTick);
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

    // unused :P
    public void moveRiders(Entity entity, Vec3 pPos) {
        if (!entity.noPhysics) {
            Vec3 vec3 = collideNoEntityCollision(entity, pPos);
            double d0 = vec3.lengthSqr();
            if (d0 > 1.0E-7D) {
                if (entity.fallDistance != 0.0F && d0 >= 1.0D) {
                    BlockHitResult blockhitresult = entity.level.clip(new ClipContext(entity.position(), entity.position().add(vec3), ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, entity));
                    if (blockhitresult.getType() != HitResult.Type.MISS) {
                        entity.resetFallDistance();
                    }
                }

                entity.setPos(entity.getX() + vec3.x, entity.getY() + vec3.y, entity.getZ() + vec3.z);
            }


            boolean flag2 = !Mth.equal(pPos.x, vec3.x);
            boolean flag = !Mth.equal(pPos.z, vec3.z);
            entity.horizontalCollision = flag2 || flag;
            entity.verticalCollision = pPos.y != vec3.y;
            entity.verticalCollisionBelow = entity.verticalCollision && pPos.y < 0.0D;

            entity.setOnGround(entity.verticalCollision && pPos.y < 0.0D);
            BlockPos blockpos = entity.getOnPosLegacy();
            BlockState blockstate = entity.level.getBlockState(blockpos);
            //entity.checkFallDamage(vec3.y, entity.isOnGround(), blockstate, blockpos);
            if (!entity.isRemoved()) {
                if (entity.horizontalCollision) {
                    Vec3 vec31 = entity.getDeltaMovement();
                    entity.setDeltaMovement(flag2 ? 0.0D : vec31.x, vec31.y, flag ? 0.0D : vec31.z);
                }

                Block block = blockstate.getBlock();
                if (pPos.y != vec3.y) {
                    block.updateEntityAfterFallOn(entity.level, entity);
                }

                double d1 = vec3.x;
                double d2 = vec3.y;
                double d3 = vec3.z;
                entity.flyDist = (float) ((double) entity.flyDist + vec3.length() * 0.6D);
                boolean flag1 = blockstate.is(BlockTags.CLIMBABLE) || blockstate.is(Blocks.POWDER_SNOW);
                if (!flag1) {
                    d2 = 0.0D;
                }
                entity.moveDist += (float)Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3) * 0.6F;


                entity.setDeltaMovement(entity.getDeltaMovement());
            }

        }
    }
    private Vec3 collideNoEntityCollision(Entity entity, Vec3 pVec) {
        AABB aabb = entity.getBoundingBox();
        List<VoxelShape> list = entity.level.getEntityCollisions(entity, aabb.expandTowards(pVec));
        Vec3 collidedVector = pVec.lengthSqr() == 0.0D ? pVec : collideBoundingBox(entity, pVec, aabb, entity.level, list);
        collidedVector = new Vec3(pVec.x(), collidedVector.y(), pVec.z());
        boolean collidedX = pVec.x != collidedVector.x;
        boolean collidedVertically = pVec.y != collidedVector.y;
        boolean collidedZ = pVec.z != collidedVector.z;
        boolean isGrounded = entity.isOnGround() || collidedVertically && pVec.y < 0.0D;
        float stepHeight = getStepHeight();

        return collidedVector;
    }

    // Easy solution for body rotation - don't!
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