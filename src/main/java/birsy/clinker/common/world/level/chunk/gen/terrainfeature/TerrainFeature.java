package birsy.clinker.common.world.level.chunk.gen.terrainfeature;

import birsy.clinker.common.world.level.chunk.gen.GenerationRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * For large scale generation features, such as bridge caverns or rivers.
 * Calculated on generation region load.
 */
public abstract class TerrainFeature {
    protected List<AABB> boundingBoxes;

    public TerrainFeature() {
        this.boundingBoxes = new ArrayList<>();
    }

    public void generate(GenerationRegion region) {}
    public double transformDensity(float initialDensity, BlockPos pos) {return initialDensity;}
    public BlockState getBlockState(BlockState initialBlockState, float densitySample, BlockPos pos) {return initialBlockState;}
}
