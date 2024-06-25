package birsy.clinker.common.world.entity;

import birsy.clinker.common.world.entity.gnomad.GnomadSoldierEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.GnomadSquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.TargetSupplyDepotSensor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

public class FaeUrnEntity extends PathfinderMob implements SmartBrainOwner<FaeUrnEntity> {
    protected FaeUrnEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public List<? extends ExtendedSensor<? extends FaeUrnEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<FaeUrnEntity>().setRadius(24),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends FaeUrnEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<? extends FaeUrnEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<FaeUrnEntity>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>(),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
    }
}
