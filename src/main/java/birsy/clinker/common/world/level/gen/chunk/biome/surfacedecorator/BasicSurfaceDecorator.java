package birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator;

import birsy.clinker.common.world.level.gen.NoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.ExecutionException;

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
    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, DerivativeProvider noiseDerivative, NoiseSampler sampler) throws ExecutionException {
        boolean underwater = !chunk.getFluidState(pos.above()).isEmpty();
        chunk.setBlockState(pos, underwater ? underwaterState : topState, false);
        pos.move(Direction.DOWN);

        for (int i = 0; i < Math.min(this.soilDepth, depth); i++) {
            chunk.setBlockState(pos, soilState, false);
            pos.move(Direction.DOWN);
        }
    }
}
