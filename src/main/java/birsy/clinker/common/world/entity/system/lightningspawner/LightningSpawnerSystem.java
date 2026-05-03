package birsy.clinker.common.world.entity.system.lightningspawner;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class LightningSpawnerSystem extends SavedData {
    final ServerLevel level;

    public LightningSpawnerSystem(ServerLevel level) {
        this.level = level;
    }

    public static LightningSpawnerSystem get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(
                        () -> new LightningSpawnerSystem(level),
                        (data, registry) -> new LightningSpawnerSystem(level),
                        null
                ), "lightning_spawners"
        );
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return null;
    }
}
