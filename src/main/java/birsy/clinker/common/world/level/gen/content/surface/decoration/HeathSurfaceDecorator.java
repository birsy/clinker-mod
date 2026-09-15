package birsy.clinker.common.world.level.gen.content.surface.decoration;

import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import static birsy.clinker.common.world.level.gen.content.surface.decoration.SurfaceDecorationHelpers.*;
import java.util.function.Consumer;

public class HeathSurfaceDecorator extends SurfaceDecorator {
    @Override
    public void declareDependencies(Consumer<NoiseProvider> consumer) {
        consumer.accept(FNLNoiseProvider.create("heathsurface"));
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (!requireFloor(ctx)) return;

        NoiseSampler sampler = ctx.getSampler(0);

        if (ctx.maxUpwardsOffset() > 0 && ctx.maxDownwardsOffset() == 0) {
            int ashAmount = (int) Mth.map(
                    noiseWithDither(pos, ctx, sampler, 1.0 / 16.0, 0.0, 0.2),
                    -1, 1, -1, 3
            );
            placeAshLayerAbove(pos, ctx, ashAmount);
        }

        if (steepnessThreshold(pos, ctx, sampler, 1 / 24.0, 65.0F, 0, 1, 0, 2)) {
            int peatDepth = (int) Math.round( Mth.clampedMap(
                    noiseWithDither(pos, ctx, sampler, 1 / 16.0, 5.0F, 0.3),
                    -0.3, 1, 0, 3
            ));
            if (placeColumn(pos, ctx, ClinkerBlocks.PEAT_MOSS.get().defaultBlockState(), peatDepth)) return;

            int ashDepth = (int) Math.round(Mth.clampedMap(
                    noiseWithDither(pos, ctx, sampler, 1 / 16.0, 10.0F, 0.3),
                    0, 1, 0, 3)) - peatDepth;
            if (placeColumn(pos, ctx, ClinkerBlocks.PACKED_ASH.get().defaultBlockState(), ashDepth)) return;
        }
    }
}
