package birsy.clinker.client.audio;

import birsy.clinker.core.registry.world.ClinkerDimensions;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OthershoreWindSound extends TickableSound {
    private final ClientPlayerEntity player;
    private float windSpeed;
    private Vector3d windDirection;

    public OthershoreWindSound(ClientPlayerEntity playerIn) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT);
        this.player = playerIn;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
        this.windSpeed = 5.0F;
    }

    public void tick() {
        if (!this.player.removed && this.player.world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
            Vector3d lookVector = this.player.getLookVec();
            double windDirectionMul = MathUtils.mapRange(0.0F, 1.0F, 0.5F, 1.0F, (float) windDirection.dotProduct(lookVector));
            this.windSpeed = 5.0F;
            this.windSpeed += MathHelper.sin(this.player.worldClient.getGameTime() * 0.125F) * 2;

            this.x = this.player.getPosX();
            this.y = this.player.getPosY();
            this.z = this.player.getPosZ();
        } else {
            this.finishPlaying();
        }
    }
}
