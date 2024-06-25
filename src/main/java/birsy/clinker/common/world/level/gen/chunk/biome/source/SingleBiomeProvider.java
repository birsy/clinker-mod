package birsy.clinker.common.world.level.gen.chunk.biome.source;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public class SingleBiomeProvider implements BiomeProvider {
    public Holder<Biome> biome;

    public SingleBiomeProvider(Holder<Biome> biome) {
        this.biome = biome;
    }

    @Override
    public Holder<Biome> getBiome(double x, double y, double z) {
        return biome;
    }

    @Override
    public Set<Holder<Biome>> getPossibleBiomes() {
        return Set.of(biome);
    }
}
