package birsy.clinker.common.ordnance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public record OrdnanceGradient(
        float startRed, float startGreen, float startBlue,
        float endRed, float endGreen, float endBlue,
        float overlayRed, float overlayGreen, float overlayBlue, float overlayAlpha) {

    public static final MapCodec<OrdnanceGradient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.INT.fieldOf("start").forGetter(OrdnanceGradient::startColor),
                            Codec.INT.fieldOf("end").forGetter(OrdnanceGradient::endColor),
                            Codec.INT.fieldOf("overlay").forGetter(OrdnanceGradient::overlayColor),
                            Codec.FLOAT.fieldOf("overlayAlpha").forGetter(OrdnanceGradient::overlayAlpha)
            ).apply(instance, OrdnanceGradient::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OrdnanceGradient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, OrdnanceGradient::startColor,
            ByteBufCodecs.INT, OrdnanceGradient::endColor,
            ByteBufCodecs.INT, OrdnanceGradient::overlayColor,
            ByteBufCodecs.FLOAT, OrdnanceGradient::overlayAlpha,
            OrdnanceGradient::new
    );

    public OrdnanceGradient(int start, int end, int overlay, float overlayAlpha) {
        this(FastColor.ARGB32.red(start), FastColor.ARGB32.green(start), FastColor.ARGB32.blue(start),
             FastColor.ARGB32.red(end), FastColor.ARGB32.green(end), FastColor.ARGB32.blue(end),
             FastColor.ARGB32.red(overlay), FastColor.ARGB32.green(overlay), FastColor.ARGB32.blue(overlay),
             overlayAlpha
        );
    }
    public OrdnanceGradient() {
        this(1, 1, 1,
             1, 1, 1,
             1, 1, 1,
             0
        );
    }

    public float red(float delta) { return Mth.lerp(delta * overlayAlpha, Mth.lerp(delta, startRed, endRed), overlayRed); }
    public float green(float delta) { return Mth.lerp(delta * overlayAlpha, Mth.lerp(delta, startGreen, endGreen), overlayGreen); }
    public float blue(float delta) { return Mth.lerp(delta * overlayAlpha, Mth.lerp(delta, startBlue, endBlue), overlayBlue); }

    public int startColor() { return FastColor.ARGB32.colorFromFloat(1.0F, startRed, startGreen, startBlue); }
    public int endColor() { return FastColor.ARGB32.colorFromFloat(1.0F, endRed, endGreen, endBlue); }
    public int overlayColor() { return FastColor.ARGB32.colorFromFloat(1.0F, overlayRed, overlayGreen, overlayBlue); }
}
