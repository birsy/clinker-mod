package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.world.block.plant.CorpseLilyBulbBlock;
import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CorpseLilyFeature extends Feature<NoneFeatureConfiguration> {
    public CorpseLilyFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockPos belowOrigin = origin.below();
        if (level.getBlockState(origin).canBeReplaced() && ClinkerBlocks.CORPSE_LILY_BULB.get().mayPlaceOn(level.getBlockState(belowOrigin), level, belowOrigin)) {
            if (random.nextInt(4) == 0) {
                // occasionally spawns as the bud
                level.setBlock(origin, ClinkerBlocks.CORPSE_LILY_BUD.get().defaultBlockState(), 2);
            } else {
                CorpseLilyBulbBlock.place(level, origin, true);
            }
            return true;
        }

        return false;
    }
}
