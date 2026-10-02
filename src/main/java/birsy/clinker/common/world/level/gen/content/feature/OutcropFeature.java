package birsy.clinker.common.world.level.gen.content.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.ArrayList;
import java.util.List;

public class OutcropFeature extends Feature<SimpleBlockConfiguration> {
    public OutcropFeature(Codec<SimpleBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        BlockStateProvider stateProvider = context.config().toPlace();

        int centralRockCount = random.nextInt(1,3);
        List<BlockPos> centralRocks = new ArrayList<>();
        for (int i = 0; i < centralRockCount; i++) {
            int x = (int) (origin.getX() + random.triangle(0, 5)),
                z = (int) (origin.getZ() + random.triangle(0, 5));
            centralRocks.add(new BlockPos(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z), z));
        }

        int boulderCount = random.nextInt(6,12);
        List<BlockPos> outerBoulders = new ArrayList<>();
        for (int i = 0; i < boulderCount; i++) {
            int x = (int) (origin.getX() + random.triangle(0, 10)),
                z = (int) (origin.getZ() + random.triangle(0, 10));
            outerBoulders.add(new BlockPos(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z), z));
        }

        for (BlockPos rockOrigin : centralRocks)
            placeBoulder(level, random, rockOrigin, random.triangle(4, 2), random.triangle(10, 6), stateProvider.getState(random, rockOrigin));
        for (BlockPos rockOrigin : outerBoulders)
            placeBoulder(level, random, rockOrigin, 0.5 + random.nextDouble() * 2, 0.5 + random.nextDouble() * 2, stateProvider.getState(random, rockOrigin));

        return true;
    }

    void placeBoulder(WorldGenLevel level, RandomSource random, BlockPos origin, double radius, double height, BlockState state) {
        int maxRad = (int) Math.ceil(radius + 2), maxHeight = (int) Math.ceil(height + 2);
        Iterable<BlockPos> positions = BlockPos.betweenClosed(
                origin.getX() - maxRad, origin.getY() - maxHeight, origin.getZ() - maxRad,
                origin.getX() + maxRad, origin.getY() + maxHeight, origin.getZ() + maxRad
        );
        double offsetXZ = radius % 2 > 1 ? 0.5 : 0.0, offsetY = height % 2 > 1 ? 0.5 : 0.0;
        boolean tall = height > 3;

        NormalNoise noise = NormalNoise.create(random, -3, 1.0);

        for (BlockPos pos : positions) {
            double yDist = (pos.getY() + offsetY) - origin.getY();
            double h = height;
            if (yDist < 0) h *= 2;
            yDist /= h;
            if (tall) yDist = Math.sqrt(Math.abs(yDist)) * Math.signum(yDist);

            double xzDist = Mth.length((pos.getX() + offsetXZ) - origin.getX(), (pos.getZ() + offsetXZ) - origin.getZ());
            xzDist += noise.getValue(pos.getX(), pos.getY(), pos.getZ()) * 1.5;
            double r = radius;
            if (tall) r -= Math.max(0, yDist);
            xzDist /= r;

            double fac = Mth.length(xzDist, yDist);
            if (fac > 1) continue;

            level.setBlock(pos, state, 2);
        }
    }
}
