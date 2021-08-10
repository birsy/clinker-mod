package birsy.clinker.common.world.feature.cavebiomehack.cavebiomefeatures;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

import java.util.Random;

public class AbstractCaveFeature {
    public AbstractCaveFeature() {
    }

    public boolean generate(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, ReplaceSphereConfiguration config) {
        return true;
    }
}