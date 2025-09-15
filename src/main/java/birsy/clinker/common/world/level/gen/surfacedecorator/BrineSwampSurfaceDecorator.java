package birsy.clinker.common.world.level.gen.surfacedecorator;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class BrineSwampSurfaceDecorator extends SurfaceDecorator {

    public BrineSwampSurfaceDecorator() {}

    @Override
    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, NoiseComputerContext noiseContext, RandomSource random) {
        int offset = 0;
        double noise3 = noiseContext.noiseComputerExecutor().compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[3]);
        double noise5 = noiseContext.noiseComputerExecutor().compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[5]);

        double waterloggingNoise = noise3 +
                noiseContext.noiseComputerExecutor().compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[2]) * 0.5;

        double dither = random.nextDouble() * 2 - 1;

        if (pos.getY() == seaLevel - 1 && waterloggingNoise > 0 && maxElevationIncrease <= 0) {
            chunk.setBlockState(pos, Blocks.WATER.defaultBlockState(), false);
            pos.move(Direction.DOWN);
            chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
            offset++;
        } else {
            if (pos.getY() < seaLevel + 5 + noise5 * 3) {
                if (pos.getY() > seaLevel + 3 + noise5 * 2 + dither * 0.5) {
                    chunk.setBlockState(pos,
                            Math.max(maxElevationDecrease, maxElevationDecrease) < 1 ? ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState() : ClinkerBlocks.CALC.get().defaultBlockState(),
                            false
                    );
                } else {
                    chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                }
            } else {

                boolean shouldPlaceGrass = !chunk.getBlockState(pos.above()).canOcclude() && noise5 + dither * 0.3 > 0;

                shouldPlaceGrass &= Math.max(maxElevationDecrease, maxElevationDecrease) < 1 || noise5 > 0.5;
                shouldPlaceGrass &= Math.max(maxElevationDecrease, maxElevationDecrease) < 2;

                if(shouldPlaceGrass) {
                    chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
                } else {
                    chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
                }
            }
        }
        pos.move(Direction.DOWN);

        for (int i = offset; i < Math.min(8, depth); i++) {
            chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
            pos.move(Direction.DOWN);
        }
    }
}
