package birsy.clinker.datagen.providers;

import birsy.clinker.common.world.block.plant.DoubleSheetMossBlock;
import birsy.clinker.common.world.block.plant.StromatoliteBlock;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Function;

import static birsy.clinker.core.registry.ClinkerBlocks.*;


public class ClinkerBlockStateProvider extends BlockStateProvider {
    public ClinkerBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Clinker.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // saltpetre leached dirt
        {
            String name = name(SALTPETRE_LEACHED_DIRT.get());
            this.simpleBlockWithItem(SALTPETRE_LEACHED_DIRT.get(), cubeAllOverlay(name, modLoc("block/" + name), modLoc("block/" + name + "_crystals")));
        }

        //brimstone
        {
            ResourceLocation brimstoneTop = this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_end");
            String brimstoneName = name(BRIMSTONE.get());
            this.simpleBlockWithVariationAndTransformation(BRIMSTONE.get(), (i) -> {
                String suffix = i == 0 ? "" : "_" + i;
                return this.models().cubeColumn(
                        "brimstone" + suffix,
                        this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName + suffix),
                        brimstoneTop
                );
            }, (i) -> {
                String suffix = i == 0 ? "" : "_" + i;
                return this.models().withExistingParent(
                                "brimstone" + suffix + "_mirrored", ModelProvider.BLOCK_FOLDER + "/cube_column_mirrored")
                        .texture("side", this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName + suffix))
                        .texture("end", brimstoneTop
                        );
            }, 4, false, true);
            this.simpleBlockItem(BRIMSTONE.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName)));
            this.slabBlockWithVariation(BRIMSTONE_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName + (i == 0 ? "" : "_" + i)),
                    (i) -> brimstoneTop,
                    (i) -> brimstoneTop, 4
            );
            this.simpleBlockItem(BRIMSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_slab")));
            this.stairsBlockWithVariation(BRIMSTONE_STAIRS.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName + (i == 0 ? "" : "_" + i)),
                    (i) -> brimstoneTop,
                    (i) -> brimstoneTop, 4
            );
            this.simpleBlockItem(BRIMSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_stairs")));

            this.wallBlock((WallBlock) BRIMSTONE_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName));
            this.simpleBlockItem(BRIMSTONE_WALL.get(), this.models().wallInventory("brimstone_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneName)));
        }

        // brimstone pillar
        this.axisBlock((RotatedPillarBlock) BRIMSTONE_PILLAR.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_pillar"));
        this.simpleBlockItem(BRIMSTONE_PILLAR.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_pillar")));

        // cobbled brimstone
        {
            ResourceLocation cobbledBrimstone = this.modLoc(ModelProvider.BLOCK_FOLDER + "/cobbled_brimstone");
            this.simpleBlockWithItem(COBBLED_BRIMSTONE.get(), this.models().cubeAll("cobbled_brimstone", cobbledBrimstone));

            this.slabBlock((SlabBlock) COBBLED_BRIMSTONE_SLAB.get(), cobbledBrimstone, cobbledBrimstone);
            this.simpleBlockItem(COBBLED_BRIMSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/cobbled_brimstone_slab")));

            this.stairsBlock((StairBlock) COBBLED_BRIMSTONE_STAIRS.get(), cobbledBrimstone);
            this.simpleBlockItem(COBBLED_BRIMSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/cobbled_brimstone_stairs")));

            this.wallBlock((WallBlock) COBBLED_BRIMSTONE_WALL.get(), cobbledBrimstone);
            this.simpleBlockItem(COBBLED_BRIMSTONE_WALL.get(), this.models().wallInventory("cobbled_brimstone_wall_inventory", cobbledBrimstone));
        }

        // brimstone bricks
        {
            String brimstoneBricksName = name(BRIMSTONE_BRICKS.get());

            this.simpleBlockWithVariation(BRIMSTONE_BRICKS.get(), 4);
            this.simpleBlockItem(BRIMSTONE_BRICKS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneBricksName)));

            this.slabBlockWithVariation(BRIMSTONE_BRICK_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneBricksName + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneBricksName + (i == 0 ? "" : "_" + i)),
                    4);
            this.simpleBlockItem(BRIMSTONE_BRICK_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_brick_slab")));

            this.stairsBlockWithVariation(BRIMSTONE_BRICK_STAIRS.get(), (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneBricksName + (i == 0 ? "" : "_" + i)), 4);
            this.simpleBlockItem(BRIMSTONE_BRICK_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_brick_stairs")));

            this.wallBlock((WallBlock) BRIMSTONE_BRICK_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneBricksName));
            this.simpleBlockItem(BRIMSTONE_BRICK_WALL.get(), this.models().wallInventory("brimstone_brick_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + brimstoneBricksName)));
        }

        // cracked brimstone bricks
        {
            String crackedBrimstoneBricksName = name(CRACKED_BRIMSTONE_BRICKS.get());

            this.simpleBlockWithVariation(CRACKED_BRIMSTONE_BRICKS.get(), 4);
            this.simpleBlockItem(CRACKED_BRIMSTONE_BRICKS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + crackedBrimstoneBricksName)));

            this.slabBlockWithVariation(CRACKED_BRIMSTONE_BRICK_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + crackedBrimstoneBricksName + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + crackedBrimstoneBricksName + (i == 0 ? "" : "_" + i)),
                    4);
            this.simpleBlockItem(CRACKED_BRIMSTONE_BRICK_SLAB.get(),
                    this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/cracked_brimstone_brick_slab")));

            this.stairsBlockWithVariation(CRACKED_BRIMSTONE_BRICK_STAIRS.get(), (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + crackedBrimstoneBricksName + (i == 0 ? "" : "_" + i)), 4);
            this.simpleBlockItem(CRACKED_BRIMSTONE_BRICK_STAIRS.get(),
                    this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/cracked_brimstone_brick_stairs")));

            this.wallBlock((WallBlock) CRACKED_BRIMSTONE_BRICK_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + crackedBrimstoneBricksName));
            this.simpleBlockItem(CRACKED_BRIMSTONE_BRICK_WALL.get(),
                    this.models().wallInventory("cracked_brimstone_brick_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + crackedBrimstoneBricksName)));
        }

        // polished brimstone
        {
            ResourceLocation polishedBrimstoneTop = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_top");
            ResourceLocation polishedBrimstoneSide = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_side");
            ResourceLocation polishedBrimstoneBottom = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_bottom");

            ResourceLocation polishedBrimstoneSlab = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_slab");
            ResourceLocation polishedBrimstoneStairs = polishedBrimstoneTop;

            this.simpleBlockWithItem(POLISHED_BRIMSTONE.get(), this.models().cubeBottomTop("polished_brimstone", polishedBrimstoneSide, polishedBrimstoneBottom, polishedBrimstoneTop));

            this.models().cubeColumn("polished_brimstone_double_slab", polishedBrimstoneSlab, polishedBrimstoneBottom);
            this.slabBlock((SlabBlock) POLISHED_BRIMSTONE_SLAB.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_double_slab"), polishedBrimstoneSlab, polishedBrimstoneBottom, polishedBrimstoneBottom);
            this.simpleBlockItem(POLISHED_BRIMSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_slab")));

            this.stairsBlock((StairBlock) POLISHED_BRIMSTONE_STAIRS.get(), polishedBrimstoneStairs);
            this.simpleBlockItem(POLISHED_BRIMSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_stairs")));

            this.wallExtended(POLISHED_BRIMSTONE_WALL.get(),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_post"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_side"), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_top"), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_bottom"),
                    polishedBrimstoneBottom, this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_bottom"), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_bottom")
            );
        }

        this.getVariantBuilder(CHISELED_BRIMSTONE.get())
                .partialState().with(HorizontalDirectionalBlock.FACING, Direction.NORTH).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_north", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_north")))
                ).partialState().with(HorizontalDirectionalBlock.FACING, Direction.SOUTH).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_south", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_south")))
                ).partialState().with(HorizontalDirectionalBlock.FACING, Direction.EAST).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_east", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_east")))
                ).partialState().with(HorizontalDirectionalBlock.FACING, Direction.WEST).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_west", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_west")))
                );
        this.simpleBlockItem(CHISELED_BRIMSTONE.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_north")));

        // capstone bricks
        {
            this.simpleBlockWithVariation(CAPSTONE_BRICKS.get(), 4);
            this.simpleBlockItem(CAPSTONE_BRICKS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks")));

            this.slabBlockWithVariation(CAPSTONE_BRICK_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks" + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks" + (i == 0 ? "" : "_" + i)),
                    4);
            this.simpleBlockItem(CAPSTONE_BRICK_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_brick_slab")));

            this.stairsBlockWithVariation(CAPSTONE_BRICK_STAIRS.get(), (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks" + (i == 0 ? "" : "_" + i)), 4);
            this.simpleBlockItem(CAPSTONE_BRICK_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_brick_stairs")));

            this.wallBlock((WallBlock) CAPSTONE_BRICK_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks"));
            this.simpleBlockItem(CAPSTONE_BRICK_WALL.get(), this.models().wallInventory("capstone_brick_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks")));
        }

        // polished capstone
        {
            ResourceLocation polishedCapstone = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone");
            ResourceLocation polishedCapstoneSlab = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_slab");
            ResourceLocation polishedCapstoneStairs = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_stairs");

            this.simpleBlockWithItem(POLISHED_CAPSTONE.get(), this.models().cubeAll("polished_capstone", polishedCapstone));

            this.models().cubeColumn("polished_capstone_double_slab", polishedCapstoneSlab, polishedCapstone);
            this.slabBlock((SlabBlock) POLISHED_CAPSTONE_SLAB.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_double_slab"), polishedCapstoneSlab, polishedCapstone, polishedCapstone);
            this.simpleBlockItem(POLISHED_CAPSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_slab")));

            this.stairsBlock((StairBlock) POLISHED_CAPSTONE_STAIRS.get(), polishedCapstoneStairs);
            this.simpleBlockItem(POLISHED_CAPSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_stairs")));

            this.wallExtended(POLISHED_CAPSTONE_WALL.get(),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_post"),

                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_side"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_top"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_bottom"),

                    polishedCapstone,
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_bottom"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_bottom")
            );
        }

        // calc
        {
            ResourceLocation calc = this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc");
            String blockName = name(CALC.get());
            this.simpleBlockWithVariationAndTransformation(
                    CALC.get(), (i) -> {
                        String suffix = i == 0 ? "" : "_" + i;
                        return this.models().cubeAll(
                                blockName + suffix,
                                this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + blockName + suffix)
                        );
                    }, (i) -> {
                        String suffix = i == 0 ? "" : "_" + i;
                        return this.models().singleTexture(
                                blockName + suffix + "_mirrored",
                                this.mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_mirrored_all"),
                                "all",
                                this.modLoc( ModelProvider.BLOCK_FOLDER + "/" + blockName + suffix)
                        );
                    },
                    2, false, true
            );
            this.simpleBlockItem(CALC.get(), this.models().getExistingFile(calc));

            this.slabBlockWithVariation(CALC_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc" + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc" + (i == 0 ? "" : "_" + i)),
                    2);
            this.simpleBlockItem(CALC_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_slab")));

            this.stairsBlockWithVariation(CALC_STAIRS.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc" + (i == 0 ? "" : "_" + i)),
                    2);
            this.simpleBlockItem(CALC_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_stairs")));

            this.wallBlock(CALC_WALL.get(), calc);
            this.simpleBlockItem(CALC_WALL.get(), this.models().wallInventory("calc_wall_inventory", calc));
        }

        // calc bricks
        {
            ResourceLocation calcBricks = this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_bricks");

            this.simpleBlockWithVariation(CALC_BRICKS.get(), 6);
            this.simpleBlockItem(CALC_BRICKS.get(), this.models().getExistingFile(calcBricks));

            this.slabBlockWithVariation(CALC_BRICK_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_bricks" + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_bricks" + (i == 0 ? "" : "_" + i)),
                    6);
            this.simpleBlockItem(CALC_BRICK_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_brick_slab")));

            this.stairsBlockWithVariation(CALC_BRICK_STAIRS.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_bricks" + (i == 0 ? "" : "_" + i)),
                    6);
            this.simpleBlockItem(CALC_BRICK_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_brick_stairs")));

            //this.wallBlock(CALC_BRICK_WALL.get(), CALC_BRICKS);
            //this.simpleBlockItem(CALC_BRICK_WALL.get(), this.models().wallInventory("calc_brick_wall_inventory", CALC_BRICKS));

            ResourceLocation calcBrickWall = this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_brick_wall");
            ResourceLocation calcBrickWallSide = this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_brick_wall_side");
            ResourceLocation calcBrickWallTop = this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc_brick_wall_top");

            this.wallExtended(CALC_BRICK_WALL.get(),
                    calcBrickWall,
                    calcBrickWallSide, calcBrickWallTop, calcBrickWall,
                    calcBrickWall, calcBrickWall, calcBrickWall
            );
        }

        // polished calc
        {
            ResourceLocation polishedCalc = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc");
            ResourceLocation polishedCalcTop = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_top");
            ResourceLocation polishedCalcBottom = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_bottom");
            ResourceLocation polishedCalcStairs = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_stairs");

            this.simpleBlockWithItem(POLISHED_CALC.get(), this.models().cubeBottomTop("polished_calc", polishedCalc, polishedCalcBottom, polishedCalcTop));

            this.models().cubeColumn("polished_calc_double_slab", polishedCalcStairs, polishedCalcBottom);
            this.slabBlock(POLISHED_CALC_SLAB.get(),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_double_slab"),
                    polishedCalcStairs, polishedCalcBottom, polishedCalcBottom);
            this.simpleBlockItem(POLISHED_CALC_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_slab")));

            this.stairsBlock(POLISHED_CALC_STAIRS.get(), polishedCalcStairs, polishedCalcBottom, polishedCalcBottom);
            this.simpleBlockItem(POLISHED_CALC_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_stairs")));

            ResourceLocation polishedCalcWallSide = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_wall_side");
            ResourceLocation polishedCalcWallPost = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_wall_post");
            ResourceLocation polishedCalcWallBottom = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_wall_bottom");
            ResourceLocation polishedCalcWallTop = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_calc_wall_top");

            this.wallExtended(POLISHED_CALC_WALL.get(),
                    polishedCalcWallPost,
                    polishedCalcWallSide, polishedCalcWallTop, polishedCalcWallBottom,
                    polishedCalcTop, polishedCalcWallBottom, polishedCalcWallBottom
            );
        }

        // salt moss
        {
            ResourceLocation saltmoss = this.modLoc(ModelProvider.BLOCK_FOLDER + "/saltmoss");
            ResourceLocation saltmossSide = this.modLoc(ModelProvider.BLOCK_FOLDER + "/saltmoss_side");
            ResourceLocation calc = this.modLoc(ModelProvider.BLOCK_FOLDER + "/calc");
            String name = name(SALTMOSS.get());

            this.simpleBlockWithVariationAndTransformation(
                    SALTMOSS.get(),
                    (i) -> this.models().cubeBottomTop(name, saltmossSide, calc, saltmoss),
                    (i) -> this.cubeBottomTopMirrored(name + "_mirrored", saltmossSide, calc, saltmoss),
                    1, false, true
            );
            this.simpleBlockItem(SALTMOSS.get(), this.models().getExistingFile(saltmoss));
        }

        this.simpleBlockWithVariationAndTransformation(
                SALT_GRAVEL.get(), (i) -> this.models().cubeAll(
                        name(SALT_GRAVEL.get()),
                        this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(SALT_GRAVEL.get()))
                ), (i) -> this.models().singleTexture(
                        name(SALT_GRAVEL.get()) + "_mirrored",
                        this.mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_mirrored_all"),
                        "all",
                        this.modLoc( ModelProvider.BLOCK_FOLDER + "/" + name(SALT_GRAVEL.get()))
                ),
                1, true, true
        );
        this.simpleBlockItem(SALT_GRAVEL.get(), this.models().getExistingFile(this.modLoc( ModelProvider.BLOCK_FOLDER + "/" + name(SALT_GRAVEL.get()))));

        // plants
        {
            ResourceLocation brambleBlossom = this.modLoc(ModelProvider.BLOCK_FOLDER + "/bramble_blossom");
            simpleBlock(BRAMBLE_BLOSSOM.get(), this.models().cross("bramble_blossom", brambleBlossom).renderType("cutout"));
            this.flatBlockItem(BRAMBLE_BLOSSOM.get());

            this.simpleBlockWithVariation(WITHERING_BRAMBLE_BLOSSOM.get(), (i) -> {
                String suffix = i == 0 ? "" : "_" + i;
                String name = "withering_bramble_blossom" + suffix;
                return this.models().cross(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/withering_bramble_blossom" + suffix)).renderType("cutout");
            }, 2);
            this.flatBlockItem(WITHERING_BRAMBLE_BLOSSOM.get());
            this.itemModels().basicItem(THORNY_STEM.get().asItem());

            this.simpleBlock(SHEET_MOSS.get(),
                    this.models().singleTexture(name(SHEET_MOSS.get()),
                            this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss"), "texture",
                            this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(SHEET_MOSS.get()))
                    ).renderType("cutout")
            );

            this.simpleBlockWithVariationAndTransformation(SHEET_MOSS.get(),
                    (i) -> {
                        String suffix = i == 0 ? "" : "_" + i;
                        return this.models().singleTexture(name(SHEET_MOSS.get()) + suffix,
                                this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss"), "texture",
                                this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(SHEET_MOSS.get()) + suffix)
                        ).renderType("cutout");
                    },
                    (i) -> {
                        String suffix = i == 0 ? "" : "_" + i;
                        return this.models().singleTexture(name(SHEET_MOSS.get()) + suffix + "_mirrored",
                                this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss_mirrored"), "texture",
                                this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(SHEET_MOSS.get()) + suffix)
                        ).renderType("cutout");
                    },
                    2, false, false
            );
            this.flatBlockItem(SHEET_MOSS.get());

            this.getVariantBuilder(LONG_SHEET_MOSS.get())
                    .partialState().with(DoubleSheetMossBlock.HALF, DoubleBlockHalf.UPPER)
                    .addModels(
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_top",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_top")
                            ).renderType("cutout")),
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_top_1",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_top_1")
                            ).renderType("cutout")),
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_top_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss_mirrored"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_top")
                            ).renderType("cutout")),
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_top_1_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss_mirrored"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_top_1")
                            ).renderType("cutout"))
                    )
                    .partialState().with(DoubleSheetMossBlock.HALF, DoubleBlockHalf.LOWER)
                    .addModels(
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_bottom",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_bottom")
                            ).renderType("cutout")),
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_bottom_1",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_bottom_1")
                            ).renderType("cutout")),
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_bottom_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss_mirrored"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_bottom")
                            ).renderType("cutout")),
                            new ConfiguredModel(this.models().singleTexture(name(LONG_SHEET_MOSS.get()) + "_bottom_1_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_sheet_moss_mirrored"), "texture",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(LONG_SHEET_MOSS.get()) + "_bottom_1")
                            ).renderType("cutout"))
                    );
            this.flatBlockItem(LONG_SHEET_MOSS.get(),
                    this.modLoc( "block/" + name(LONG_SHEET_MOSS.get()) + "_bottom")
            );


            String saltmossSproutsName = name(SALTMOSS_SPROUTS.get());
            this.simpleBlockWithVariationAndTransformation(
                    SALTMOSS_SPROUTS.get(),
                    (i) -> this.denseCross(saltmossSproutsName,
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + saltmossSproutsName))
                            .renderType("cutout"),
                    (i) -> this.denseCrossMirrored(saltmossSproutsName + "_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + saltmossSproutsName))
                            .renderType("cutout"),
                    1, false, false
            );
            this.flatBlockItem(SALTMOSS_SPROUTS.get());

            String driedSaltmossName = name(DRIED_SALTMOSS_SPROUTS.get());
            this.simpleBlockWithVariationAndTransformation(
                    DRIED_SALTMOSS_SPROUTS.get(),
                    (i) -> this.models().cross(driedSaltmossName + (i == 0 ? "" : "_" + i),
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + driedSaltmossName + (i == 0 ? "" : "_" + i)))
                            .renderType("cutout"),
                    (i) -> this.crossMirrored(driedSaltmossName + (i == 0 ? "" : "_" + i) + "_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + driedSaltmossName + (i == 0 ? "" : "_" + i)))
                            .renderType("cutout"),
                    2, false, false
            );
            this.flatBlockItem(DRIED_SALTMOSS_SPROUTS.get());

            String saltmossBloomName = name(SALTMOSS_BLOSSOM.get());
            this.simpleBlockWithVariationAndTransformation(
                    SALTMOSS_BLOSSOM.get(),
                    (i) -> this.models().cross(saltmossBloomName + (i == 0 ? "" : "_" + i),
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + saltmossBloomName + (i == 0 ? "" : "_" + i)))
                            .renderType("cutout"),
                    (i) -> this.crossMirrored(saltmossBloomName + (i == 0 ? "" : "_" + i) + "_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + saltmossBloomName + (i == 0 ? "" : "_" + i)))
                            .renderType("cutout"),
                    2, false, false
            );
            this.flatBlockItem(SALTMOSS_BLOSSOM.get());

            String yarrowName = name(YARROW.get());
            this.simpleBlockWithVariationAndTransformation(
                    YARROW.get(),
                    (i) -> this.models().cross(yarrowName,
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + yarrowName))
                            .renderType("cutout"),
                    (i) -> this.crossMirrored(yarrowName + "_mirrored",
                                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + yarrowName))
                            .renderType("cutout"),
                    1, false, false
            );
            this.flatBlockItem(YARROW.get());
        }

        // stromatolites
        {
            VariantBlockStateBuilder stromatoliteBuilder = this.getVariantBuilder(STROMATOLITE.get());
            String stromatoliteName = name(STROMATOLITE.get());
            for (int i = 0; i <= StromatoliteBlock.MAX_SIZE_INCLUSIVE; i++) {
                stromatoliteBuilder.partialState().with(StromatoliteBlock.SIZE, i).addModels(
                        new ConfiguredModel(
                                this.models().getExistingFile(
                                        this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + stromatoliteName + "_" + i)
                                )
                        )
                );
            }

            this.simpleBlockItem(STROMATOLITE.get(), this.models().getExistingFile(
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + stromatoliteName + "_" + StromatoliteBlock.MAX_SIZE_INCLUSIVE)
            ));
        }
    }

    public void flatBlockItem(Block block) {
        this.flatBlockItem(block, this.modLoc( "block/" + name(block)));
    }

    public void flatBlockItem(Block block, ResourceLocation texture) {
        this.itemModels().getBuilder(key(block).getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }

    private void simpleBlockWithVariation(Block block, int variations) {
        String blockName = name(block);
        this.simpleBlockWithVariation(block, (i) -> {
            String suffix = i == 0 ? "" : "_" + i;
            String name = blockName + suffix;
            return this.models().cubeAll(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name));
        }, variations);
    }
    private void simpleBlockWithVariation(Block block, Function<Integer, ModelBuilder> modelFactory, int variations) {
        for (int i = 0; i < variations; i++) {
            this.getVariantBuilder(block).partialState().addModels(ConfiguredModel.builder()
                    .modelFile(modelFactory.apply(i))
                    .buildLast()
            );
        }
    }
    private void simpleBlockWithVariationAndTransformation(Block block, int variations, boolean rotateX, boolean rotateY) {
        String blockName = name(block);
        this.simpleBlockWithVariationAndTransformation(
                block, (i) -> {
                    String suffix = i == 0 ? "" : "_" + i;
                    return this.models().cubeAll(
                            blockName + suffix,
                            this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + blockName + suffix)
                    );
                }, (i) -> {
                    String suffix = i == 0 ? "" : "_" + i;
                    return this.models().singleTexture(
                            blockName + suffix + "_mirrored",
                            this.mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_mirrored_all"),
                            "all",
                            this.modLoc( ModelProvider.BLOCK_FOLDER + "/" + blockName + suffix)
                    );
                },
                variations, rotateX, rotateY
        );
    }
    private void simpleBlockWithVariationAndTransformation(Block block, Function<Integer, ModelBuilder> modelFactory, Function<Integer, ModelBuilder> mirroredModelFactory, int variations, boolean rotateX, boolean rotateY) {
        ModelBuilder[] models = new ModelBuilder[variations * 2];
        for (int i = 0; i < variations; i++) {
            models[i*2] = modelFactory.apply(i);
            models[i*2 + 1] = mirroredModelFactory.apply(i);
        }

        for (int i = 0; i < variations; i++) {
            this.getVariantBuilder(block).partialState().addModels(
                    ConfiguredModel.builder().modelFile(models[i*2]).buildLast(),
                    ConfiguredModel.builder().modelFile(models[i*2 + 1]).buildLast()
            );

            if (rotateX && rotateY) {
                for (int rotX = 1; rotX < 4; rotX++) {
                    for (int rotY = 0; rotY < 4; rotY++) {
                        this.getVariantBuilder(block).partialState().addModels(
                                ConfiguredModel.builder().modelFile(models[i*2]).rotationX(rotX * 90).rotationY(rotY * 90).buildLast(),
                                ConfiguredModel.builder().modelFile(models[i*2 + 1]).rotationX(rotX * 90).rotationY(rotY * 90).buildLast()
                        );
                    }
                }
            } else if (rotateX) {
                for (int rot = 1; rot < 4; rot++) {
                    this.getVariantBuilder(block).partialState().addModels(
                            ConfiguredModel.builder().modelFile(models[i*2]).rotationX(rot * 90).buildLast(),
                            ConfiguredModel.builder().modelFile(models[i*2 + 1]).rotationX(rot * 90).buildLast()
                    );
                }
            } else if (rotateY) {
                for (int rot = 1; rot < 4; rot++) {
                    this.getVariantBuilder(block).partialState().addModels(
                            ConfiguredModel.builder().modelFile(models[i*2]).rotationY(rot * 90).buildLast(),
                            ConfiguredModel.builder().modelFile(models[i*2 + 1]).rotationY(rot * 90).buildLast()
                    );
                }
            }
        }
    }

    private void slabBlockWithVariation(Block block, Function<Integer, ResourceLocation> doubleSlabFactory, Function<Integer, ResourceLocation> texFactory, int variations) {
        this.slabBlockWithVariation(block, doubleSlabFactory, texFactory, texFactory, texFactory, variations);
    }
    private void slabBlockWithVariation(Block block,
                                        Function<Integer, ResourceLocation> doubleSlabFactory,
                                        Function<Integer, ResourceLocation> sideTexFactory,
                                        Function<Integer, ResourceLocation> bottomTexFactory,
                                        Function<Integer, ResourceLocation> topTexFactory,
                                        int variations) {
        String blockName = name(block);
        for (int i = 0; i < variations; i++) {
            String suffix = i == 0 ? "" : "_" + i;
            ResourceLocation sideTex = sideTexFactory.apply(i);
            ResourceLocation bottomTex = bottomTexFactory.apply(i);
            ResourceLocation topTex = topTexFactory.apply(i);
            getVariantBuilder(block)
                    .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(this.models().getExistingFile(doubleSlabFactory.apply(i))))
                    .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(models().slab(blockName + suffix, sideTex, bottomTex, topTex)))
                    .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(models().slabTop(blockName + "_top" + suffix, sideTex, bottomTex, topTex)));
        }
    }

    private void stairsBlockWithVariation(Block block, Function<Integer, ResourceLocation> texFactory, int variations) {
        this.stairsBlockWithVariation(block, texFactory, texFactory, texFactory, variations);
    }
    private void stairsBlockWithVariation(Block block,
                                          Function<Integer, ResourceLocation> sideTexFactory,
                                          Function<Integer, ResourceLocation> bottomTexFactory,
                                          Function<Integer, ResourceLocation> topTexFactory,
                                          int variations) {
        ModelFile[] stairs = new ModelFile[variations];
        ModelFile[] stairsInner = new ModelFile[variations];
        ModelFile[] stairsOuter = new ModelFile[variations];

        String blockName = name(block);
        for (int i = 0; i < variations; i++) {
            String suffix = i == 0 ? "" : "_" + i;
            ResourceLocation sideTex = sideTexFactory.apply(i);
            ResourceLocation bottomTex = bottomTexFactory.apply(i);
            ResourceLocation topTex = topTexFactory.apply(i);
            stairs[i] = models().stairs(blockName + suffix, sideTex, bottomTex, topTex);
            stairsInner[i] = models().stairsInner(blockName + "_inner" + suffix, sideTex, bottomTex, topTex);
            stairsOuter[i] = models().stairsOuter(blockName + "_outer" + suffix, sideTex, bottomTex, topTex);
        }

        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    Half half = state.getValue(StairBlock.HALF);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);
                    int yRot = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) yRot += 270;
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP) yRot += 90;
                    yRot %= 360;
                    boolean uvlock = yRot != 0 || half == Half.TOP;

                    // build model for each variation...
                    ConfiguredModel.Builder builder = ConfiguredModel.builder();
                    for (int i = 0; i < variations; i++) {
                        if (i > 0) builder = builder.nextModel();
                        builder = builder
                                .modelFile(shape == StairsShape.STRAIGHT ? stairs[i] : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner[i] : stairsOuter[i])
                                .rotationX(half == Half.BOTTOM ? 0 : 180)
                                .rotationY(yRot)
                                .uvLock(uvlock);
                    }

                    return builder.build();
                }, StairBlock.WATERLOGGED);
    }

    private void wallExtended(Block block,
                              ResourceLocation wallPost,
                              ResourceLocation wallSide, ResourceLocation wallTop, ResourceLocation wallBottom,
                              ResourceLocation wallTallSide, ResourceLocation wallTallTop, ResourceLocation wallTallBottom) {
        String name = name(block);

        ModelBuilder postModel = this.models().withExistingParent(name + "_post", this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_wall_extended_post"))
                .texture("wall_post", wallPost);

        ModelBuilder[] sideModels = new ModelBuilder[4];
        ModelBuilder[] sideModelsTall = new ModelBuilder[4];
        for (int i = 0; i < sideModels.length; i++) {
            Direction direction = Direction.from2DDataValue(i);
            String dirName = direction.getName().toLowerCase();
            sideModels[i] = this.models().withExistingParent(name + "_side_" + dirName, this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_wall_extended_side_" + dirName))
                    .texture("wall_side", wallSide)
                    .texture("wall_bottom", wallBottom)
                    .texture("wall_top", wallTop);
            sideModelsTall[i] = this.models().withExistingParent(name + "_side_tall_" + dirName, this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_wall_extended_side_tall_" + dirName))
                    .texture("wall_side_tall", wallTallSide)
                    .texture("wall_bottom_tall", wallTallBottom)
                    .texture("wall_top_tall", wallTallTop);
        }

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(postModel).addModel()
                .condition(WallBlock.UP, true).end();
        WALL_PROPS.entrySet().stream()
                .filter(e -> e.getKey().getAxis().isHorizontal())
                .forEach(entry -> {
                    builder.part()
                            .modelFile(sideModels[entry.getKey().get2DDataValue()])
                            .addModel()
                            .condition(entry.getValue(), WallSide.LOW);
                    builder.part()
                            .modelFile(sideModelsTall[entry.getKey().get2DDataValue()])
                            .addModel()
                            .condition(entry.getValue(), WallSide.TALL);
                });

        // inventory
        this.simpleBlockItem(block, this.models().withExistingParent(name + "_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/template_wall_extended_inventory"))
                .texture("wall_post", wallPost)
                .texture("wall_side", wallSide)
                .texture("wall_top", wallTop)
        );
    }

    public ModelBuilder cubeBottomTopMirrored(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return this.cubeMirrored(name, bottom, top, side, side, side, side);
    }

    public ModelBuilder cubeMirrored(String name, ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation east, ResourceLocation west) {
        return this.models().withExistingParent(name, "cube_mirrored")
                .texture("down", down)
                .texture("up", up)
                .texture("north", north)
                .texture("south", south)
                .texture("east", east)
                .texture("west", west);
    }

    public ModelBuilder cross(Block block) {
        return this.models().cross(name(block), this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(block)));
    }

    public ModelBuilder crossMirrored(Block block) {
        return this.crossMirrored(name(block), this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(block)));
    }

    public ModelBuilder crossMirrored(String name, ResourceLocation cross) {
        return this.models().singleTexture(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/cross_mirrored"), "cross", cross);
    }

    public ModelBuilder denseCross(Block block) {
        return this.denseCross(name(block), this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + name(block)));
    }

    public ModelBuilder denseCross(String name, ResourceLocation cross) {
        return this.models().singleTexture(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/cross_dense"), "cross", cross);
    }

    public ModelBuilder denseCrossMirrored(String name, ResourceLocation cross) {
        return this.models().singleTexture(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/cross_dense_mirrored"), "cross", cross);
    }

    public ModelBuilder cubeAllOverlay(String name, ResourceLocation all, ResourceLocation overlay) {
        return this.models().withExistingParent(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/cube_all_overlay"))
                .texture("all", all)
                .texture("overlay", overlay);
    }


    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }
}
