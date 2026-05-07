package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SnakesSurfaceDecorator extends SurfaceDecorator {
    private final int seaLevel;

    public SnakesSurfaceDecorator(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[4]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[7]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (ctx.surfaceDirection() != Direction.DOWN) return;

        int surfaceY = pos.getY();
        int offset = 0;

        double noise4 = ctx.retrieve(pos, ClinkerNoiseComputers.BASE_NOISE_2D[4]);
        double noise5 = ctx.retrieve(pos, ClinkerNoiseComputers.BASE_NOISE_2D[5]);

        int sandDepth = surfaceY < seaLevel ? 3 : ctx.random().nextInt(1, 3);
        int distanceAboveSeaLevel = surfaceY - seaLevel;

        double rockiness = (1 - Math.abs(noise5)) * 4;
        if (distanceAboveSeaLevel > rockiness) sandDepth = 0;
        if (noise4 > 0 && ctx.maxDownwardsOffset() == 1) sandDepth = 0;
        if (ctx.maxDownwardsOffset() >= 2) sandDepth = 0;

        boolean placeGrass = false;
        double grassiness = noise5 * 5;
        if (ctx.maxDownwardsOffset() >= 1) grassiness += 4;
        if (distanceAboveSeaLevel - 5 > grassiness) placeGrass = true;

        double ungrassiness = noise4 - 0.5 + ctx.random().triangle(0, 0.2) + ctx.maxDownwardsOffset() * 0.25;
        if (ungrassiness > 0) placeGrass = false;

        if (placeGrass) {
            ctx.place(pos, ClinkerBlocks.SALTMOSS.get().defaultBlockState());
            pos.move(ctx.surfaceDirection());
            offset++;
        }

        if (sandDepth > 0) {
            sandDepth = Math.min(sandDepth, ctx.maximumDepth());
            for (int i = offset; i < sandDepth; i++) {
                ctx.place(pos, ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState());
                pos.move(ctx.surfaceDirection());
                offset++;
            }
        }

        int stoneDepth = Math.min(15, ctx.maximumDepth());
        for (int i = offset; i < stoneDepth; i++) {
            ctx.place(pos, ClinkerBlocks.CALC.get().defaultBlockState());
            pos.move(ctx.surfaceDirection());
            offset++;
        }
    }
}
