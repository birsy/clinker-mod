package birsy.clinker.common.world.level.gen.chunk.biome.source;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Set;

public interface BiomeProvider {
    Holder<Biome> getBiome(double x, double y, double z);
    Set<Holder<Biome>> getPossibleBiomes();
}