package birsy.clinker.client.render.world;

import birsy.clinker.client.render.world.cloud.UpperLayerCloudRenderer;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.types.StormApproachingWeather;
import birsy.clinker.common.world.level.weather.types.StormWeather;
import net.minecraft.util.Mth;

public class OthershoreStormRenderHelper {
    public static float getNormalizedStormApproachDistance(OthershoreWeatherSystem weatherSystem, float partialTicks) {
        return Mth.map(StormApproachingWeather.getStormApproachProgress(weatherSystem, partialTicks), 0.0F, 0.95F, 1.0F, 0.0F);
    }

    public static float getStormCloudAlpha(OthershoreWeatherSystem weatherSystem, float partialTicks) {
        if (weatherSystem.getWeather() instanceof StormApproachingWeather) {
            float progress = StormApproachingWeather.getStormApproachProgress(weatherSystem, partialTicks);
            float fadeIn = Mth.clampedMap(progress, 0.0F, 0.5F, 0.0F, 1.0F);
            float fadeOut = Mth.clampedMap(progress, 0.93F, 0.96F, 1.0F, 0.0F);
            return fadeIn * fadeOut;
        }
        return 0.0F;
    }

    public static float getStormIntensity(double cameraY, OthershoreWeatherSystem weatherSystem, float partialTicks) {
        float yMultiplier = (float) Mth.smoothstep(Mth.clampedMap(cameraY,
                UpperLayerCloudRenderer.LOWER_CLOUD_HEIGHT,  UpperLayerCloudRenderer.UPPER_CLOUD_HEIGHT, 1.0F, 0.0F));
        if (weatherSystem.getWeather() instanceof StormApproachingWeather) {
            return (float) Mth.smoothstep(Mth.clampedMap(StormApproachingWeather.getStormApproachProgress(weatherSystem, partialTicks),
                    0.923F, 0.94F, 0.0F, 1.0F)) * yMultiplier;
        } else if (weatherSystem.getWeather() instanceof StormWeather storm) {
            return (float) Mth.smoothstep(Mth.clampedMap(storm.getStormProgress(weatherSystem, partialTicks),
                    0.9F, 1.0F, 1.0F, 0.0F)) * yMultiplier;
        }

        return 0.0F;
    }
}
