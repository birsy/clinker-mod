package birsy.clinker.datagen.providers;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static birsy.clinker.core.registry.ClinkerBlocks.*;

public class ClinkerRecipeProvider extends RecipeProvider {
    public ClinkerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // brimstone
        stoneSet(output,
                BRIMSTONE, BRIMSTONE_SLAB, BRIMSTONE_STAIRS, BRIMSTONE_WALL,
                POLISHED_BRIMSTONE, POLISHED_BRIMSTONE_SLAB, POLISHED_BRIMSTONE_STAIRS, POLISHED_BRIMSTONE_WALL,
                BRIMSTONE_BRICKS, BRIMSTONE_BRICK_SLAB, BRIMSTONE_BRICK_STAIRS, BRIMSTONE_BRICK_WALL,
                COBBLED_BRIMSTONE, COBBLED_BRIMSTONE_SLAB, COBBLED_BRIMSTONE_STAIRS, COBBLED_BRIMSTONE_WALL,
                BRIMSTONE_PILLAR, CHISELED_BRIMSTONE
        );

        // calc
        stoneSet(output,
                CALC, CALC_SLAB, CALC_STAIRS, CALC_WALL,
                POLISHED_CALC, POLISHED_CALC_SLAB, POLISHED_CALC_STAIRS, POLISHED_CALC_WALL,
                CALC_BRICKS, CALC_BRICK_SLAB, CALC_BRICK_STAIRS, CALC_BRICK_WALL,
                null, null
        );
    }

    protected static void stoneSet(RecipeOutput recipeOutput,
                                   ItemLike raw, ItemLike rawSlab, ItemLike rawStairs, ItemLike rawWall,
                                   ItemLike polished, ItemLike polishedSlab, ItemLike polishedStairs, ItemLike polishedWall,
                                   ItemLike brick, ItemLike brickSlab, ItemLike brickStairs, ItemLike brickWall,
                                   @Nullable ItemLike pillar, @Nullable ItemLike chiseled)
    {
        stoneSlab(recipeOutput, rawSlab, raw);
        stoneStair(recipeOutput, rawStairs, raw);
        stoneWall(recipeOutput, rawWall, raw);

        polished(recipeOutput, RecipeCategory.BUILDING_BLOCKS, polished, raw);
        stoneSlab(recipeOutput, polishedSlab, polished);
        stoneStair(recipeOutput, polishedStairs, polished);
        stoneWall(recipeOutput, polishedWall, polished);

        polished(recipeOutput, RecipeCategory.BUILDING_BLOCKS, brick, polished);
        stoneSlab(recipeOutput, brickSlab, brick);
        stoneStair(recipeOutput, brickStairs, brick);
        stoneWall(recipeOutput, brickWall, brick);

        Set<ItemLike> mutuallyStonecuttable = new HashSet<>();
        Collections.addAll(mutuallyStonecuttable, raw, polished, brick);
        if (pillar != null) {
            chiseled(recipeOutput, RecipeCategory.BUILDING_BLOCKS, pillar, raw);
            chiseled(recipeOutput, RecipeCategory.BUILDING_BLOCKS, pillar, polished);
            chiseled(recipeOutput, RecipeCategory.BUILDING_BLOCKS, pillar, brick);

            mutuallyStonecuttable.add(pillar);
        }

        if (chiseled != null) {
            chiseled(recipeOutput, RecipeCategory.BUILDING_BLOCKS, chiseled, rawSlab);
            chiseled(recipeOutput, RecipeCategory.BUILDING_BLOCKS, chiseled, polishedSlab);
            chiseled(recipeOutput, RecipeCategory.BUILDING_BLOCKS, chiseled, brickSlab);

            mutuallyStonecuttable.add(chiseled);
        }

        for (ItemLike item1 : mutuallyStonecuttable) {
            for (ItemLike item2 : mutuallyStonecuttable) {
                if (item1 == item2) continue;
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, item1, item2);
            }
        }
    }

    protected static void stoneSet(RecipeOutput recipeOutput,
                                   ItemLike raw, ItemLike rawSlab, ItemLike rawStairs, ItemLike rawWall,
                                   ItemLike polished, ItemLike polishedSlab, ItemLike polishedStairs, ItemLike polishedWall,
                                   ItemLike brick, ItemLike brickSlab, ItemLike brickStairs, ItemLike brickWall,
                                   ItemLike cobbled, ItemLike cobbledSlab, ItemLike cobbledStairs, ItemLike cobbledWall,
                                   @Nullable ItemLike pillar, @Nullable ItemLike chiseled)
    {
        stoneSlab(recipeOutput, rawSlab, raw);
        stoneStair(recipeOutput, rawStairs, raw);
        stoneWall(recipeOutput, rawWall, raw);

        polished(recipeOutput, RecipeCategory.BUILDING_BLOCKS, polished, raw);
        stoneSlab(recipeOutput, polishedSlab, polished);
        stoneStair(recipeOutput, polishedStairs, polished);
        stoneWall(recipeOutput, polishedWall, polished);

        polished(recipeOutput, RecipeCategory.BUILDING_BLOCKS, brick, polished);
        stoneSlab(recipeOutput, brickSlab, brick);
        stoneStair(recipeOutput, brickStairs, brick);
        stoneWall(recipeOutput, brickWall, brick);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(cobbled), RecipeCategory.BUILDING_BLOCKS, raw, 0.1F, 200)
                .unlockedBy(getHasName(cobbled), has(cobbled))
                .save(recipeOutput);
        stoneSlab(recipeOutput, cobbledSlab, cobbled);
        stoneStair(recipeOutput, cobbledStairs, cobbled);
        stoneWall(recipeOutput, cobbledWall, cobbled);

        Set<ItemLike> mutuallyStonecuttable = new HashSet<>();
        Collections.addAll(mutuallyStonecuttable, raw, polished, brick, cobbled);

        Criterion<InventoryChangeTrigger.TriggerInstance> hasAnyTrigger = inventoryTrigger(
                        ItemPredicate.Builder.item().of(raw),
                        ItemPredicate.Builder.item().of(polished),
                        ItemPredicate.Builder.item().of(brick),
                        ItemPredicate.Builder.item().of(cobbled)
                );
        if (pillar != null) {
            Ingredient material = Ingredient.of(raw, polished, brick, cobbled);
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pillar).define('#', material)
                    .pattern("#")
                    .pattern("#")
                    .unlockedBy("has_any_" + getItemName(raw), hasAnyTrigger).save(recipeOutput);

            mutuallyStonecuttable.add(pillar);
        }

        if (chiseled != null) {
            Ingredient slabMaterial = Ingredient.of(rawSlab, polishedSlab, brickSlab, cobbledSlab);
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, chiseled).define('#', slabMaterial)
                    .pattern("#")
                    .pattern("#")
                    .unlockedBy("has_any_" + getItemName(raw), hasAnyTrigger).save(recipeOutput);

            mutuallyStonecuttable.add(chiseled);
        }

        for (ItemLike item1 : mutuallyStonecuttable) {
            for (ItemLike item2 : mutuallyStonecuttable) {
                if (item1 == item2) continue;
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, item1, item2);
            }
        }
    }

    protected static void stoneSlab(RecipeOutput recipeOutput, ItemLike slab, ItemLike material) {
        slab(recipeOutput, slab, material);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, slab, material, 2);
    }
    protected static void stoneStair(RecipeOutput recipeOutput, ItemLike stair, ItemLike material) {
        stair(recipeOutput, stair, material);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, stair, material);
    }
    protected static void stoneWall(RecipeOutput recipeOutput, ItemLike wall, ItemLike material) {
        wall(recipeOutput, wall, material);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, wall, material);
    }

    protected static void slab(RecipeOutput recipeOutput, ItemLike slab, ItemLike material) {
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, slab, material);
    }
    protected static void stair(RecipeOutput recipeOutput, ItemLike stair, ItemLike material) {
        stairBuilder(stair, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(recipeOutput);
    }
    protected static void wall(RecipeOutput recipeOutput, ItemLike wall, ItemLike material) {
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, wall, material);
    }
}
