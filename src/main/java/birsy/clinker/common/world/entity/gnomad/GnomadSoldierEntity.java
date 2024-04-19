package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.common.world.entity.ai.SetRandomEntityLookTarget;
import birsy.clinker.common.world.entity.ai.SetWalkTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.InitiateRelaxWithSquad;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.SitAndRelaxWithSquad;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.GnomadSquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.TargetSupplyDepotSensor;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.ReactToUnreachableTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class GnomadSoldierEntity extends GnomadEntity implements SmartBrainOwner<GnomadSoldierEntity> {
    public GnomadSoldierEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends GnomadSoldierEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<GnomadSoldierEntity>().setRadius(24),
                new HurtBySensor<>(),
                new GnomadSquadSensor<>(),
                new SupplyDepotSensor<GnomadSoldierEntity>().setRadius(24),
                new TargetSupplyDepotSensor<GnomadSoldierEntity>().setRadius(32)
        );
    }


    @Override
    public BrainActivityGroup<? extends GnomadSoldierEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<? extends GnomadSoldierEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<GnomadSoldierEntity>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>(),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
//        return BrainActivityGroup.idleTasks(
//                new FirstApplicableBehaviour<>(
//                        new TargetOrRetaliate<>(),
//                        new FirstApplicableBehaviour<GnomadSoldierEntity>( // walk to the relaxation point and relax.
//                                new SitAndRelaxWithSquad<>()
//                                        .runFor(entity -> entity.getRandom().nextInt(5 * 20, 30 * 20))
//                                        .cooldownFor((entity) -> 10 * 20),
//                                new ReactToUnreachableTarget<>().reaction((entity, towering) -> BrainUtils.clearMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get())), // if we can't reach the relaxation spot, forget about it.
//                                new SetWalkTarget<>() {
//                                    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
//                                            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
//                                            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_PRESENT));
//                                    @Override protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {return MEMORY_REQUIREMENTS; } }
//                                        .position((entity -> BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter()))
//                                        .closeEnoughWhen((entity) -> 2)
//                                        .cooldownFor((entity) -> 10 * 20)
//                        ).startCondition((entity -> BrainUtils.hasMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()))), //... but only if we have a relaxation spot currently.
//                        new SetPlayerLookTarget<>(),
//                        new SetRandomEntityLookTarget<>().predicate((entity) -> entity instanceof GnomadEntity).probabilityPerEntity(0.5F),
//                        new SetRandomLookTarget<>()
//                ),
//                new OneRandomBehaviour<>(
//                        new SetRandomWalkTarget<>(),
//                        new InitiateRelaxWithSquad<>(),
//                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
//                )
//        );
    }
}
