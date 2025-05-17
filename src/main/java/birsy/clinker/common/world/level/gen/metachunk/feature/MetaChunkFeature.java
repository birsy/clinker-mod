package birsy.clinker.common.world.level.gen.metachunk.feature;

import birsy.clinker.common.world.level.gen.metachunk.MetaChunk;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class MetaChunkFeature {
    public final int originX, originZ;

    protected MetaChunkFeature(int originX, int originZ) {
        this.originX = originX;
        this.originZ = originZ;
    }

    public void plan(MetaChunk metaChunk, RandomSource random) {}

    public abstract boolean containedInRange(int xMin, int zMin, int xMax, int zMax);

    public void realizeNoise(ChunkAccess chunk) {}

    public void realizeBlocks(ChunkAccess chunk) {}
}
