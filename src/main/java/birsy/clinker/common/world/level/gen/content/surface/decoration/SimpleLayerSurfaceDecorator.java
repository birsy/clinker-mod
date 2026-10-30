package birsy.clinker.common.world.level.gen.content.surface.decoration;

import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static birsy.clinker.common.world.level.gen.content.surface.decoration.SurfaceDecorationHelpers.*;

public class SimpleLayerSurfaceDecorator extends SurfaceDecorator {
    final int minLayerHeight, maxLayerHeight;
    final Supplier<BlockState> surfaceStateProvider, soilStateProvider;
    BlockState surfaceState, soilState;

    public SimpleLayerSurfaceDecorator(int minLayerHeight, int maxLayerHeight, Supplier<BlockState> surfaceState, Supplier<BlockState> soilState) {
        this.minLayerHeight = minLayerHeight;
        this.maxLayerHeight = maxLayerHeight;
        this.surfaceStateProvider = surfaceState;
        this.soilStateProvider = soilState;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.surfaceState = surfaceStateProvider.get();
        this.soilState = soilStateProvider.get();
    }

    @Override
    public void declareDependencies(Consumer<NoiseProvider> consumer) {
        consumer.accept(FNLNoiseProvider.create("surface_height_noise"));
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (requireFloor(ctx)) return;
        NoiseSampler sampler = ctx.getSampler(0);

        int height = (int) Mth.map(
                noiseWithDither(pos, ctx, sampler, 1.0 / 64.0, 0.0, 0.2),
                -1, 1, minLayerHeight, maxLayerHeight
        );
        if (ctx.surfaceY() > height) return;

        if (steepnessThreshold(pos, ctx, sampler, 1.0F / 32.0F, -493.0, -1, 0.5, 0, 2)) {
            int surfaceDepth = (int) Mth.map(
                    noiseWithDither(pos, ctx, sampler, 1.0 / 32.0, 43.0, 0.2),
                    -1, 1, 1, 3
            );
            if (placeColumn(pos, ctx, surfaceState, surfaceDepth)) return;
        }
        int soilDepth = (int) Mth.map(
                noiseWithDither(pos, ctx, sampler, 1.0 / 32.0, 783.0, 0.2),
                -1, 1, 1, 3
        );
        if (placeColumn(pos, ctx, soilState, soilDepth)) return;
    }
}
