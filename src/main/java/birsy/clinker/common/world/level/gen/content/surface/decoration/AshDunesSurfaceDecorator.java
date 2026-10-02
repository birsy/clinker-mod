package birsy.clinker.common.world.level.gen.content.surface.decoration;

import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

import static birsy.clinker.common.world.level.gen.content.surface.decoration.SurfaceDecorationHelpers.*;
import static birsy.clinker.common.world.level.gen.content.surface.decoration.SurfaceDecorationHelpers.placeColumn;

public class AshDunesSurfaceDecorator extends SurfaceDecorator {
    @Override
    public void declareDependencies(Consumer<NoiseProvider> consumer) {
        consumer.accept(FNLNoiseProvider.create("ashdunessurface"));
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (!requireFloor(ctx)) return;

        NoiseSampler sampler = ctx.getSampler(0);

        if (ctx.maxUpwardsOffset() > 0 && ctx.maxDownwardsOffset() == 0) {
            int ashAmount = (int) Mth.map(
                    noiseWithDither(pos, ctx, sampler, 1.0 / 16.0, 0.0, 0.2),
                    -1, 0.8, -1, 7
            );
            placeAshLayerAbove(pos, ctx, ashAmount);
        }

        if (steepnessThreshold(pos, ctx, sampler, 1 / 24.0, 65.0F, -1, 1, 0, 2)) {
            int ashDepth = (int) Math.round( Mth.map(
                    noiseWithDither(pos, ctx, sampler, 1 / 16.0, 5.0F, 0.3),
                    -0.3, 1, 0, 3
            ));
            if (placeColumn(pos, ctx, ClinkerBlocks.ASH.get().defaultBlockState(), ashDepth)) return;

            int packedAshDepth = Math.min(ashDepth + 1, 1);
            if (placeColumn(pos, ctx, ClinkerBlocks.PACKED_ASH.get().defaultBlockState(), packedAshDepth)) return;
        }
    }
}
