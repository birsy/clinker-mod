package birsy.clinker.common.world.level.gen.system.surface.decorator;

import net.minecraft.core.BlockPos;

public abstract class SurfaceDecorator {
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx);
}
