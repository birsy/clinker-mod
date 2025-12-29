package birsy.clinker.common.world.level.gen.surfacedecorator;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;

public class BrineSwampSurfaceDecorator extends SurfaceDecorator {

    public BrineSwampSurfaceDecorator() {}

    @Override
    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, int surfaceHeight, NoiseComputerContext noiseContext, RandomSource random) {
        NoiseComputerExecutor executor = noiseContext.noiseComputerExecutor();

        int offset = 0;
        double noise3 = executor.compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[3]);
        double noise5 = executor.compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[5]);

        double waterloggingNoise = noise5 + noise3 * 0.5;

        double dither = random.nextDouble() * 2 - 1;

        boolean placedSand = false;

        if (pos.getY() > surfaceHeight - 10) {
            if (pos.getY() == seaLevel - 1 && waterloggingNoise > 0.2 && maxElevationIncrease <= 0) {
                chunk.setBlockState(pos, Blocks.WATER.defaultBlockState(), false);
                pos.move(Direction.DOWN);

                if (waterloggingNoise > 0.5) {
                    chunk.setBlockState(pos, Blocks.WATER.defaultBlockState(), false);
                    pos.move(Direction.DOWN);
                    offset++;
                }

                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                placedSand = true;
                offset++;
            } else if (pos.getY() == seaLevel - 2 && waterloggingNoise > 0.5 && maxElevationIncrease <= 0) {
                chunk.setBlockState(pos, Blocks.WATER.defaultBlockState(), false);
                pos.move(Direction.DOWN);

                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                placedSand = true;
                offset++;
            } else {
                if (pos.getY() < seaLevel + 5 + noise5 * 3) {
                    boolean isBorder = Math.max(maxElevationDecrease, maxElevationDecrease) >= 1;
                    isBorder = (isBorder && noise3 > 0) || Math.max(maxElevationDecrease, maxElevationDecrease) >= 2;

                    if (isBorder) {
                        chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
                    } else {
                        double grassNoise = waterloggingNoise + dither * 0.05;

                        boolean placeGrass = grassNoise < 0.9 && pos.getY() >= seaLevel+1;
                        if (pos.getY() == seaLevel + 1) placeGrass &= maxElevationDecrease == 0;

                        if (placeGrass) {
                            chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
                        } else {
                            chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                            placedSand = true;
                        }
                    }
                } else {
                    boolean shouldPlaceGrass = noise5 + dither * 0.1 > -0.5;

                    shouldPlaceGrass &= maxElevationDecrease < 1 || noise5 > -0.2;
                    shouldPlaceGrass &= maxElevationDecrease < 2;

                    if(shouldPlaceGrass) {
                        chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
                    } else {
                        chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
                    }
                }
            }
            offset++;
            pos.move(Direction.DOWN);
        }

        int sandBlocks = !placedSand ? 0 : random.nextInt(2, 3);
        for (int i = offset; i < Math.min(20 + noise3 * 4, depth); i++) {
            if (sandBlocks > 0) {
                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                sandBlocks--;
            } else {
                chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
            }
            pos.move(Direction.DOWN);
        }
    }

    @Override
    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y) {
        return canSeeSun || y >= OthershoreBiomeSource.SEA_HEIGHT - 2;
    }
}
