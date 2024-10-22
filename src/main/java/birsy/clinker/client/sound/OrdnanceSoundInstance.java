package birsy.clinker.client.sound;

import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class OrdnanceSoundInstance extends AbstractTickableSoundInstance {
    private final Entity entity;
    private final float maxFuseTime;
    private final Supplier<Float> fuseTimeSupplier;

    public OrdnanceSoundInstance(Entity pEntity, float maxFuseTime, Supplier<Float> fuseTimeSupplier) {
        super(ClinkerSounds.ORDNANCE_SPARKLE_LOOP.get(),  SoundSource.BLOCKS, RandomSource.create(0));
        this.maxFuseTime = maxFuseTime;
        this.fuseTimeSupplier = fuseTimeSupplier;
        this.volume = 1;
        this.pitch = 1;
        this.entity = pEntity;
        this.looping = true;
        this.attenuation = Attenuation.NONE;
        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }

    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    public void tick() {
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
            float factor = (fuseTimeSupplier.get() / maxFuseTime);
            this.pitch = (float) (0.8F + (Math.pow(factor, 16.0F) * 5));
            if (factor > 0.95) {
                this.volume = 0;
                return;
            }
            float dist = (float) Minecraft.getInstance().cameraEntity.position().distanceTo(new Vec3(this.x, this.y, this.z));
            this.volume = (1.0F / (dist * dist)) * 3;
        }
    }

    public void stopPlaying() {
        this.stop();
    }
}
