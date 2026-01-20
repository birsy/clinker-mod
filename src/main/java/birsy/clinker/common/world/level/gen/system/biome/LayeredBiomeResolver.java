package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;

public final class LayeredBiomeResolver {
    private final Holder<Biome>[] biomeByProtoBiomeId;
    private final BiomeLayer resolverLayer;

    public final ImmutableSet<Holder<Biome>> possibleBiomes;

    private LayeredBiomeResolver(HolderGetter<Biome> biomeGetter, BiomeLayer bottomLayer) {
        biomeByProtoBiomeId = new Holder[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()];
        // fill in biomes
        Holder.Reference<Biome> voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);
        for (ProtoBiome protoBiome : ClinkerRegistries.PROTO_BIOME_REGISTRY) {
            biomeByProtoBiomeId[protoBiome.id] = protoBiome.biome.map(biomeGetter::getOrThrow).orElse(voidBiome);
        }

        this.possibleBiomes = ImmutableSet.copyOf(biomeByProtoBiomeId);

        this.resolverLayer = bottomLayer;
    }

    public Holder<Biome> resolveBiome(int qX, int qZ) {
        int blockX = QuartPos.toBlock(qX), blockZ = QuartPos.toBlock(qZ);
        ProtoBiome protoBiome = resolverLayer.getOrCreateCellAt(blockX, blockZ);
        return biomeByProtoBiomeId[protoBiome.id];
    }

    public static Builder builder(int startingScale) { return new Builder(startingScale); }
    public static final class Builder {
        private final List<BiomeLayerOperation[]>[] unbuiltLayersByScale;
        private int scale;

        private Builder(int startingScale) {
            this.scale = startingScale;
            unbuiltLayersByScale = new List[startingScale + 1];
            for (int i = 0; i < unbuiltLayersByScale.length; i++)
                unbuiltLayersByScale[i] = new ArrayList<>();
        }

        public Builder layer(BiomeLayerOperation... operations) {
            unbuiltLayersByScale[scale].add(operations);
            return this;
        }

        public Builder zoom() {
            if (this.scale == 1) return this;
            this.scale--;
            return this;
        }

        public LayeredBiomeResolver build(HolderGetter<Biome> biomeGetter, RandomState randomState, UncachedNoiseContext context) {
            BiomeLayer lastLayer = null;
            int index = 0;
            for (int cellScale = unbuiltLayersByScale.length - 1; cellScale >= 0; cellScale--) {
                List<BiomeLayerOperation[]> unbuiltLayers = unbuiltLayersByScale[cellScale];
                for (int j = 0; j < unbuiltLayers.size(); j++) {
                    BiomeLayerOperation[] unbuiltLayer = unbuiltLayers.get(j);
                    BiomeLayer nextLayer = new BiomeLayer(
                            lastLayer,
                            randomState.getOrCreateRandomFactory(Clinker.resource("layer_" + index++)),
                            context,
                            cellScale,
                            unbuiltLayer
                    );
                    lastLayer = nextLayer;
                }
            }

            return new LayeredBiomeResolver(biomeGetter, lastLayer);
        }
    }
}
