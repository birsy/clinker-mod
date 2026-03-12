package birsy.clinker.common.world.entity.gnomad.mogul;

import birsy.clinker.client.entity.mogul.MogulAnimator;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.common.world.entity.ai.behaviors.*;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.*;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.GenericAttackTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class GnomadMogulEntity extends GroundLocomotionEntity implements Enemy, SquadMember<GnomadMogulEntity>, SmartBrainOwner<GnomadMogulEntity>, SkeletonParent<GnomadMogulEntity, MogulSkeleton> {
    private static final int[] ROBE_COLORS = new int[]{0x4d423c, 0x513337, 0x4a4751, 0x505049, 0x4f4c4b};
    private static final EntityDataAccessor<Integer> DATA_ROBE_COLOR = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLOATING = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.BOOLEAN);

    private Squad squad;
    private final MogulAttackHandler attackHandler;

    private boolean canStartFloating = true;
    private int ticksFloating = 0;

    public GnomadMogulEntity(EntityType<? extends GnomadMogulEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.attackHandler = new MogulAttackHandler(this);
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

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.STEP_HEIGHT, 1.1D);
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
        this.serializeSquad(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setRobeColor(pCompound.getInt("RobeColor"));
        this.deserializeSquad(pCompound);
    }

    // ai
    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }
    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new SmoothGroundNavigation(this, pLevel);
    }

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
        tickBrain(this);

        // update floating
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

        super.customServerAiStep();
    }

    @Override
    protected double getDefaultGravity() {
        return this.isFloating() ? super.getDefaultGravity() * 0.07 : super.getDefaultGravity();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public List<ExtendedSensor<GnomadMogulEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<GnomadMogulEntity>()
                        .setRadius(28.0F),
                new NearbyPlayersSensor<GnomadMogulEntity>()
                        .setRadius(28.0F),
                new GenericAttackTargetSensor<GnomadMogulEntity>()
                        .setPredicate((other, me) -> other instanceof Player),
                new HurtBySensor<>(),
                new InWaterSensor<>(),
                new SquadSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<GnomadMogulEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new InvalidateLookAtTarget<>(),
                new FloatToSurfaceOfFluid<>()
        );
    }

    @Override
    public BrainActivityGroup<GnomadMogulEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new MogulCombatStateMachine()
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
    public @Nullable Squad getSquad() { return squad; }
    @Override
    public void setSquad(@Nullable Squad squad) { this.squad = squad; }

    MogulSkeleton skeleton;
    MogulAnimator animator;
    @Override
    public void setSkeleton(MogulSkeleton skeleton) {
        this.skeleton = skeleton;
    }
    @Override
    public MogulSkeleton getSkeleton() {
        return this.skeleton;
    }
    @Override
    public void setAnimator(Animator<GnomadMogulEntity, MogulSkeleton> animator) {
        this.animator = (MogulAnimator) animator;
    }
    @Override
    public MogulAnimator getAnimator() {
        return animator;
    }
}
