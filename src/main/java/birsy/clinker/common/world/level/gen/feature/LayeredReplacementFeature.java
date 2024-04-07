package birsy.clinker.common.world.level.gen.feature;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

//TODO: add datapack configuration
public class LayeredReplacementFeature extends Feature<NoneFeatureConfiguration> {
    private FastNoiseLite noise;

    public LayeredReplacementFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
        this.noise = new FastNoiseLite();
        this.noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.noise.SetFrequency(1.0F / 3.0F);
    }

    /**
     * Places the given feature at the given location.
     * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
     * that they can safely generate into.
     *
     * @param featureContext A context object with a reference to the level and the position the feature is being placed at
     */
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureContext) {
        this.noise.SetSeed((int) featureContext.level().getSeed());
        WorldGenLevel level = featureContext.level();
        BlockPos origin = featureContext.origin();

        int range = 8 + featureContext.random().nextInt(8);
        float maxDepth = ((float) range) / 1.2F;
        float frequency = 0.75f;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int xOffset = -range; xOffset < range; ++xOffset) {
            for (int zOffset = -range; zOffset < range; ++zOffset) {
                for (int yOffset = -range; yOffset < range; ++yOffset) {
                    int x = origin.getX() + xOffset;
                    int y = origin.getY() + yOffset;
                    int z = origin.getZ() + zOffset;
                    pos.set(x, y, z);

                    if (level.getBlockState(pos).getBlock() == ClinkerBlocks.BRIMSTONE.get() && level.getBlockState(pos.above()).isAir()) {
                        float distance = (float) (Math.sqrt(pos.distSqr(origin)) / range);
                        float invertedDistance = ((Math.min(distance, 1) * -1) + 1);
                        for (int depth = 0; depth < maxDepth * invertedDistance; depth++) {
                            pos.setY(y - depth);
                            float depthDistance = Math.max(distance, (depth / (maxDepth * invertedDistance)));
                            float noiseSample = (((float) this.noise.GetNoise(x * frequency, z * frequency) + 1.2F) * 0.5F) - depthDistance;
                            noiseSample += featureContext.random().nextFloat() * 0.125F;

                            if (noiseSample > 0) {
                                //TODO: replace this with a tag.
                                if (level.getBlockState(pos).getBlock() == ClinkerBlocks.BRIMSTONE.get()) {
                                    if (noiseSample > 0.07) {
                                        level.setBlock(pos, ClinkerBlocks.CAPSTONE.get().defaultBlockState(), 2);
                                    } else {
                                        level.setBlock(pos, ClinkerBlocks.CALAMINE.get().defaultBlockState(), 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}