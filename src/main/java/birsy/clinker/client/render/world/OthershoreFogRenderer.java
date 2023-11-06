package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerWorld;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OthershoreFogRenderer {
    private static final int fogHeight = 48;
    private static final int fogEnd = fogHeight + 128;
    private static final int aquiferFogHeight = -5;
    private static final Minecraft mc = Minecraft.getInstance();

    private static final int baseEmergingTicks = 45;

    private static boolean devMode = true;

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        final Entity player = event.getCamera().getEntity();
        final Level world = player.level();
        final ClientLevel clientLevel = mc.level;

        if (world.dimension() == ClinkerWorld.OTHERSHORE) {
            event.setCanceled(true);
            Vec3 playerVecPos = player.getEyePosition((float) event.getPartialTick());
            BlockPos playerPos = BlockPos.containing(playerVecPos);

            //Make it a little foggier when it's darker....
            final float darknessAmount = (calculateInterpolatedLight(world, playerVecPos, LightLayer.SKY, false) / 16.0F);
            final float deepCaveHeightMultiplier = Mth.clamp(MathUtils.mapRange(-10, 10, 1.0F, 0, (float) playerVecPos.y), 0, 0.5F);
            float multiplier = 1.8F;

            if (event.getType() == FogType.NONE) {
                event.setFogShape(FogShape.CYLINDER);
                float farPlaneDistance = event.getFarPlaneDistance();
                float deepCaveNearFog = (float) Mth.lerp(darknessAmount, 1.0, Mth.lerp(deepCaveHeightMultiplier, 1.0, 3.0));
                float deepCaveFarFog = (float) Mth.lerp(darknessAmount, 1.0, Mth.lerp(deepCaveHeightMultiplier, 1.0, 1.2));
                event.setFarPlaneDistance(multiplier * farPlaneDistance - (darknessAmount * (farPlaneDistance * 0.8F) * deepCaveFarFog));
                event.setNearPlaneDistance(0.0F - (darknessAmount * (farPlaneDistance * 0.05F) * deepCaveNearFog));
            } else if (event.getType() != FogType.LAVA) {
                final float interpolatedAllLight = calculateInterpolatedLight(world, playerVecPos, null, true);
                event.setFogShape(FogShape.SPHERE);
                float iDarknessAmount = interpolatedAllLight;
                //Clinker.LOGGER.info(darknessAmount);
                float farPlaneDistance = event.getFarPlaneDistance();
                event.setFarPlaneDistance(farPlaneDistance - (iDarknessAmount * (farPlaneDistance * 0.6F)));
                event.setNearPlaneDistance(0.0F - (farPlaneDistance * 0.1F) - (iDarknessAmount * (farPlaneDistance * 0.6F)));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFogColors(ViewportEvent.ComputeFogColor event)
    {
        final Entity player = event.getRenderer().getMainCamera().getEntity();
        final Level world = player.level();
        final ClientLevel clientLevel = mc.level;

        if (world.dimension() == ClinkerWorld.OTHERSHORE) {
            float sunsetStrength = Mth.clamp(Mth.cos(clientLevel.getTimeOfDay((float) event.getPartialTick()) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeManager biomemanager = world.getBiomeManager();
            Vec3 vec31 = event.getRenderer().getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
            Vec3 baseColor = CubicSampler.gaussianSampleVec3(vec31, (x, y, z) -> clientLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).get().getFogColor()), sunsetStrength));
            Vec3 waterFogColor = CubicSampler.gaussianSampleVec3(vec31, (x, y, z) -> clientLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).get().getWaterFogColor()), sunsetStrength));

            Vec3 playerVecPos = player.getEyePosition((float) event.getPartialTick());

            final float deepCaveHeightMultiplier = Mth.clamp(MathUtils.mapRange(-10, 10, 1.0F, 0, (float) playerVecPos.y), 0, 0.5F);
            final float interpolatedLight = calculateInterpolatedLight(world, playerVecPos, LightLayer.SKY, true);

            Vec3 seafogColor = new Vec3(0.60F, 0.57F, 0.54F);
            Vec3 darkFogColor = getDarknessFogColors();

            float red = (float) Mth.lerp(deepCaveHeightMultiplier, baseColor.x(), seafogColor.x());
            float green = (float) Mth.lerp(deepCaveHeightMultiplier, baseColor.y(), seafogColor.y());
            float blue = (float) Mth.lerp(deepCaveHeightMultiplier, baseColor.z(), seafogColor.z());

            if (event.getRenderer().getMainCamera().getFluidInCamera() == FogType.NONE) {
                float darker = (float) Mth.clamp(MathUtils.mapRange(64.0F, 200.0F, 0.7F, 1.0F, player.getEyePosition((float) event.getPartialTick()).y), 0.1F, 1.0F);
                event.setRed  ((float) Mth.lerp(interpolatedLight, red * darker, red * darkFogColor.x()));
                event.setGreen((float) Mth.lerp(interpolatedLight, green * darker, green * darkFogColor.y()));
                event.setBlue ((float) Mth.lerp(interpolatedLight, blue * darker, blue * darkFogColor.z()));
            } else if (event.getRenderer().getMainCamera().getFluidInCamera() != FogType.LAVA){
                final float interpolatedAllLight = calculateInterpolatedLight(world, playerVecPos, null, true);
                float fluidDark = MathUtils.map(0.0F, 0.9F, interpolatedAllLight);
                event.setRed  ((float) Mth.lerp(fluidDark, event.getRed(),   waterFogColor.x() * darkFogColor.x()));
                event.setGreen((float) Mth.lerp(fluidDark, event.getGreen(), waterFogColor.y() * darkFogColor.y()));
                event.setBlue ((float) Mth.lerp(fluidDark, event.getBlue(),  waterFogColor.z() * darkFogColor.z()));
                //Clinker.LOGGER.info(interpolatedBlockLight);
            }
        }
    }

    public static Vec3 getDarknessFogColors() {
        float brightness = 1.2f;
        return new Vec3(0.17f * brightness, 0.23f * brightness, 0.14f * brightness);
    }

    public static float calculateInterpolatedLight(Level world, Vec3 playerVecPos, LightLayer lightType) {
        BlockPos pos = new BlockPos(Mth.floor(playerVecPos.x()), Mth.floor(playerVecPos.y()), Mth.floor(playerVecPos.z()));

        float xLerp = (float) Mth.frac(playerVecPos.x());
        float yLerp = (float) Mth.frac(playerVecPos.y());
        float zLerp = (float) Mth.frac(playerVecPos.z());

        float light000 = getLight(world, pos.offset(0, 0, 0), lightType);
        light000 = light000 == -1 ? 0 : light000;
        float light001 = getLight(world, pos.offset(0, 0, 1), lightType);
        light001 = light001 == -1 ? light000 : light001;
        float light010 = getLight(world, pos.offset(0, 1, 0), lightType);
        light010 = light010 == -1 ? light000 : light010;
        float light011 = getLight(world, pos.offset(0, 1, 1), lightType);
        light011 = light011 == -1 ? light000 : light011;
        float light100 = getLight(world, pos.offset(1, 0, 0), lightType);
        light100 = light100 == -1 ? light000 : light100;
        float light101 = getLight(world, pos.offset(1, 0, 1), lightType);
        light101 = light101 == -1 ? light000 : light101;
        float light110 = getLight(world, pos.offset(1, 1, 0), lightType);
        light110 = light110 == -1 ? light000 : light110;
        float light111 = getLight(world, pos.offset(1, 1, 1), lightType);
        light111 = light111 == -1 ? light000 : light111;

        float light00 = Mth.lerp(xLerp, light000, light100);
        float light01 = Mth.lerp(xLerp, light001, light101);
        float light10 = Mth.lerp(xLerp, light010, light110);
        float light11 = Mth.lerp(xLerp, light011, light111);

        float light0 = Mth.lerp(zLerp, light00, light01);
        float light1 = Mth.lerp(zLerp, light10, light11);

        float light = Mth.lerp(yLerp, light0, light1);

        return -1 * (light - 15);
    }

    public static float calculateInterpolatedLight(Level world, Vec3 playerVecPos, LightLayer lightType, boolean returnsNormalized) {
        return calculateInterpolatedLight(world, playerVecPos, lightType) / (returnsNormalized ? 15.0F : 1.0F);
    }

    private static float getLight(Level worldIn, BlockPos posIn, LightLayer lightType) {
        if (worldIn.getBlockState(posIn).canOcclude()) {
            return -1;
        }
        return lightType != null ? worldIn.getBrightness(lightType, posIn) : worldIn.getRawBrightness(posIn, 0);
    }
}