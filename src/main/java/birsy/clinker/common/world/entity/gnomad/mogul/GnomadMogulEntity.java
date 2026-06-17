package birsy.clinker.common.world.entity.gnomad.mogul;

import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulAnimator;
import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulSkeleton;
import birsy.clinker.common.world.entity.gnomad.BaseGnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.sets.SharedGnomadBehaviorSets;
import birsy.clinker.common.world.entity.system.squad.Squad;
import birsy.clinker.common.world.entity.system.squad.SquadSystem;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class GnomadMogulEntity extends BaseGnomadEntity<GnomadMogulEntity> implements SkeletonParent<GnomadMogulEntity, GnomadMogulSkeleton> {
    private static final int[] ROBE_COLORS = new int[]{0x4d423c, 0x513337, 0x4a4751, 0x505049, 0x4f4c4b};
    private static final EntityDataAccessor<Integer> DATA_ROBE_COLOR = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLOATING = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.BOOLEAN);

    private final MogulAttackHandler attackHandler;

    private boolean canStartFloating = true;
    private int ticksFloating = 0;

    public GnomadMogulEntity(EntityType<? extends GnomadMogulEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.attackHandler = new MogulAttackHandler(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.STEP_HEIGHT, 1.1)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    private Vector3f knockbackVector = new Vector3f();
    protected void attack(LivingEntity entity, float damage, float knockbackX, float knockbackY, float knockbackZ) {
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage;
        boolean intentionalHit = entity == this.getTarget();
        DamageSource source = intentionalHit ? this.damageSources().noAggroMobAttack(this) : this.damageSources().mobAttack(this);
        if (entity.hurt(source, attackDamage)) {
            float knockbackResistance = (float) entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            float knockbackMultiplier = Math.max(0.0F, 1.0F - knockbackResistance);
            Vector3f knockback = knockbackVector.set(knockbackX, knockbackY, knockbackZ)
                    .rotateY(-this.yBodyRot * Mth.DEG_TO_RAD)
                    .mul(knockbackMultiplier);
            entity.addDeltaMovement(new Vec3(knockback.x(), knockback.y(), knockback.z()));

            if (this.level() instanceof ServerLevel serverLevel) EnchantmentHelper.doPostAttackEffects(serverLevel, entity, source);
            this.setLastHurtMob(entity);
            this.playAttackSound();
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (this.level().isClientSide()) this.attackHandler.handleAnimationEvent(pId, this.animator);
    }

    // data
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ROBE_COLOR, ROBE_COLORS[0]);
        builder.define(DATA_FLOATING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("RobeColor", this.getRobeColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setRobeColor(pCompound.getInt("RobeColor"));
    }

    // ai
    private boolean canFallSafely() {
        double safeFallDist = this.getAttributeValue(Attributes.SAFE_FALL_DISTANCE);
        return this.level().clip(new ClipContext(
                this.position(),
                this.position().add(0, -safeFallDist, 0),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.WATER, this
        )).getType() == HitResult.Type.BLOCK;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        recruitNearbyGnomads();
        updateFloating();
    }

    void recruitNearbyGnomads() {
        if (this.level() instanceof ServerLevel serverLevel) {
            // if we aren't currently in a squad,
            // create one and become the leader!
            if (this.getSquad() == null) {
                Squad newSquad = SquadSystem.get(serverLevel).getOrCreate(UUID.randomUUID());
                this.setSquad(newSquad);
                newSquad.setLeader(this);
            }
            // recruit nearby gnomads into our squad
            List<SquadMember<?>> nearbyRecruits = EntityRetrievalUtil.getEntities(
                    this,
                    10, 10, 10,
                    entity -> {
                        if (entity instanceof BaseGnomadEntity<?> potentialRecruit)
                            return potentialRecruit.getSquad() == null;
                        return false;
                    }
            );
            for (SquadMember<?> recruit : nearbyRecruits) {
                recruit.setSquad(this.getSquad());
            }
        }
    }

    void updateFloating() {
        boolean isFloating = this.isFloating();
        boolean isMovingUp = this.getDeltaMovement().y > 0;
        if (!this.onGround()) {
            // we're in the air...
            if (isFloating) {
                // ...and floating!
                this.resetFallDistance();
                boolean shouldStop = false;

                // if we're going up, ignore any checks to stop falling.
                if (!isMovingUp) {
                    // stop flying if we've been falling for a while
                    if (this.ticksFloating++ > 100) shouldStop = true;
                    // stop flying if we can safely drop down
                    if (this.canFallSafely() && !shouldStop) shouldStop = true;
                } else {
                    this.ticksFloating = 0;
                }

                if (shouldStop) {
                    this.setFloating(false);
                    this.canStartFloating = false;
                }
            } else {
                //...but not floating.
                if (isMovingUp) {
                    this.canStartFloating = true;
                    this.ticksFloating = 0;
                } else {
                    // we're in freefall
                    if (this.canStartFloating && !this.canFallSafely()) {
                        // begin floating automatically if we could take fall damage in the future
                        this.setFloating(true);
                        this.ticksFloating = 0;
                    }
                }
            }
        } else {
            // we're on the ground.
            this.canStartFloating = true;
            // we can't float if we're on the ground.
            if (isFloating && !isMovingUp) this.setFloating(false);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return this.isFloating() ? super.getDefaultGravity() * 0.07 : super.getDefaultGravity();
    }


    @Override
    public BrainActivityGroup<GnomadMogulEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                SharedGnomadBehaviorSets.<GnomadMogulEntity>setIdleLookTargets()
        );
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // we're a little silly. choose a random robe
        this.setRobeColor(ROBE_COLORS[this.random.nextInt(ROBE_COLORS.length)]);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public int getRobeColor() {
        return this.entityData.get(DATA_ROBE_COLOR);
    }
    public void setRobeColor(int robeColor) {
        this.entityData.set(DATA_ROBE_COLOR, robeColor);
    }

    public boolean isFloating() {
        return this.entityData.get(DATA_FLOATING);
    }
    public void setFloating(boolean floating) {
        this.entityData.set(DATA_FLOATING, floating);
    }

    @Override
    public float squadPositionWeight() {
        return 2.5F;
    }

    GnomadMogulSkeleton skeleton;
    GnomadMogulAnimator animator;
    @Override public void setSkeleton(GnomadMogulSkeleton skeleton) {
        this.skeleton = skeleton;
    }
    @Override public void setAnimator(Animator<GnomadMogulEntity, GnomadMogulSkeleton> animator) {this.animator = (GnomadMogulAnimator) animator;}
    @Override public GnomadMogulSkeleton getSkeleton() {
        return this.skeleton;
    }
    @Override public GnomadMogulAnimator getAnimator() {
        return animator;
    }
}
