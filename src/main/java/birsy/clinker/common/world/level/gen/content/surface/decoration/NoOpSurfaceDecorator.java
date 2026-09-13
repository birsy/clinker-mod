package birsy.clinker.common.world.level.gen.content.surface.decoration;

import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import net.minecraft.core.BlockPos;

public class NoOpSurfaceDecorator extends SurfaceDecorator {
    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, SurfaceDecorationContext ctx) {}
}
