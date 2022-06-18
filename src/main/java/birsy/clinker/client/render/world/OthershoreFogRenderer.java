package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerWorld;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
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
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event)
    {
        /*final Entity player = event.getCamera().getEntity();
        final Level world = player.level;
        final ClientLevel clientLevel = mc.level;

        float devModeMultiplier = 1.3F;

        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            Vec3 playerVecPos = player.getEyePosition((float) event.getPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            final float heightMultiplier = Mth.clamp(MathUtils.mapRange(world.getMinBuildHeight() + fogHeight, world.getMinBuildHeight() + fogEnd, 1, 0, (float) playerVecPos.y), 0, 1) * 0.125F;
            final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY) * heightMultiplier;
            final float density = Mth.clamp((interpolatedLight - 3) * 0.25f / 13f, 0.02f, 1.0f);

            //Make it a little foggier when it's darker....
            final float minDensity = 0.5F;
            final float darknessMultiplier = (((calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY, false) / -1) + 15) / 15) * (1 - minDensity) + minDensity;
            if (event.getCamera().getFluidInCamera() == FogType.NONE) {
                //event.setCanceled(true);
                //event.set(density * darknessMultiplier * 10000F * devModeMultiplier);
            } else if (event.getCamera().getFluidInCamera() == FogType.WATER) {
                //event.setCanceled(true);
                //event.setDensity(event.getDensity() * 1000F * devModeMultiplier);
            }
        }*/
        final Entity player = event.getCamera().getEntity();
        final Level world = player.level;
        final ClientLevel clientLevel = mc.level;
        if (world.dimension() == ClinkerWorld.OTHERSHORE) {
            float renderDist = (float) mc.options.renderDistance().get() * 16.0F;
            event.setCanceled(true);
            event.setFarPlaneDistance(renderDist - (renderDist * 0.25F));
            event.setNearPlaneDistance(0.0F);
            event.setFogShape(FogShape.SPHERE);
        }
    }

    /*@SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event)
    {
        final Entity player = event.getInfo().getEntity();
        final Level world = player.level;
        final ClientLevel clientLevel = mc.level;

        float sunsetStrength = Mth.clamp(Mth.cos(clientLevel.getTimeOfDay((float) event.getRenderPartialTicks()) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomemanager = world.getBiomeManager();
        Vec3 vec31 = event.getRenderer().getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vec3 baseColor = CubicSampler.gaussianSampleVec3(vec31, (x, y, z) -> clientLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getFogColor()), sunsetStrength));
        Vec3 waterFogColor = CubicSampler.gaussianSampleVec3(vec31, (x, y, z) -> clientLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getWaterFogColor()), sunsetStrength));
        
        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            Vec3 playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            final float heightMultiplier = Mth.clamp(MathUtils.mapRange(world.getMinBuildHeight() + fogHeight, world.getMinBuildHeight() + fogEnd, 1, 0, (float) playerVecPos.y), 0, 1) * 0.25F;
            final float deepCaveHeightMultiplier = Mth.clamp(MathUtils.mapRange(-10, 0, 0.5F, 0, (float) playerVecPos.y), 0, 1);
            final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY, true);
            final float interpolatedBlockLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.BLOCK, true);

            Vec3 seafogColor = new Vec3(0.60F, 0.57F, 0.54F);
            Vec3 darkFogColor = getDarknessFogColors();

            float red = (float) Mth.lerp(deepCaveHeightMultiplier, baseColor.x(), seafogColor.x());
            float green = (float) Mth.lerp(deepCaveHeightMultiplier, baseColor.y(), seafogColor.y());
            float blue = (float) Mth.lerp(deepCaveHeightMultiplier, baseColor.z(), seafogColor.z());


            if (event.getInfo().getFluidInCamera() == FogType.NONE) {
                event.setRed  ((float) Mth.lerp(interpolatedLight, red, red * darkFogColor.x()));
                event.setGreen((float) Mth.lerp(interpolatedLight, green, green * darkFogColor.y()));
                event.setBlue ((float) Mth.lerp(interpolatedLight, blue, blue * darkFogColor.z()));
            } else if (event.getInfo().getFluidInCamera() != FogType.LAVA){
                event.setRed  ((float) Mth.lerp(interpolatedBlockLight, event.getRed(),   event.getRed() * darkFogColor.x()));
                event.setGreen((float) Mth.lerp(interpolatedBlockLight, event.getGreen(), event.getGreen() * darkFogColor.y()));
                event.setBlue ((float) Mth.lerp(interpolatedBlockLight, event.getBlue(),  event.getBlue() * darkFogColor.z()));
            }
        }
    }*/

    public static Vec3 getDarknessFogColors() {
        float brightness = 1.0f;
        return new Vec3(0.14f * brightness, 0.175f * brightness, 0.17f * brightness);
    }

    private static float calculateInterpolatedLight(Level world, BlockPos playerPos, Vec3 playerVecPos, LightLayer lightType) {
/*
        float[] lightValueArray = new float[8];

        int index = 0;
        for (int z = 0; z < 1; z++) {
            for (int y = 0; y < 1; y++) {
                for (int x = 0; x < 1; x++) {
                    float value = !world.getBlockState(playerPos.add(1, 0, 0)).isSolid() ? getLight(world, playerPos.add(x, y, z), lightType) : getLight(world, playerPos, lightType);
                    lightValueArray[index] = value;
                    index++;
                }
            }
        }

        Vec3 interpolationFactor = playerVecPos.subtract(new Vec3(playerPos.x(), playerPos.y(), playerPos.z()));
        return MathUtils.triLerp(new Vector3f((float) interpolationFactor.x(), (float) interpolationFactor.y(), (float) interpolationFactor.z()), lightValueArray[0], lightValueArray[1], lightValueArray[2], lightValueArray[3], lightValueArray[4], lightValueArray[5], lightValueArray[6], lightValueArray[7]);
*/
/*
        float light = getLight(world, playerPos, lightType);

        float xLight = getLight(world, playerPos.offset(1, 0, 0), lightType);
        float yLight = getLight(world, playerPos.offset(0, 1, 0), lightType);
        float zLight = getLight(world, playerPos.offset(0, 0, 1), lightType);


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

        //return (float) Mth.lerp(interpolationFactor.z, Mth.lerp(interpolationFactor.y, Mth.lerp(interpolationFactor.x, light, xLight), yLight), zLight);
        return ((((xLight + zLight) - light) + yLight) - light);
*/
        BlockPos pos0 = new BlockPos(Mth.floor(playerVecPos.x()), Mth.floor(playerVecPos.y()), Mth.floor(playerVecPos.z()));
        BlockPos pos1 = new BlockPos(Mth.ceil(playerVecPos.x()), Mth.ceil(playerVecPos.y()), Mth.ceil(playerVecPos.z()));

        float xLerp = (float) Mth.frac(playerVecPos.x());
        float yLerp = (float) Mth.frac(playerVecPos.y());
        float zLerp = (float) Mth.frac(playerVecPos.z());

        float light000 = getLight(world, pos0, lightType);
        float light001 = getLight(world, new BlockPos(pos0.getX(), pos0.getY(), pos1.getZ()), lightType);
        float light010 = getLight(world, new BlockPos(pos0.getX(), pos1.getY(), pos0.getZ()), lightType);
        float light011 = getLight(world, new BlockPos(pos0.getX(), pos1.getY(), pos1.getZ()), lightType);
        float light100 = getLight(world, new BlockPos(pos1.getX(), pos0.getY(), pos0.getZ()), lightType);
        float light101 = getLight(world, new BlockPos(pos1.getX(), pos0.getY(), pos1.getZ()), lightType);
        float light110 = getLight(world, new BlockPos(pos1.getX(), pos1.getY(), pos0.getZ()), lightType);
        float light111 = getLight(world, pos1, lightType);

        float light00 = Mth.lerp(xLerp, light000, light100);
        float light01 = Mth.lerp(xLerp, light001, light101);
        float light10 = Mth.lerp(xLerp, light010, light110);
        float light11 = Mth.lerp(xLerp, light011, light111);

        float light0 = Mth.lerp(zLerp, light00, light01);
        float light1 = Mth.lerp(zLerp, light10, light11);

        float light = Mth.lerp(yLerp, light0, light1);

        return -1 * (light - 15);
    }

    private static float calculateInterpolatedLight(Level world, BlockPos playerPos, Vec3 playerVecPos, LightLayer lightType, boolean returnsNormalized) {
        return calculateInterpolatedLight(world, playerPos, playerVecPos, lightType) / (returnsNormalized ? 15 : 1);
    }

    private static float getLight(Level worldIn, BlockPos posIn, LightLayer lightType) {
        return lightType != null ? worldIn.getBrightness(lightType, posIn) : worldIn.getRawBrightness(posIn, 0);
    }
}