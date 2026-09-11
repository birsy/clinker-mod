package birsy.clinker.common.world.level.gen.system.surface.decoration;

import net.minecraft.core.BlockPos;

public abstract class SurfaceDecorator {
    public abstract void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx);
}
