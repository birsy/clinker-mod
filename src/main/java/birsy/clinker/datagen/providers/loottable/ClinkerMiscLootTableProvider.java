package birsy.clinker.datagen.providers.loottable;

import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ClinkerMiscLootTableProvider implements LootTableSubProvider {
    public ClinkerMiscLootTableProvider(HolderLookup.Provider lookupProvider) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ClinkerLootTables.SALT_PETRE_LEACHED_DIRT_EXTRACTION,
                LootTable.lootTable().withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ClinkerItems.SALTPETRE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))
                                )
                        )
                )
        );
    }
}
