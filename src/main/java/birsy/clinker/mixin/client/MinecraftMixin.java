package birsy.clinker.mixin.client;

import com.mojang.blaze3d.pipeline.MainTarget;
import foundry.veil.deferred.GBuffer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {
//    @Redirect(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V",
//            at = @At(value = "NEW", target = "Lcom/mojang/blaze3d/pipeline/MainTarget;<init>(II)V"))
//    protected MainTarget redirectMainTargetInit(int pWidth, int pHeight) {
//        return new GBuffer(pWidth, pHeight);
//    }
}
