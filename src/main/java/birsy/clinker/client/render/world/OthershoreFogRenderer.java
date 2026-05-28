package birsy.clinker.client.render.world;

import birsy.clinker.client.ambience.AmbienceHandler;
import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerWorld;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class OthershoreFogRenderer {
    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event) {
        Entity player = event.getCamera().getEntity();
        ClientLevel level = Minecraft.getInstance().level;

        if (level.dimension() != ClinkerWorld.OTHERSHORE) return;
        if (event.getType() != FogType.NONE) return;

        float surfaceFactor = AmbienceHandler.SURFACE_AMBIENCE_HANDLER.getAboveGroundFactor(event.getPartialTick());
        surfaceFactor = Mth.sqrt(surfaceFactor);

        OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
        float stormIntensity = 0;
        if (weatherSystem != null) stormIntensity = OthershoreStormRenderHelper.getStormIntensity(weatherSystem, (float) event.getPartialTick());

        float farPlaneDist = event.getFarPlaneDistance();
        farPlaneDist *= Mth.lerp(surfaceFactor, 0.5F, 1.0F);
        farPlaneDist = Mth.lerp(stormIntensity * surfaceFactor, farPlaneDist, Math.min(farPlaneDist, 48.0F));

        float nearPlaneDist = 0.0F;
        //nearPlaneDist = Mth.lerp(stormIntensity * surfaceFactor, nearPlaneDist, Math.min(nearPlaneDist, -8.0F));

        event.setCanceled(true);
        event.setFogShape(FogShape.SPHERE);
        event.setNearPlaneDistance(nearPlaneDist);
        event.setFarPlaneDistance(farPlaneDist);
    }

    @SubscribeEvent
    public static void renderFogColors(ViewportEvent.ComputeFogColor event) {
        ClientLevel level = Minecraft.getInstance().level;

        if (level.dimension() != ClinkerWorld.OTHERSHORE) return;
        if (event.getRenderer().getMainCamera().getFluidInCamera() != FogType.NONE) return;

        float brightness = (float) Mth.map(Minecraft.getInstance().options.gamma().get(), 0.0F, 0.5F, 0.7F, 1.0F);

        OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
        float stormIntensity = 0;
        if (weatherSystem != null) stormIntensity = OthershoreStormRenderHelper.getStormIntensity(weatherSystem, (float) event.getPartialTick());
        brightness *= 1.0F - (stormIntensity * 0.8F);

        event.setRed(event.getRed() * brightness);
        event.setGreen(event.getGreen() * brightness);
        event.setBlue(event.getBlue() * brightness);
    }
}