package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SupplyDepotSensor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class GnomadSoldierEntity extends GnomadEntity implements SmartBrainOwner<GnomadSoldierEntity> {
    public GnomadSoldierEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
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
                new SupplyDepotSensor<GnomadSoldierEntity>().setRadius(24)
        );
    }

    @Override
    public BrainActivityGroup<? extends GnomadSoldierEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends GnomadSoldierEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<GnomadSoldierEntity>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new SetRandomWalkTarget<>().speedModifier(1)
        );
    }
}
