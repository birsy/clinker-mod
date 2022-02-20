package birsy.clinker.common.blockentity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class LidController {
    private final float openSpeed;
    private final float closeSpeed;

    private boolean isOpen;
    private float openness;
    private float oOpenness;

    public LidController() {
        this(0.1F);
    }

    public LidController(float speed) {
        this(speed, speed);
    }

    public LidController(float openSpeed, float closeSpeed) {
        this.openSpeed = openSpeed;
        this.closeSpeed = closeSpeed;
    }

    public void tickLid() {
        this.oOpenness = this.openness;

        if (!this.isOpen && this.openness > 0.0F) {
            this.openness = Math.max(0, this.openness - closeSpeed);
        } else if (this.isOpen && this.openness < 1.0F) {
            this.openness = Math.min(1, this.openness + openSpeed);
        }
    }

    public float getOpenness(float pPartialTicks) {
        return MathUtils.ease(Mth.lerp(pPartialTicks, this.oOpenness, this.openness), MathUtils.EasingType.easeOutBack);
    }

    public void setOpen(boolean pShouldBeOpen) {
        this.isOpen = pShouldBeOpen;
    }

    public boolean isOpen() {
        return this.isOpen;
    }
}
