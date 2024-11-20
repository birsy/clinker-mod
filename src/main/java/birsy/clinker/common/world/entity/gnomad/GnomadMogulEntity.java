package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundBrainDebugPacket;
import birsy.clinker.common.world.entity.ai.behaviors.*;
import birsy.clinker.common.world.entity.ai.behaviors.LookAtNearestEntity;
import birsy.clinker.common.world.entity.ai.behaviors.SetRandomLookTargetImproved;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.*;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.GnomadSquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.RestWithFriendsTask;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.util.MathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class GnomadMogulEntity extends GnomadEntity implements SmartBrainOwner<GnomadMogulEntity>, SkeletonParent {
    private static final int[] ROBE_COLORS = new int[]{0x4d423c, 0x513337, 0x4a4751, 0x505049, 0x4f4c4b};
    private static final EntityDataAccessor<Integer> DATA_ROBE_COLOR = SynchedEntityData.defineId(GnomadMogulEntity.class, EntityDataSerializers.INT);

    public GnomadMogulEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                //.add(NeoForgeMod.STEP_HEIGHT.value(), 1.1D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    // data
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ROBE_COLOR, ROBE_COLORS[0]);
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
        ClinkerPacketHandler.sendToClientsTrackingEntity(this, new ClientboundBrainDebugPacket(this));
       // if (this.navigation.getPath() != null)
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) this.computeHeightOffset();
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
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        // we're a little silly. choose a random robe
        this.setRobeColor(ROBE_COLORS[this.random.nextInt(ROBE_COLORS.length)]);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public int getRobeColor() {
        return this.entityData.get(DATA_ROBE_COLOR);
    }

    public void setRobeColor(int robeColor) {
        this.entityData.set(DATA_ROBE_COLOR, robeColor);
    }

    @OnlyIn(Dist.CLIENT)
    protected double prevSmoothedHeight = 0;
    @OnlyIn(Dist.CLIENT)
    protected double smoothedHeight = 0;
    @OnlyIn(Dist.CLIENT)
    protected void computeHeightOffset() {
        this.prevSmoothedHeight = this.smoothedHeight;
        Vec3 samplePosition = this.getPosition(1.0F).add(0, 0.1F, 0);
        Vec3 to = samplePosition.add(0, -this.getStepHeight() - 0.1F, 0);
        HitResult result = this.level().clip(new ClipContext(samplePosition, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this));
        double height = (result.getLocation().y + this.getY()) / 2.0F;
        this.smoothedHeight = Mth.lerp(0.3F, this.smoothedHeight, height);
        if (this.smoothedHeight > this.getY()) this.smoothedHeight = this.getY();
        this.smoothedHeight = MathUtils.clampDifference(this.smoothedHeight, this.getY(), this.getStepHeight());
    }
    @OnlyIn(Dist.CLIENT)
    public float getHeightOffset(float partialTick) {
        return (float) (Mth.lerp(partialTick, prevSmoothedHeight, smoothedHeight) - this.getPosition(partialTick).y);
    }

    Skeleton<?> skeleton;
    @Override
    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    @Override
    public Skeleton getSkeleton() {
        return skeleton;
    }
}
