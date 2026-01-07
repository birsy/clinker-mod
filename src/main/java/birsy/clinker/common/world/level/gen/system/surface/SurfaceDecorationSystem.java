package birsy.clinker.common.world.level.gen.system.surface;

import birsy.clinker.common.world.level.gen.content.surface.DefaultSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Map;
import java.util.Set;

// constructs the surface layer for a chunk
public class SurfaceDecorationSystem {
    private static final ResourceLocation SURFACE_BUILDER_RANDOM = Clinker.resource("surface_builder_random");

    protected final int maxElevationDifference;
    protected final int seaLevel;
    protected final BlockState defaultBlock;

    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceDecorator> biomeToDecorator;
    private static final SurfaceDecorator DEFAULT = new DefaultSurfaceDecorator();

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

    SurfaceDecorator getSurfaceDecorator(Holder<Biome> biome) {
        return biomeToDecorator.getOrDefault(biome, DEFAULT);
    }

    public void applySurfaceDecorations(WorldGenLevel level, ChunkAccess chunk, RandomState randomState,
                                 NoiseFieldCache noiseFieldCache,
                                 NoiseField surfaceHeightField, int minSurfaceHeight, int maxSurfaceHeight,
                                 Set<Holder<Biome>> biomes) {
        prefillNoiseFields(noiseFieldCache, minSurfaceHeight, maxSurfaceHeight, biomes);

        NoiseContext noiseContext = new NoiseContext(noiseFieldCache);
        noiseContext.setRange(minSurfaceHeight, maxSurfaceHeight);

        PositionalRandomFactory surfaceBuilderRandomFactory = randomState.getOrCreateRandomFactory(SURFACE_BUILDER_RANDOM);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(),
                                 surfacePos = new BlockPos.MutableBlockPos(),
                                 scratchPos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                decorateColumn(
                        level, chunk,
                        x, z,
                        surfaceHeightField,
                        noiseContext,
                        surfaceBuilderRandomFactory,
                        pos, surfacePos, scratchPos
                );
            }
        }
    }

    private void prefillNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight, Set<Holder<Biome>> biomes) {
        for (Holder<Biome> biome : biomes) {
            getSurfaceDecorator(biome).prefillNoiseFields(cache, minSurfaceHeight, maxSurfaceHeight);
        }
    }

    private void decorateColumn(
            WorldGenLevel level, ChunkAccess chunk,
            int localX, int localZ,
            NoiseField surfaceHeightField, NoiseContext context, PositionalRandomFactory randomFactory,
            BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos surfacePos, BlockPos.MutableBlockPos scratchPos) {
        int worldX = localX + chunk.getPos().getMinBlockX();
        int worldZ = localZ + chunk.getPos().getMinBlockZ();

        int startY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ);
        int baseSurfaceHeight = (int) surfaceHeightField.retrieve(localX, 0, localZ);

        pos.set(worldX, startY, worldZ);

        boolean visibleToSun = true;

        while (pos.getY() > chunk.getMinBuildHeight() + 1) {
            if (level.getBlockState(pos) == defaultBlock) {
                decorateSurfaceAt(
                        level, chunk,
                        context, randomFactory,
                        baseSurfaceHeight, visibleToSun,
                        pos, surfacePos, scratchPos
                );
                visibleToSun = false;
            }
            pos.move(Direction.DOWN);
        }
    }

    private void decorateSurfaceAt(
            WorldGenLevel level, ChunkAccess chunk,
            NoiseContext context, PositionalRandomFactory randomFactory,
            int baseSurfaceHeight, boolean visibleToSun,
            BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos surfacePos, BlockPos.MutableBlockPos scratchPos) {
        Holder<Biome> biome = chunk.getNoiseBiome(
                QuartPos.fromBlock(pos.getX()),
                QuartPos.fromBlock(pos.getY()),
                QuartPos.fromBlock(pos.getZ())
        );
        SurfaceDecorator decorator = getSurfaceDecorator(biome);

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
        if (decorator.shouldCalculateElevationChange(visibleToSun, pos.getY())) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                scratchPos.set(pos).move(dir);
                boolean solid = level.getBlockState(scratchPos)
                        .isCollisionShapeFullBlock(level, scratchPos);
                Direction step = solid ? Direction.UP : Direction.DOWN;

                for (int i = 0; i <= maxElevationDifference; i++) {
                    scratchPos.move(step);
                    if (scratchPos.getY() < chunk.getMinBuildHeight() + 1) break;
                    if (solid) maxElevationIncrease = Math.max(maxElevationIncrease, i);
                    else maxElevationDecrease = Math.max(maxElevationDecrease, i + 1);
                    boolean topReached = solid != level.getBlockState(scratchPos)
                                    .isCollisionShapeFullBlock(level, scratchPos);
                    if (topReached) break;
                }
            }
        }

        decorator.decorateSurface(
                chunk, surfacePos.set(pos), seaLevel,
                visibleToSun, depth, maxElevationIncrease, maxElevationDecrease, baseSurfaceHeight,
                context, randomFactory.at(pos)
        );

        pos.move(Direction.DOWN, depth - 1);
    }
}
