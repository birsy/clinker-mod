package birsy.clinker.common.world.level.gen.system.surface.decorator;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;

public class SurfaceDecorationSystem {
    private static final ResourceLocation SURFACE_BUILDER_RANDOM = Clinker.resource("surface_builder_random");

    protected final BlockState defaultBlock;

    final Object2ObjectOpenHashMap<Holder<Biome>, SurfaceDecorator> biomeToDecorator;
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

        for (SurfaceDecorator decorator : new HashSet<>(this.biomeToDecorator.values())) {
            decorator.initialize();
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
        SurfaceDecorationContext surfaceDecorationContext = new SurfaceDecorationContext(level, chunk, noiseFieldCache.context, random);
        for (int x = 0; x < 16; x++) {
            int cX = x + 1, wX = x + minX;

            for (int z = 0; z < 16; z++) {
                int cZ = z + 1, wZ = z + minZ;

                List<BlockSpan> column = spans[cX][cZ];
                int i = 0;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                    adjacencies[i++] = spans[cX + direction.getStepX()][cZ + direction.getStepZ()];

                decorateColumn(pos, wX, wZ, x, z, column, adjacencies, offsetFields, prefilled, level, chunk, noiseFieldCache, surfaceDecorationContext);
            }
        }
    }

    List<BlockSpan>[][] buildSpansForChunk(WorldGenLevel level, ChunkAccess chunk) {
        List<BlockSpan>[][] spans = new List[18][18];

        int minBuildHeight = chunk.getMinBuildHeight();
        ChunkPos chunkPos = chunk.getPos();
        int minBlockX = chunkPos.getMinBlockX() - 1,
                minBlockZ = chunkPos.getMinBlockZ() - 1;

        ChunkAccess[][] neighboringChunks = new ChunkAccess[3][3];
        neighboringChunks[1][1] = chunk;

        for (int localX = 0; localX < 18; localX++) {
            int worldX = localX + minBlockX;
            int chunkOffsetX = 1 + (localX == 0 ? -1 : (localX == 17 ? 1 : 0));

            for (int localZ = 0; localZ < 18; localZ++) {
                int worldZ = localZ + minBlockZ;
                int chunkOffsetZ = 1 + (localZ == 0 ? -1 : (localZ == 17 ? 1 : 0));

                ChunkAccess targetChunk = neighboringChunks[chunkOffsetX][chunkOffsetZ];
                if (targetChunk == null) {
                    targetChunk = level.getChunk(chunkPos.x + (chunkOffsetX - 1), chunkPos.z + (chunkOffsetZ - 1));
                    neighboringChunks[chunkOffsetX][chunkOffsetZ] = targetChunk;
                }

                int startY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, worldX, worldZ);
                spans[localX][localZ] = buildSpansForColumn(targetChunk, worldX, worldZ, startY, minBuildHeight);
            }
        }
        return spans;
    }
    List<BlockSpan> buildSpansForColumn(ChunkAccess chunk, int worldX, int worldZ, int startY, int minBuildHeight) {
        List<BlockSpan> result = new ArrayList<>();
        boolean solid = false;
        BlockState spanTopState = Blocks.VOID_AIR.defaultBlockState();
        int spanTopY = Integer.MAX_VALUE;
        BlockState previousState = Blocks.VOID_AIR.defaultBlockState();
        BlockState airState = Blocks.AIR.defaultBlockState();

        int sx = SectionPos.sectionRelative(worldX),
            sz = SectionPos.sectionRelative(worldZ);

        int y = startY;
        while (y >= minBuildHeight) {
            int sectionIndex = chunk.getSectionIndex(y);
            if (sectionIndex < 0 || sectionIndex >= chunk.getSectionsCount()) {
                if (solid) {
                    result.add(new BlockSpan(previousState, y + 1, spanTopState, spanTopY, true));
                    solid = false;
                    spanTopState = airState;
                    spanTopY = y;
                }
                previousState = airState;
                y--;
                continue;
            }

            LevelChunkSection section = chunk.getSection(sectionIndex);
            int sy = SectionPos.sectionRelative(y);
            int sectionMinY = y - sy;

            if (section.hasOnlyAir()) {
                // whole section is air, can mostly skip
                if (solid) {
                    result.add(new BlockSpan(previousState, y + 1, spanTopState, spanTopY, true));
                    solid = false;
                    spanTopState = airState;
                    spanTopY = y;
                }
                previousState = airState;
                y = sectionMinY - 1;
            } else {
                // section has content
                for (; sy >= 0 && y >= minBuildHeight; sy--, y--) {
                    BlockState currentState = section.getBlockState(sx, sy, sz);
                    boolean nextSolid = currentState.isSolid();
                    if (solid != nextSolid) {
                        result.add(new BlockSpan(previousState, y + 1, spanTopState, spanTopY, solid));
                        solid = nextSolid;
                        spanTopState = currentState;
                        spanTopY = y;
                    }
                    previousState = currentState;
                }
            }
        }
        // finish off the final span
        result.add(new BlockSpan(Blocks.VOID_AIR.defaultBlockState(), minBuildHeight, spanTopState, spanTopY, solid));
        // void span
        result.add(new BlockSpan(Blocks.VOID_AIR.defaultBlockState(), Integer.MIN_VALUE, Blocks.VOID_AIR.defaultBlockState(), minBuildHeight, solid));

        return result;
    }

    void decorateColumn(BlockPos.MutableBlockPos pos, int x, int z, int localX, int localZ,
                        List<BlockSpan> column, List<BlockSpan>[] adjacentColumns, NoiseField[] offsetFields, Set<SurfaceDecorator> prefilledSurfaceDecorators,
                        WorldGenLevel level, ChunkAccess chunk, NoiseFieldCache cache, SurfaceDecorationContext context) {
        double biomeOffsetX = offsetFields[0].retrieve(localX, 0, localZ),
                biomeOffsetZ = offsetFields[1].retrieve(localX, 0, localZ);
        int bX = QuartPos.fromBlock((int) Math.round(x + biomeOffsetX)),
            bZ = QuartPos.fromBlock((int) Math.round(z + biomeOffsetZ));

        // skip the first span, as it is always air
        // the last span, too, is the void
        for (int i = 1; i < column.size() - 1; i++) {
            BlockSpan previousSpan = column.get(i - 1);
            BlockSpan span = column.get(i);
            BlockSpan nextSpan = column.get(i + 1);

            // first, we fill it with the fill block!
            if (span.solid()) {
                int y = span.bottomY();
                pos.set(x, y, z);

                BlockState state;

                int sX = SectionPos.sectionRelative(x), sY = SectionPos.sectionRelative(y), sZ = SectionPos.sectionRelative(z);
                int sectionIndex = chunk.getSectionIndex(y);
                LevelChunkSection section = chunk.getSection(sectionIndex);

                double biomeOffsetY = (biomeOffsetX + biomeOffsetZ) * 0.25F;
                int bY = QuartPos.fromBlock((int) Math.round(y + biomeOffsetY));
                Holder<Biome> biome = level.getNoiseBiome(bX, bY, bZ);
                SurfaceDecorator decorator = this.biomeToDecorator.getOrDefault(biome, null);

                // loop through the spans blocks
                for (; y < span.topY() + 1; y++) {
                    pos.setY(y);

                    // recompute decorator if needed
                    int nextBY = QuartPos.fromBlock((int) Math.round(y + biomeOffsetY));
                    if (nextBY != bY) {
                        bY = nextBY;
                        Holder<Biome> nextBiome = level.getNoiseBiome(bX, bY, bZ);
                        if (nextBiome != biome) {
                            biome = nextBiome;
                            decorator = this.biomeToDecorator.getOrDefault(biome, null);
                        }
                    }
                    if (decorator == null) continue;

                    state = decorator.getFillBlock(pos, biomeOffsetY, level, chunk, context.context(), context.random());
                    if (state == null) continue; // null = default filler block

                    // recompute section pos if needed
                    int nextSectionIndex = chunk.getSectionIndex(y);
                    if (nextSectionIndex != sectionIndex) {
                        sectionIndex = nextSectionIndex;
                        section = chunk.getSection(sectionIndex);
                        sY = 0;
                    }
                    section.setBlockState(sX, sY++, sZ, state);
                }
            }

            int surfaceY = span.topY();
            boolean floor = span.solid();
            if (!floor) surfaceY++;

            Holder<Biome> biome = level.getNoiseBiome(bX, QuartPos.fromBlock(surfaceY), bZ);
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
            BlockState surfaceState = floor ? previousSpan.bottomState() : nextSpan.topState();

            if (!prefilledSurfaceDecorators.contains(decorator)) {
                decorator.prefillNoiseFields(cache);
                prefilledSurfaceDecorators.add(decorator);
            }
            context.updateForSurface(
                    surfaceY, surfaceNormal, surfaceState,
                    maxUpwardsOffset, maxDownwardsOffset, maximumDepth, visibleToSky
            );
            pos.set(x, surfaceY, z);
            decorator.decorateSurface(pos, context);
        }
    }
}
