package birsy.clinker.client.render.world;

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
            float fadeOut = Mth.clampedMap(progress, 0.95F, 1.0F, 1.0F, 0.0F);
            return fadeIn * fadeOut;
        }
        return 0.0F;
    }

    public static float getStormIntensity(OthershoreWeatherSystem weatherSystem, float partialTicks) {
        if (weatherSystem.getWeather() instanceof StormApproachingWeather) {
            return Mth.clampedMap(StormApproachingWeather.getStormApproachProgress(weatherSystem, partialTicks),
                    0.93F, 0.95F, 0.0F, 1.0F);
        } else if (weatherSystem.getWeather() instanceof StormWeather) {
            // todo: fade out!
            return 1.0F;
        }

        return 0.0F;
    }
}
