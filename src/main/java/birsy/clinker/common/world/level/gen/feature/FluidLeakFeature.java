package birsy.clinker.common.world.level.gen.feature;

import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class FluidLeakFeature extends Feature<BlockStateConfiguration> {
    public FluidLeakFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        // if the block above us is air, don't place
        if (!level.getBlockState(pos.above()).isSolid())
            return false;

        // look at neighbor blocks to see if there's between 1 and 2 empty neighbors
        int openings = 0;
        for (Direction direction : Direction.values()) {
            if (!level.getBlockState(pos.relative(direction)).isSolid())
                openings++;
            if (openings > 2)
                return false;
        }
        if (openings == 0)
            return false;

        level.setBlock(pos, context.config().state, 2);
        level.scheduleTick(pos, context.config().state.getFluidState().getType(), 0);
        return true;
    }
}
