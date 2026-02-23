package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SnakesSurfaceDecorator extends SurfaceDecorator {

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[4]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[7]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, int seaLevel, ChunkAccess chunk, NoiseContext noiseContext, RandomSource random, SurfaceDecorationContext surfaceContext) {
        int surfaceY = pos.getY();
        int offset = 0;

        double noise4 = noiseContext.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[4], pos.getX(), pos.getY(), pos.getZ());
        double noise5 = noiseContext.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[5], pos.getX(), pos.getY(), pos.getZ());

        boolean placeGrass = false;
        int sandDepth = 0;
        if (surfaceY < seaLevel) {
            sandDepth = 3;
        } else {
            sandDepth = random.nextInt(1, 3);
        }

        int distanceAboveSeaLevel = surfaceY - seaLevel;

        double rockiness = (1 - Math.abs(noise5)) * 4;
        if (distanceAboveSeaLevel > rockiness) sandDepth = 0;
        if (noise4 > 0 && surfaceContext.maxElevationDecrease() == 1) sandDepth = 0;
        if (surfaceContext.maxElevationDecrease() >= 2) sandDepth = 0;

        double grassiness = noise5 * 5;
        if (surfaceContext.maxElevationDecrease() >= 1) grassiness += 4;
        if (distanceAboveSeaLevel - 5 > grassiness) placeGrass = true;

        double ungrassiness = noise4 - 0.5;
        ungrassiness += random.triangle(0, 0.2);
        ungrassiness += surfaceContext.maxElevationDecrease() * 0.25;
        if (ungrassiness > 0) placeGrass = false;

        if (placeGrass) {
            chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
            pos.move(Direction.DOWN);
            offset++;
        }

        if (sandDepth > 0) {
            sandDepth = Math.min(sandDepth, surfaceContext.depth());

            for (int i = offset; i < sandDepth; i++) {
                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                pos.move(Direction.DOWN);
                offset++;
            }
        }

        int stoneDepth = Math.min(15, surfaceContext.depth());
        for (int i = offset; i < stoneDepth; i++) {
            chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
            pos.move(Direction.DOWN);
            offset++;
        }
//        double surfaceHeight = surfaceContext.surfaceHeight(),
//                surfaceHeightGradient = surfaceContext.surfaceHeightGradient();
//        int depth = surfaceContext.depth(),
//            maxElevationDecrease = surfaceContext.maxElevationDecrease(),
//            maxElevationIncrease = surfaceContext.maxElevationIncrease();
//
//        int offset = 0;
//        double noise3 = noiseContext.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[3], pos.getX(), pos.getY(), pos.getZ());
//        int rockDepth = (int) Math.min(20 + noise3 * 4, depth);
//        rockDepth -= (int) (surfaceHeightGradient * 3);
//        if (rockDepth <= 0) return;
//
//        boolean placedSand = false;
//
//        if (pos.getY() > surfaceHeight - 10) {
//            double gN1 = noiseContext.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6], pos.getX(), pos.getY(), pos.getZ());
//            if (pos.getY() > seaLevel - 5) {
//
//                double gN2 = noiseContext.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[7], pos.getX(), pos.getY(), pos.getZ());
//
//                double groundNoise = Math.min(Math.abs(gN2), 1.);
//                groundNoise *= groundNoise * (3. - 2. * groundNoise);
//                groundNoise = 1. - groundNoise;
//
//                groundNoise += gN1;
//                groundNoise = Math.max(groundNoise, Math.abs(gN1) * 2.);
//
//
//                //groundNoise = 1.-groundNoise;
//                groundNoise *= 20.;
//                groundNoise -= 11.;
//                double m2 = gN1 * 2. - 5.;
//                groundNoise = Math.max(groundNoise, gN1 * 2. - 5.);
//
//                if (pos.getY() > seaLevel - groundNoise * 0.5) {
//                    boolean isBorder = maxElevationDecrease >= 1;
//                    isBorder = (isBorder && noise3 > 0) || maxElevationDecrease >= 2;
//
//                    if (isBorder) {
//                        chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
//                    } else {
//
//                        boolean placeGrass = groundNoise <= m2 + 1. && pos.getY() >= seaLevel + 1;
//                        if (pos.getY() == seaLevel + 1) placeGrass &= maxElevationDecrease == 0;
//
//                        if (placeGrass) {
//                            chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
//                        } else {
//                            chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
//                            placedSand = true;
//                        }
//                        pos.move(Direction.DOWN);
//                    }
//                }
//            }
//
//            int sandBlocks = !placedSand ? 0 : random.nextInt(2, 3);
//            for (int i = offset; i < rockDepth; i++) {
//                if (pos.getY() < seaLevel && pos.getY() > seaLevel - 5 * gN1) {
//                    chunk.setBlockState(pos, ClinkerBlocks.MUD.get().defaultBlockState(), false);
//                    pos.move(Direction.DOWN);
//                    continue;
//                }
//                if (sandBlocks > 0) {
//                    chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
//                    sandBlocks--;
//                } else {
//                    chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
//                }
//                pos.move(Direction.DOWN);
//            }
//            offset++;
//            pos.move(Direction.DOWN);
//        }
    }

    @Override
    public boolean shouldCalculateElevationChange(boolean visibleToSky, int y, double surfaceHeight) {
        return visibleToSky || y >= OthershoreGenerationConstants.SEA_HEIGHT - 2;
    }
}
