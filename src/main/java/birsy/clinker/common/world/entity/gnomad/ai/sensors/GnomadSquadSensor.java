package birsy.clinker.common.world.entity.gnomad.ai.sensors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.Comparator;
import java.util.List;

public class GnomadSquadSensor<E extends GnomadEntity> extends NearbyLivingEntitySensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ClinkerMemoryModules.GNOMADS_IN_SQUAD.get());

    public GnomadSquadSensor() {
        this.setScanRate(entity -> 200);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.GNOMAD_SQUAD_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        SquareRadius radius = this.radius;

        if (radius == null) {
            double dist = entity.getAttributeValue(Attributes.FOLLOW_RANGE);
            radius = new SquareRadius(dist, dist);
        }

        List<GnomadEntity> entities = EntityRetrievalUtil.getEntities(level, entity.getBoundingBox().inflate(radius.xzRadius(), radius.yRadius(), radius.xzRadius()), obj -> obj instanceof GnomadEntity gnomad && predicate().test(gnomad, entity));
        entities.sort(Comparator.comparingDouble(squadmate -> entity.distanceToSqr(squadmate)));
        BrainUtils.setMemory(entity, ClinkerMemoryModules.GNOMADS_IN_SQUAD.get(), entities);
    }
}
