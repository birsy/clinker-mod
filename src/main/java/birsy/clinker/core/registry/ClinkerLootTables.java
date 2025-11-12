package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootTable;

public class ClinkerLootTables {
    public static final ResourceKey<LootTable> SALT_PETRE_LEACHED_DIRT_EXTRACTION = register("salt_petre_leached_dirt_extraction");

    private static ResourceKey<LootTable> register(String pKey) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, Clinker.resource(pKey));
        return key;
    }
}
