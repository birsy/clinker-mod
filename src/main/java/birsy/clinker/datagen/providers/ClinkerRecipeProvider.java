package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static birsy.clinker.core.registry.ClinkerBlocks.*;
import static birsy.clinker.core.registry.ClinkerItems.*;

public class ClinkerRecipeProvider extends RecipeProvider {
    public ClinkerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // brimstone
        stoneSet(output, BRIMSTONE, BRIMSTONE_SLAB, BRIMSTONE_STAIRS, BRIMSTONE_WALL)
                .polished(POLISHED_BRIMSTONE, POLISHED_BRIMSTONE_SLAB, POLISHED_BRIMSTONE_STAIRS, POLISHED_BRIMSTONE_WALL)
                .bricks(BRIMSTONE_BRICKS, BRIMSTONE_BRICK_SLAB, BRIMSTONE_BRICK_STAIRS, BRIMSTONE_BRICK_WALL)
                .crackedBricks(CRACKED_BRIMSTONE_BRICKS, CRACKED_BRIMSTONE_BRICK_SLAB, CRACKED_BRIMSTONE_BRICK_STAIRS, CRACKED_BRIMSTONE_BRICK_WALL)
                .cobbled(COBBLED_BRIMSTONE, COBBLED_BRIMSTONE_SLAB, COBBLED_BRIMSTONE_STAIRS, COBBLED_BRIMSTONE_WALL)
                .pillar(BRIMSTONE_PILLAR).chiseled(CHISELED_BRIMSTONE)
                .build();

        // calc
        stoneSet(output, CALC, CALC_SLAB, CALC_STAIRS, CALC_WALL)
                .polished(POLISHED_CALC, POLISHED_CALC_SLAB, POLISHED_CALC_STAIRS, POLISHED_CALC_WALL)
                .bricks(CALC_BRICKS, CALC_BRICK_SLAB, CALC_BRICK_STAIRS, CALC_BRICK_WALL)
                .build();

        // silly saltmoss recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SALTMOSS)
                .define('C', CALC).define('S', SALTMOSS_SPROUTS)
                .pattern("S")
                .pattern("C")
                .unlockedBy(getHasName(SALTMOSS_SPROUTS), has(SALTMOSS_SPROUTS)).save(output);

        // dismal aspen
        String hasDismalAspenName = "has_dismal_aspen";
        Criterion<?> dismalAspenRecipeUnlockCriteria =
                woodSet(output, hasDismalAspenName,
                        BUNDLED_DISMAL_ASPEN_LOGS,
                        DISMAL_ASPEN_PLANKS,
                        DISMAL_ASPEN_STAIRS,
                        DISMAL_ASPEN_SLAB,
                        DISMAL_ASPEN_DOOR,
                        DISMAL_ASPEN_TRAPDOOR,
                        DISMAL_ASPEN_FENCE,
                        DISMAL_ASPEN_FENCE_GATE,
                        DISMAL_ASPEN_BUTTON,
                        DISMAL_ASPEN_PRESSURE_PLATE);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, DISMAL_ASPEN_PLANKS)
                .requires(DISMAL_ASPEN_LOG)
                .unlockedBy(hasDismalAspenName, dismalAspenRecipeUnlockCriteria)
                .save(output, Clinker.resource("dismal_aspen_planks_from_single_log"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BUNDLED_DISMAL_ASPEN_LOGS)
                .define('#', DISMAL_ASPEN_LOG)
                .pattern("##")
                .pattern("##")
                .unlockedBy(hasDismalAspenName, dismalAspenRecipeUnlockCriteria).save(output);

        // mortar & pestle
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, MORTAR)
                .define('#', ClinkerTags.Items.BASALT)
                .pattern("# #")
                .pattern("###")
                .unlockedBy("has_any_basalt", has(ClinkerTags.Items.BASALT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, PESTLE)
                .define('#', ClinkerTags.Items.BASALT)
                .pattern("#")
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_any_basalt", has(ClinkerTags.Items.BASALT)).save(output);

        // lead stuff
        oreSmelting(output, List.of(LEAD_ORE), RecipeCategory.MISC, LEAD_INGOT, 0.7F, 200, "lead_ingot");
        oreBlasting(output, List.of(LEAD_ORE), RecipeCategory.MISC, LEAD_INGOT, 0.7F, 100, "lead_ingot");
        oreSmelting(output, List.of(RAW_LEAD), RecipeCategory.MISC, LEAD_INGOT, 0.7F, 200, "lead_ingot");
        oreBlasting(output, List.of(RAW_LEAD), RecipeCategory.MISC, LEAD_INGOT, 0.7F, 100, "lead_ingot");
        oreSmelting(output, List.of(RAW_LEAD_BLOCK), RecipeCategory.MISC, LEAD_BLOCK, 0.7F, 200 * 7, "lead_block");
        oreBlasting(output, List.of(RAW_LEAD_BLOCK), RecipeCategory.MISC, LEAD_BLOCK, 0.7F, 100 * 7, "lead_block");
        storage(output, RAW_LEAD, RAW_LEAD_BLOCK);
        storage(output, LEAD_INGOT, LEAD_BLOCK);
        storage(output, LEAD_NUGGET, LEAD_INGOT);
        toolSet(output, LEAD_INGOT, LEAD_NUGGET,
                LEAD_SWORD, LEAD_PICKAXE, LEAD_AXE, LEAD_SHOVEL, LEAD_HOE,
                LEAD_HELMET, LEAD_CHESTPLATE, LEAD_LEGGINGS, LEAD_BOOTS);
    }



    protected static void sword(RecipeOutput recipeOutput, ItemLike tool, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, tool, 1)
                .define('#', material)
                .define('/', Items.STICK)
                .pattern("#")
                .pattern("#")
                .pattern("/")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void pickaxe(RecipeOutput recipeOutput, ItemLike tool, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, tool, 1)
                .define('#', material)
                .define('/', Items.STICK)
                .pattern("###")
                .pattern(" / ")
                .pattern(" / ")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void axe(RecipeOutput recipeOutput, ItemLike tool, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, tool, 1)
                .define('#', material)
                .define('/', Items.STICK)
                .pattern("##")
                .pattern("#/")
                .pattern(" /")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void shovel(RecipeOutput recipeOutput, ItemLike tool, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, tool, 1)
                .define('#', material)
                .define('/', Items.STICK)
                .pattern("#")
                .pattern("/")
                .pattern("/")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void hoe(RecipeOutput recipeOutput, ItemLike tool, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, tool, 1)
                .define('#', material)
                .define('/', Items.STICK)
                .pattern("##")
                .pattern(" /")
                .pattern(" /")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void helmet(RecipeOutput recipeOutput, ItemLike armor, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armor, 1)
                .define('#', material)
                .pattern("###")
                .pattern("# #")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void chestplate(RecipeOutput recipeOutput, ItemLike armor, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armor, 1)
                .define('#', material)
                .pattern("# #")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void leggings(RecipeOutput recipeOutput, ItemLike armor, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armor, 1)
                .define('#', material)
                .pattern("###")
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void boots(RecipeOutput recipeOutput, ItemLike armor, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armor, 1)
                .define('#', material)
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
    protected static void toolSet(RecipeOutput recipeOutput, ItemLike material, ItemLike nugget,
                                      ItemLike sword, ItemLike pickaxe, ItemLike axe, ItemLike shovel, ItemLike hoe,
                                      ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {
        sword(recipeOutput, sword, material);
        pickaxe(recipeOutput, pickaxe, material);
        axe(recipeOutput, axe, material);
        shovel(recipeOutput, shovel, material);
        hoe(recipeOutput, hoe, material);

        helmet(recipeOutput, helmet, material);
        chestplate(recipeOutput, chestplate, material);
        leggings(recipeOutput, leggings, material);
        boots(recipeOutput, boots, material);

        ItemLike[] items = {sword, pickaxe, axe, shovel, hoe, helmet, chestplate, leggings, boots};

        SimpleCookingRecipeBuilder smeltBuilder = SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(items),
                RecipeCategory.MISC,
                nugget,
                0.1F,
                200);
        for (ItemLike item : items) smeltBuilder.unlockedBy(getHasName(item), has(item));
        smeltBuilder.save(recipeOutput, Clinker.resource("decomposing_" + BuiltInRegistries.ITEM.getKey(nugget.asItem()).getPath() + "_from_smelting"));

        SimpleCookingRecipeBuilder blastBuilder = SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(items),
                RecipeCategory.MISC,
                nugget,
                0.1F,
                100);
        for (ItemLike item : items) blastBuilder.unlockedBy(getHasName(item), has(item));
        blastBuilder.save(recipeOutput, Clinker.resource("decomposing_" + BuiltInRegistries.ITEM.getKey(nugget.asItem()).getPath() + "_from_blasting"));
    }

    protected static void storage(RecipeOutput recipeOutput, ItemLike unpacked, ItemLike packed) {
        String unpackedName = BuiltInRegistries.ITEM.getKey(unpacked.asItem()).getPath();
        String packedName = BuiltInRegistries.ITEM.getKey(packed.asItem()).getPath();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, packed, 1)
                .define('#', unpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput, Clinker.resource(packedName + "_from_" + unpackedName));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpacked, 9)
                .requires(packed)
                .unlockedBy(getHasName(packed), has(packed))
                .save(recipeOutput, Clinker.resource(unpackedName + "_from_" + packedName));
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

    protected static Criterion<?> woodSet(RecipeOutput recipeOutput,
                                  String hasName,
                                  ItemLike log,
                                  ItemLike planks, ItemLike stairs, ItemLike slab,
                                  ItemLike door, ItemLike trapdoor,
                                  ItemLike fence, ItemLike fenceGate,
                                  ItemLike button, ItemLike pressurePlate) {
        Criterion<?> hasWoodSet = inventoryTrigger(ItemPredicate.Builder.item().of(log, planks, stairs, slab, door, trapdoor, fence, fenceGate, button, pressurePlate));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4).requires(log)
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        stairBuilder(stairs, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        doorBuilder(door, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        trapdoorBuilder(trapdoor, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        fenceBuilder(fence, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        fenceGateBuilder(fenceGate, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        buttonBuilder(button, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);
        pressurePlateBuilder(RecipeCategory.BUILDING_BLOCKS, pressurePlate, Ingredient.of(planks))
                .unlockedBy(hasName, hasWoodSet)
                .save(recipeOutput);

        return hasWoodSet;
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

    static StoneSetBuilder stoneSet(RecipeOutput recipeOutput, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
        return new StoneSetBuilder(recipeOutput, base, slab, stairs, wall);
    }

    static class StoneSetBuilder {
        private final RecipeOutput recipeOutput;
        private final Map<String, Variant> variants = new HashMap<>();
        private final Map<String, Variant> nonMossyVariants = new HashMap<>();
        private final Map<String, Variant> mossyVariants = new HashMap<>();
        private final Map<String, Variant> crackedVariants = new HashMap<>();

        private final Map<String, SingleBlockVariant> singleBlockVariants = new HashMap<>();

        private StoneSetBuilder(RecipeOutput recipeOutput, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            this.recipeOutput = recipeOutput;
            this.variant("raw", (variantFetcher) -> {
                if (variantFetcher.variantExists("cobbled")) {
                    Variant cobbled = variantFetcher.fetchVariant("cobbled");
                    SimpleCookingRecipeBuilder.smelting(Ingredient.of(base), RecipeCategory.BUILDING_BLOCKS, cobbled.base, 0.1F, 200)
                            .unlockedBy(getHasName(cobbled.base), has(cobbled.base))
                            .save(recipeOutput);
                }
            }, base, slab, stairs, wall);
        }

        public StoneSetBuilder variant(String variantName, Consumer<VariantFetcher> recipeMaker, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            Variant variant = new Variant(recipeMaker, base, slab, stairs, wall, null);
            nonMossyVariants.put(variantName, variant);
            variants.put(variantName, variant);
            return this;
        }
        public StoneSetBuilder mossyVariant(String baseName, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall, ItemLike moss) {
            Variant variant = new Variant(null, base, slab, stairs, wall, moss);
            mossyVariants.put(baseName, variant);
            variants.put("mossy_" + baseName, variant);
            return this;
        }
        public StoneSetBuilder crackedVariant(String baseName, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            Variant variant = new Variant(null, base, slab, stairs, wall, null);
            crackedVariants.put(baseName, variant);
            nonMossyVariants.put("cracked_" + baseName, variant);
            variants.put("cracked_" + baseName, variant);
            return this;
        }

        public StoneSetBuilder singleBlockVariant(String variantName, Consumer<VariantFetcher> recipeMaker, ItemLike block) {
            singleBlockVariants.put(variantName, new SingleBlockVariant(recipeMaker, block));
            return this;
        }

        public StoneSetBuilder polished(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            return variant("polished", (variantFetcher) -> {
                Variant raw = variantFetcher.fetchVariant("raw");
                RecipeProvider.polished(this.recipeOutput, RecipeCategory.BUILDING_BLOCKS, base, raw.base);
            }, base, slab, stairs, wall);
        }
        public StoneSetBuilder crackedPolished(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            return crackedVariant("polished", base, slab, stairs, wall);
        }
        public StoneSetBuilder mossyPolished(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall, ItemLike moss) {
            return mossyVariant("polished", base, slab, stairs, wall, moss);
        }

        public StoneSetBuilder bricks(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            return variant("bricks", (variantFetcher) -> {
                if (variantFetcher.variantExists("polished")) {
                    Variant polished = variantFetcher.fetchVariant("polished");
                    RecipeProvider.polished(this.recipeOutput, RecipeCategory.BUILDING_BLOCKS, base, polished.base);
                } else {
                    Variant raw = variantFetcher.fetchVariant("raw");
                    RecipeProvider.polished(this.recipeOutput, RecipeCategory.BUILDING_BLOCKS, base, raw.base);
                }
            }, base, slab, stairs, wall);
        }
        public StoneSetBuilder crackedBricks(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            return crackedVariant("bricks", base, slab, stairs, wall);
        }
        public StoneSetBuilder mossyBricks(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall, ItemLike moss) {
            return mossyVariant("bricks", base, slab, stairs, wall, moss);
        }

        public StoneSetBuilder cobbled(ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
            return variant("cobbled", (variantFetcher) -> {
                Variant raw = variantFetcher.fetchVariant("raw");
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(base), RecipeCategory.BUILDING_BLOCKS, raw.base, 0.0F, 200)
                        .unlockedBy(getHasName(raw.base), has(raw.base))
                        .save(recipeOutput);
            }, base, slab, stairs, wall);
        }

        public StoneSetBuilder pillar(ItemLike block) {
            return singleBlockVariant("pillar", null, block);
        }
        public StoneSetBuilder chiseled(ItemLike block) {
            return singleBlockVariant("chiseled", null, block);
        }

        public void build() {
            VariantFetcher fetcher = new VariantFetcher(singleBlockVariants, variants);
            List<ItemLike> any = new ArrayList<>();
            List<ItemLike> pillarCraftable = new ArrayList<>();
            List<ItemLike> chiseledCraftable = new ArrayList<>();

            Set<ItemLike> mutuallyStonecuttable = new HashSet<>();
            Set<ItemLike> mossyMutuallyStonecuttable = new HashSet<>();

            for (Map.Entry<String, Variant> entry : variants.entrySet()) {
                Variant variant = entry.getValue();

                if (variant.baseRecipe != null) variant.baseRecipe.accept(fetcher);
                stoneSlab(recipeOutput, variant.slab, variant.base);
                stoneStair(recipeOutput, variant.stairs, variant.base);
                stoneWall(recipeOutput, variant.wall, variant.base);

                pillarCraftable.add(variant.base);
                chiseledCraftable.add(variant.slab);
                Collections.addAll(any, variant.base, variant.slab, variant.stairs, variant.wall);
            }

            for (Map.Entry<String, Variant> entry : mossyVariants.entrySet()) {
                String name = entry.getKey();

                Variant originalVariant = fetcher.fetchVariant(name);
                Variant mossyVariant = entry.getValue();

                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyVariant.base).requires(originalVariant.base).requires(mossyVariant.moss)
                        .unlockedBy(getHasName(originalVariant.base), has(originalVariant.base))
                        .save(recipeOutput);
                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyVariant.slab).requires(originalVariant.slab).requires(mossyVariant.moss)
                        .unlockedBy(getHasName(originalVariant.base), has(originalVariant.base))
                        .save(recipeOutput, BuiltInRegistries.ITEM.getKey(mossyVariant.slab.asItem()).getPath()
                                + "_from_" + BuiltInRegistries.ITEM.getKey(originalVariant.slab.asItem()).getPath() );
                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyVariant.stairs).requires(originalVariant.stairs).requires(mossyVariant.moss)
                        .unlockedBy(getHasName(originalVariant.base), has(originalVariant.base))
                        .save(recipeOutput, BuiltInRegistries.ITEM.getKey(mossyVariant.stairs.asItem()).getPath()
                                + "_from_" + BuiltInRegistries.ITEM.getKey(originalVariant.stairs.asItem()).getPath() );
                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyVariant.wall).requires(originalVariant.wall).requires(mossyVariant.moss)
                        .unlockedBy(getHasName(originalVariant.base), has(originalVariant.base))
                        .save(recipeOutput, BuiltInRegistries.ITEM.getKey(mossyVariant.wall.asItem()).getPath()
                                + "_from_" + BuiltInRegistries.ITEM.getKey(originalVariant.wall.asItem()).getPath() );
            }

            for (Map.Entry<String, Variant> entry : crackedVariants.entrySet()) {
                String name = entry.getKey();

                Variant originalVariant = fetcher.fetchVariant(name);
                Variant crackedVariant = entry.getValue();

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(originalVariant.base), RecipeCategory.BUILDING_BLOCKS, crackedVariant.base, 0.0F, 200)
                        .unlockedBy(getHasName(originalVariant.base), has(originalVariant.base))
                        .save(recipeOutput);
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(originalVariant.slab), RecipeCategory.BUILDING_BLOCKS, crackedVariant.slab, 0.0F, 200)
                        .unlockedBy(getHasName(originalVariant.slab), has(originalVariant.slab))
                        .save(recipeOutput, BuiltInRegistries.ITEM.getKey(crackedVariant.slab.asItem()).getPath()
                                + "_from_" + BuiltInRegistries.ITEM.getKey(originalVariant.slab.asItem()).getPath() );
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, crackedVariant.slab, originalVariant.slab);
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, originalVariant.slab, crackedVariant.slab);

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(originalVariant.stairs), RecipeCategory.BUILDING_BLOCKS, crackedVariant.stairs, 0.0F, 200)
                        .unlockedBy(getHasName(originalVariant.stairs), has(originalVariant.stairs))
                        .save(recipeOutput, BuiltInRegistries.ITEM.getKey(crackedVariant.stairs.asItem()).getPath()
                                + "_from_" + BuiltInRegistries.ITEM.getKey(originalVariant.stairs.asItem()).getPath() );
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, crackedVariant.stairs, originalVariant.stairs);
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, originalVariant.stairs, crackedVariant.stairs);

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(originalVariant.wall), RecipeCategory.BUILDING_BLOCKS, crackedVariant.wall, 0.0F, 200)
                        .unlockedBy(getHasName(originalVariant.wall), has(originalVariant.wall))
                        .save(recipeOutput, BuiltInRegistries.ITEM.getKey(crackedVariant.wall.asItem()).getPath()
                                + "_from_" + BuiltInRegistries.ITEM.getKey(originalVariant.wall.asItem()).getPath() );
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, crackedVariant.wall, originalVariant.wall);
                stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, originalVariant.wall, crackedVariant.wall);
            }


            for (SingleBlockVariant variant : singleBlockVariants.values()) {
                if (variant.baseRecipe != null) variant.baseRecipe.accept(fetcher);
                any.add(variant.block());
            }

            String hasAnyTriggerName = "has_any_" + getItemName(fetcher.fetchVariant("raw").base);
            Criterion<InventoryChangeTrigger.TriggerInstance> hasAnyTrigger = inventoryTrigger(
                    any.stream().map((item) -> ItemPredicate.Builder.item().of(item).build()).toArray(ItemPredicate[]::new)
            );

            if (fetcher.singleBlockVariantExists("pillar")) {
                SingleBlockVariant pillar = fetcher.fetchSingleBlockVariant("pillar");
                Ingredient material = Ingredient.of(pillarCraftable.stream().map(ItemStack::new));
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pillar.block).define('#', material)
                        .pattern("#")
                        .pattern("#")
                        .unlockedBy(hasAnyTriggerName, hasAnyTrigger).save(recipeOutput);

            }

            if (fetcher.singleBlockVariantExists("chiseled")) {
                SingleBlockVariant chiseled = fetcher.fetchSingleBlockVariant("chiseled");
                Ingredient slabMaterial = Ingredient.of(chiseledCraftable.stream().map(ItemStack::new));
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, chiseled.block).define('#', slabMaterial)
                        .pattern("#")
                        .pattern("#")
                        .unlockedBy(hasAnyTriggerName, hasAnyTrigger).save(recipeOutput);
            }

            for (Variant variant : nonMossyVariants.values())
                mutuallyStonecuttable.add(variant.base);
            for (SingleBlockVariant variant : singleBlockVariants.values())
                mutuallyStonecuttable.add(variant.block);
            for (ItemLike item1 : mutuallyStonecuttable) {
                for (ItemLike item2 : mutuallyStonecuttable) {
                    if (item1 == item2) continue;
                    stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, item1, item2);
                }
            }

            for (Variant variant : mossyVariants.values())
                mossyMutuallyStonecuttable.add(variant.base);
            for (ItemLike item1 : mossyMutuallyStonecuttable) {
                for (ItemLike item2 : mossyMutuallyStonecuttable) {
                    if (item1 == item2) continue;
                    stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, item1, item2);
                }
            }
        }

        private record VariantFetcher(Map<String, SingleBlockVariant> singleBlockVariants, Map<String, Variant> variants) {
            boolean variantExists(String name) {
                return variants.containsKey(name);
            }

            Variant fetchVariant(String name) {
                if (variants.containsKey(name))
                    return variants.get(name);
                throw new IllegalArgumentException("No variant registered by the name of " + name + "!");
            }

            boolean singleBlockVariantExists(String name) {
                return singleBlockVariants.containsKey(name);
            }

            SingleBlockVariant fetchSingleBlockVariant(String name) {
                if (singleBlockVariants.containsKey(name))
                    return singleBlockVariants.get(name);
                throw new IllegalArgumentException("No single block variant registered by the name of " + name + "!");
            }
        }
        private record SingleBlockVariant(Consumer<VariantFetcher> baseRecipe, ItemLike block) {}
        private record Variant(Consumer<VariantFetcher> baseRecipe, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall, ItemLike moss) {}
    }
}
