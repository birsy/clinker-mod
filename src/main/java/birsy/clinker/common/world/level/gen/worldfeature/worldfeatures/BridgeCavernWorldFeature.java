package birsy.clinker.common.world.level.gen.worldfeature.worldfeatures;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

public class BridgeCavernWorldFeature extends WorldFeature {
    int x, y, z;

    public BridgeCavernWorldFeature(int depth, int separationRadius) {
        super(depth, separationRadius);
    }

    @Override
    public int getCenterX() {
        return x;
    }

    @Override
    public int getCenterZ() {
        return z;
    }

    @Override
    public boolean within(int minX, int minZ, int maxX, int maxZ) {
        return false;
    }

    @Override
    public boolean plan(LevelAccessor level, MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
        return false;
    }
}
