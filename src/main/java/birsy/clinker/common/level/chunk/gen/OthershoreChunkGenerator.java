package birsy.clinker.common.level.chunk.gen;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class OthershoreChunkGenerator extends ChunkGenerator {
    public static final Codec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.create((codec) -> commonCodec(codec).and(codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> {
        return generator.biomeSource;
    }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((generator) -> {
        return generator.settingsHolder;
    }))).apply(codec, codec.stable(OthershoreChunkGenerator::new)));
    protected final Holder<NoiseGeneratorSettings> settingsHolder;
    protected final NoiseGeneratorSettings settings;

    public OthershoreChunkGenerator(Registry<StructureSet> pStructureSets, BiomeSource pBiomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(pStructureSets, Optional.empty(), pBiomeSource);
        this.settingsHolder = settings;
        this.settings = this.settingsHolder.get();
    }

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public void buildSurface(WorldGenRegion p_224174_, StructureManager p_224175_, RandomState p_224176_, ChunkAccess p_224177_) {
    }

    public int getSpawnHeight(LevelHeightAccessor pLevel) {
        return pLevel.getMaxBuildHeight();
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender densityBlender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                boolean hasSetHeightmap = false;
                for(int y = chunk.getMaxBuildHeight(); y < chunk.getMinBuildHeight(); y--) {
                    BlockState state = getBlockStateAtPos(x, y, z);
                    chunk.setBlockState(pos.set(x, y, z), state, false);
                    Clinker.LOGGER.info(state.getBlock().toString());
                    if (state.canOcclude() && !hasSetHeightmap) {
                        heightmap.update(x, y, z, state);
                        heightmap1.update(x, y, z, state);
                        hasSetHeightmap = true;
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    public BlockState getBlockStateAtPos(int x, int y, int z) {
        return y > 128 ? this.getStone(x, y, z) : this.getAir(x, y, z);
    }

    public BlockState getStone(int x, int y, int z) {
        return settings.defaultBlock();
    }

    public BlockState getAir(int x, int y, int z) {
        return Blocks.AIR.defaultBlockState();
    }

    public int getBaseHeight(int x, int y, Heightmap.Types heightmap, LevelHeightAccessor level, RandomState random) {
        return 128;
    }


    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor chunk, RandomState random) {
        BlockState[] states = new BlockState[chunk.getHeight()];

        int iY = 0;
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            states[iY] = getBlockStateAtPos(x, y, z);
            iY++;
        }

        return new NoiseColumn(chunk.getMinBuildHeight(), states);
    }

    public void addDebugScreenInfo(List<String> p_224179_, RandomState p_224180_, BlockPos p_224181_) {
    }

    public void applyCarvers(WorldGenRegion p_224166_, long p_224167_, RandomState p_224168_, BiomeManager p_224169_, StructureManager p_224170_, ChunkAccess p_224171_, GenerationStep.Carving p_224172_) {
    }

    public void spawnOriginalMobs(WorldGenRegion pLevel) {
    }

    public int getMinY() {
        return -63;
    }

    public int getGenDepth() {
        return 384;
    }

    public int getSeaLevel() {
        return 128;
    }
}
