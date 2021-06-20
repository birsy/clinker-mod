package birsy.clinker.common.world.gen.carver;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class OreVein extends WorldCarver<OreVeinConfig> {
    protected Set<Block> carvableBlocks = ImmutableSet.of(ClinkerBlocks.COBBLED_BRIMSTONE.get(), ClinkerBlocks.BRIMSTONE.get(), ClinkerBlocks.BRIMSTONE_BRICKS.get(), ClinkerBlocks.PACKED_ASH.get(), ClinkerBlocks.ROOTED_PACKED_ASH.get(), ClinkerBlocks.ROCKY_PACKED_ASH.get(), ClinkerBlocks.ASH.get(), ClinkerBlocks.ASH_LAYER.get(), ClinkerBlocks.ROOTED_ASH.get());

    public OreVein(Codec<OreVeinConfig> config, int maxHeight) {
        super(config, maxHeight);
    }

    protected boolean isCarvable(BlockState state) {
        return this.carvableBlocks.contains(state.getBlock());
    }

    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, OreVeinConfig config) {
        return rand.nextFloat() <= config.probability;
    }

    public boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, OreVeinConfig config) {
        int i = (this.func_222704_c() * 2 - 1) * 16;
        int j = rand.nextInt(rand.nextInt(rand.nextInt(15) + 1) + 1);

        for(int k = 0; k < j; ++k) {
            double d0 = (double)(chunkXOffset * 16 + rand.nextInt(16));
            double d1 = (double)this.func_230361_b_(rand);
            double d2 = (double)(chunkZOffset * 16 + rand.nextInt(16));
            int l = 1;
            if (rand.nextInt(4) == 0) {
                double d3 = 0.5D;
                float f1 = (1.0F + rand.nextFloat() * 6.0F) * 2;
                this.func_227205_a_(chunk, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, d0, d1, d2, f1, 0.5D, carvingMask, config);
                l += rand.nextInt(4);
            }

            for(int k1 = 0; k1 < l; ++k1) {
                float f = rand.nextFloat() * ((float)Math.PI * 2F);
                float f3 = (rand.nextFloat() - 0.5F) / 4.0F;
                float f2 = this.func_230359_a_(rand) * 2;
                int i1 = i - rand.nextInt(i / 4);
                int j1 = 0;
                this.func_227206_a_(chunk, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, d0, d1, d2, f2 * getVeinRadiusMultiplier(config, (int) d1), f, f3, 0, i1, 1.0D, carvingMask, config);
            }
        }

        return true;
    }

    private float getVeinRadiusMultiplier(OreVeinConfig config, int y) {
        float maxMultiplier = MathHelper.clamp(MathUtils.mapRange(config.maxHeight, config.maxHeight + config.maxFalloff, 0, 1, y), 0, 1);
        float minMultiplier = MathHelper.clamp(MathUtils.mapRange(config.minHeight - config.minFalloff, config.minHeight, 0, 1, y), 0, 1);
        return (config.veinSize * 2) * maxMultiplier * minMultiplier;
    }

    protected float func_230359_a_(Random rand) {
        float f = rand.nextFloat() * 2.0F + rand.nextFloat();
        if (rand.nextInt(10) == 0) {
            f *= rand.nextFloat() * rand.nextFloat() * 3.0F + 1.0F;
        }

        return f;
    }

    protected int func_230361_b_(Random rand) {
        return rand.nextInt(rand.nextInt(120) + 8);
    }

    protected void func_227205_a_(IChunk chunk, Function<BlockPos, Biome> biomePos, long seed, int seaLevel, int chunkX, int chunkZ, double randOffsetXCoord, double startY, double randOffsetZCoord, float p_227205_14_, double p_227205_15_, BitSet carvingMask, OreVeinConfig config) {
        double d0 = 1.5D + (double)(MathHelper.sin(((float)Math.PI / 2F)) * p_227205_14_);
        double d1 = d0 * p_227205_15_;
        this.attemptCarve(chunk, biomePos, seed, seaLevel, chunkX, chunkZ, randOffsetXCoord + 1.0D, startY, randOffsetZCoord, d0, d1, carvingMask, config);
    }

    protected void func_227206_a_(IChunk chunk, Function<BlockPos, Biome> biomePos, long seed, int seaLevel, int chunkX, int chunkZ, double randOffsetXCoord, double startY, double randOffsetZCoord, float caveRadius, float pitch, float p_227206_16_, int p_227206_17_, int p_227206_18_, double p_227206_19_, BitSet p_227206_21_, OreVeinConfig config) {
        Random random = new Random(seed);
        int i = random.nextInt(p_227206_18_ / 2) + p_227206_18_ / 4;
        boolean flag = random.nextInt(6) == 0;
        float f = 0.0F;
        float f1 = 0.0F;

        for(int j = p_227206_17_; j < p_227206_18_; ++j) {
            double d0 = 1.5D + (double)(MathHelper.sin((float)Math.PI * (float)j / (float)p_227206_18_) * caveRadius);
            double d1 = d0 * p_227206_19_;
            float f2 = MathHelper.cos(p_227206_16_);
            randOffsetXCoord += (double)(MathHelper.cos(pitch) * f2);
            startY += (double)MathHelper.sin(p_227206_16_);
            randOffsetZCoord += (double)(MathHelper.sin(pitch) * f2);
            p_227206_16_ = p_227206_16_ * (flag ? 0.92F : 0.7F);
            p_227206_16_ = p_227206_16_ + f1 * 0.1F;
            pitch += f * 0.1F;
            f1 = f1 * 0.9F;
            f = f * 0.75F;
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (j == i && caveRadius > 1.0F) {
                this.func_227206_a_(chunk, biomePos, random.nextLong(), seaLevel, chunkX, chunkZ, randOffsetXCoord, startY, randOffsetZCoord, (random.nextFloat() * 0.5F + 0.5F) * getVeinRadiusMultiplier(config, (int) startY), pitch - ((float)Math.PI / 2F), p_227206_16_ / 3.0F, j, p_227206_18_, 1.0D, p_227206_21_, config);
                this.func_227206_a_(chunk, biomePos, random.nextLong(), seaLevel, chunkX, chunkZ, randOffsetXCoord, startY, randOffsetZCoord, (random.nextFloat() * 0.5F + 0.5F) * getVeinRadiusMultiplier(config, (int) startY), pitch + ((float)Math.PI / 2F), p_227206_16_ / 3.0F, j, p_227206_18_, 1.0D, p_227206_21_, config);
                return;
            }

            if (random.nextInt(4) != 0) {
                if (!this.func_222702_a(chunkX, chunkZ, randOffsetXCoord, randOffsetZCoord, j, p_227206_18_, caveRadius)) {
                    return;
                }

                this.attemptCarve(chunk, biomePos, seed, seaLevel, chunkX, chunkZ, randOffsetXCoord, startY, randOffsetZCoord, d0, d1, p_227206_21_, config);
            }
        }

    }

    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return p_222708_3_ <= -0.7D || p_222708_1_ * p_222708_1_ + p_222708_3_ * p_222708_3_ + p_222708_5_ * p_222708_5_ >= 1.0D;
    }

    protected boolean generateOre(IChunk chunk, Function<BlockPos, Biome> p_230358_2_, BitSet carvingMask, Random rand, BlockPos.Mutable placementPos, BlockPos.Mutable p_230358_6_, BlockPos.Mutable p_230358_7_, int p_230358_8_, int p_230358_9_, int p_230358_10_, int posX, int posZ, int p_230358_13_, int posY, int p_230358_15_, MutableBoolean isSurface, OreVeinConfig config) {
        int i = p_230358_13_ | p_230358_15_ << 4 | posY << 8;
        if (carvingMask.get(i)) {
            return false;
        } else {
            carvingMask.set(i);
            placementPos.setPos(posX, posY, posZ);
            BlockState blockstate = chunk.getBlockState(placementPos);
            BlockState blockstate1 = chunk.getBlockState(p_230358_6_.setAndMove(placementPos, Direction.UP));
            if (blockstate.matchesBlock(ClinkerBlocks.ASH.get()) || blockstate.matchesBlock(ClinkerBlocks.ROOTED_ASH.get()) || blockstate.matchesBlock(ClinkerBlocks.ASH_LAYER.get()) ||
                    blockstate.matchesBlock(ClinkerBlocks.PACKED_ASH.get()) || blockstate.matchesBlock(ClinkerBlocks.ROOTED_PACKED_ASH.get())) {
                isSurface.setTrue();
            }

            if (!this.canCarveBlock(blockstate, blockstate1)) {
                return false;
            } else {
                if (rand.nextInt(3) == 0) {
                    if (rand.nextFloat() <= config.density) {
                        chunk.setBlockState(placementPos, config.ore, false);
                    } else {
                        chunk.setBlockState(placementPos, config.filler, false);
                    }
                    if (isSurface.isTrue()) {
                        p_230358_7_.setAndMove(placementPos, Direction.DOWN);
                        if (chunk.getBlockState(p_230358_7_).matchesBlock(ClinkerBlocks.PACKED_ASH.get())) {
                            chunk.setBlockState(p_230358_7_, p_230358_2_.apply(placementPos).getGenerationSettings().getSurfaceBuilderConfig().getTop(), false);
                        }
                    }
                }

                return true;
            }
        }
    }
    
    
    protected boolean attemptCarve(IChunk chunk, Function<BlockPos, Biome> biomePos, long seed, int seaLevel, int chunkX, int chunkZ, double randOffsetXCoord, double startY, double randOffsetZCoord, double p_227208_14_, double p_227208_16_, BitSet carvingMask, OreVeinConfig config) {
        Random random = new Random(seed + (long)chunkX + (long)chunkZ);
        double d0 = (double)(chunkX * 16 + 8);
        double d1 = (double)(chunkZ * 16 + 8);
        if (!(randOffsetXCoord < d0 - 16.0D - p_227208_14_ * 2.0D) && !(randOffsetZCoord < d1 - 16.0D - p_227208_14_ * 2.0D) && !(randOffsetXCoord > d0 + 16.0D + p_227208_14_ * 2.0D) && !(randOffsetZCoord > d1 + 16.0D + p_227208_14_ * 2.0D)) {
            int i = Math.max(MathHelper.floor(randOffsetXCoord - p_227208_14_) - chunkX * 16 - 1, 0);
            int j = Math.min(MathHelper.floor(randOffsetXCoord + p_227208_14_) - chunkX * 16 + 1, 16);
            int k = Math.max(MathHelper.floor(startY - p_227208_16_) - 1, 1);
            int l = Math.min(MathHelper.floor(startY + p_227208_16_) + 1, this.maxHeight - 8);
            int i1 = Math.max(MathHelper.floor(randOffsetZCoord - p_227208_14_) - chunkZ * 16 - 1, 0);
            int j1 = Math.min(MathHelper.floor(randOffsetZCoord + p_227208_14_) - chunkZ * 16 + 1, 16);
            if (this.func_222700_a(chunk, chunkX, chunkZ, i, j, k, l, i1, j1)) {
                return false;
            } else {
                boolean flag = false;
                BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
                BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
                BlockPos.Mutable blockpos$mutable2 = new BlockPos.Mutable();

                for(int k1 = i; k1 < j; ++k1) {
                    int l1 = k1 + chunkX * 16;
                    double d2 = ((double)l1 + 0.5D - randOffsetXCoord) / p_227208_14_;

                    for(int i2 = i1; i2 < j1; ++i2) {
                        int j2 = i2 + chunkZ * 16;
                        double d3 = ((double)j2 + 0.5D - randOffsetZCoord) / p_227208_14_;
                        if (!(d2 * d2 + d3 * d3 >= 1.0D)) {
                            MutableBoolean mutableboolean = new MutableBoolean(false);

                            for(int k2 = l; k2 > k; --k2) {
                                double d4 = ((double)k2 - 0.5D - startY) / p_227208_16_;
                                if (!this.func_222708_a(d2, d4, d3, k2)) {
                                    flag |= this.generateOre(chunk, biomePos, carvingMask, random, blockpos$mutable, blockpos$mutable1, blockpos$mutable2, seaLevel, chunkX, chunkZ, l1, j2, k1, k2, i2, mutableboolean, config);
                                }
                            }
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }
}
