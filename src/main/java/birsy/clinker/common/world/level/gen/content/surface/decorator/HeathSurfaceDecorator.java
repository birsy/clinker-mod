package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.block.FallingLayerBlock;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.BASE_NOISE_2D;
import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.BASE_NOISE_2D_ALT;

public class HeathSurfaceDecorator extends SurfaceDecorator {
    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(BASE_NOISE_2D[4]);
        cache.fillNoiseField(BASE_NOISE_2D[5]);
        cache.fillNoiseField(BASE_NOISE_2D_ALT[5]);
        cache.fillNoiseField(BASE_NOISE_2D[6]);
        cache.fillNoiseField(BASE_NOISE_2D[7]);
        cache.fillNoiseField(BASE_NOISE_2D_ALT[7]);
        cache.fillNoiseField(BASE_NOISE_2D[8]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (ctx.surfaceDirection() != Direction.DOWN) return;

        double wiggleNoise = ctx.retrieve(pos, BASE_NOISE_2D[4]);

        double cliff0Fac = ctx.retrieve(pos, BASE_NOISE_2D[6]);
        double cliff0 = Math.pow(Math.abs(cliff0Fac), 1 / 12.0) * Math.signum(cliff0Fac) * 8;

        double cliff1Fac = ctx.retrieve(pos, BASE_NOISE_2D_ALT[7]) - 0.5;
        double cliff1 = Math.pow(Math.abs(cliff1Fac), 1 / 24.0) * Math.signum(cliff1Fac) * 30;
        double cliff1Mask = ctx.retrieve(pos, BASE_NOISE_2D[8]) * 0.5 + 0.5;
        cliff1 *= cliff1Mask;

        double surfaceFac = ctx.retrieve(pos, BASE_NOISE_2D[7]);
        double surface = surfaceFac * 8;

        boolean placeStone = false;
        if (cliff0 > surface && cliff0 > cliff1)
            placeStone = cliff0Fac + wiggleNoise * 0.1 < 0.24;
        else if (cliff1 > surface && cliff1 > cliff0)
            placeStone = cliff1Fac + wiggleNoise * 0.1 < 0.2;

        if (!placeStone) {
            double erosionMask = ctx.retrieve(pos, BASE_NOISE_2D[5]);
            erosionMask += ctx.random().triangle(0, 0.5);

            boolean placeSoil = (erosionMask < 0 || ctx.maxDownwardsOffset() < 1) && ctx.maxDownwardsOffset() <= 3;
            placeSoil &= ctx.retrieve(pos, BASE_NOISE_2D_ALT[5]) + ctx.random().triangle(0, 0.2) < 0.5;

            BlockState soilState = ClinkerBlocks.PEAT_MOSS.get().defaultBlockState();
            if (wiggleNoise - 0.3 + ctx.random().triangle(0, 0.25) > 0)
                soilState = ClinkerBlocks.PACKED_ASH.get().defaultBlockState();

            ctx.place(pos, placeSoil ? soilState : ClinkerBlocks.BRIMSTONE.get().defaultBlockState());

            if (ctx.maxUpwardsOffset() == 1 && ctx.maxDownwardsOffset() == 0) {
                double ashNoise = wiggleNoise + 0.5 + ctx.random().triangle(0, 0.5);
                int ashAmount = (int) (Math.clamp(ashNoise, 0, 1) * 5);
                if (ashAmount > 0) {
                    pos.move(0, 1, 0);
                    ctx.place(pos,
                            ClinkerBlocks.ASH_LAYER.get().defaultBlockState()
                                    .setValue(FallingLayerBlock.LAYERS, ashAmount));
                    pos.move(ctx.surfaceDirection());
                }
            }

            if (placeSoil) {
                int soilDepth = ctx.random().nextIntBetweenInclusive(1, 2) - ctx.maxDownwardsOffset();
                for (int i = 0; i < Math.min(soilDepth, ctx.maximumDepth()); i++) {
                    pos.move(ctx.surfaceDirection());
                    ctx.place(pos, soilState);
                }
            }
        }
    }
}
