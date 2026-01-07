package birsy.clinker.client.render.world;

import birsy.clinker.client.ambience.AmbienceHandler;
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
        event.setCanceled(true);
        event.setFogShape(FogShape.SPHERE);
        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(event.getFarPlaneDistance() * Mth.lerp(surfaceFactor, 0.5F, 1.0F));
    }

    @SubscribeEvent
    public static void renderFogColors(ViewportEvent.ComputeFogColor event) {
        if (event.getRenderer().getMainCamera().getFluidInCamera() == FogType.NONE) {
            float brightness = (float) Mth.lerp(Minecraft.getInstance().options.gamma().get(), 0.5F, 1.0F);
            event.setRed(event.getRed() * brightness);
            event.setGreen(event.getGreen() * brightness);
            event.setBlue(event.getBlue() * brightness);
        }
    }
}