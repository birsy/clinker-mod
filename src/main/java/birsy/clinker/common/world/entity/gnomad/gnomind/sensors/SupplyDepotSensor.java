package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.world.entity.gnomad.GnomadSupplyDepot;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.ChunkPos;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SupplyDepotSensor<E extends LivingEntity> extends NearbyLivingEntitySensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get());

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.SUPPLY_DEPOT_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        SquareRadius radius = this.radius;

        if (radius == null) {
            double dist = entity.getAttributeValue(Attributes.FOLLOW_RANGE);
            radius = new SquareRadius(dist, dist);
        }

        List<GnomadSupplyDepot> list = new ArrayList<>();

        for (ChunkPos chunkPos : ChunkPos.rangeClosed( // search nearby block entities for depots, too.
                new ChunkPos(entity.blockPosition().offset( Mth.ceil(this.radius.xzRadius()), Mth.ceil(this.radius.yRadius()), Mth.ceil(this.radius.xzRadius()))),
                new ChunkPos(entity.blockPosition().offset(-Mth.ceil(this.radius.xzRadius()),-Mth.ceil(this.radius.yRadius()),-Mth.ceil(this.radius.xzRadius())))).toList()) {
            level.getChunk(chunkPos.x, chunkPos.z).getBlockEntities().forEach((bpos, ent) -> {
                if (ent instanceof GnomadSupplyDepot depot) {
                    if (Math.abs(bpos.getY() - entity.getBlockY()) > this.radius.yRadius()) return;
                    list.add(depot);
                }
            });
        }
        list.addAll(EntityRetrievalUtil.getEntities(level, entity.getBoundingBox().inflate(radius.xzRadius(), radius.yRadius(), radius.xzRadius()),
                obj -> obj instanceof GnomadSupplyDepot));
        list.sort(Comparator.comparingDouble(depot -> entity.distanceToSqr(depot.getDepotLocation())));
        BrainUtils.setMemory(entity, ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get(), list);
    }
}
