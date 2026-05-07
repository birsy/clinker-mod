package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import static birsy.clinker.common.world.level.gen.content.surface.decorator.SurfaceDecorationHelpers.*;
import static birsy.clinker.common.world.level.gen.content.surface.decorator.SurfaceDecorationHelpers.noiseWithDither;
import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class AshSteppeSurfaceDecorator extends SurfaceDecorator {

    public AshSteppeSurfaceDecorator() {}

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(BASE_NOISE_2D[5]);
        cache.fillNoiseField(BASE_NOISE_2D_ALT[5]);
        cache.fillNoiseField(BASE_NOISE_2D[3]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (!requireFloor(ctx)) return;

        if (ctx.maxUpwardsOffset() > 0 && ctx.maxDownwardsOffset() == 0) {
            int ashAmount = (int) Mth.map(
                    noiseWithDither(pos, ctx, BASE_NOISE_2D[3].get(), 0.2),
                    -1, 1, -1, 6
            );
            placeAshLayerAbove(pos, ctx, ashAmount);
        }

        if (steepnessThreshold(pos, ctx, BASE_NOISE_2D_ALT[5].get(), -0.8, 1, 0, 4)) {
            double surfaceDepth = Mth.clampedMap(
                    noiseWithDither(pos, ctx, BASE_NOISE_2D[5].get(), 0.1),
                    -0.5, 1, 0, 3
            );
            if (surfaceDepth <= 0) return;
            if (surfaceDepth > 1.3) {
                if (placeColumn(pos, ctx, ClinkerBlocks.ASH.get().defaultBlockState(), 1)) return;
                surfaceDepth--;
            }
            if (placeColumn(pos, ctx, ClinkerBlocks.PACKED_ASH.get().defaultBlockState(), (int) surfaceDepth)) return;
        }
    }
}
