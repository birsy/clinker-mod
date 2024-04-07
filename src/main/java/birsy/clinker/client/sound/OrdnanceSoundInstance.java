package birsy.clinker.client.sound;

import birsy.clinker.common.world.entity.OldOrdnanceEntity;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OrdnanceSoundInstance extends AbstractTickableSoundInstance {
    private final OldOrdnanceEntity entity;

    public OrdnanceSoundInstance(OldOrdnanceEntity pEntity) {
        super(ClinkerSounds.ORDNANCE_SPARKLE_LOOP.get(),  SoundSource.BLOCKS, RandomSource.create(0));
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
            float factor = ((float)this.entity.getFuseTime() / (float)this.entity.getMaxFuseTime());
            this.pitch = (float) (0.8F + (Math.pow(factor, 16.0F) * 5));
            if (factor > 0.95) {
                this.volume = 0;
                return;
            }
            float dist = (float) Minecraft.getInstance().cameraEntity.position().distanceTo(new Vec3(this.x, this.y, this.z));
            this.volume = (1.0F / (dist * dist)) * 3;
        }
    }
}
