package birsy.clinker.common.world.level.gen.metachunk.feature;

import birsy.clinker.common.world.level.gen.metachunk.MetaChunk;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public class TestMCFeature extends MetaChunkFeature {
    public static final int RADIUS = 24;
    private int originY;

    public TestMCFeature(int originX, int originZ) {
        super(originX, originZ);
    }

    @Override
    public void plan(MetaChunk metaChunk, RandomSource random) {
        this.originY = 100;
    }

    @Override
    public boolean containedInRange(int xMin, int zMin, int xMax, int zMax) {
        return true;
    }

    @Override
    public void realizeBlocks(ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int cX = 0; cX < 16; cX++) {
            for (int cZ = 0; cZ < 16; cZ++) {
                for (int cY = originY - RADIUS; cY < originY + RADIUS; cY++) {
                    pos.set(cX + chunk.getPos().getMinBlockX(), cY, cZ + chunk.getPos().getMinBlockZ());
                    //chunk.setBlockState(pos, ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), false);
                    double distance = Math.sqrt(
                            pos.distToCenterSqr(this.originX + 0.5, this.originY + 0.5, this.originZ + 0.5)
                    );
                    if (distance < RADIUS) {
                        chunk.setBlockState(pos, ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), false);
                    }
                }
            }
        }
    }
}
