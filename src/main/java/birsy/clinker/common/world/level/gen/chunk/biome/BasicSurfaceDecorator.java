package birsy.clinker.common.world.level.gen.chunk.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class BasicSurfaceDecorator extends SurfaceDecorator {
    private final BlockState topState, soilState, underwaterState;
    private final int soilDepth;

    public BasicSurfaceDecorator(BlockState topState, BlockState soilState, BlockState underwaterState, int soilDepth) {
        this.topState = topState;
        this.soilState = soilState;
        this.underwaterState = underwaterState;
        this.soilDepth = soilDepth;
    }

    @Override
    public void buildSurface(BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, float noiseDerivative, int maxElevationIncrease, int maxElevationDecrease, ChunkAccess chunk, NoiseGeneratorSettings settings) {
        boolean underwater = !chunk.getFluidState(pos.above()).isEmpty();
        chunk.setBlockState(pos, underwater ? underwaterState : topState, false);
        pos.move(Direction.DOWN);

        for (int i = 0; i < this.soilDepth; i++) {
            if (chunk.getBlockState(pos) == settings.defaultBlock()) {
                chunk.setBlockState(pos, soilState, false);
                pos.move(Direction.DOWN);
            } else {
                return;
            }
        }
    }
}
