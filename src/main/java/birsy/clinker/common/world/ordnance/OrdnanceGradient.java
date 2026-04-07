package birsy.clinker.common.world.ordnance;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public record OrdnanceGradient(
        float startRed, float startGreen, float startBlue,
        float endRed, float endGreen, float endBlue,
        float overlayRed, float overlayGreen, float overlayBlue, float overlayAlpha) {
    public OrdnanceGradient(int start, int end, int overlay) {
        this(FastColor.ARGB32.red(start), FastColor.ARGB32.green(start), FastColor.ARGB32.blue(start),
             FastColor.ARGB32.red(end), FastColor.ARGB32.green(end), FastColor.ARGB32.blue(end),
             FastColor.ARGB32.red(overlay), FastColor.ARGB32.green(overlay), FastColor.ARGB32.blue(overlay),
             FastColor.ARGB32.alpha(overlay)
        );
    }
    public OrdnanceGradient() {
        this(1, 1, 1,
             1, 1, 1,
             1, 1, 1,
             0
        );
    }

    float red(float delta) { return Mth.lerp(delta * overlayAlpha, Mth.lerp(delta, startRed, endRed), overlayRed); }
    float green(float delta) { return Mth.lerp(delta * overlayAlpha, Mth.lerp(delta, startGreen, endGreen), overlayGreen); }
    float blue(float delta) { return Mth.lerp(delta * overlayAlpha, Mth.lerp(delta, startBlue, endBlue), overlayBlue); }

    public int startColor() { return FastColor.ARGB32.colorFromFloat(1.0F, startRed, startGreen, startBlue); }
    public int endColor() { return FastColor.ARGB32.colorFromFloat(1.0F, endRed, endGreen, endBlue); }
    public int overlayColor() { return FastColor.ARGB32.colorFromFloat(overlayAlpha, overlayRed, overlayGreen, overlayBlue); }
}
