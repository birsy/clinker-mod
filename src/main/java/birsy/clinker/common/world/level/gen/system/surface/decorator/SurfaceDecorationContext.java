package birsy.clinker.common.world.level.gen.system.surface.decorator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class SurfaceDecorationContext {
    private final WorldGenLevel level;
    private final ChunkAccess chunk;
    private final RandomSource random;
    private Direction surfaceDirection;
    private BlockState surfaceState;
    private int maxUpwardsOffset;
    private int maxDownwardsOffset;
    private int maximumDepth;
    private boolean visibleToSky;
    private int surfaceY;

    public SurfaceDecorationContext(WorldGenLevel level, ChunkAccess chunk, RandomSource random) {
        this.level = level;
        this.chunk = chunk;
        this.random = random;
    }

    void updateForSurface(int surfaceY, Direction surfaceDirection, BlockState surfaceState, int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky) {
        this.surfaceY = surfaceY;
        this.surfaceDirection = surfaceDirection;
        this.surfaceState = surfaceState;
        this.maxDownwardsOffset = maxDownwardsOffset;
        this.maxUpwardsOffset = maxUpwardsOffset;
        this.maximumDepth = maximumDepth;
        this.visibleToSky = visibleToSky;
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
}
