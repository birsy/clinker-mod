package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.common.world.entity.ai.SetRandomEntityLookTarget;
import birsy.clinker.common.world.entity.ai.SetWalkTarget;
import birsy.clinker.common.world.entity.ai.SetWalkTargetToEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.*;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.GnomadSquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.TargetSupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.squad.ResupplyTask;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromBlockMemory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.ReactToUnreachableTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.AvoidEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class GnomadRuntEntity extends GnomadEntity implements SmartBrainOwner<GnomadRuntEntity> {
    public GnomadRuntEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.05D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D);
    }

    /* AI --------------------------- */

    @Override
    protected Brain.Provider<GnomadRuntEntity> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<ExtendedSensor<GnomadRuntEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new GnomadSquadSensor<>(),
                new HurtBySensor<>(),
                new SupplyDepotSensor<>(),
                new TargetSupplyDepotSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<GnomadRuntEntity> getCoreTasks() { // these are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new WalkOrRunToWalkTarget<>(),
                new AvoidEntity<>().avoiding(entity ->
                        !entity.getType().is(ClinkerTags.DOESNT_SCARE_GNOMAD_RUNTS)
                        &&  entity instanceof LivingEntity
                        && !entity.isDeadOrDying()
                        &&  entity.attackable())
                .startCondition((runt) -> true) // avoid anything that isnt a gnomad, unless we have a bomb.

        );
    }

    @Override
    public BrainActivityGroup<GnomadRuntEntity> getIdleTasks() { // these are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>(),
                        new FirstApplicableBehaviour<GnomadRuntEntity>( // walk to the relaxation point and relax.
                                new SitAndRelaxWithSquad<>()
                                        .runFor(entity -> entity.getRandom().nextInt(5 * 20, 30 * 20))
                                        .cooldownFor((entity) -> 10 * 20),
                                new ReactToUnreachableTarget<>().reaction((entity, towering) -> BrainUtils.clearMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get())), // if we can't reach the relaxation spot, forget about it.
                                new SetWalkTarget<>() {
                                    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
                                            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
                                            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_PRESENT));
                                    @Override protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {return MEMORY_REQUIREMENTS; } }
                                        .position((entity -> BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter()))
                                        .closeEnoughWhen((entity) -> 2)
                                        .cooldownFor((entity) -> 10 * 20)
                        ).startCondition((entity -> BrainUtils.hasMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()))), //... but only if we have a relaxation spot currently.
                        new SetPlayerLookTarget<>(),
                        new SetRandomEntityLookTarget<>().predicate((entity) -> entity instanceof GnomadEntity).probabilityPerEntity(0.5F),
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>(),
                        new InitiateRelaxWithSquad<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<GnomadRuntEntity> getFightTasks() { // these are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }
}
