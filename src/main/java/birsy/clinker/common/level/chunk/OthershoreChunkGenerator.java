package birsy.clinker.common.level.chunk;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class OthershoreChunkGenerator extends ChunkGenerator {
    private final long seed;
    protected final Supplier<NoiseGeneratorSettings> settings;
    private final OthershoreNoiseSampler noiseSampler;
    private final BaseStoneSource baseStoneSource;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final int height;


    public OthershoreChunkGenerator(BiomeSource biomeSource, BiomeSource runtimeBiomeSource, Supplier<NoiseGeneratorSettings> settings, long seed) {
        super(biomeSource, runtimeBiomeSource, settings.get().structureSettings(), seed);
        this.seed = seed;
        this.noiseSampler = new OthershoreNoiseSampler((int) this.seed);
        NoiseGeneratorSettings noisegeneratorsettings = settings.get();
        this.settings = settings;
        NoiseSettings noisesettings = noisegeneratorsettings.noiseSettings();
        this.height = noisesettings.height();
        this.defaultBlock = noisegeneratorsettings.getDefaultBlock();
        this.defaultFluid = noisegeneratorsettings.getDefaultFluid();
        this.baseStoneSource = new SingleBaseStoneSource(this.defaultBlock);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return null;
    }

    @Override
    public ChunkGenerator withSeed(long pSeed) {
        return null;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion pLevel, ChunkAccess pChunk) {

    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk) {
        return null;
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel) {
        return null;
    }
}
