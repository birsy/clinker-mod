package birsy.clinker.client.sound;

import birsy.clinker.common.world.block.blockentity.StoveBlockEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StoveSoundInstance extends AbstractTickableSoundInstance {
    private final StoveBlockEntity stove;
    
    public StoveSoundInstance(StoveBlockEntity stove) {
        super(ClinkerSounds.BLOCK_STOVE_LOOP.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.stove = stove;
        this.x = this.stove.getBlockPos().getX() + 0.5;
        this.y = this.stove.getBlockPos().getY() + 0.5;
        this.z = this.stove.getBlockPos().getZ() + 0.5;
        this.relative = true;
        this.looping = true;
    }

    @Override
    public void tick() {
        if (this.stove.isRemoved()) {
            this.stop();
            return;
        }
        this.relative = true;
        this.looping = true;
        float dist = (float) Minecraft.getInstance().cameraEntity.position().distanceTo(new Vec3(this.x, this.y, this.z));
        this.volume = Math.min(1.0F, (1.0F / (dist * dist)) * 3);
    }
}
