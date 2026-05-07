package birsy.clinker.common.world.level.gen.system.surface.decorator;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import net.minecraft.core.BlockPos;

public abstract class SurfaceDecorator {
    public abstract void prefillNoiseFields(NoiseFieldCache cache);
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx);
}
