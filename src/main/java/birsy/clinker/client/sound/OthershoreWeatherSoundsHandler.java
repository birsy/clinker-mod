package birsy.clinker.client.sound;

import birsy.clinker.client.ambience.AmbienceHandler;
import birsy.clinker.client.render.world.OthershoreStormRenderHelper;
import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class OthershoreWeatherSoundsHandler {
    private static LoopSoundInstance sound = null;

    // really bad code here
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) { stopSound(); return; }

        OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
        if (weatherSystem == null) { stopSound(); return; }

        boolean shouldBePlaying = false;
        float weatherFactor = OthershoreStormRenderHelper.getStormIntensity(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y(), weatherSystem, 1.0F);
        if (weatherFactor > 0.01) shouldBePlaying = true;

        if (shouldBePlaying && sound == null) {
            sound = new LoopSoundInstance(SoundEvents.ELYTRA_FLYING);
            Minecraft.getInstance().getSoundManager().play(sound);
        } else if (!shouldBePlaying) {
            stopSound(); return;
        }
    }

    private static void stopSound() {
        if (sound == null) return;
        Minecraft.getInstance().getSoundManager().stop(sound);
        sound = null;
    }

    public static class LoopSoundInstance extends AbstractTickableSoundInstance {
        public LoopSoundInstance(SoundEvent soundEvent) {
            super(soundEvent, SoundSource.WEATHER, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0F;
            this.relative = true;
        }

        @Override
        public void tick() {
            float exposure = AmbienceHandler.EXPOSURE_TRACKER.getExposureFactor(1.0F) * 0.5F + 0.5F;
            float undergroundness = AmbienceHandler.SURFACE_TRACKER.getAboveGroundFactor(1.0F);

            OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
            float weatherFactor = 0.0F;
            if (weatherSystem != null) weatherFactor = OthershoreStormRenderHelper.getStormIntensity(
                    Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y(),
                    weatherSystem, 1.0F);

            float curve = exposure * undergroundness * weatherFactor;
            curve *= curve;
            this.volume = curve * 0.5F;
            this.pitch = 0.5F;
        }
    }
}
