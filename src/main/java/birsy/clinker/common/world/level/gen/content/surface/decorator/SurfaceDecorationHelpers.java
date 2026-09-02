package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.block.FallingLayerBlock;
import birsy.clinker.common.world.level.gen.system.noise.Synthesizer;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class SurfaceDecorationHelpers {
    public static boolean requireFloor(SurfaceDecorationContext ctx) {
        return ctx.surfaceDirection() == Direction.DOWN;
    }

    public static boolean requireCeiling(SurfaceDecorationContext ctx) {
        return ctx.surfaceDirection() == Direction.UP;
    }

    public static boolean requireSky(SurfaceDecorationContext ctx) {
        return ctx.visibleToSky();
    }

    public static boolean outsideRange(int value, int minInclusive, int maxExclusive) {
        return value < minInclusive || value >= maxExclusive;
    }

    public static boolean steepnessThreshold(SurfaceDecorationContext ctx, int threshold) {
        return ctx.maxUpwardsOffset() + ctx.maxDownwardsOffset() <= threshold;
    }

    public static boolean steepnessThreshold(
            BlockPos pos,
            SurfaceDecorationContext ctx,
            Synthesizer computer,
            double minRange,
            double maxRange,
            int minThreshold,
            int maxThreshold
    ) {
        int threshold = (int) Math.round(Mth.clampedMap(
                ctx.retrieve(computer, pos.getX(), pos.getY(), pos.getZ()),
                minRange, maxRange,
                minThreshold, maxThreshold
        ));
        return steepnessThreshold(ctx, threshold);
    }

    public static boolean noiseInThreshold(
            BlockPos pos,
            SurfaceDecorationContext ctx,
            Synthesizer computer,
            double minThreshold,
            double maxThreshold,
            double ditherAmount
    ) {
        double noise = noiseWithDither(pos, ctx, computer, ditherAmount);
        return noise > minThreshold && noise <= maxThreshold;
    }

    public static boolean noiseAbove(
            BlockPos pos,
            SurfaceDecorationContext ctx,
            Synthesizer computer,
            double threshold,
            double ditherAmount
    ) {
        return noiseWithDither(pos, ctx, computer, ditherAmount) > threshold;
    }

    public static boolean placeColumn(
            BlockPos.MutableBlockPos pos,
            SurfaceDecorationContext ctx,
            BlockState state,
            int depth
    ) {
        if (depth <= 0) return false;
        for (int i = 0; i < depth; i++) {
            ctx.place(pos, state);
            pos.move(ctx.surfaceDirection());
            if (ctx.outOfRange(pos)) return true;
        }
        return false;
    }

    public static double noiseWithDither(
            BlockPos pos,
            SurfaceDecorationContext ctx,
            Synthesizer computer,
            double ditherAmount
    ) {
        double noise = ctx.retrieve(computer, pos.getX(), pos.getY(), pos.getZ());
        noise += ctx.random().triangle(0, ditherAmount);
        return noise;
    }

    public static boolean placeAshLayerAbove(
            BlockPos pos,
            SurfaceDecorationContext ctx,
            int layerCount
    ) {
        if (layerCount <= 0) return false;

        BlockPos ashPos = pos.above();
        boolean waterlogged = ctx.surfaceState().getFluidState().is(Fluids.WATER);

        if (layerCount >= 8) {
            ctx.place(ashPos, ClinkerBlocks.ASH.get().defaultBlockState());
        } else {
            ctx.place(
                    ashPos,
                    ClinkerBlocks.ASH_LAYER.get().defaultBlockState()
                            .setValue(FallingLayerBlock.LAYERS, layerCount)
                            .setValue(FallingLayerBlock.WATERLOGGED, waterlogged)
            );
        }
        return true;
    }
}
