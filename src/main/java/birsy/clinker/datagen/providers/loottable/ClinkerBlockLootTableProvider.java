package birsy.clinker.datagen.providers.loottable;

import birsy.clinker.common.block.MothBallBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static birsy.clinker.core.registry.ClinkerBlocks.*;
import static birsy.clinker.core.registry.ClinkerItems.*;

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
        return BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    @Override
    protected void generate() {
        this.add(BRIMSTONE.get(), createSingleItemTableWithSilkTouch(BRIMSTONE.get(), COBBLED_BRIMSTONE.get()));
        this.add(LEAD_ORE.get(), createOreDrop(LEAD_ORE.get(), RAW_LEAD.get()));

        this.add(SALTMOSS.get(), createSingleItemTableWithSilkTouch(SALTMOSS.get(), CALC.get()));
        this.add(SALTPETRE_LEACHED_DIRT.get(),
                this.applyExplosionDecay(
                        SALTPETRE_LEACHED_DIRT.get(),
                        LootTable.lootTable()
                                .withPool(
                                        LootPool.lootPool()
                                                .when(this.hasSilkTouch())
                                                .add(LootItem.lootTableItem(SALTPETRE_LEACHED_DIRT.get()))
                                ).withPool(
                                        LootPool.lootPool()
                                                .when(this.hasSilkTouch().invert())
                                                .add(LootItem.lootTableItem(Items.DIRT))
                                ).withPool(
                                        LootPool.lootPool()
                                                .when(this.hasSilkTouch().invert())
                                                .add(LootItem.lootTableItem(SALTPETRE))
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                )
                )
        );
        this.add(BARRIERROCK.get(), createSilkTouchOnlyTable(BARRIERROCK.get()));

        this.add(CORPSE_LILY_BUD.get(),
                createSingleItemTable(CORPSE_LILY_SEEDS));
        this.add(CORPSE_LILY_BULB.get(),
                createSingleItemTableWithSilkTouch(CORPSE_LILY_BULB.get(), CORPSE_LILY_SEEDS.get(), UniformGenerator.between(1.0F, 3.0F)));

        this.add(MOTH_BALL.get(),
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(this.applyExplosionDecay(MOTH_BALL.get(),
                                                LootItem.lootTableItem(MOTH_BALL.get()).apply(
                                                            List.of(1, 2, 3),
                                                            count -> SetItemCountFunction.setCount(ConstantValue.exactly((float) count))
                                                                    .when(
                                                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(MOTH_BALL.get())
                                                                            .setProperties(
                                                                                    StatePropertiesPredicate.Builder.properties().hasProperty(MothBallBlock.COUNT, count)
                                                                            )
                                                                    )
                                                        )
                                        )
                                )
                )
        );

        // autogenerate everything else!
        for (Block block : this.getKnownBlocks()) {
            if (blocksWithLootTables.contains(block)) continue;
            if (block instanceof LiquidBlock) continue;

            if (block.getLootTable().location().equals(ResourceLocation.withDefaultNamespace("empty"))) continue;

            if (block instanceof SlabBlock slab) {
                this.add(slab, this.createSlabItemTable(slab));
            } else if (block instanceof BushBlock plant) {
                this.add(plant, createShearsOrSilkTouchOnlyTable(plant));
            } else if (block instanceof DoorBlock door) {
                this.add(door, this.createDoorTable(door));
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
