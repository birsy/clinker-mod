package birsy.clinker.mixin.client;

import birsy.clinker.core.Clinker;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "net.minecraft.client.gui.Font$StringRenderOutput")
public abstract class FontMixin {
    @Unique
    int clinker$offsetIndex;

    @Inject(method = "accept", at = @At(value = "HEAD"))
    private void clinker$acceptGlyph(int positionInCurrentSequence, Style style, int codePoint, CallbackInfoReturnable<Boolean> cir) {
        clinker$offsetIndex = positionInCurrentSequence;
    }

    @ModifyArgs(method = "accept",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;renderChar(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;ZZFFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"
            )
    )
    private void clinker$acceptGlyph(Args args) {
        float x = args.get(4), y = args.get(5);
        BakedGlyph glyph = args.get(0);
        int salt = glyph.hashCode();
        long random = Mth.getSeed(clinker$offsetIndex, salt, 0);
        float xOffset = (((random >>  0) & 64L) / 64.0F) * 2.0F - 1.0F,
              yOffset = (((random >> 16) & 64L) / 64.0F) * 2.0F - 1.0F;
        args.set(4, (float) (x + xOffset * 0.5F * Clinker.FONT_WIGGLINESS));
        args.set(5, (float) (y + yOffset * Clinker.FONT_WIGGLINESS));
    }
}
