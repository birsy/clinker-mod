package birsy.clinker.common.world.level.gen.system.metachunk;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.*;
import birsy.clinker.common.world.level.gen.system.noise.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MetaChunkMap {
    public static final int MAX_DEPTH_EXCLUSIVE = 10;
    private final ObjectArrayList<WorldFeatureSpawnSet>[] worldFeatureSetsByDepth;
    private final RandomState randomState;
    private final PositionalRandomFactory metaChunkRandom;
    private final Cache<Long, MetaChunk>[] metaChunkCache;
    private final UncachedNoiseContext uncachedNoiseContext;

    public MetaChunkMap(RandomState randomState) {
        this.randomState = randomState;
        this.metaChunkRandom = randomState.random.fromHashOf(Clinker.resource("meta_chunk")).forkPositional();

        this.metaChunkCache = new Cache[MAX_DEPTH_EXCLUSIVE];
        for (int i = 0; i < this.metaChunkCache.length; i++) {
            this.metaChunkCache[i] = Caffeine.newBuilder()
                    .maximumSize(512)
                    .executor(Runnable::run)
                    .build();
        }

        // init the world feature array from the registry
        this.worldFeatureSetsByDepth = new ObjectArrayList[MAX_DEPTH_EXCLUSIVE];
        for (int i = 0; i < this.worldFeatureSetsByDepth.length; i++)
            this.worldFeatureSetsByDepth[i] = new ObjectArrayList<>();
        ClinkerRegistries.WORLD_FEATURE_SPAWN_SET_REGISTRY.stream().forEach((featureSet) -> {
            if (featureSet.metaChunkDepth() < MAX_DEPTH_EXCLUSIVE - 1)
                this.worldFeatureSetsByDepth[featureSet.metaChunkDepth()].add(featureSet);
        });

        this.uncachedNoiseContext = new UncachedNoiseContext(((SeededNoiseHolderHolder)(Object) randomState).clinker$noiseHolder());
    }

    int getMetaChunkSizeForDepth(int depth) {
        return 16 << depth;
    }

    MetaChunk getMetaChunk(LevelAccessor level, int depth, int blockX, int blockZ, WorldFeatureContext worldContext) {
        int size = getMetaChunkSizeForDepth(depth);
        int metaChunkX = Math.floorDiv(blockX, size), metaChunkZ = Math.floorDiv(blockZ, size);
        long key = MetaChunk.asLong(metaChunkX, metaChunkZ);

        return metaChunkCache[depth].get(key, (k) -> {
            int minX = metaChunkX * size, maxX = minX + size - 1;
            int minZ = metaChunkZ * size, maxZ = minZ + size - 1;

            Set<WorldFeature> worldFeaturesInChunk = new HashSet<>(16);
            Consumer<WorldFeature> collector = worldFeaturesInChunk::add;
            // generate "parents"
            if (depth < MAX_DEPTH_EXCLUSIVE - 1) {
                int parentSize = getMetaChunkSizeForDepth(depth + 1);
                for (int xOffset = -1; xOffset <= 1; xOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        MetaChunk parent = getMetaChunk(
                                level,
                                depth + 1,
                                (Math.floorDiv(blockX, parentSize) + xOffset) * parentSize,
                                (Math.floorDiv(blockZ, parentSize) + zOffset) * parentSize,
                                worldContext
                        );

                        // propagate features "downward" to "child"
                        for (WorldFeature worldFeature : parent.worldFeatures) {
                            if (worldFeature.within(minX, minZ, maxX, maxZ)) {
                                worldFeaturesInChunk.add(worldFeature);
                                worldFeature.collectChildFeatures(depth, collector);
                            }
                        }
                    }
                }
            }
            generateWorldFeatures(level, depth, minX, minZ, maxX, maxZ, collector, worldContext);

            return new MetaChunk(worldFeaturesInChunk, size, depth, metaChunkX, metaChunkZ);
        });
    }

    void generateWorldFeatures(LevelAccessor level, int depth, int minX, int minZ, int maxX, int maxZ, Consumer<WorldFeature> collector, WorldFeatureContext worldContext) {
        if (depth <= 0) return;
        RandomSource random = metaChunkRandom.at(minX, depth, minZ);

        List<WorldFeature> spawnSetFeatures = new ArrayList<>(16);

        for (WorldFeatureSpawnSet spawnSet : this.worldFeatureSetsByDepth[depth]) {
            for (WorldFeatureSpawnSet.WorldFeatureSpawn featureSpawn : spawnSet.features()) {
                int count = featureSpawn.count().sample(random);
                NEXT_FEATURE:
                for (int i = 0; i < count; i++) {
                    Optional<? extends WorldFeature> maybeRealizedFeature = featureSpawn.featureType().realize(level, minX, minZ, maxX, maxZ, depth, random, uncachedNoiseContext, worldContext);
                    if (maybeRealizedFeature.isEmpty()) continue;

                    WorldFeature realizedFeature = maybeRealizedFeature.get();
                    int x = realizedFeature.getCenterX(),
                        z = realizedFeature.getCenterZ();

                    for (WorldFeature otherFeature : spawnSetFeatures) {
                        double distance = Mth.length(x - otherFeature.getCenterX(), z - otherFeature.getCenterZ());
                        if (distance < realizedFeature.type().separationRadius() + otherFeature.type().separationRadius()) continue NEXT_FEATURE;
                    }
                    spawnSetFeatures.add(realizedFeature);
                    collector.accept(realizedFeature);
                }
            }
            spawnSetFeatures.clear();
        }
    }

    public WorldFeatureSet getWorldFeatures(LevelAccessor level, int blockX, int blockZ, WorldFeatureContext context) {
        return getMetaChunk(level, 0, blockX, blockZ, context).compiledWorldFeatures.get();
    }
}
