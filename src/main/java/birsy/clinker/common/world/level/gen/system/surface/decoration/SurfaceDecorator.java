package birsy.clinker.common.world.level.gen.system.surface.decoration;

import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class SurfaceDecorator {
    public void initialize() {}
    public void declareDependencies(Consumer<NoiseProvider> consumer) {}
    @Nullable public BlockState getFillBlock(BlockPos pos, NoiseSampler[] samplers) { return null; }
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx);
}
