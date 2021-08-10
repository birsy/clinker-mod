package birsy.clinker.common.world.feature.cavebiomehack.cavebiomefeatures;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlobReplacementConfig;

import java.util.Random;

public class AbstractCaveFeature {
    public AbstractCaveFeature() {
    }

    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BlobReplacementConfig config) {
        return true;
    }
}