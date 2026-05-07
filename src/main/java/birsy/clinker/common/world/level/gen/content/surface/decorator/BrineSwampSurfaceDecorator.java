package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;

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
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (ctx.surfaceDirection() != Direction.DOWN) return;

        double noise3 = ctx.retrieve(pos, ClinkerNoiseComputers.BASE_NOISE_2D[3]);
        int rockDepth = (int) Math.min(20 + noise3 * 4, ctx.maximumDepth());
        if (rockDepth <= 0) return;

        double noise5 = ctx.retrieve(pos, ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        double waterloggingNoise = noise5 + noise3 * 0.8;
        double dither = ctx.random().nextDouble() * 2 - 1;

        boolean placedSand = false;
        int offset = 0;

        if (pos.getY() < seaLevel + 5 + noise5 * 3) {
            if (pos.getY() == seaLevel - 1 && waterloggingNoise > 0 && ctx.maxUpwardsOffset() <= 0) {
                ctx.place(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState());
                placedSand = true;
                offset++;
            } else if (pos.getY() == seaLevel - 2 && waterloggingNoise > 0.5 && ctx.maxUpwardsOffset() <= 0) {
                ctx.place(pos, Blocks.WATER.defaultBlockState());
                pos.move(ctx.surfaceDirection());
                ctx.place(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState());
                placedSand = true;
                offset++;
            } else {
                boolean isBorder = Math.max(ctx.maxDownwardsOffset(), ctx.maxUpwardsOffset()) >= 1;
                isBorder = (isBorder && noise3 > 0) || Math.max(ctx.maxDownwardsOffset(), ctx.maxUpwardsOffset()) >= 2;

                if (isBorder) {
                    ctx.place(pos, ClinkerBlocks.CALC.get().defaultBlockState());
                } else {
                    double grassNoise = waterloggingNoise + dither * 0.05;
                    boolean placeGrass = grassNoise < 0.9 && pos.getY() >= seaLevel + 1;
                    if (pos.getY() == seaLevel + 1) placeGrass &= ctx.maxDownwardsOffset() == 0;

                    if (placeGrass) {
                        ctx.place(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState());
                    } else {
                        ctx.place(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState());
                        placedSand = true;
                    }
                }
            }
            offset++;
            pos.move(ctx.surfaceDirection());
        } else {
            boolean shouldPlaceGrass = noise5 + dither * 0.1 > -0.5;
            shouldPlaceGrass &= ctx.maxDownwardsOffset() < 1 || noise5 > -0.2;
            shouldPlaceGrass &= ctx.maxDownwardsOffset() < 2;

            if (shouldPlaceGrass) {
                ctx.place(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState());
            } else {
                ctx.place(pos, ClinkerBlocks.CALC.get().defaultBlockState());
            }
            offset++;
            pos.move(ctx.surfaceDirection());
        }

        int sandBlocks = !placedSand ? 0 : ctx.random().nextInt(2, 3);
        for (int i = offset; i < rockDepth; i++) {
            if (sandBlocks > 0) {
                ctx.place(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState());
                sandBlocks--;
            } else {
                ctx.place(pos, ClinkerBlocks.CALC.get().defaultBlockState());
            }
            pos.move(ctx.surfaceDirection());
        }
    }
}
