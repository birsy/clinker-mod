package birsy.clinker.mixin.common;

import birsy.clinker.core.registry.ClinkerDataAttachments;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static birsy.clinker.common.world.ChestRerollHandler.*;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin {
    // wildcard mixins
    // doesn't work?
    // why not??
//    @Redirect(method="*", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
//    private void clinker$unpackLootTable(RandomizableContainerBlockEntity instance, Player player) {
//        unpackLootTable(instance, player);
//    }


    // have to do this bullshit instead :V
    @Redirect(method="isEmpty", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void clinker$unpackLootTable$isEmptyPre(RandomizableContainerBlockEntity instance, Player player) {
        unpackLootTable(instance, player);
    }
    @Redirect(method="getItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void clinker$unpackLootTable$getItemPre(RandomizableContainerBlockEntity instance, Player player) {
        unpackLootTable(instance, player);
    }
    @Redirect(method="removeItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void clinker$unpackLootTable$removeItemPre(RandomizableContainerBlockEntity instance, Player player) {
        unpackLootTable(instance, player);
    }
    @Redirect(method="removeItemNoUpdate", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void clinker$unpackLootTable$removeItemNoUpdatePre(RandomizableContainerBlockEntity instance, Player player) {
        unpackLootTable(instance, player);
    }
    @Redirect(method="setItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void clinker$unpackLootTable$setItemPre(RandomizableContainerBlockEntity instance, Player player) {
        unpackLootTable(instance, player);
    }
    @Redirect(method="createMenu", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void clinker$unpackLootTable$createMenuPre(RandomizableContainerBlockEntity instance, Player player) {
        unpackLootTable(instance, player);
    }

    @Inject(method = "removeItem", at = @At("TAIL"))
    private void clinker$removeItem(int pIndex, int pCount, CallbackInfoReturnable<ItemStack> cir) {
        RandomizableContainerBlockEntity me = (RandomizableContainerBlockEntity)(Object)this;
        disableRerolls(me);
    }

    @Inject(method = "removeItemNoUpdate", at = @At("TAIL"))
    private void clinker$removeItemNoUpdate(int pIndex, CallbackInfoReturnable<ItemStack> cir) {
        RandomizableContainerBlockEntity me = (RandomizableContainerBlockEntity)(Object)this;
        disableRerolls(me);
    }

    @Inject(method = "setItem", at = @At("TAIL"))
    private void clinker$setItem(int pIndex, ItemStack pStack, CallbackInfo ci) {
        RandomizableContainerBlockEntity me = (RandomizableContainerBlockEntity)(Object)this;
        disableRerolls(me);
    }
}
