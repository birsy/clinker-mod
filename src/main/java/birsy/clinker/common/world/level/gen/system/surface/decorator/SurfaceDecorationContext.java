package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.Synthesizer;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.Supplier;

public final class SurfaceDecorationContext implements NoiseContext {
    private final WorldGenLevel level;
    private final ChunkAccess chunk;
    private final CachedNoiseContext context;
    private final RandomSource random;
    private Direction surfaceDirection;
    private BlockState surfaceState;
    private int maxUpwardsOffset;
    private int maxDownwardsOffset;
    private int maximumDepth;
    private boolean visibleToSky;
    private int surfaceY;

    public SurfaceDecorationContext(WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random) {
        this.level = level;
        this.chunk = chunk;
        this.context = context;
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
    public double retrieve(BlockPos pos, Synthesizer computer) { return this.retrieve(computer, pos.getX(), pos.getY(), pos.getZ()); }
    public double retrieve(BlockPos pos, Supplier<Synthesizer> computer) { return this.retrieve(computer, pos.getX(), pos.getY(), pos.getZ()); }

    @Override public double retrieve(Synthesizer computer, int x, int y, int z) { return this.context.retrieve(computer, x, y, z); }
    @Override public VoronoiEvaluator getVoronoi(String name) { return this.context.getVoronoi(name); }
    @Override public double sample(String name, double x, double y, double z) { return this.context.sample(name, x, y, z); }
    @Override public double sample(String name, double x, double y) { return this.context.sample(name, x, y); }

    public boolean outOfRange(BlockPos pos) {
        int yDiff = surfaceY - pos.getY();
        if (surfaceDirection == Direction.DOWN)
             return yDiff < -maximumDepth;
        else return yDiff > maximumDepth;
    }

    public WorldGenLevel level() { return level; }
    public ChunkAccess chunk() { return chunk; }
    public CachedNoiseContext context() { return context; }
    public RandomSource random() { return random; }
    public Direction surfaceDirection() { return surfaceDirection; }
    public BlockState surfaceState() { return surfaceState; }
    public int maxUpwardsOffset() { return maxUpwardsOffset; }
    public int maxDownwardsOffset() { return maxDownwardsOffset; }
    public int maximumDepth() { return maximumDepth; }
    public boolean visibleToSky() { return visibleToSky; }
    public int surfaceY() { return surfaceY; }
}
