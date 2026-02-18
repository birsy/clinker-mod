package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.core.util.noise.FastNoiseLite;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.function.Supplier;

public class SeededNoiseHolder implements NoiseRegistry, NoiseProvider {
    protected final Object2ObjectOpenHashMap<String, FastNoiseLite> noises;
    protected final Object2ObjectOpenHashMap<String, VoronoiDefinition> voronoiDefinitions;
    protected final PositionalRandomFactory worldRandom;
    
    public SeededNoiseHolder(PositionalRandomFactory worldRandom) {
        this.worldRandom = worldRandom;
        this.noises = new Object2ObjectOpenHashMap<>(16);
        this.voronoiDefinitions = new Object2ObjectOpenHashMap<>(4);
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
    public void registerVoronoi(String name, Supplier<VoronoiDefinition> factory) {
        if (!voronoiDefinitions.containsKey(name)) {
            voronoiDefinitions.put(name, factory.get());
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
