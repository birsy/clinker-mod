package birsy.clinker.common.world.level.heat;

import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Arrays;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class HeatSystem extends SavedData {
    final ServerLevel level;
    final ObjectList<HeatPacket> heatPackets = new ObjectArrayList<>();

    public HeatSystem(ServerLevel level) {
        this.level = level;
    }

    public static HeatSystem get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(
                        () -> new HeatSystem(level),
                        (data, registry) -> HeatSystem.load(level, data, registry),
                        null
                ), "heat_system"
        );
    }

    public void spawnPacket(BlockPos pos) {
        heatPackets.add(new HeatPacket(pos.getX(), pos.getY(), pos.getZ()));
    }

    public void tick() {
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        Direction[] directions = Direction.values();
        float[] conductivityByDirectionIndex = new float[directions.length];
        RandomSource random = level.getRandom();
        for (HeatPacket packet : heatPackets) {
            mPos.set(packet.x, packet.y, packet.z);

            // heat that goes outside loaded boundaries - well, it's not loaded,
            // so we can probably just pretend it dissipates into the atmosphere sink or something
            if (!level.isLoaded(mPos) || !level.isInWorldBounds(mPos)) {
                packet.markForRemoval();
                continue;
            }

            BlockState currentState = level.getBlockState(mPos);
            HeatPropertiesProvider provider = currentState.getBlock() instanceof HeatPropertiesProvider stateAsProvider ?
                    stateAsProvider : DefaultHeatPropertiesProvider.INSTANCE;

            HeatReader reader = currentState.getBlock() instanceof HeatReader stateAsReader ?
                    stateAsReader : DefaultHeatReader.INSTANCE;

            reader.onPacketPassed(level, mPos, currentState);

            float absorptionProbability = provider.getAbsorptionProbability(level, mPos, currentState);
            if (random.nextFloat() <= absorptionProbability) {
                packet.markForRemoval();
                reader.onPacketConsumed(level, mPos, currentState);
                continue;
            }

            // todo: cache this
            float totalConductivityWeight = 0;
            for (Direction dir : directions) {
                float conductivity = provider.getHeatConductivityWeightForDirection(level, mPos, currentState, dir);
                conductivityByDirectionIndex[dir.get3DDataValue()] = conductivity;
                totalConductivityWeight += conductivity;
            }

            Direction conductionDirection = Direction.DOWN;
            float randomNumber = level.random.nextFloat() * totalConductivityWeight;
            float rangeEnd = 0.0F;
            for (int j = 0; j < conductivityByDirectionIndex.length; j++) {
                rangeEnd += conductivityByDirectionIndex[j];
                if (randomNumber < rangeEnd) {
                    conductionDirection = Direction.from3DDataValue(j);
                    break;
                }
            }

            packet.x += conductionDirection.getStepX();
            packet.y += conductionDirection.getStepY();
            packet.z += conductionDirection.getStepZ();
            packet.timeAlive++;
        }

        heatPackets.removeIf(HeatPacket::shouldRemove);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLongArray("heat_packet_positions",
                heatPackets.stream()
                        .filter(HeatPacket::shouldSave)
                        .map(packet -> BlockPos.asLong(packet.x, packet.y, packet.z))
                        .toList()
        );
        tag.putIntArray("heat_packet_times_alive",
                heatPackets.stream()
                        .filter(HeatPacket::shouldSave)
                        .map(packet -> packet.timeAlive)
                        .toList()
        );
        return tag;
    }

    static HeatSystem load(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        HeatSystem system = new HeatSystem(level);

        long[] heatPacketPositions = tag.getLongArray("heat_packet_positions");
        int[] heatPacketTimesAlive = tag.getIntArray("heat_packet_times_alive");
        for (int i = 0; i < heatPacketPositions.length; i++) {
            long packedPos = heatPacketPositions[i];
            int x = BlockPos.getX(packedPos),
                y = BlockPos.getY(packedPos),
                z = BlockPos.getZ(packedPos);
            int timeAlive = heatPacketTimesAlive[i];
            HeatPacket packet = new HeatPacket(x, y, z);
            packet.timeAlive = timeAlive;
            system.heatPackets.add(packet);
        }
        return system;
    }

    @SubscribeEvent
    public static void tickHeatSystem(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("clinker.tickHeatSystem");

            HeatSystem heatSystem = get(level);
            heatSystem.tick();

            profiler.pop();
        }
    }

    static class HeatPacket {
        int x, y, z;
        int timeAlive = 0;
        boolean markedForRemoval = false;

        HeatPacket(int x, int y, int z) {
            this.x = x; this.y = y; this.z = z;
        }

        void markForRemoval() { markedForRemoval = true; }
        boolean shouldRemove() { return markedForRemoval; }
        boolean shouldSave() { return !markedForRemoval; }
    }

    // todo: maybe make this tag-based, or data-map based?
    // so modpack authors can add their own without getting into the coding weeds.
    static class DefaultHeatPropertiesProvider implements HeatPropertiesProvider {
        static DefaultHeatPropertiesProvider INSTANCE = new DefaultHeatPropertiesProvider();
        @Override
        public float getHeatConductivityWeightForDirection(Level level, BlockPos pos, BlockState state, Direction direction) {
            return 1.0F / 6.0F;
        }
        @Override
        public float getAbsorptionProbability(Level level, BlockPos pos, BlockState state) {
            return 0.01F;
        }
    }
    static class DefaultHeatReader implements HeatReader {
        static DefaultHeatReader INSTANCE = new DefaultHeatReader();
        // no op
    }
}
