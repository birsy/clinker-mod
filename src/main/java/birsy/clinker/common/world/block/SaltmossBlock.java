package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.lighting.LightEngine;

public class SaltmossBlock extends Block implements BonemealableBlock {
    public static final MapCodec<SaltmossBlock> CODEC = simpleCodec(SaltmossBlock::new);

    @Override
    public MapCodec<SaltmossBlock> codec() {
        return CODEC;
    }

    public SaltmossBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static boolean canGrow(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = reader.getBlockState(blockpos);
        int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
        return i < reader.getMaxLightLevel();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canGrow(state, level, pos)) {
            level.setBlockAndUpdate(pos, ClinkerBlocks.CALC.get().defaultBlockState());
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
//        BlockState blockstate = level.getBlockState(pos);
//        BlockPos blockpos = pos.above();
//        ChunkGenerator chunkgenerator = level.getChunkSource().getGenerator();
//        Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
//        if (blockstate.is(Blocks.CRIMSON_NYLIUM)) {
//            this.place(registry, NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, level, chunkgenerator, random, blockpos);
//        } else if (blockstate.is(Blocks.WARPED_NYLIUM)) {
//            this.place(registry, NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, level, chunkgenerator, random, blockpos);
//            this.place(registry, NetherFeatures.NETHER_SPROUTS_BONEMEAL, level, chunkgenerator, random, blockpos);
//            if (random.nextInt(8) == 0) {
//                this.place(registry, NetherFeatures.TWISTING_VINES_BONEMEAL, level, chunkgenerator, random, blockpos);
//            }
//        }
    }

//    private void place(
//            Registry<ConfiguredFeature<?, ?>> featureRegistry,
//            ResourceKey<ConfiguredFeature<?, ?>> featureKey,
//            ServerLevel level,
//            ChunkGenerator chunkGenerator,
//            RandomSource random,
//            BlockPos pos
//    ) {
//        featureRegistry.getHolder(featureKey).ifPresent(p_255920_ -> p_255920_.value().place(level, chunkGenerator, random, pos));
//    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}
