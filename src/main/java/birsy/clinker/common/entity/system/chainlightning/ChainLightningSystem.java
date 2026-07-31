package birsy.clinker.common.entity.system.chainlightning;

import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.UUID;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ChainLightningSystem extends SavedData {
    final ServerLevel level;
    final Object2ObjectOpenHashMap<UUID, ChainLightning> activeChainLightnings = new Object2ObjectOpenHashMap<>();

    public ChainLightningSystem(ServerLevel level) {
        this.level = level;
    }

    public static ChainLightningSystem get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(
                        () -> new ChainLightningSystem(level),
                        (data, registry) -> new ChainLightningSystem(level),
                        null
                ), "chain_lightning_tracker"
        );
    }

    public void emit(UUID sourceUUID, Entity... entities) {
        this.emit(sourceUUID, ChainLightning::damage, entities);
    }
    public void emit(UUID sourceUUID, BiConsumer<ChainLightning, Entity> damageCallback, Entity... entities) {
        ChainLightning lightning = activeChainLightnings.computeIfAbsent(sourceUUID, id -> new ChainLightning(sourceUUID, this.level, damageCallback));
        for (Entity entity : entities) lightning.shock(entity);
    }

    public void tick() {
        for (ChainLightning lightning : activeChainLightnings.values()) lightning.tick();
        activeChainLightnings.values().removeIf(lightning -> lightning.markedForRemoval);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return tag;
    }

    @SubscribeEvent
    public static void tickChainLightning(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ProfilerFiller profiler = level.getServer().getProfiler();
            profiler.push("clinker.tickChainLightning");

            ChainLightningSystem chainLightningSystem = get(level);
            chainLightningSystem.tick();

            profiler.pop();
        }
    }

    @SubscribeEvent
    public static void entityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getLightning().level() instanceof ServerLevel level) {
            ChainLightningSystem chainLightningSystem = get(level);
            chainLightningSystem.emit(
                    event.getLightning().getUUID(),
                    event.getLightning()
            );
        }
    }
}
