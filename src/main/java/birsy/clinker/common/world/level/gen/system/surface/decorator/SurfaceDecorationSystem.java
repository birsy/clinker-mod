package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;

// constructs the surface layer for a chunk
public class SurfaceDecorationSystem {
    private static final ResourceLocation SURFACE_BUILDER_RANDOM = Clinker.resource("surface_builder_random");

    protected final int maxElevationDifference;
    protected final int seaLevel;
    protected final BlockState defaultBlock;

    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceDecorator> biomeToDecorator;

    private final ThreadLocal<List<SurfaceToDecorate>> surfaces =
            ThreadLocal.withInitial(() -> new ArrayList<>(16 * 16 * 3));
    private final ThreadLocal<Set<SurfaceDecorator>> containedSurfaceDecorators =
            ThreadLocal.withInitial(() -> new HashSet<>(5));

    public SurfaceDecorationSystem(int maxElevationDifference, int seaLevel, BlockState defaultBlock, HolderGetter<Biome> biomeGetter) {
        this.maxElevationDifference = maxElevationDifference;
        this.seaLevel = seaLevel;
        this.defaultBlock = defaultBlock;

        this.biomeToDecorator = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<TagKey<Biome>, SurfaceDecorator> entry : SurfaceDecorators.decoratorByBiomeTag.entrySet()) {
            HolderSet.Named<Biome> tag = biomeGetter.getOrThrow(entry.getKey());
            for (Holder<Biome> biome : tag) {
                this.biomeToDecorator.put(biome, entry.getValue());
            }
        }
        for (Map.Entry<ResourceKey<Biome>, SurfaceDecorator> entry : SurfaceDecorators.decoratorByBiome.entrySet()) {
            Holder<Biome> biome = biomeGetter.getOrThrow(entry.getKey());
            this.biomeToDecorator.put(biome, entry.getValue());
        }
    }

    public void decorate(NoiseFieldCache noiseFieldCache,
                         NoiseField heightmapField, NoiseField heightmapGradientField,
                         WorldGenLevel level, ChunkAccess chunk, RandomState randomState) {
        RandomSource random = randomState.getOrCreateRandomFactory(SURFACE_BUILDER_RANDOM)
                .at(chunk.getPos().x, 0, chunk.getPos().z);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(),
                                 scratchPos = new BlockPos.MutableBlockPos();

        List<SurfaceToDecorate> surfaces = this.surfaces.get();
        surfaces.clear();
        Set<SurfaceDecorator> containedDecorators = this.containedSurfaceDecorators.get();
        containedDecorators.clear();

        this.findSurfaces(pos, scratchPos, heightmapField, heightmapGradientField, level, chunk, random, surfaces, containedDecorators);

        this.applySurfaceDecorations(pos, level, chunk, random, noiseFieldCache, surfaces, containedDecorators);
    }

    private void findSurfaces(BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos scratchPos,
                              NoiseField heightmapField, NoiseField heightmapGradientField,
                              WorldGenLevel level, ChunkAccess chunk, RandomSource random,
                              List<SurfaceToDecorate> surfaces, Set<SurfaceDecorator> containedDecorators) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                searchColumnForSurfaces(
                        x, z,
                        pos, scratchPos,
                        heightmapField, heightmapGradientField,
                        level, chunk, random,
                        surfaces, containedDecorators
                );
            }
        }
    }

    private void searchColumnForSurfaces(int localX, int localZ,
                                         BlockPos.MutableBlockPos pos,
                                         BlockPos.MutableBlockPos scratchPos,
                                         NoiseField heightmapField, NoiseField heightmapGradientField,
                                         WorldGenLevel level, ChunkAccess chunk, RandomSource random,
                                         List<SurfaceToDecorate> surfaces, Set<SurfaceDecorator> containedDecorators) {
        int startY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ);
        int worldX = localX + chunk.getPos().getMinBlockX();
        int worldZ = localZ + chunk.getPos().getMinBlockZ();

        double heightmapHeight = heightmapField.retrieve(localX, 0, localZ);
        double heightmapGradient = Math.sqrt(heightmapGradientField.retrieve(localX, 0, localZ));

        boolean visibleToSky = true;

        pos.set(worldX, startY, worldZ);
        BlockState previousBlockState = Blocks.AIR.defaultBlockState();
        while (pos.getY() > chunk.getMinBuildHeight() + 1) {
            BlockState state = level.getBlockState(pos);
            if (state == defaultBlock && previousBlockState.isAir()) {
                createSurface(
                        pos, scratchPos, heightmapHeight, heightmapGradient, visibleToSky,
                        level, chunk, random,
                        surfaces, containedDecorators
                );
                visibleToSky = false;
            }
            previousBlockState = state;
            pos.move(Direction.DOWN);
        }
    }

    private void createSurface(BlockPos.MutableBlockPos pos,
                               BlockPos.MutableBlockPos scratchPos,
                               double heightmapHeight, double heightmapGradient, boolean visibleToSky,
                               WorldGenLevel level, ChunkAccess chunk, RandomSource random,
                               List<SurfaceToDecorate> surfaces, Set<SurfaceDecorator> containedDecorators) {
        int biomeOffsetX = Math.clamp(pos.getX() + random.nextIntBetweenInclusive(-1, 1), chunk.getPos().getMinBlockX(), chunk.getPos().getMaxBlockX()),
            biomeOffsetZ = Math.clamp(pos.getZ() + random.nextIntBetweenInclusive(-1, 1), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockZ());
        Holder<Biome> biome = chunk.getNoiseBiome(
                QuartPos.fromBlock(biomeOffsetX),
                QuartPos.fromBlock(pos.getY()),
                QuartPos.fromBlock(biomeOffsetZ)
        );
        SurfaceDecorator decorator = this.biomeToDecorator.getOrDefault(biome, null);
        if (decorator == null) return;
        containedDecorators.add(decorator);

        // compute depth
        int depth = 1;
        scratchPos.set(pos);
        while (scratchPos.getY() > chunk.getMinBuildHeight() + 1) {
            scratchPos.move(Direction.DOWN);
            if (level.getBlockState(scratchPos) != defaultBlock) break;
            depth++;
        }

        // compute elevation changes
        int maxElevationIncrease = 0,
            maxElevationDecrease = 0;
        if (decorator.shouldCalculateElevationChange(visibleToSky, pos.getY(), heightmapHeight)) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                scratchPos.set(pos).move(dir);
                boolean solid = level.getBlockState(scratchPos).isSolidRender(level, scratchPos);
                Direction step = solid ? Direction.UP : Direction.DOWN;

                for (int i = 0; i <= maxElevationDifference; i++) {
                    scratchPos.move(step);
                    if (scratchPos.getY() <= chunk.getMinBuildHeight()) break;
                    if (solid)
                        maxElevationIncrease = Math.max(maxElevationIncrease, i);
                    else
                        maxElevationDecrease = Math.max(maxElevationDecrease, i + 1);
                    boolean surfaceReached = solid != level.getBlockState(scratchPos).isSolidRender(level, scratchPos);

                    if (surfaceReached) break;
                }
            }
        }

        surfaces.add(new SurfaceToDecorate(
                decorator,
                pos.getX(), pos.getY(), pos.getZ(),
                new SurfaceDecorationContext(
                        visibleToSky,
                        depth,
                        maxElevationIncrease, maxElevationDecrease,
                        heightmapHeight, heightmapGradient)
                )
        );
    }


    private void applySurfaceDecorations(BlockPos.MutableBlockPos pos,
                                        WorldGenLevel level, ChunkAccess chunk, RandomSource random,
                                        NoiseFieldCache noiseFieldCache,
                                         List<SurfaceToDecorate> surfaces, Set<SurfaceDecorator> containedDecorators) {
        for (SurfaceDecorator decorator : containedDecorators)
            decorator.prefillNoiseFields(noiseFieldCache);

        CachedNoiseContext noiseContext = noiseFieldCache.context;
        for (SurfaceToDecorate surface : surfaces) {
            pos.set(surface.x, surface.y, surface.z);
            surface.decorator.decorateSurface(pos, seaLevel, chunk, noiseContext, random, surface.context);
        }
    }

    private record SurfaceToDecorate(SurfaceDecorator decorator, int x, int y, int z, SurfaceDecorationContext context) {}
}
