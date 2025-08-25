package birsy.clinker.common.world.level.gen.noise;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class NoiseHolder {
    private final Map<String, FastNoiseLite> noises;
    private final PositionalRandomFactory worldRandom;
    
    public NoiseHolder(PositionalRandomFactory worldRandom) {
        this.worldRandom = worldRandom;
        this.noises = new ConcurrentHashMap<>(16);
    }
    
    public void registerNoise(String name, Supplier<FastNoiseLite> factory) {
        if (!noises.containsKey(name)) {
            FastNoiseLite newNoise = factory.get();
            newNoise.SetSeed(worldRandom.fromHashOf(name).nextInt());
            noises.put(name, newNoise);
        }
    }

    public void registerNoise(String name) {
        this.registerNoise(name, () -> {
            FastNoiseLite noise = new FastNoiseLite();
            noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
            noise.SetFractalType(FastNoiseLite.FractalType.None);
            noise.SetFractalOctaves(0);
            noise.SetFrequency(1);
            return noise;
        });
    }

    public void registerNoise(String name, int octaves, double lacunarity, double gain, double weightedStrength) {
        this.registerNoise(name, () -> {
            FastNoiseLite noise = new FastNoiseLite();
            noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
            
            noise.SetFractalType(FastNoiseLite.FractalType.FBm);
            noise.SetFrequency(1);

            noise.SetFractalOctaves(octaves);
            noise.SetFractalLacunarity((float) lacunarity);
            noise.SetFractalGain((float) gain);
            noise.SetFractalWeightedStrength((float) weightedStrength);
            return noise;
        });
    }
    
    public double sample(String name, double x, double y, double z) {
        if (!noises.containsKey(name)) {
            Clinker.LOGGER.warn("No noise registered of name [{}] !", name);
            return 0;
        }
        return noises.get(name).GetNoise(x, y, z);
    }

    public double sample(String name, double x, double y) {
        if (!noises.containsKey(name)) {
            Clinker.LOGGER.warn("No noise registered of name [{}] !", name);
            return 0;
        }
        return noises.get(name).GetNoise(x, y);
    }
}
