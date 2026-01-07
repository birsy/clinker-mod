package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.core.util.noise.FastNoiseLite;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.function.Supplier;

public class SeededNoiseHolder implements NoiseRegistry, NoiseProvider {
    private final Object2ObjectOpenHashMap<String, FastNoiseLite> noises;
    private final PositionalRandomFactory worldRandom;
    
    public SeededNoiseHolder(PositionalRandomFactory worldRandom) {
        this.worldRandom = worldRandom;
        this.noises = new Object2ObjectOpenHashMap<>(16);
    }

    @Override
    public void registerNoise(String name, Supplier<FastNoiseLite> factory) {
        if (!noises.containsKey(name)) {
            FastNoiseLite newNoise = factory.get();
            newNoise.SetSeed(worldRandom.fromHashOf(name).nextInt());
            noises.put(name, newNoise);
        }
    }

    @Override
    public double sample(String name, double x, double y, double z) {
        return noises.get(name).GetNoise(x, y, z);
    }

    @Override
    public double sample(String name, double x, double y) {
        return noises.get(name).GetNoise(x, y);
    }
}
