package birsy.clinker.common.world.entity.gnomad.mogul;

import birsy.clinker.client.entity.mogul.MogulAnimator;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.necromancer.SkeletonParent;
import birsy.clinker.common.networking.packet.debug.ClientboundBrainDebugPacket;
import birsy.clinker.common.world.entity.ai.behaviors.*;
import birsy.clinker.common.world.entity.ai.behaviors.LookAtNearestEntity;
import birsy.clinker.common.world.entity.ai.behaviors.SetRandomLookTargetImproved;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.*;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.GnomadSquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.RestWithFriendsTask;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.util.MathUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.GenericAttackTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class GnomadMogulEntity extends GnomadEntity implements SmartBrainOwner<GnomadMogulEntity>, SkeletonParent<GnomadMogulEntity, MogulSkeleton, MogulAnimator> {
    private static final int[] ROBE_COLORS = new int[]{0x4d423c, 0x513337, 0x4a4751, 0x505049, 0x4f4c4b};
    private static final EntityDataAccessor<Integer> DATA_ROBE_COLOR = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.INT);
    private final MogulAttackHandler attackHandler;

    public GnomadMogulEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
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
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("RobeColor", this.getRobeColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("RobeColor")) {
            this.setRobeColor(pCompound.getInt("RobeColor"));
        }
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

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
        super.customServerAiStep();
        PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundBrainDebugPacket(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) this.computeHeightOffset();
        if (!this.level().isClientSide()) {
            this.attackHandler.tick();
            if (this.tickCount % 100 == 0) {
                int attack = this.random.nextIntBetweenInclusive(0, 2);
                if (attack == 0) this.attackHandler.beginAttack(MogulAttackHandler.SWING_UP);
                if (attack == 1) this.attackHandler.beginAttack(MogulAttackHandler.SWING_LEFT);
                if (attack == 2) this.attackHandler.beginAttack(MogulAttackHandler.SWING_RIGHT);
            }

            if ((this.tickCount - 16) % 100 == 0) {
                this.attackHandler.cancelAttack();
            }
        }
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
                new GnomadSquadSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<GnomadMogulEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new SetWatchingEntity<>(),
                new InvalidateLookAtTarget<>(),
                new InvalidateCurrentSquadTask<>(),
                new MoveToWalkTarget<GnomadMogulEntity>().startCondition(mob -> !mob.isSitting()),
                new FloatToSurfaceOfFluid<>()
        );
    }

    @Override
    public BrainActivityGroup<GnomadMogulEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                // look behaviors
                new FirstApplicableBehaviour(
                        new SetRandomLookTargetImproved<>()
                                .lookTime(mob -> RandomUtil.randomNumberBetween(0, 16 * 20))
                                .lookChance(ConstantFloat.of(0.2F)),
                        new LookAtNearestPlayerExpiring<>()
                                .expirationTime(mob -> RandomUtil.randomNumberBetween(0, 8 * 20))
                                .startCondition(mob -> RandomUtil.fiftyFifty()), // 50/50 chance of looking at player or a friend.
                        new LookAtNearestEntity<>()
                                .expirationTime(mob -> RandomUtil.randomNumberBetween(0, 8 * 20))
                                .predicate((entity, mob) -> entity.getType().is(ClinkerTags.GNOMADS))
                ),
                // walking behaviors
                new FirstApplicableBehaviour(
                        // if we have a rest task, run that!
                        RestWithFriendsTask.createBehavior(),
                        new CustomBehaviour<GnomadMogulEntity>(mob -> mob.setSitting(false)).startCondition(GnomadEntity::isSitting),
                        new StayNearSquad().radius(16, 16),
                        new OneRandomBehaviour(
                                new SetRandomWalkTarget<>().speedModifier(0.7F),
                                new Idle<>().runFor(mob -> RandomUtil.randomNumberBetween(30, 120))
                        )
                ),
                new VolunteerForSquadTask<>()
                        .shouldVolunteer((mob, task) -> task instanceof RestWithFriendsTask)
                        .startCondition((mob) -> RandomUtil.oneInNChance(15 * 20)),
                new InitiateRelaxWithSquad<>().startCondition((mob) -> RandomUtil.oneInNChance(60 * 20))
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

    protected double prevSmoothedHeight = 0;
    
    protected double smoothedHeight = 0;
    
    protected void computeHeightOffset() {
        this.prevSmoothedHeight = this.smoothedHeight;
        Vec3 samplePosition = this.getPosition(1.0F).add(0, 0.1F, 0);
        Vec3 to = samplePosition.add(0, -this.maxUpStep() - 0.1F, 0);
        HitResult result = this.level().clip(new ClipContext(samplePosition, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this));
        double height = (result.getLocation().y + this.getY()) / 2.0F;
        this.smoothedHeight = Mth.lerp(0.3F, this.smoothedHeight, height);
        if (this.smoothedHeight > this.getY()) this.smoothedHeight = this.getY();
        this.smoothedHeight = MathUtil.clampDifference(this.smoothedHeight, this.getY(), this.maxUpStep());
    }
    
    public float getHeightOffset(float partialTick) {
        return (float) (Mth.lerp(partialTick, prevSmoothedHeight, smoothedHeight) - this.getPosition(partialTick).y);
    }

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
    public void setAnimator(MogulAnimator animator) {
        this.animator = animator;
    }
    @Override
    public MogulAnimator getAnimator() {
        return animator;
    }
}
