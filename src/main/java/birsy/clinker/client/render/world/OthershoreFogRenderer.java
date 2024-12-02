package birsy.clinker.client.render.world;

import birsy.clinker.client.ambience.AmbienceHandler;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerWorld;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
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
        event.setFarPlaneDistance( event.getFarPlaneDistance() * Mth.lerp(surfaceFactor, 0.25F, 1.0F));
    }

    @SubscribeEvent
    public static void renderFogColors(ViewportEvent.ComputeFogColor event) {
        Entity player = event.getRenderer().getMainCamera().getEntity();
        ClientLevel level = Minecraft.getInstance().level;

        if (level.dimension() != ClinkerWorld.OTHERSHORE) return;
        if (event.getRenderer().getMainCamera().getFluidInCamera() == FogType.LAVA || event.getRenderer().getMainCamera().getFluidInCamera() == FogType.POWDER_SNOW) return;

        BiomeManager biomemanager = level.getBiomeManager();
        Vec3 biomeSamplePos = event.getRenderer().getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vec3 baseColor;
        if (event.getRenderer().getMainCamera().getFluidInCamera() != FogType.WATER) {
            baseColor = CubicSampler.gaussianSampleVec3(biomeSamplePos, (x, y, z) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).value().getFogColor()));
        } else {
            baseColor = CubicSampler.gaussianSampleVec3(biomeSamplePos, (x, y, z) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).value().getWaterFogColor()));
        }

        float baseR = (float) baseColor.x, baseG = (float) baseColor.y, baseB = (float) baseColor.z;
        float caveR = 0.204F, caveG = 0.276F, caveB = 0.168F;
        float surfaceFactor = AmbienceHandler.SURFACE_AMBIENCE_HANDLER.getAboveGroundFactor(event.getPartialTick());
        surfaceFactor = Mth.sqrt(surfaceFactor);
        float deepDark = 0.6F;//Mth.clamp(MathUtils.mapRange(64.0F, 200.0F, 0.7F, 1.0F, (float)player.getEyePosition((float) event.getPartialTick()).y), 0.1F, 1.0F);
        event.setRed  (baseR * Mth.lerp(surfaceFactor, caveR, 1.0F) * deepDark);
        event.setGreen(baseG * Mth.lerp(surfaceFactor, caveG, 1.0F) * deepDark);
        event.setBlue (baseB * Mth.lerp(surfaceFactor, caveB, 1.0F) * deepDark);
    }
}