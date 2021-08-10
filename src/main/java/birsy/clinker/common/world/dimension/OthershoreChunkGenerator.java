package birsy.clinker.common.world.dimension;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.StructureSettings;

public class OthershoreChunkGenerator extends ChunkGenerator {
    public OthershoreChunkGenerator (BiomeSource biomeSource, StructureSettings dimensionStructuresSettings) {
        super(biomeSource, dimensionStructuresSettings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return null;
    }

    @Override
    public ChunkGenerator withSeed(long p_230349_1_) {
        return null;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, ChunkAccess p_225551_2_) {

    }

    @Override
    public void fillFromNoise(LevelAccessor p_230352_1_, StructureFeatureManager p_230352_2_, ChunkAccess p_230352_3_) {

    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmapType) {
        return 0;
    }

    @Override
    public BlockGetter getBaseColumn(int p_230348_1_, int p_230348_2_) {
        return null;
    }
}
