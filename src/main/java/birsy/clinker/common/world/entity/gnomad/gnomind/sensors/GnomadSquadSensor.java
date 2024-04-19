package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

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

public class GnomadSquadSensor<E extends GnomadEntity> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ClinkerMemoryModules.GNOMADS_IN_SQUAD.get());

    public GnomadSquadSensor() {
        this.setScanRate(entity -> 20);
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
        List<GnomadEntity> squadMates = entity.squad.getMembersImmutable();
        BrainUtils.setMemory(entity, ClinkerMemoryModules.GNOMADS_IN_SQUAD.get(), squadMates);
    }
}
