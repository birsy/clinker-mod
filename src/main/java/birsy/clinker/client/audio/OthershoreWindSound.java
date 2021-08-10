package birsy.clinker.client.audio;

import birsy.clinker.core.registry.world.ClinkerDimensions;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OthershoreWindSound extends AbstractTickableSoundInstance {
    private final LocalPlayer player;
    private float windSpeed;
    private Vec3 windDirection;

    public OthershoreWindSound(LocalPlayer playerIn) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.AMBIENT);
        this.player = playerIn;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
        this.windSpeed = 5.0F;
    }

    public void tick() {
        if (!this.player.removed && this.player.level.dimension() == ClinkerDimensions.OTHERSHORE) {
            Vec3 lookVector = this.player.getLookAngle();
            double windDirectionMul = MathUtils.mapRange(0.0F, 1.0F, 0.5F, 1.0F, (float) windDirection.dot(lookVector));
            this.windSpeed = 5.0F;
            this.windSpeed += Mth.sin(this.player.clientLevel.getGameTime() * 0.125F) * 2;

            this.x = this.player.getX();
            this.y = this.player.getY();
            this.z = this.player.getZ();
        } else {
            this.stop();
        }
    }
}
