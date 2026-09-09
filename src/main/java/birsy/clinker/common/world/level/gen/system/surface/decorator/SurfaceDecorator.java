package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public abstract class SurfaceDecorator {
    // run at chunk generator initialization. gets around some deferred registration weirdness :P
    public void initialize() {}

    // gets the "fill block" for a biome - e.g, sandstone for a desert, etc. optimized path for mass placement
    @Nullable public BlockState getFillBlock(BlockPos pos, double offsetNoise, WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random) { return null; }

    // decorating the surface with noise field stuffs
    public abstract void prefillNoiseFields(NoiseFieldCache cache);
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx);
}
