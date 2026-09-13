package birsy.clinker.common.world.level.gen.system.surface.decoration;

import birsy.clinker.common.world.level.gen.system.biome.BiomeGenerationInfo;
import birsy.clinker.common.world.level.gen.system.biome.placement.BiomeList;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.core.Clinker;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;

public class SurfaceDecorationSystem {
    private static final ResourceLocation SURFACE_BUILDER_RANDOM = Clinker.resource("surface_builder_random");

    protected final BlockState defaultBlock;

    public SurfaceDecorationSystem(BlockState defaultBlock, BiomeList biomes) {
        this.defaultBlock = defaultBlock;
        for (Holder<Biome> biome : biomes.possibleBiomes()) {
            BiomeGenerationInfo.fromBiome(biome).decorator().initialize();
        }
    }

    public void decorate(WorldGenLevel level, ChunkAccess chunk, RandomState randomState) {
        List<BlockSpan>[][] spans = this.buildSpansForChunk(level, chunk);

        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX(), minZ = chunkPos.getMinBlockZ();
        List<BlockSpan>[] adjacencies = new List[4];
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        PositionalRandomFactory randomFactory = randomState.getOrCreateRandomFactory(SURFACE_BUILDER_RANDOM);
        RandomSource random = randomFactory.at(chunk.getPos().x, 0, chunk.getPos().z);

        SurfaceDecorationContext surfaceDecorationContext = new SurfaceDecorationContext(level, chunk, random);
        NoiseSampler biomeOffsetSampler = FNLNoiseProvider.create("biomeOffset").fromRandom(randomFactory.fromHashOf("biomeOffset"));

        Map<SurfaceDecorator, NoiseSampler[]> decoratorToSamplers = new HashMap<>(4);
        for (int z = 0; z < 16; z++) {
            int cZ = z + 1,
                wZ = z + minZ;
            for (int x = 0; x < 16; x++) {
                int cX = x + 1,
                    wX = x + minX;
                List<BlockSpan> column = spans[cX][cZ];
                int i = 0;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                    adjacencies[i++] = spans[cX + direction.getStepX()][cZ + direction.getStepZ()];

                double biomeOffsetX = biomeOffsetSampler.sample(wX / 4.0, wZ / 4.0) * 5,
                       biomeOffsetY = biomeOffsetSampler.sample(wX / 32.0 + 500, wZ / 32.0) * 5,
                       biomeOffsetZ = biomeOffsetSampler.sample(wX / 4.0, wZ / 4.0 + 500) * 5;
                int qX = QuartPos.fromBlock(minX + (int) Math.round(x + biomeOffsetX)),
                    qZ = QuartPos.fromBlock(minZ + (int) Math.round(z + biomeOffsetZ));
                decorateColumn(pos, wX, wZ, qX, qZ, biomeOffsetY, column, adjacencies, decoratorToSamplers, randomFactory, level, chunk, surfaceDecorationContext);
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

    void decorateColumn(BlockPos.MutableBlockPos pos, int x, int z, int qX, int qZ, double biomeOffsetY,
                        List<BlockSpan> column, List<BlockSpan>[] adjacentColumns, Map<SurfaceDecorator, NoiseSampler[]> decoratorToSamplers,
                        PositionalRandomFactory randomFactory, WorldGenLevel level, ChunkAccess chunk, SurfaceDecorationContext context) {
        // skip the first span, as it is always air
        // the last span, too, is the void
        for (int i = 1; i < column.size() - 1; i++) {
            BlockSpan previousSpan = column.get(i - 1);
            BlockSpan span = column.get(i);
            BlockSpan nextSpan = column.get(i + 1);

            fillSpan(pos, x, z, qX, qZ, biomeOffsetY, span, decoratorToSamplers, randomFactory, level, chunk);
            decorateSpan(pos, x, z, qX, qZ, i == 1,
                    previousSpan, span, nextSpan, adjacentColumns,
                    decoratorToSamplers, randomFactory, level, context
            );
        }
    }

    void fillSpan(BlockPos.MutableBlockPos pos, int x, int z, int qX, int qZ, double biomeOffsetY, BlockSpan span,
                  Map<SurfaceDecorator, NoiseSampler[]> decoratorToSamplers, PositionalRandomFactory randomFactory, WorldGenLevel level, ChunkAccess chunk) {
        // first, we fill it with the fill block!
        if (span.solid()) {
            int y = span.bottomY();
            pos.set(x, y, z);

            BlockState state;

            int sX = SectionPos.sectionRelative(x), sY = SectionPos.sectionRelative(y), sZ = SectionPos.sectionRelative(z);
            int sectionIndex = chunk.getSectionIndex(y);
            LevelChunkSection section = chunk.getSection(sectionIndex);

            int qY = QuartPos.fromBlock((int) Math.round(y + biomeOffsetY));
            Holder<Biome> biome = level.getNoiseBiome(qX, qY, qZ);
            SurfaceDecorator decorator = BiomeGenerationInfo.fromBiome(biome).decorator();

            // loop through the spans blocks
            for (; y < span.topY() + 1; y++) {
                pos.setY(y);

                // recompute decorator if needed
                int nextQY = QuartPos.fromBlock((int) Math.round(y + biomeOffsetY));
                if (nextQY != qY) {
                    qY = nextQY;
                    Holder<Biome> nextBiome = level.getNoiseBiome(qX, qY, qZ);
                    if (nextBiome != biome) {
                        biome = nextBiome;
                        decorator = BiomeGenerationInfo.fromBiome(biome).decorator();
                    }
                }

                state = decorator.getFillBlock(pos, decoratorToSamplers.computeIfAbsent(decorator, key -> fillNoiseSamplers(randomFactory, key)));
                if (state == null) continue; // null = default filler block

                // recompute section pos if needed
                int nextSectionIndex = chunk.getSectionIndex(y);
                if (nextSectionIndex != sectionIndex) {
                    sectionIndex = nextSectionIndex;
                    section = chunk.getSection(sectionIndex);
                    sY = 0;
                }
                section.setBlockState(sX, sY, sZ, state);
                sY++;
            }
        }
    }

    void decorateSpan(BlockPos.MutableBlockPos pos, int x, int z, int qX, int qZ, boolean visibleToSky,
                      BlockSpan previousSpan, BlockSpan span, BlockSpan nextSpan, List<BlockSpan>[] adjacentColumns,
                      Map<SurfaceDecorator, NoiseSampler[]> decoratorToSamplers, PositionalRandomFactory randomFactory, WorldGenLevel level,
                      SurfaceDecorationContext context) {
        int surfaceY = span.topY();
        boolean floor = span.solid();
        if (!floor) surfaceY++;
        // determine biome
        int qY = QuartPos.fromBlock(surfaceY);
        Holder<Biome> biome = level.getNoiseBiome(qX, qY, qZ);
        SurfaceDecorator decorator = BiomeGenerationInfo.fromBiome(biome).decorator();

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

        int maximumDepth = floor ? span.height() : previousSpan.height();
        Direction surfaceNormal = floor ? Direction.DOWN : Direction.UP;
        BlockState surfaceState = floor ? previousSpan.bottomState() : nextSpan.topState();

        context.updateForSurface(
                surfaceY, surfaceNormal, surfaceState,
                maxUpwardsOffset, maxDownwardsOffset, maximumDepth, visibleToSky,
                decoratorToSamplers.computeIfAbsent(decorator, key -> fillNoiseSamplers(randomFactory, key))
        );
        pos.set(x, surfaceY, z);
        decorator.decorateSurface(pos, context);
    }

    static NoiseSampler[] fillNoiseSamplers(PositionalRandomFactory randomFactory, SurfaceDecorator decorator) {
        List<NoiseProvider> noiseProviders = new ArrayList<>();
        decorator.declareDependencies(noiseProviders::add);
        NoiseSampler[] samplers = new NoiseSampler[noiseProviders.size()];
        for (int j = 0; j < noiseProviders.size(); j++) {
            NoiseProvider provider = noiseProviders.get(j);
            samplers[j] = provider.fromRandom(randomFactory.fromHashOf(provider.name()));
        }
        return samplers;
    }
}
