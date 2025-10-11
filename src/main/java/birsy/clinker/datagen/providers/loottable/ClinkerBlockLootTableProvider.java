package birsy.clinker.datagen.providers.loottable;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

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
