package birsy.clinker.common.world.level;

import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class BlockBreakageSystem extends SavedData {
    // arbitrary random breakage id
    private static final int BREAK_ID_HASH = 17834146;
    final ServerLevel level;
    final Long2ObjectMap<BreakState> breakStateTracker = new Long2ObjectOpenHashMap<>();

    public BlockBreakageSystem(ServerLevel level) {
        this.level = level;
    }

    public static BlockBreakageSystem get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(
                        () -> new BlockBreakageSystem(level),
                        (data, registry) -> BlockBreakageSystem.load(level, data, registry),
                        null
                ), "amber_breakage_system"
        );
    }

    public int getBreakageProgress(BlockPos pos) {
        long key = pos.asLong();
        if (breakStateTracker.containsKey(key))
            return breakStateTracker.get(key).progress;
        return -1;
    }

    public void addBreakage(BlockPos pos, int amount) { addBreakageUpTo(pos, 1, 9); }
    public void addBreakageUpTo(BlockPos pos, int amount, int maximum) {
        long key = pos.asLong();
        BreakState breakState = breakStateTracker.computeIfAbsent(key, newKey -> new BreakState(BlockPos.of(newKey)));
        breakState.update(level, Math.min(Math.min(breakState.progress + amount, maximum), 9));
    }

    public void clearBreakage(BlockPos pos) {
        this.updateBreakage(pos, -1);
    }

    public void updateBreakage(BlockPos pos, int progress) {
        long key = pos.asLong();
        if (progress < 0) {
            breakStateTracker.remove(key);
        } else {
            breakStateTracker.computeIfAbsent(key, newKey -> new BreakState(BlockPos.of(newKey)))
                    .update(level, progress);
        }
    }

    public void tick() {
        // update existing break states to decay
        for (BreakState breakState : breakStateTracker.values()) {
            breakState.decayTicks--;
            if (breakState.decayTicks < 0) breakState.update(level, breakState.progress - 1);
        }
        // clear out any finished break states
        breakStateTracker.values().removeIf(breakState -> breakState.progress < 0);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag listTag = new ListTag(breakStateTracker.size());
        for (BreakState breakState : breakStateTracker.values()) {
            listTag.add(breakState.save());
        }
        tag.put("breakStateEntries", listTag);
        return tag;
    }

    public static BlockBreakageSystem load(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        BlockBreakageSystem system = new BlockBreakageSystem(level);
        ListTag listTag = tag.getList("breakStateEntries", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            BreakState breakState = BreakState.load(listTag.getCompound(i));
            if (breakState == null) continue;
            system.breakStateTracker.put(breakState.pos.asLong(), breakState);
        }
        for (BreakState value : system.breakStateTracker.values()) value.apply(level);
        return system;
    }

    @SubscribeEvent
    public static void tickAmberBreakageSystem(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("clinker.tickAmberBreakage");

            BlockBreakageSystem blockBreakageSystem = get(level);
            blockBreakageSystem.tick();

            profiler.pop();
        }
    }

    static class BreakState {
        final BlockPos pos;
        final int id;
        int progress = -1, decayTicks = -1;

        BreakState(BlockPos pos) {
            this.pos = pos;
            // weird hash thing
            // unlikely this conflicts with any entity ids...
            this.id = pos.hashCode() ^ BlockBreakageSystem.BREAK_ID_HASH;
        }

        public void update(ServerLevel level, int progress) {
            this.progress = progress;
            this.decayTicks = 20;
            this.apply(level);
        }

        public void apply(ServerLevel level) {
            level.destroyBlockProgress(id, pos, progress);
        }

        CompoundTag save() {
            CompoundTag tag = new CompoundTag(3);
            tag.put("pos", NbtUtils.writeBlockPos(this.pos));
            tag.putInt("progress", this.progress);
            tag.putInt("decay", this.decayTicks);
            return tag;
        }

        @Nullable
        static BreakState load(CompoundTag tag) {
            Optional<BlockPos> optionalPos = NbtUtils.readBlockPos(tag, "pos");
            return optionalPos.map(pos -> {
                BreakState state = new BreakState(pos);
                state.progress = tag.getInt("progress");
                state.decayTicks = tag.getInt("decay");
                return state;
            }).orElse(null);
        }
    }
}
