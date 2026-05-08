package birsy.clinker.common.world.level.gen.system.metachunk;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType.*;

public class MetaChunk {
    final int size, depth;
    final int metaChunkX, metaChunkZ;
    final Set<WorldFeatureInstance<?>> worldFeatures;
    Lazy<WorldFeatureSet> compiledWorldFeatures;

    public MetaChunk(Set<WorldFeatureInstance<?>> worldFeatures, int size, int depth, int metaChunkX, int metaChunkZ) {
        this.worldFeatures = Collections.unmodifiableSet(worldFeatures);
        this.compiledWorldFeatures = Lazy.of(() -> new WorldFeatureSet(worldFeatures));

        this.size = size;
        this.depth = depth;
        this.metaChunkX = metaChunkX;
        this.metaChunkZ = metaChunkZ;
    }

    public int minX() { return metaChunkX * size; }
    public int minZ() { return metaChunkZ * size; }
    public int maxX() { return metaChunkX * size + size - 1; }
    public int maxZ() { return metaChunkZ * size + size - 1; }

    public static long asLong(int metaChunkX, int metaChunkZ) {
        return ChunkPos.asLong(metaChunkX, metaChunkZ);
    }
}