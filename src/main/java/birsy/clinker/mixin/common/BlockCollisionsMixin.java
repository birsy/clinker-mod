package birsy.clinker.mixin.common;

import birsy.clinker.common.entity.system.EntityCollisionCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.BiFunction;

@Mixin(BlockCollisions.class)
public abstract class BlockCollisionsMixin {
    @Unique private Set<Block> clinker$skippedBlocks;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void clinker$initBlockCollisions(CollisionGetter collisionGetter, Entity entity, AABB box, boolean onlySuffocatingBlocks, BiFunction resultProvider, CallbackInfo ci) {
        clinker$skippedBlocks = EntityCollisionCache.INSTANCE.getSkippedBlocks(entity);
    }

    @ModifyExpressionValue(method = "computeNext", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape clinker$overrideBlockCollisions(VoxelShape original, @Local BlockState state) {
        if (!clinker$skippedBlocks.isEmpty() && clinker$skippedBlocks.contains(state.getBlock()))
            return Shapes.empty();
        return original;
    }
}
