package birsy.clinker.client.model.entity;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import net.minecraft.util.Mth;
import net.tslat.smartbrainlib.util.RandomUtil;

public abstract class GnomadSkeleton extends InterpolatedSkeleton {
    boolean maskShaking = false;
    int maskShakeTime = 0, maskShakeDuration = 1;

    protected int getNewMaskShakeDuration() {
        return RandomUtil.randomNumberBetween(1 * 20, 4 * 20);
    }

    protected float maskShakeSpeed() {
        return 0.875F;
    }

    protected abstract void applyMaskShake(float headRotation, float maskRotation);

    @Override
    public void animate(AnimationProperties properties) {
        for (Object value : this.parts.values()) if (value instanceof InterpolatedBone bone) bone.reset();
        float ageInTicks = properties.getNumProperty("ageInTicks");

        if (maskShaking) {
            float normalizedTime = (float) this.maskShakeTime / this.maskShakeDuration;
            float shakeAmount = Mth.clamp(-4.0F*normalizedTime*normalizedTime + 4.0F*normalizedTime, 0, 1);
            shakeAmount *= shakeAmount * shakeAmount * shakeAmount;
            float headShake = Mth.sin(ageInTicks * maskShakeSpeed()) * shakeAmount * 0.1F;
            float faceShake = Mth.sin(0.5F + ageInTicks * maskShakeSpeed()) * shakeAmount  * 0.5F;

            this.applyMaskShake(headShake * Mth.RAD_TO_DEG, faceShake * Mth.RAD_TO_DEG);

            this.maskShakeTime++;
            // if we're out of time, stop shaking
            if (this.maskShakeTime > this.maskShakeDuration) {
                this.maskShaking = false;
                this.maskShakeTime = 0;
            }
        } else if (RandomUtil.oneInNChance(100)) {
            this.maskShaking = true;
            this.maskShakeTime = 0;
            this.maskShakeDuration = getNewMaskShakeDuration();
        }
    }
}
