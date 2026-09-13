package birsy.clinker.common.world.level.gen.content.surface.decoration;

import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BrineSwampSurfaceDecorator extends SurfaceDecorator {
    private final int seaLevel;
    private final int calcHeight;
    private BlockState calc, saltMoss, saltGravel;

    public BrineSwampSurfaceDecorator(int seaLevel) {
        this.seaLevel = seaLevel;
        this.calcHeight = seaLevel + 32;
    }

    @Override
    public void initialize() {
        this.calc = ClinkerBlocks.CALC.get().defaultBlockState();
        this.saltMoss = ClinkerBlocks.SALTMOSS.get().defaultBlockState();
        this.saltGravel = ClinkerBlocks.SALT_GRAVEL.get().defaultBlockState();
    }

    @Override
    public @Nullable BlockState getFillBlock(BlockPos pos, NoiseSampler[] samplers) {
        if (pos.getY() < calcHeight) return this.calc;
        return null;
    }

    @Override
    public void declareDependencies(Consumer<NoiseProvider> consumer) {
        consumer.accept(FNLNoiseProvider.create("noiseA"));
        consumer.accept(FNLNoiseProvider.create("noiseB"));
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {
        if (ctx.surfaceDirection() != Direction.DOWN) return;

        double noise3 = ctx.getSampler(0).sample(pos.getX() / 8.0, pos.getZ() / 8.0);
        double noise5 = ctx.getSampler(0).sample(pos.getX() / 32.0, pos.getZ() / 32.0);
        double waterloggingNoise = noise5 + noise3 * 0.8;
        double dither = ctx.random().nextDouble() * 2 - 1;

        boolean placedSand = false;
        int offset = 0;

        if (pos.getY() < seaLevel + 5 + noise5 * 3) {
            if (pos.getY() == seaLevel - 1 && waterloggingNoise > 0 && ctx.maxUpwardsOffset() <= 0) {
                ctx.place(pos, saltGravel);
                placedSand = true;
                offset++;
            } else if (pos.getY() == seaLevel - 2 && waterloggingNoise > 0.5 && ctx.maxUpwardsOffset() <= 0) {
                ctx.place(pos, Blocks.WATER.defaultBlockState());
                pos.move(ctx.surfaceDirection());
                ctx.place(pos, saltGravel);
                placedSand = true;
                offset++;
            } else {
                boolean isBorder = Math.max(ctx.maxDownwardsOffset(), ctx.maxUpwardsOffset()) >= 1;
                isBorder = (isBorder && noise3 > 0) || Math.max(ctx.maxDownwardsOffset(), ctx.maxUpwardsOffset()) >= 2;

                if (!isBorder) {
                    double grassNoise = waterloggingNoise + dither * 0.05;
                    boolean placeGrass = grassNoise < 0.9 && pos.getY() >= seaLevel + 1;
                    if (pos.getY() == seaLevel + 1) placeGrass &= ctx.maxDownwardsOffset() == 0;

                    if (placeGrass) {
                        ctx.place(pos, saltMoss);
                    } else {
                        ctx.place(pos, saltGravel);
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
                ctx.place(pos, saltMoss);
            }
            offset++;
            pos.move(ctx.surfaceDirection());
        }

        int sandBlocks = !placedSand ? 0 : ctx.random().nextInt(2, 3);
        for (int i = offset; i < sandBlocks; i++) {
            ctx.place(pos, saltGravel);
            pos.move(ctx.surfaceDirection());
        }
    }
}
