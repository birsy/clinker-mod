package birsy.clinker.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Debug(export = true)
@Mixin(targets = "net.minecraft.client.renderer.texture.SpriteContents$InterpolationData")
public abstract class SpriteContents$InterpolationDataMixin {
    @ModifyArg(method = "uploadInterpolatedFrame",
               at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;setPixelRGBA(III)V"),
               index = 2
    )
    int clinker$fixInterpolationAlpha(int color,
                                      @Local(ordinal = 0) double delta,
                                      @Local(ordinal = 9) int color0,
                                      @Local(ordinal = 10) int color1,
                                      @Local(ordinal = 11) int mixedR,
                                      @Local(ordinal = 12) int mixedG,
                                      @Local(ordinal = 13) int mixedB) {
        int a0 = (color0 >>> 24) & 0xFF, a1 = (color1 >>> 24) & 0xFF;
        int mixedA = (int)(delta * (double)a0 + (1.0 - delta) * (double)a1);
        return mixedA << 24 | mixedR << 16 | mixedG << 8 | mixedB;
    }
}
