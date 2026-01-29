package birsy.clinker.mixin.common;

import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.UnaryOperator;

// this sucks sooo bad
@Mixin(ChunkPyramid.class)
public class ChunkPyramidMixin {
    // add a requirement to have all neighboring noise stages generated
    // change the write radius in ChunkStatus.SURFACE from 0 -> 1
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/status/ChunkPyramid$Builder;step(Lnet/minecraft/world/level/chunk/status/ChunkStatus;Ljava/util/function/UnaryOperator;)Lnet/minecraft/world/level/chunk/status/ChunkPyramid$Builder;",
                    ordinal = 6
            ),
            index = 1
    )
    private static UnaryOperator<ChunkStep.Builder> clinker$increaseSurfaceRadius(UnaryOperator<ChunkStep.Builder> task) {
        return builder -> task.apply(builder)
                .addRequirement(ChunkStatus.NOISE, 1)
                .blockStateWriteRadius(1);
    }
}
