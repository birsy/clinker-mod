package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SnakesSurfaceDecorator extends SurfaceDecorator {
    private final int seaLevel;

    public SnakesSurfaceDecorator(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[4]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[7]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, Direction surfaceNormal,
                                int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky,
                                WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random) {
        if (surfaceNormal != Direction.DOWN) return;

        int surfaceY = pos.getY();
        int offset = 0;

        double noise4 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[4], pos.getX(), pos.getY(), pos.getZ());
        double noise5 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[5], pos.getX(), pos.getY(), pos.getZ());

        int sandDepth = surfaceY < seaLevel ? 3 : random.nextInt(1, 3);
        int distanceAboveSeaLevel = surfaceY - seaLevel;

        double rockiness = (1 - Math.abs(noise5)) * 4;
        if (distanceAboveSeaLevel > rockiness) sandDepth = 0;
        if (noise4 > 0 && maxDownwardsOffset == 1) sandDepth = 0;
        if (maxDownwardsOffset >= 2) sandDepth = 0;

        boolean placeGrass = false;
        double grassiness = noise5 * 5;
        if (maxDownwardsOffset >= 1) grassiness += 4;
        if (distanceAboveSeaLevel - 5 > grassiness) placeGrass = true;

        double ungrassiness = noise4 - 0.5 + random.triangle(0, 0.2) + maxDownwardsOffset * 0.25;
        if (ungrassiness > 0) placeGrass = false;

        if (placeGrass) {
            chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
            pos.move(surfaceNormal);
            offset++;
        }

        if (sandDepth > 0) {
            sandDepth = Math.min(sandDepth, maximumDepth);
            for (int i = offset; i < sandDepth; i++) {
                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                pos.move(surfaceNormal);
                offset++;
            }
        }

        int stoneDepth = Math.min(15, maximumDepth);
        for (int i = offset; i < stoneDepth; i++) {
            chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
            pos.move(surfaceNormal);
            offset++;
        }
    }
}
