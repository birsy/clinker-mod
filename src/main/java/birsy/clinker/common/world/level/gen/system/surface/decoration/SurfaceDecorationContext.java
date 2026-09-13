package birsy.clinker.common.world.level.gen.system.surface.decoration;

import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class SurfaceDecorationContext {
    final WorldGenLevel level;
    final ChunkAccess chunk;
    final RandomSource random;
    Direction surfaceDirection;
    BlockState surfaceState;
    int maxUpwardsOffset;
    int maxDownwardsOffset;
    int maximumDepth;
    boolean visibleToSky;
    int surfaceY;
    NoiseSampler[] samplers;


    public SurfaceDecorationContext(WorldGenLevel level, ChunkAccess chunk, RandomSource random) {
        this.level = level;
        this.chunk = chunk;
        this.random = random;
    }

    void updateForSurface(int surfaceY, Direction surfaceDirection, BlockState surfaceState, int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky, NoiseSampler[] samplers) {
        this.surfaceY = surfaceY;
        this.surfaceDirection = surfaceDirection;
        this.surfaceState = surfaceState;
        this.maxDownwardsOffset = maxDownwardsOffset;
        this.maxUpwardsOffset = maxUpwardsOffset;
        this.maximumDepth = maximumDepth;
        this.visibleToSky = visibleToSky;
        this.samplers = samplers;
    }

    public BlockState place(BlockPos pos, BlockState state) { return this.chunk.setBlockState(pos, state, false); }

    public boolean outOfRange(BlockPos pos) {
        int yDiff = surfaceY - pos.getY();
        if (surfaceDirection == Direction.DOWN)
             return yDiff < -maximumDepth;
        else return yDiff > maximumDepth;
    }
    public WorldGenLevel level() { return level; }
    public ChunkAccess chunk() { return chunk; }
    public RandomSource random() { return random; }
    public Direction surfaceDirection() { return surfaceDirection; }
    public BlockState surfaceState() { return surfaceState; }
    public int maxUpwardsOffset() { return maxUpwardsOffset; }
    public int maxDownwardsOffset() { return maxDownwardsOffset; }
    public int maximumDepth() { return maximumDepth; }
    public boolean visibleToSky() { return visibleToSky; }
    public int surfaceY() { return surfaceY; }
    public NoiseSampler getSampler(int index) { return samplers[index]; }
}
