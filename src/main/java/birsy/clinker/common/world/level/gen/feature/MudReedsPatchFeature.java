package birsy.clinker.common.world.level.gen.feature;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class MudReedsPatchFeature extends Feature<NoneFeatureConfiguration> {
    private static CachedFastNoise noise = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(1.0F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(2);
        return new CachedFastNoise(n);
    });

    private static final BlockState[] STATE_BY_FACTOR = new BlockState[] {
            ClinkerBlocks.SHORT_MUD_REEDS.get().defaultBlockState(),
            ClinkerBlocks.MUD_REEDS.get().defaultBlockState(),
            ClinkerBlocks.TALL_MUD_REEDS.get().defaultBlockState()
    };

    public MudReedsPatchFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos.MutableBlockPos pos = origin.mutable();
        boolean placed = false;

        int originHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());
        int radius = random.nextInt(3, 8);
        for (int xOffset = -radius; xOffset < radius; xOffset++) {
            int x = origin.getX() + xOffset;
            pos.setX(x);
            for (int zOffset = -radius; zOffset < radius; zOffset++) {
                int z = origin.getZ() + zOffset;
                pos.setZ(z);
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                pos.setY(y);

                if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP, SupportType.FULL)) continue;
                if (Math.abs(y - originHeight) > 2) continue;

                float freq = 1 / 8.0F;
                float offsetIntensity = 8.0F;
                float offsetX = (float) noise.get(x * freq, 0, z * freq) * offsetIntensity;
                float offsetZ = (float) noise.get(x * freq, 5, z * freq) * offsetIntensity;
                float distance = (float) Math.sqrt(origin.distToCenterSqr(x + offsetX, y, z + offsetZ));
                float factor = 1 - (distance / radius);

                factor *= factor;
                factor += random.nextFloat() * -0.5F;
                if (factor < 0) continue;

                BlockState stateForPlacement = STATE_BY_FACTOR[Mth.clamp(Mth.floor(factor * STATE_BY_FACTOR.length), 0, STATE_BY_FACTOR.length - 1)];
                if (stateForPlacement.getBlock() instanceof DoublePlantBlock) {
                    if (level.getBlockState(pos.above()).isAir()) {
                        level.setBlock(pos, stateForPlacement.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
                        level.setBlock(pos.above(), stateForPlacement.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
                        placed = true;
                    }
                } else {
                    level.setBlock(pos, stateForPlacement, 2);
                    placed = true;
                }
            }
        }

        return placed;
    }

}
