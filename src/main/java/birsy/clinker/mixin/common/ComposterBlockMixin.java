package birsy.clinker.mixin.common;

import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
    @Inject(method = "extractProduce",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z")
    )
    private static void clinker$extractProduce(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (level.random.nextInt(32) == 0) {
            Vec3 itemPos = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            ItemEntity saltpetreDrop = new ItemEntity(level, itemPos.x(), itemPos.y(), itemPos.z(), new ItemStack(ClinkerItems.SALTPETRE.get()));
            saltpetreDrop.setDefaultPickUpDelay();
            level.addFreshEntity(saltpetreDrop);
        }
    }
}
