package birsy.clinker.mixin.common;

import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Debug(export = true)
@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin {
}
