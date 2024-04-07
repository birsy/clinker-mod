package birsy.clinker.common.world.level.gen.chunk.densityfunction;

import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.function.Function;

// really hacky solution
public class FastNoiseDensityFunction implements DensityFunction.SimpleFunction {
    final FastNoiseLite noise;
    final Function<NoiseContext, Double> transformer;

    protected FastNoiseDensityFunction(FastNoiseLite noise, Function<NoiseContext, Double> transformer) {
        this.noise = noise;
        this.transformer = transformer;
    }

    protected FastNoiseDensityFunction(FastNoiseLite noise) {
        this.noise = noise;
        this.transformer = (noiseContext -> (double) noiseContext.noise.GetNoise((float) noiseContext.x, (float) noiseContext.y, (float) noiseContext.z));
    }

    public FastNoiseDensityFunction(FastNoiseLite noise, float offset) {
        this.noise = noise;
        this.transformer = (noiseContext -> (double) noiseContext.noise.GetNoise((float) noiseContext.x, (float) noiseContext.y + offset, (float) noiseContext.z));
    }

    @Override
    public double compute(FunctionContext context) {
        return transformer.apply(new NoiseContext(this.noise, context.blockX(), context.blockY(), context.blockZ()));
    }

    @Override
    public double minValue() {
        return -1;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return null;
    }

    protected record NoiseContext(FastNoiseLite noise, double x, double y, double z) {}
}
