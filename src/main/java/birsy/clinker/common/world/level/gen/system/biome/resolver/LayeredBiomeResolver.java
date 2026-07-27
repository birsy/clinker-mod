package birsy.clinker.common.world.level.gen.system.biome.resolver;

import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.*;
import java.util.function.Function;

public final class LayeredBiomeResolver {
    public final int layerCount;
    private final BiomeLayer[] layers;
    private final BiomeLayer resolverLayer;

    private LayeredBiomeResolver(BiomeLayer... layers) {
        this.layers = layers;
        this.layerCount = layers.length;
        this.resolverLayer = layers[layers.length - 1];
    }

    public ProtoBiome getProtoBiome(int qX, int qZ, int layerIndex) {
        int blockX = QuartPos.toBlock(qX), blockZ = QuartPos.toBlock(qZ);
        return layers[layerIndex].getOrCreateCellAt(blockX, blockZ);
    }
    public ProtoBiome getProtoBiome(int qX, int qZ) {
        int blockX = QuartPos.toBlock(qX), blockZ = QuartPos.toBlock(qZ);
        return resolverLayer.getOrCreateCellAt(blockX, blockZ);
    }

    public ProtoBiome[] getProtoBiomeArea(int minQX, int minQZ, int maxQX, int maxQZ) {
        return getProtoBiomeArea(minQX, minQZ, maxQX, maxQZ, layers.length - 1);
    }

    public ProtoBiome[] getProtoBiomeArea(int minQX, int minQZ, int maxQX, int maxQZ, int layerIndex) {
        BiomeLayer layer = layers[layerIndex];
        int cellScale = layer.cellScale;

        int minCellX = BiomeLayer.toCellPos(QuartPos.toBlock(minQX), cellScale), minCellZ = BiomeLayer.toCellPos(QuartPos.toBlock(minQZ), cellScale);
        int maxCellX = BiomeLayer.toCellPos(QuartPos.toBlock(maxQX), cellScale), maxCellZ = BiomeLayer.toCellPos(QuartPos.toBlock(maxQZ), cellScale);
        int cellsX = maxCellX - minCellX + 1, cellsZ = maxCellZ - minCellZ + 1;

        int[] ids = layer.getIdsInArea(minCellX, minCellZ, cellsX, cellsZ);

        int sizeX = maxQX - minQX, sizeZ = maxQZ - minQZ;
        ProtoBiome[] out = new ProtoBiome[sizeX * sizeZ];
        for (int z = 0; z < sizeZ; z++) {
            int cellZ = BiomeLayer.toCellPos(QuartPos.toBlock(minQZ + z), cellScale) - minCellZ;
            for (int x = 0; x < sizeX; x++) {
                int cellX = BiomeLayer.toCellPos(QuartPos.toBlock(minQX + x), cellScale) - minCellX;
                int id = ids[cellZ * cellsX + cellX];
                out[z * sizeX + x] = ClinkerRegistries.PROTO_BIOME_REGISTRY.byIdOrThrow(id);
            }
        }
        return out;
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

        public LayeredBiomeResolver build(Function<ResourceLocation, PositionalRandomFactory> randomState, UncachedNoiseContext context) {
            BiomeLayer lastLayer = null;
            List<BiomeLayer> builtLayers = new ArrayList<>();
            int index = 0;
            for (int cellScale = unbuiltLayersByScale.length - 1; cellScale >= 0; cellScale--) {
                List<BiomeLayerOperation[]> unbuiltLayers = unbuiltLayersByScale[cellScale];
                for (int j = 0; j < unbuiltLayers.size(); j++) {
                    BiomeLayerOperation[] unbuiltLayer = unbuiltLayers.get(j);
                    BiomeLayer nextLayer = new BiomeLayer(
                            lastLayer,
                            randomState.apply(Clinker.resource("layer_" + index++)),
                            context,
                            cellScale,
                            unbuiltLayer
                    );
                    builtLayers.add(nextLayer);
                    lastLayer = nextLayer;
                }
            }
            return new LayeredBiomeResolver(builtLayers.toArray(new BiomeLayer[0]));
        }
    }
}
