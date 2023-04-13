package birsy.clinker.common.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public interface IVelocityTilt {

    default void updateEntityRotation(Entity entity) {
        float multiplier = getVelocityTiltIntensity();
        float delta = getVelocityTiltSmoothness();
        setPitch(Mth.rotLerp(delta, getPitch(1.0F), (float) (entity.getDeltaMovement().z() * multiplier))); //this.getDeltaMovement().z() * multiplier, Math.toRadians(-45), Math.toRadians(45));
        setRoll(Mth.rotLerp(delta, getRoll(1.0F),  (float) (entity.getDeltaMovement().x() * multiplier * -1.0F))); //Mth.clamp(this.getDeltaMovement().x() * multiplier, Math.toRadians(-45), Math.toRadians(45));
    }
    void setPitch(float pitch);
    void setRoll(float roll);
    float getRoll(float partialTicks);
    float getPitch(float partialTicks);

    default float getVelocityTiltIntensity() {
        return 120.0F;
    }
    default float getVelocityTiltSmoothness() {
        return 0.25F;
    }
}
