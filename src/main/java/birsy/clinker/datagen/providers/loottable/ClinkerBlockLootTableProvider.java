package birsy.clinker.datagen.providers.loottable;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.Set;

public class ClinkerBlockLootTableProvider extends BlockLootSubProvider {
    private final Set<Block> blocksWithLootTables = new HashSet<>(ClinkerBlocks.BLOCKS.getEntries().size());

    public ClinkerBlockLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void add(Block block, LootTable.Builder builder) {
        blocksWithLootTables.add(block);
        super.add(block, builder);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ClinkerBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    @Override
    protected void generate() {
        this.add(ClinkerBlocks.BRIMSTONE.get(), createSingleItemTableWithSilkTouch(ClinkerBlocks.BRIMSTONE.get(), ClinkerBlocks.COBBLED_BRIMSTONE.get()));
        this.add(ClinkerBlocks.SALTMOSS.get(), createSingleItemTableWithSilkTouch(ClinkerBlocks.SALTMOSS.get(), ClinkerBlocks.CALC.get()));
        this.add(ClinkerBlocks.SALTPETRE_LEACHED_DIRT.get(),
                this.applyExplosionDecay(
                        ClinkerBlocks.SALTPETRE_LEACHED_DIRT.get(),
                        LootTable.lootTable()
                                .withPool(
                                        LootPool.lootPool()
                                                .when(this.hasSilkTouch())
                                                .add(LootItem.lootTableItem(ClinkerBlocks.SALTPETRE_LEACHED_DIRT.get()))
                                ).withPool(
                                        LootPool.lootPool()
                                                .when(this.hasSilkTouch().invert())
                                                .add(LootItem.lootTableItem(Items.DIRT))
                                ).withPool(
                                        LootPool.lootPool()
                                                .when(this.hasSilkTouch().invert())
                                                .add(LootItem.lootTableItem(ClinkerItems.SALTPETRE))
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                )
                )
        );


        // autogenerate everything else!
        for (Block block : this.getKnownBlocks()) {
            if (blocksWithLootTables.contains(block)) continue;
            if (block instanceof LiquidBlock) continue;

            // Clinker.LOGGER.info(block.getName());
            if (block instanceof SlabBlock slab) {
                this.add(slab, this.createSlabItemTable(slab));
            } else if (block instanceof BushBlock plant) {
                this.add(plant, createShearsOrSilkTouchOnlyTable(plant));
            } else {
                this.dropSelf(block);
            }
        }
    }

    protected LootTable.Builder createShearsOrSilkTouchOnlyTable(ItemLike item) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().when(HAS_SHEARS.or(this.hasSilkTouch()))
                        .setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item)));
    }
}
