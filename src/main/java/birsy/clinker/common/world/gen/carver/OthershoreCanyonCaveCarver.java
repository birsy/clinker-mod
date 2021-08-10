package birsy.clinker.common.world.gen.carver;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class OthershoreCanyonCaveCarver extends WorldCarver<ProbabilityFeatureConfiguration> {
    protected Set<Block> carvableBlocks = ImmutableSet.of(Blocks.STONE, ClinkerBlocks.BRIMSTONE.get(), Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
    private final float[] rs = new float[1024];

    public OthershoreCanyonCaveCarver(Codec<ProbabilityFeatureConfiguration> p_i231916_1_) {
        super(p_i231916_1_, 256);
    }

    protected boolean canReplaceBlock(BlockState p_222706_1_) {
        return this.carvableBlocks.contains(p_222706_1_.getBlock());
    }

    public boolean isStartChunk(Random rand, int chunkX, int chunkZ, ProbabilityFeatureConfiguration config) {
        return rand.nextFloat() <= config.probability;
    }

    public boolean carve(ChunkAccess chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityFeatureConfiguration config) {
        int i = (this.getRange() * 2 - 1) * 16;
        double d0 = chunkXOffset * 16 + rand.nextInt(16);
        double d1 = rand.nextInt(rand.nextInt(40) + 8) + 20;
        double d2 = chunkZOffset * 16 + rand.nextInt(16);
        float f = rand.nextFloat() * ((float)Math.PI * 2F);
        float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        double d3 = 3.0D;
        float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
        int j = i - rand.nextInt(i / 4);
        int k = 0;
        this.genCanyon(chunk, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, d0, d1, d2, f2, f, f1, 0, j, 3.0D, carvingMask);
        return true;
    }

    private void genCanyon(ChunkAccess chunk, Function<BlockPos, Biome> biomePos, long seed, int seaLevel, int chunkX, int chunkZ, double p_227204_8_, double p_227204_10_, double p_227204_12_, float p_227204_14_, float p_227204_15_, float p_227204_16_, int p_227204_17_, int p_227204_18_, double p_227204_19_, BitSet p_227204_21_) {
        Random random = new Random(seed);
        float f = 1.0F;

        for(int i = 0; i < 256; ++i) {
            if (i == 0 || random.nextInt(3) == 0) {
                f = 1.0F + random.nextFloat() * random.nextFloat();
            }

            this.rs[i] = f * f;
        }

        float f4 = 0.0F;
        float f1 = 0.0F;

        for(int j = p_227204_17_; j < p_227204_18_; ++j) {
            double d0 = 1.5D + (double)(Mth.sin((float)j * (float)Math.PI / (float)p_227204_18_) * p_227204_14_);
            double d1 = d0 * p_227204_19_;
            d0 = d0 * ((double)random.nextFloat() * 0.25D + 0.75D);
            d1 = d1 * ((double)random.nextFloat() * 0.25D + 0.75D);
            float f2 = Mth.cos(p_227204_16_);
            float f3 = Mth.sin(p_227204_16_);
            p_227204_8_ += (double)(Mth.cos(p_227204_15_) * f2);
            p_227204_10_ += (double)f3;
            p_227204_12_ += (double)(Mth.sin(p_227204_15_) * f2);
            p_227204_16_ = p_227204_16_ * 0.7F;
            p_227204_16_ = p_227204_16_ + f1 * 0.05F;
            p_227204_15_ += f4 * 0.05F;
            f1 = f1 * 0.8F;
            f4 = f4 * 0.5F;
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f4 = f4 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (random.nextInt(4) != 0) {
                if (!this.canReach(chunkX, chunkZ, p_227204_8_, p_227204_12_, j, p_227204_18_, p_227204_14_)) {
                    return;
                }

                this.carveSphere(chunk, biomePos, seed, seaLevel, chunkX, chunkZ, p_227204_8_, p_227204_10_, p_227204_12_, d0, d1, p_227204_21_);
            }
        }

    }

    protected boolean skip(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return (p_222708_1_ * p_222708_1_ + p_222708_5_ * p_222708_5_) * (double)this.rs[p_222708_7_ - 1] + p_222708_3_ * p_222708_3_ / 6.0D >= 1.0D;
    }

    @Override
    protected boolean carveBlock(ChunkAccess chunk, Function<BlockPos, Biome> biomePos, BitSet carvingMask, Random rand, BlockPos.MutableBlockPos p_230358_5_, BlockPos.MutableBlockPos p_230358_6_, BlockPos.MutableBlockPos p_230358_7_, int p_230358_8_, int p_230358_9_, int p_230358_10_, int posX, int posZ, int p_230358_13_, int posY, int p_230358_15_, MutableBoolean isSurface) {
        int i = p_230358_13_ | p_230358_15_ << 4 | posY << 8;
        if (carvingMask.get(i)) {
            return false;
        } else {
            carvingMask.set(i);
            p_230358_5_.set(posX, posY, posZ);
            BlockState blockstate = chunk.getBlockState(p_230358_5_);
            BlockState blockstate1 = chunk.getBlockState(p_230358_6_.setWithOffset(p_230358_5_, Direction.UP));
            if (blockstate.is(ClinkerBlocks.ASH.get()) || blockstate.is(ClinkerBlocks.ROOTED_ASH.get()) || blockstate.is(ClinkerBlocks.ASH_LAYER.get()) ||
                    blockstate.is(ClinkerBlocks.PACKED_ASH.get()) || blockstate.is(ClinkerBlocks.ROOTED_PACKED_ASH.get())) {
                isSurface.setTrue();
            }

            if (!this.canReplaceBlock(blockstate, blockstate1)) {
                return false;
            } else {
                if (posY < 16) {
                    chunk.setBlockState(p_230358_5_, Blocks.WATER.defaultBlockState(), false);
                } else {
                    chunk.setBlockState(p_230358_5_, CAVE_AIR, false);
                    if (isSurface.isTrue()) {
                        p_230358_7_.setWithOffset(p_230358_5_, Direction.DOWN);
                        if (chunk.getBlockState(p_230358_7_).is(ClinkerBlocks.PACKED_ASH.get())) {
                            chunk.setBlockState(p_230358_7_, biomePos.apply(p_230358_5_).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), false);
                        }
                    }
                }

                return true;
            }
        }
    }
}
