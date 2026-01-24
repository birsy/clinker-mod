package birsy.clinker.common.world.level.gen.system.biome;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.Set;

public final class BiomeList {
    private final Object2IntMap<Holder<Biome>> biomeToId;
    private final Holder<Biome>[] idToBiome;
    private final int maxId;

    public BiomeList(BiomeSource biomeSource) {
        Set<Holder<Biome>> possibleBiomes = biomeSource.possibleBiomes();
        int biomeId = 0;

        this.biomeToId = new Object2IntArrayMap<>(possibleBiomes.size());
        this.idToBiome = new Holder[possibleBiomes.size()];
        for (Holder<Biome> biome : possibleBiomes) {
            this.biomeToId.put(biome, biomeId);
            this.idToBiome[biomeId] = biome;
            biomeId++;
        }

        this.maxId = biomeId;
    }

    public Holder<Biome> byId(int id) {
        return idToBiome[id];
    }

    public int getId(Holder<Biome> biome) {
        return biomeToId.getInt(biome);
    }

    public int maxId() {
        return maxId;
    }
}
