package birsy.clinker.common.world.level.gen.worldfeature;

import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class MetaChunk {
    final int size;
    final int metaChunkX, metaChunkZ;
    List<WorldFeature> worldFeatures = new ArrayList<>(16);

    public MetaChunk(int size, int metaChunkX, int metaChunkZ) {
        this.size = size;
        this.metaChunkX = metaChunkX;
        this.metaChunkZ = metaChunkZ;
    }

    void propagateFeatures(MetaChunk chunk) {
        for (WorldFeature worldFeature : worldFeatures) {
            if (worldFeature.within(chunk.minX(), chunk.minZ(), chunk.maxX(), chunk.maxZ())) {
                chunk.worldFeatures.add(worldFeature);
            }
        }
    }

    public int minX() { return metaChunkX * size; }
    public int minZ() { return metaChunkZ * size; }
    public int maxX() { return metaChunkX * size + size - 1; }
    public int maxZ() { return metaChunkZ * size + size - 1; }

    long asLong() {
        return asLong(this.metaChunkX, this.metaChunkZ);
    }

    public static long asLong(int metaChunkX, int metaChunkZ) {
        return ChunkPos.asLong(metaChunkX, metaChunkZ);
    }
}