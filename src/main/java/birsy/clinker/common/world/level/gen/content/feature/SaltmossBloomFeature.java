package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.world.block.plant.OthershorePlantBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SaltmossBloomFeature extends Feature<NoneFeatureConfiguration> {
    public SaltmossBloomFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        boolean placed = false;

        BlockPos.MutableBlockPos pos = origin.mutable();
        BlockPos.MutableBlockPos belowPos = origin.mutable();

        OthershorePlantBlock sproutsBlock = ClinkerBlocks.DRIED_SALTMOSS_SPROUTS.get();
        for (int i = 0; i < random.nextInt(2, 9); i++) {
            pos.set(origin).move((int) (random.nextGaussian() * 1.5), 2, (int) (random.nextGaussian() * 1.5));
            belowPos.set(pos).move(0, -1, 0);
            // column scan
            for (int y = 0; y < 4; y++) {
                if (level.getBlockState(pos).canBeReplaced() && sproutsBlock.mayPlaceOn(level.getBlockState(belowPos), level, belowPos)) {
                    level.setBlock(pos, sproutsBlock.defaultBlockState(), 2);
                    placed = true;
                    break;
                }

                pos.move(0, -1, 0);
                belowPos.move(0, -1, 0);
            }
        }

        pos.set(origin);
        belowPos.set(origin).move(0, -1, 1);
        OthershorePlantBlock blossomBlock = ClinkerBlocks.SALTMOSS_BLOSSOM.get();
        if (level.getBlockState(pos).canBeReplaced() && blossomBlock.mayPlaceOn(level.getBlockState(belowPos), level, belowPos)) {
            level.setBlock(pos, blossomBlock.defaultBlockState(), 2);
            placed = true;
        }

        return placed;
    }

}
