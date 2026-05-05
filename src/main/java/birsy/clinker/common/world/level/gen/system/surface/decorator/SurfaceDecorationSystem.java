package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.content.surface.decorator.DebugSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;

public class SurfaceDecorationSystem {
    private static final ResourceLocation SURFACE_BUILDER_RANDOM = Clinker.resource("surface_builder_random");

    protected final BlockState defaultBlock;

    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceDecorator> biomeToDecorator;
    final SurfaceDecorator debugDecorator = new DebugSurfaceDecorator();
    public SurfaceDecorationSystem(BlockState defaultBlock, HolderGetter<Biome> biomeGetter) {
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

        List<BlockSpan>[][] spans = this.buildSpansForChunk(level, chunk);

        NoiseField[] offsetFields = {
                noiseFieldCache.fillNoiseField(ClinkerNoiseComputers.SURFACE_DECORATOR_OFFSET_X),
                noiseFieldCache.fillNoiseField(ClinkerNoiseComputers.SURFACE_DECORATOR_OFFSET_Z)
        };
        Set<SurfaceDecorator> prefilled = new HashSet<>(4);

        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(), minZ = chunkPos.getMinBlockZ();
        List<BlockSpan>[] adjacencies = new List[4];
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        RandomSource random = randomState.getOrCreateRandomFactory(SURFACE_BUILDER_RANDOM)
                .at(chunk.getPos().x, 0, chunk.getPos().z);
        for (int x = 0; x < 16; x++) {
            int cX = x + 1, wX = x + minX;

            for (int z = 0; z < 16; z++) {
                int cZ = z + 1, wZ = z + minZ;

                List<BlockSpan> column = spans[cX][cZ];
                int i = 0;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                    adjacencies[i++] = spans[cX + direction.getStepX()][cZ + direction.getStepZ()];

                decorateColumn(pos, wX, wZ, x, z, column, adjacencies, offsetFields, prefilled, chunkPos, noiseFieldCache, level, chunk, random);
            }
        }
    }

    List<BlockSpan>[][] buildSpansForChunk(WorldGenLevel level, ChunkAccess chunk) {
        List<BlockSpan>[][] spans = new List[18][18];

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minBuildHeight = chunk.getMinBuildHeight();
        int minBlockX = chunk.getPos().getMinBlockX() - 1,
            minBlockZ = chunk.getPos().getMinBlockZ() - 1;

        for (int localX = 0; localX < 18; localX++) {
            int worldX = localX + minBlockX;
            for (int localZ = 0; localZ < 18; localZ++) {
                int worldZ = localZ + minBlockZ;
                int startY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, worldX, worldZ);
                spans[localX][localZ] = buildSpansForColumn(
                        level, pos,
                        worldX, worldZ,
                        startY, minBuildHeight
                );
            }
        }
        return spans;
    }
    List<BlockSpan> buildSpansForColumn(WorldGenLevel level, BlockPos.MutableBlockPos pos, int worldX, int worldZ, int startY, int minBuildHeight) {
        List<BlockSpan> result = new ArrayList<>();
        boolean solid = false;
        // maybe should be infinity? from the "upper void" down to the first solid surface.
        int spanTopY = level.getMaxBuildHeight();

        pos.set(worldX, startY, worldZ);
        for (int y = startY; y >= minBuildHeight; y--) {
            pos.setY(y);
            boolean nextSolid = level.getBlockState(pos).isSolid();
            if (solid != nextSolid) {
                result.add(new BlockSpan(y + 1, spanTopY, solid));
                solid = nextSolid;
                spanTopY = y;
            }
        }
        // finish off the final span
        result.add(new BlockSpan(minBuildHeight, spanTopY, solid));
        return result;
    }

    void decorateColumn(BlockPos.MutableBlockPos pos, int x, int z, int localX, int localZ,
                        List<BlockSpan> column, List<BlockSpan>[] adjacentColumns, NoiseField[] offsetFields, Set<SurfaceDecorator> prefilledSurfaceDecorators,
                        ChunkPos chunkPos, NoiseFieldCache cache, WorldGenLevel level, ChunkAccess chunk, RandomSource random) {
        // skip the first span, as it is always air
        for (int i = 1; i < column.size(); i++) {
            BlockSpan previousSpan = column.get(i - 1);
            BlockSpan span = column.get(i);

            int surfaceY = span.topY();
            boolean floor = span.solid();
            if (!floor) surfaceY++;
            // determine biome
            double biomeOffsetX = offsetFields[0].retrieve(localX, 0, localZ),
                   biomeOffsetZ = offsetFields[1].retrieve(localX, 0, localZ);
            int bX = (int) Math.round(x + biomeOffsetX), bY = surfaceY,
                bZ = (int) Math.round(z + biomeOffsetZ);
            Holder<Biome> biome = level.getNoiseBiome(QuartPos.fromBlock(bX), QuartPos.fromBlock(bY), QuartPos.fromBlock(bZ));
            SurfaceDecorator decorator = this.biomeToDecorator.getOrDefault(biome, null);
            if (decorator == null) continue;

            // determine slope
            int maxUpwardsOffset = 0, maxDownwardsOffset = 0;
            for (int j = 0; j < adjacentColumns.length; j++) {
                BlockSpan adjacentSpan = BlockSpan.spanAtY(adjacentColumns[j], surfaceY);
                if (floor) {
                    int sY = surfaceY + 1;
                    if (adjacentSpan.solid()) maxUpwardsOffset = Math.max(maxUpwardsOffset, adjacentSpan.topY() - sY + 1);
                    else maxDownwardsOffset = Math.max(maxDownwardsOffset, sY - adjacentSpan.bottomY());
                } else {
                    int sY = surfaceY - 1;
                    if (adjacentSpan.solid()) maxDownwardsOffset = Math.max(maxDownwardsOffset, sY - adjacentSpan.bottomY());
                    else maxUpwardsOffset = Math.max(maxUpwardsOffset, adjacentSpan.topY() - sY);
                }
            }

            boolean visibleToSky = i == 1;
            int maximumDepth = floor ? span.height() : previousSpan.height();
            Direction surfaceNormal = floor ? Direction.DOWN : Direction.UP;

            if (!prefilledSurfaceDecorators.contains(decorator)) {
                decorator.prefillNoiseFields(cache);
                prefilledSurfaceDecorators.add(decorator);
            }
            pos.set(x, surfaceY, z);
            decorator.decorateSurface(
                    pos, surfaceNormal,
                    maxUpwardsOffset, maxDownwardsOffset, maximumDepth, visibleToSky,
                    level, chunk, cache.context, random
            );
        }
    }
}
