package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

import javax.annotation.Nullable;
import java.util.List;

public class NearbyCoverSensor<E extends PathfinderMob & SquadMember<E>> extends ExtendedSensor<E> {
    private static final int SCAN_INTERVAL = 2;
    private static final List<MemoryModuleType<?>> MEMORIES =
            ObjectArrayList.of(ClinkerMemoryModules.COVER_POSITION.get());

    public NearbyCoverSensor() {
        this.setScanRate(entity -> SCAN_INTERVAL);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.NEARBY_COVER.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        BlockPos entityBlockPos = entity.blockPosition();
        level.getPoiManager()
                .findClosest(
                        typeHolder -> typeHolder.is(PoiTypes.FISHERMAN),
                        pos -> true,
                        entityBlockPos,
                        48,
                        PoiManager.Occupancy.ANY)
                .ifPresent(pos ->
                        BrainUtils.setMemory(
                                entity,
                                ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get(),
                                GlobalPos.of(level.dimension(), pos)
                        )
                );

    }

    private int scorePosition(BlockPos pos) {
        return 0;
    }

    @Nullable
    private BlockPos getRandomBlockPosAtSurface(ServerLevel level, E entity) {
        BlockPos.MutableBlockPos pos = RandomUtil
                .getRandomPositionWithinRange(entity.blockPosition(), 10, 5, 10).mutable();
        if (!level.getBlockState(pos).isSolid()) {
            for (int i = 0; i < 8; i++) {
                pos.move(Direction.DOWN);
                // return the position above the first non-air block
                if (level.getBlockState(pos).isSolid()) return pos.move(Direction.UP);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                pos.move(Direction.DOWN);
                if (!level.getBlockState(pos).isSolid()) return pos;
            }
        }

        return null;
    }
}
