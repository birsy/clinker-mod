package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class PeatBlobFeature extends Feature<NoneFeatureConfiguration> {
    public PeatBlobFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise noise = NormalNoise.create(worldgenrandom, -3, new double[]{(double)1.0F});

        int noiseIntensity = 7;
        int radius = 6;
        int generationRadius = radius + noiseIntensity;

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-generationRadius, -generationRadius, -generationRadius), origin.offset(generationRadius, generationRadius, generationRadius))) {

            double distanceToCenter = pos.distToCenterSqr(origin.getCenter());
            distanceToCenter = Math.sqrt(distanceToCenter);
            distanceToCenter += noise.getValue(pos.getX(),pos.getY(),pos.getZ()) * noiseIntensity;

            BlockState currentBlockstate = level.getBlockState(pos);

            if (distanceToCenter <= radius && currentBlockstate.isSolid()) {

                BlockPos abovePos = pos.above();
                BlockState currentAboveBlockstate = level.getBlockState(abovePos);

                level.setBlock(pos, ClinkerBlocks.PEAT_MOSS.get().defaultBlockState(), 3);

                if (currentAboveBlockstate.isAir() && random.nextInt(3) != 0)

                    level.setBlock(abovePos, ClinkerBlocks.PEAT_MOSS_BUDS.get().defaultBlockState(), 3);

            }
        }


        return true;
    }
}
