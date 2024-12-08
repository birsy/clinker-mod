package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class ClinkerBlockStateProvider extends BlockStateProvider {
    public ClinkerBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Clinker.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //brimstone
        {
            ResourceLocation BRIMSTONE_TOP = this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone_end");
            this.simpleBlockWithVariationAndTransformation(ClinkerBlocks.BRIMSTONE.get(), (i) -> {
                String suffix = i == 0 ? "" : "_" + i;
                return this.models().cubeColumn(
                        "brimstone" + suffix,
                        this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone" + suffix),
                        BRIMSTONE_TOP
                );
            }, (i) -> {
                String suffix = i == 0 ? "" : "_" + i;
                return this.models().withExistingParent(
                                "brimstone" + suffix + "_mirrored", ModelProvider.BLOCK_FOLDER + "/cube_column_mirrored")
                        .texture("side", this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone" + suffix))
                        .texture("end", BRIMSTONE_TOP
                        );
            }, 4, false, true);
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone")));
            this.slabBlockWithVariation(ClinkerBlocks.BRIMSTONE_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone" + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone" + (i == 0 ? "" : "_" + i)),
                    (i) -> BRIMSTONE_TOP,
                    (i) -> BRIMSTONE_TOP, 4
            );
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_slab")));
            this.stairsBlockWithVariation(ClinkerBlocks.BRIMSTONE_STAIRS.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone" + (i == 0 ? "" : "_" + i)),
                    (i) -> BRIMSTONE_TOP,
                    (i) -> BRIMSTONE_TOP, 4
            );
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_stairs")));

            this.wallBlock((WallBlock) ClinkerBlocks.BRIMSTONE_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone"));
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_WALL.get(), this.models().wallInventory("brimstone_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone/brimstone")));
        }

        // brimstone pillar
        this.axisBlock((RotatedPillarBlock) ClinkerBlocks.BRIMSTONE_PILLAR.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_pillar"));
        this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_PILLAR.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_pillar")));

        // cobbled brimstone
        {
            ResourceLocation COBBLED_BRIMSTONE = this.modLoc(ModelProvider.BLOCK_FOLDER + "/cobbled_brimstone");
            this.simpleBlockWithItem(ClinkerBlocks.COBBLED_BRIMSTONE.get(), this.models().cubeAll("cobbled_brimstone", COBBLED_BRIMSTONE));

            this.slabBlock((SlabBlock) ClinkerBlocks.COBBLED_BRIMSTONE_SLAB.get(), COBBLED_BRIMSTONE, COBBLED_BRIMSTONE);
            this.simpleBlockItem(ClinkerBlocks.COBBLED_BRIMSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/cobbled_brimstone_slab")));

            this.stairsBlock((StairBlock) ClinkerBlocks.COBBLED_BRIMSTONE_STAIRS.get(), COBBLED_BRIMSTONE);
            this.simpleBlockItem(ClinkerBlocks.COBBLED_BRIMSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/cobbled_brimstone_stairs")));

            this.wallBlock((WallBlock) ClinkerBlocks.COBBLED_BRIMSTONE_WALL.get(), COBBLED_BRIMSTONE);
            this.simpleBlockItem(ClinkerBlocks.COBBLED_BRIMSTONE_WALL.get(), this.models().wallInventory("cobbled_brimstone_wall_inventory", COBBLED_BRIMSTONE));
        }

        // brimstone bricks
        {
            this.simpleBlockWithVariation(ClinkerBlocks.BRIMSTONE_BRICKS.get(), 4);
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_BRICKS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_bricks")));

            this.slabBlockWithVariation(ClinkerBlocks.BRIMSTONE_BRICK_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_bricks" + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_bricks/brimstone_bricks" + (i == 0 ? "" : "_" + i)),
                    4);
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_BRICK_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_brick_slab")));

            this.stairsBlockWithVariation(ClinkerBlocks.BRIMSTONE_BRICK_STAIRS.get(), (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_bricks/brimstone_bricks" + (i == 0 ? "" : "_" + i)), 4);
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_BRICK_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_brick_stairs")));

            this.wallBlock((WallBlock) ClinkerBlocks.BRIMSTONE_BRICK_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_bricks/brimstone_bricks"));
            this.simpleBlockItem(ClinkerBlocks.BRIMSTONE_BRICK_WALL.get(), this.models().wallInventory("brimstone_brick_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/brimstone_bricks/brimstone_bricks")));
        }

        // polished brimstone
        {
            ResourceLocation POLISHED_BRIMSTONE_TOP = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_top");
            ResourceLocation POLISHED_BRIMSTONE_SIDE = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_side");
            ResourceLocation POLISHED_BRIMSTONE_BOTTOM = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_bottom");

            ResourceLocation POLISHED_BRIMSTONE_SLAB = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_slab");
            ResourceLocation POLISHED_BRIMSTONE_STAIRS = POLISHED_BRIMSTONE_TOP;

            this.simpleBlockWithItem(ClinkerBlocks.POLISHED_BRIMSTONE.get(), this.models().cubeBottomTop("polished_brimstone", POLISHED_BRIMSTONE_SIDE, POLISHED_BRIMSTONE_BOTTOM, POLISHED_BRIMSTONE_TOP));

            this.models().cubeColumn("polished_brimstone_double_slab", POLISHED_BRIMSTONE_SLAB, POLISHED_BRIMSTONE_BOTTOM);
            this.slabBlock((SlabBlock) ClinkerBlocks.POLISHED_BRIMSTONE_SLAB.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_double_slab"), POLISHED_BRIMSTONE_SLAB, POLISHED_BRIMSTONE_BOTTOM, POLISHED_BRIMSTONE_BOTTOM);
            this.simpleBlockItem(ClinkerBlocks.POLISHED_BRIMSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_slab")));

            this.stairsBlock((StairBlock) ClinkerBlocks.POLISHED_BRIMSTONE_STAIRS.get(), POLISHED_BRIMSTONE_STAIRS);
            this.simpleBlockItem(ClinkerBlocks.POLISHED_BRIMSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_stairs")));

            this.wallExtended(ClinkerBlocks.POLISHED_BRIMSTONE_WALL.get(),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_post"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_side"), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_top"), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_bottom"),
                    POLISHED_BRIMSTONE_BOTTOM, this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_bottom"), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_brimstone_wall_bottom")
            );
        }

        this.getVariantBuilder(ClinkerBlocks.CHISELED_BRIMSTONE.get())
                .partialState().with(HorizontalDirectionalBlock.FACING, Direction.NORTH).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_north", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_north")))
                ).partialState().with(HorizontalDirectionalBlock.FACING, Direction.SOUTH).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_south", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_south")))
                ).partialState().with(HorizontalDirectionalBlock.FACING, Direction.EAST).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_east", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_east")))
                ).partialState().with(HorizontalDirectionalBlock.FACING, Direction.WEST).addModels(
                        new ConfiguredModel(this.models().cubeAll("chiseled_brimstone_west", this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_west")))
                );
        this.simpleBlockItem(ClinkerBlocks.CHISELED_BRIMSTONE.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/chiseled_brimstone_north")));

        // capstone bricks
        {
            this.simpleBlockWithVariation(ClinkerBlocks.CAPSTONE_BRICKS.get(), 4);
            this.simpleBlockItem(ClinkerBlocks.CAPSTONE_BRICKS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks")));

            this.slabBlockWithVariation(ClinkerBlocks.CAPSTONE_BRICK_SLAB.get(),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks" + (i == 0 ? "" : "_" + i)),
                    (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks/capstone_bricks" + (i == 0 ? "" : "_" + i)),
                    4);
            this.simpleBlockItem(ClinkerBlocks.CAPSTONE_BRICK_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_brick_slab")));

            this.stairsBlockWithVariation(ClinkerBlocks.CAPSTONE_BRICK_STAIRS.get(), (i) -> this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks/capstone_bricks" + (i == 0 ? "" : "_" + i)), 4);
            this.simpleBlockItem(ClinkerBlocks.CAPSTONE_BRICK_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_brick_stairs")));

            this.wallBlock((WallBlock) ClinkerBlocks.CAPSTONE_BRICK_WALL.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks/capstone_bricks"));
            this.simpleBlockItem(ClinkerBlocks.CAPSTONE_BRICK_WALL.get(), this.models().wallInventory("capstone_brick_wall_inventory", this.modLoc(ModelProvider.BLOCK_FOLDER + "/capstone_bricks/capstone_bricks")));
        }

        // polished capstone
        {
            ResourceLocation POLISHED_CAPSTONE = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone");
            ResourceLocation POLISHED_CAPSTONE_SLAB = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_slab");
            ResourceLocation POLISHED_CAPSTONE_STAIRS = this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_stairs");

            this.simpleBlockWithItem(ClinkerBlocks.POLISHED_CAPSTONE.get(), this.models().cubeAll("polished_capstone", POLISHED_CAPSTONE));

            this.models().cubeColumn("polished_capstone_double_slab", POLISHED_CAPSTONE_SLAB, POLISHED_CAPSTONE);
            this.slabBlock((SlabBlock) ClinkerBlocks.POLISHED_CAPSTONE_SLAB.get(), this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_double_slab"), POLISHED_CAPSTONE_SLAB, POLISHED_CAPSTONE, POLISHED_CAPSTONE);
            this.simpleBlockItem(ClinkerBlocks.POLISHED_CAPSTONE_SLAB.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_slab")));

            this.stairsBlock((StairBlock) ClinkerBlocks.POLISHED_CAPSTONE_STAIRS.get(), POLISHED_CAPSTONE_STAIRS);
            this.simpleBlockItem(ClinkerBlocks.POLISHED_CAPSTONE_STAIRS.get(), this.models().getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_stairs")));

            this.wallExtended(ClinkerBlocks.POLISHED_CAPSTONE_WALL.get(),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_post"),

                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_side"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_top"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_bottom"),

                    POLISHED_CAPSTONE,
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_bottom"),
                    this.modLoc(ModelProvider.BLOCK_FOLDER + "/polished_capstone_wall_bottom")
            );
        }
    }

    private void simpleBlockWithVariation(Block block, int variations) {
        String blockName = name(block);
        this.simpleBlockWithVariation(block, (i) -> {
            String suffix = i == 0 ? "" : "_" + i;
            String name = blockName + suffix;
            return this.models().cubeAll(name, this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + blockName + "/" + name));
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
                            this.modLoc(ModelProvider.BLOCK_FOLDER + "/" + blockName + "/" + blockName + suffix)
                    );
                }, (i) -> {
                    String suffix = i == 0 ? "" : "_" + i;
                    return this.models().singleTexture(
                            blockName + suffix + "_mirrored",
                            this.mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_mirrored_all"),
                            "all",
                            this.modLoc( ModelProvider.BLOCK_FOLDER + "/" + blockName + "/" + blockName + suffix)
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

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }
}
