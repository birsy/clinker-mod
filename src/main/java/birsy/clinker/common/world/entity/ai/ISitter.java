package birsy.clinker.common.world.entity.ai;

import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public interface ISitter {

    default void updateSitting() {
        this.setSitTicks(Mth.clamp(this.getSitTicks(1.0F) + ((this.isSitting() ? 1.0F : -1.0F) * (0.075F)), 0, 1));
    }
    boolean isSitting();
    void setSitting(boolean sitting);
    float getSitTicks(float partialTicks);
    void setSitTicks(float sitTicks);
}
