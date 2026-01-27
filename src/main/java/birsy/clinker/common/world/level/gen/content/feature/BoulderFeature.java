package birsy.clinker.common.world.level.gen.content.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BoulderFeature extends Feature<BlockStateConfiguration> {
    public BoulderFeature(Codec<BlockStateConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockStateConfiguration placementState = context.config();

        origin = origin.below();
        for (int i = 0; i < 3; i++) {
            int xzRadius = random.nextInt(2, 3),
                yRadius = random.nextInt(1, 3);
            double offsetX = random.triangle(0, 2),
                   offsetY = random.triangle(0, 0.5),
                   offsetZ = random.triangle(0, 2);

            for (BlockPos pos : BlockPos.betweenClosed(
                    origin.offset(-Mth.ceil(xzRadius + offsetX), -Mth.ceil(yRadius + offsetY), -Mth.ceil(xzRadius + offsetZ)),
                    origin.offset(Mth.ceil(xzRadius + offsetX), Mth.ceil(yRadius + offsetY), Mth.ceil(xzRadius + offsetZ)))) {
                double xDist, yDist, zDist;
                xDist = Math.abs((origin.getX() + 0.5 + offsetX) - pos.getX()) / xzRadius;
                yDist = Math.abs((origin.getY() + 0.5 + offsetY) - pos.getY()) / yRadius;
                zDist = Math.abs((origin.getZ() + 0.5 + offsetZ) - pos.getZ()) / xzRadius;

                if ((xDist * xDist) + (yDist * yDist) + (zDist * zDist) <= 1) {
                    level.setBlock(pos, placementState.state, 3);
                }
            }

            origin = origin.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }

        return true;
    }
}
