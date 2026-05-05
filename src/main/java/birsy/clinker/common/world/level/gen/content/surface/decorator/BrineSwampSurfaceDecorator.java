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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;

public class BrineSwampSurfaceDecorator extends SurfaceDecorator {
    private final int seaLevel;

    public BrineSwampSurfaceDecorator(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[3]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[5]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, Direction surfaceNormal,
                                int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky,
                                WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random) {
        if (surfaceNormal != Direction.DOWN) return;

        double noise3 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[3], pos.getX(), pos.getY(), pos.getZ());
        int rockDepth = (int) Math.min(20 + noise3 * 4, maximumDepth);
        if (rockDepth <= 0) return;

        double noise5 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[5], pos.getX(), pos.getY(), pos.getZ());
        double waterloggingNoise = noise5 + noise3 * 0.8;
        double dither = random.nextDouble() * 2 - 1;

        boolean placedSand = false;
        int offset = 0;

        if (pos.getY() < seaLevel + 5 + noise5 * 3) {
            if (pos.getY() == seaLevel - 1 && waterloggingNoise > 0 && maxUpwardsOffset <= 0) {
                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                placedSand = true;
                offset++;
            } else if (pos.getY() == seaLevel - 2 && waterloggingNoise > 0.5 && maxUpwardsOffset <= 0) {
                chunk.setBlockState(pos, Blocks.WATER.defaultBlockState(), false);
                pos.move(surfaceNormal);
                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                placedSand = true;
                offset++;
            } else {
                boolean isBorder = Math.max(maxDownwardsOffset, maxUpwardsOffset) >= 1;
                isBorder = (isBorder && noise3 > 0) || Math.max(maxDownwardsOffset, maxUpwardsOffset) >= 2;

                if (isBorder) {
                    chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
                } else {
                    double grassNoise = waterloggingNoise + dither * 0.05;
                    boolean placeGrass = grassNoise < 0.9 && pos.getY() >= seaLevel + 1;
                    if (pos.getY() == seaLevel + 1) placeGrass &= maxDownwardsOffset == 0;

                    if (placeGrass) {
                        chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
                    } else {
                        chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                        placedSand = true;
                    }
                }
            }
            offset++;
            pos.move(surfaceNormal);
        } else {
            boolean shouldPlaceGrass = noise5 + dither * 0.1 > -0.5;
            shouldPlaceGrass &= maxDownwardsOffset < 1 || noise5 > -0.2;
            shouldPlaceGrass &= maxDownwardsOffset < 2;

            if (shouldPlaceGrass) {
                chunk.setBlockState(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState(), false);
            } else {
                chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
            }
            offset++;
            pos.move(surfaceNormal);
        }

        int sandBlocks = !placedSand ? 0 : random.nextInt(2, 3);
        for (int i = offset; i < rockDepth; i++) {
            if (sandBlocks > 0) {
                chunk.setBlockState(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState(), false);
                sandBlocks--;
            } else {
                chunk.setBlockState(pos, ClinkerBlocks.CALC.get().defaultBlockState(), false);
            }
            pos.move(surfaceNormal);
        }
    }
}
