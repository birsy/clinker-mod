package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.CubicSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OthershoreFogRenderer {
    private static final int fogHeight = 48;
    private static final int fogEnd = fogHeight + 12;
    private static final Minecraft mc = Minecraft.getInstance();


    @SubscribeEvent
    public static void onRenderFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        final Entity player = event.getInfo().getEntity();
        final Level world = player.level;

        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            Vec3 playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            final float heightMultiplier = Mth.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1) * 0.5F;
            final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY) * heightMultiplier;
            final float density = Mth.clamp((interpolatedLight - 3) * 0.25f / 13f, 0.02f, 1.0f);

            //Make it a little foggier when it's darker....
            final float darknessMultiplier = 1 + calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY, true);
            if (event.getInfo().getFluidInCamera().isEmpty()) {
                event.setCanceled(true);
                event.setDensity(density * darknessMultiplier);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event)
    {
        final Entity player = event.getInfo().getEntity();
        final Level world = player.level;
        final ClientLevel clientWorld = mc.level;

        float f12 = Mth.clamp(Mth.cos(clientWorld.getTimeOfDay((float) event.getRenderPartialTicks()) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomemanager = world.getBiomeManager();
        Vec3 vector3d1 = event.getRenderer().getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vec3 baseColor = CubicSampler.gaussianSampleVec3(vector3d1, (x, y, z) -> clientWorld.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getFogColor()), f12));

        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            Vec3 playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            final float heightMultiplier = Mth.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1);
            final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY, true);

            Vector3f seafogColor = new Vector3f(0.60F, 0.57F, 0.54F);
            float brightness = 0.25f;
            Vector3f fogColor = new Vector3f(0.179f * brightness, 0.179f * brightness, 0.167f * brightness);

            event.setRed  ((float) Mth.lerp(interpolatedLight, Mth.lerp(heightMultiplier, baseColor.x(), seafogColor.x()), fogColor.x()));
            event.setGreen((float) Mth.lerp(interpolatedLight, Mth.lerp(heightMultiplier, baseColor.y(), seafogColor.y()), fogColor.y()));
            event.setBlue ((float) Mth.lerp(interpolatedLight, Mth.lerp(heightMultiplier, baseColor.z(), seafogColor.z()), fogColor.z()));
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        final Entity player = event.getInfo().getEntity();
        final Level world = player.level;

        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
        }
    }

    private static float calculateInterpolatedLight(Level world, BlockPos playerPos, Vec3 playerVecPos, LightLayer lightType) {
        float light;
        float xLight;
        float yLight;
        float zLight;

        if (lightType != null) {
            light = getLight(world, playerPos, lightType);

            xLight = getLight(world, playerPos.offset(1, 0, 0), lightType);
            yLight = getLight(world, playerPos.offset(0, 1, 0), lightType);
            zLight = getLight(world, playerPos.offset(0, 0, 1), lightType);
        } else {
            light = world.getMaxLocalRawBrightness(playerPos);

            xLight = world.getMaxLocalRawBrightness(playerPos.offset(1, 0, 0));
            yLight = world.getMaxLocalRawBrightness(playerPos.offset(0, 1, 0));
            zLight = world.getMaxLocalRawBrightness(playerPos.offset(0, 0, 1));
        }

        //Corrects light levels in tight areas. Assumes that solid blocks have the same light level as the block the player is currently in.
        if (world.getBlockState(playerPos.offset(1, 0, 0)).canOcclude()) {
            xLight = light;
        }
        if (world.getBlockState(playerPos.offset(0, 1, 0)).canOcclude()) {
            yLight = light;
        }
        if (world.getBlockState(playerPos.offset(0, 0, 1)).canOcclude()) {
            zLight = light;
        }

        Vec3 interpolationFactor = playerVecPos.subtract(new Vec3(playerPos.getX(), playerPos.getY(), playerPos.getZ()));

        xLight = (float) Mth.lerp(interpolationFactor.x, light, xLight);
        yLight = (float) Mth.lerp(interpolationFactor.y, light, yLight);
        zLight = (float) Mth.lerp(interpolationFactor.z, light, zLight);

        return ((((xLight + zLight) - light) + yLight) - light);
    }

    private static float calculateInterpolatedLight(Level world, BlockPos playerPos, Vec3 playerVecPos, LightLayer lightType, boolean returnsNormalized) {
        if (!returnsNormalized) {
            return calculateInterpolatedLight(world, playerPos, playerVecPos, lightType);
        } else {
            return MathUtils.mapRange(0, 15, 1, 0, calculateInterpolatedLight(world, playerPos, playerVecPos, lightType));
        }
    }

    private static float getLight(Level worldIn, BlockPos posIn, LightLayer lightType) {
        return (float) worldIn.getBrightness(lightType, posIn);
    }
}
