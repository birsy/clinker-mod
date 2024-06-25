package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.chunk.biome.TerrainLayer;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// a chunk of chunks. used for large scale feature generation
public class MetaChunk {
    public static final int SIZE_IN_CHUNKS = 16;
    public static final int SIZE_IN_BLOCKS = SIZE_IN_CHUNKS * 16;

    private final WorldGenLevel level;
    private final MetaChunkPos position;
    private final RandomSource random;

    private final List<TerrainFeaturePlacer> placers = new ArrayList<>();
    private final List<TerrainFeature> features = new ArrayList<>();

    private final long[] generated = new long[4];

    public MetaChunk(WorldGenLevel level, MetaChunkPos position) {
        this.level = level;
        this.position = position;
        this.random = RandomSource.create(level.getSeed() + ChunkPos.asLong(position.x, position.z));

        this.placers.add(new DebugTerrainFeaturePlacer(SphereTestTerrainFeature::new));
    }

    // generates the terrain features within a meta-chunk
    public void populate() {
        for (TerrainFeaturePlacer placer : this.placers) {
            for (int i = 0; i < placer.getCountInMetaChunk(this.random, this); i++) {
                BlockPos pos = placer.getPotentialPlacementPosition(this.random, this);
                // todo: collision checks, etc.
                this.features.add(placer.create(pos));
            }
        }

        for (TerrainFeature feature : this.features) {
            feature.generateLayout(this.random, this);
        }
    }

    public List<TerrainFeature> getFeaturesInChunk(ChunkPos pos) {
        List<TerrainFeature> featuresInChunk = new ArrayList<>();

        featureLoop:
        for (TerrainFeature feature : this.features) {
            if (!feature.shouldGenerateInChunk(pos)) continue;

            for (BoundingBox boundingBox : feature.getBoundingBoxes()) {
                if (boundingBox.intersects(pos.getMinBlockX(), pos.getMinBlockZ(), pos.getMaxBlockX(), pos.getMaxBlockZ())) {
                    featuresInChunk.add(feature);
                    continue featureLoop;
                }
            }
        }

        return featuresInChunk;
    }

    public record MetaChunkPos(int x, int z) {
        int getMinBlockX() {
            return this.x * SIZE_IN_BLOCKS;
        }
        int getMinBlockZ() {
            return this.z * SIZE_IN_BLOCKS;
        }
        int getMaxBlockX() {
            return (this.x + 1) * SIZE_IN_BLOCKS;
        }
        int getMaxBlockZ() {
            return (this.z + 1) * SIZE_IN_BLOCKS;
        }

        int getMinChunkX() {
            return this.x * SIZE_IN_CHUNKS;
        }
        int getMinChunkZ() {
            return this.z * SIZE_IN_CHUNKS;
        }
        int getMaxChunkX() {
            return (this.x + 1) * SIZE_IN_CHUNKS;
        }
        int getMaxChunkZ() {
            return (this.z + 1) * SIZE_IN_CHUNKS;
        }

        public long hash() {
            return this.x & 0xFFFFFFFFL | (this.z & 0xFFFFFFFFL) << 32;
        }

        public static MetaChunkPos fromChunkPos(ChunkPos chunkPos) {
            return new MetaChunkPos(chunkPos.x / SIZE_IN_CHUNKS, chunkPos.z / SIZE_IN_CHUNKS);
        }
    }

    // TERRAIN FEATURES
    // --------------------------------------------------------------------------------------------------------------------------

    public static abstract class TerrainFeaturePlacer {
        protected final Function<BlockPos, TerrainFeature> factory;

        protected TerrainFeaturePlacer(Function<BlockPos, TerrainFeature> factory) {
            this.factory = factory;
        }

        public abstract int getCountInMetaChunk(RandomSource random, MetaChunk metaChunk);
        // returns a place where a feature can spawn, given no collisions
        // dude i am so tired
        public abstract BlockPos getPotentialPlacementPosition(RandomSource random, MetaChunk metaChunk);
        public TerrainFeature create(BlockPos placementPos) { return this.factory.apply(placementPos); }
    }

    public static abstract class TerrainFeature {
        protected final BlockPos generationPosition;

        protected TerrainFeature(BlockPos generationPosition) {
            this.generationPosition = generationPosition;
        }

        public abstract void generateLayout(RandomSource random, MetaChunk metaChunk);
        public abstract List<BoundingBox> getBoundingBoxes();
        public abstract boolean shouldGenerateInChunk(ChunkPos pos);
        public abstract void modifyNoiseLayers(TerrainLayer layer);
        public abstract float modifyNoiseLayers(float noise, double x, double y, double z);
    }

    // DEBUG TEST SHIT
    // --------------------------------------------------------------------------------------------------------------------------

    public static class DebugTerrainFeaturePlacer extends TerrainFeaturePlacer {
        public DebugTerrainFeaturePlacer(Function<BlockPos, TerrainFeature> factory) {
            super(factory);
        }

        @Override
        public int getCountInMetaChunk(RandomSource random, MetaChunk metaChunk) {
            return 1;
        }

        @Override
        public BlockPos getPotentialPlacementPosition(RandomSource random, MetaChunk metaChunk) {
            int offset = SIZE_IN_BLOCKS / 2;
            return new BlockPos(metaChunk.position.getMinBlockX() + offset, 170, metaChunk.position.getMinBlockZ() + offset);
        }
    }

    public static class SphereTestTerrainFeature extends TerrainFeature {
        private static final int RADIUS = 64;
        private final List<BoundingBox> boundingBoxes;
        public SphereTestTerrainFeature(BlockPos generationPosition) {
            super(generationPosition);
            this.boundingBoxes = List.of(new BoundingBox(generationPosition).inflatedBy(RADIUS + 16));
        }


        @Override
        public void generateLayout(RandomSource random, MetaChunk metaChunk) {
            return;
        }

        @Override
        public List<BoundingBox> getBoundingBoxes() {
            return this.boundingBoxes;
        }

        @Override
        public boolean shouldGenerateInChunk(ChunkPos pos) {
            return true;
        }

        @Override
        public void modifyNoiseLayers(TerrainLayer layer) {
            for (TerrainLayer.Gexel gexel : layer) {
                float noise = gexel.get();
                noise += Math.max(RADIUS - this.generationPosition.distToCenterSqr(gexel.x, gexel.y, gexel.y), 0.0F);
                gexel.set(noise);
            }
        }
        // todo: remove this haha
        @Override
        public float modifyNoiseLayers(float noise, double x, double y, double z) {
            float val = noise;
            float distSqr = (float) generationPosition.distToCenterSqr(x, y, z);
            double sphereSDF = (RADIUS - Math.sqrt(distSqr)) / RADIUS;
            val = (float) -MathUtils.smoothMinExpo(-val, -sphereSDF, 0.1F);
            //val += Math.max(RADIUS - this.generationPosition.distToCenterSqr(x, y, z), 0.0F);
            return val;
        }
    }
}
