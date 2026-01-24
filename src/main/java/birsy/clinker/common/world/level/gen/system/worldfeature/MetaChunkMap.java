package birsy.clinker.common.world.level.gen.system.worldfeature;

import birsy.clinker.common.world.level.gen.system.noise.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class MetaChunkMap {
    public static final int MAX_DEPTH = 10;
    private final List<WorldFeatureSpawnSet>[] worldFeatureSetsByDepth;
    private final RandomState randomState;
    private final PositionalRandomFactory metaChunkRandom;
    private final Map<Long, MetaChunk>[] metaChunkCache;
    private UncachedNoiseContext uncachedNoiseContext;

    public MetaChunkMap(RandomState randomState) {
        this.randomState = randomState;
        this.metaChunkRandom = randomState.random.fromHashOf(Clinker.resource("meta_chunk")).forkPositional();

        this.metaChunkCache = new ConcurrentHashMap[MAX_DEPTH];
        for (int i = 0; i < this.metaChunkCache.length; i++) {
            this.metaChunkCache[i] = new ConcurrentHashMap<>();
        }

        // init the world feature array from the registry
        this.worldFeatureSetsByDepth = new List[MAX_DEPTH];
        for (int i = 0; i < this.worldFeatureSetsByDepth.length; i++)
            this.worldFeatureSetsByDepth[i] = new ArrayList<>();
        Stream<WorldFeatureSpawnSet> worldFeatureSets =
                ServerLifecycleHooks.getCurrentServer().registryAccess()
                .lookupOrThrow(ClinkerDynamicRegistries.WORLD_FEATURE_REGISTRY_KEY)
                .listElements().map(Holder.Reference::value);
        worldFeatureSets.forEach((featureSet) -> {
            for (int i = 0; i < featureSet.metaChunkDepths().size(); i++)
                this.worldFeatureSetsByDepth[i].add(featureSet);
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

        return metaChunkCache[depth].computeIfAbsent(key, (param) -> {
            // create a new meta-chunk.
            MetaChunk newChunk = new MetaChunk(size, depth, metaChunkX, metaChunkZ);

            // generate "parents"
            if (depth < MAX_DEPTH-1) {
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
                        parent.propagateFeatures(newChunk);
                    }
                }
            }

            generateWorldFeatures(level, depth, newChunk, worldContext);
            return newChunk;
        });
    }

    void generateWorldFeatures(LevelAccessor level, int depth, MetaChunk metaChunk, WorldFeatureContext worldContext) {
        if (depth <= 0) return;
        RandomSource random = metaChunkRandom.at(metaChunk.minX(), depth, metaChunk.maxZ());

//        if (depth == 6) {
//            WorldFeature river = new UndergroundRiverWorldFeature(depth, 0);
//            river.plan(level, metaChunk, random, context);
//            metaChunk.worldFeatures.add(river);
//        }
//
//        if (depth == 4) {
//            for (int i = 0; i < random.nextInt(4, 12); i++) {
//                WorldFeature lake = new UndergroundLakeWorldFeature(depth, 0);
//                lake.plan(level, metaChunk, random, noiseContext);
//                metaChunk.worldFeatures.add(lake);
//            }
//        }

        List<WorldFeature> spawnSetFeatures = new ArrayList<>(16);

        for (WorldFeatureSpawnSet spawnSet : this.worldFeatureSetsByDepth[depth]) {
            for (WorldFeatureSpawnSet.WorldFeatureInstance feature : spawnSet.features()) {
                int count = feature.count().sample(random);
                featureSet:
                for (int i = 0; i < count; i++) {
                    WorldFeature realizedFeature = feature.feature().create(metaChunk, feature.spacingRadius());
                    boolean placed = realizedFeature.plan(level, metaChunk, random, uncachedNoiseContext, worldContext);
                    int x = realizedFeature.getCenterX(), z = realizedFeature.getCenterZ();
                    int radius = realizedFeature.separationRadius;
                    if (placed) {
                        for (WorldFeature otherFeature : spawnSetFeatures) {
                            double distance = Mth.length(x - otherFeature.getCenterX(), z - otherFeature.getCenterZ());
                            if (distance < radius + otherFeature.separationRadius) {
                                continue featureSet;
                            }
                        }

                        spawnSetFeatures.add(realizedFeature);
                        metaChunk.worldFeatures.add(realizedFeature);
                    }
                }
            }
            spawnSetFeatures.clear();
        }
    }

    public Collection<WorldFeature> getWorldFeatures(LevelAccessor level, int blockX, int blockZ, WorldFeatureContext context) {
        return getMetaChunk(level, 0, blockX, blockZ, context).worldFeatures;
    }
}
