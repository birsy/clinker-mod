package birsy.clinker.common.world.level.gen.feature;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SurfaceDriedCloversFeature extends Feature<NoneFeatureConfiguration> {
    private static final float[][] heightDifferenceKernel = Util.make(() -> {
        int radius = 4;
        float trueRadius = radius + 0.5F;
                                // add one to the diameter, accounting for the center block.
        float[][] k = new float[(radius * 2) + 1][(radius * 2) + 1];
        for (int x = 0; x < k.length; x++) {
            for (int z = 0; z < k[x].length; z++) {
                float xp = x - trueRadius;
                float zp = z - trueRadius;
                float dist = Mth.sqrt(xp * xp + zp * zp) / trueRadius;
                k[x][z] = Math.max((1 - dist) * (1 - dist) * (1 - dist), 0);
            }
        }

        return k;
    });
    private static FastNoiseLite noise = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(1.0F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(2);
        return n;
    });


    public SurfaceDriedCloversFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for(int xOffset = 0; xOffset < 16; ++xOffset) {
            for(int zOffset = 0; zOffset < 16; ++zOffset) {
                int x = origin.getX() + xOffset;
                int z = origin.getZ() + zOffset;
                int heightmap = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);

                pos.set(x, heightmap, z);
                if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP, SupportType.FULL)) continue;

                float diff = getWeightedAverageHeightDifference(pos, level);
                float frequency = 1.0F / 8.0F;
                float factor = (float) (diff * noise.GetNoise(x * frequency, z * frequency));
                if (factor > 0.05F && level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, ClinkerBlocks.DRIED_CLOVERS.get().defaultBlockState(), 2);
                }
            }
        }

        return true;
    }

    public float getWeightedAverageHeightDifference(BlockPos pos, WorldGenLevel level) {
        int originX = pos.getX();
        int originY = pos.getY();
        int originZ = pos.getZ();

        float diff = 0;
        int kOffset = Mth.floor(heightDifferenceKernel.length / 2.0);
        for (int xK = 0; xK < heightDifferenceKernel.length; xK++) {
            for (int zK = 0; zK < heightDifferenceKernel[xK].length; zK++) {
                int xOffset = xK - kOffset;
                int zOffset = zK - kOffset;

                int height = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, originX + xOffset, originZ + zOffset);
                float difference = Math.max(height - originY, 0) * heightDifferenceKernel[xK][zK];

                diff += difference;
            }
        }

        return diff;
    }
}
