package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
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
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OthershoreFogRenderer {
    private static final int fogHeight = 48;
    private static final int fogEnd = fogHeight + 128;
    private static final int aquiferFogHeight = 25;
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Entity player = event.getInfo().getEntity();
        Level world = player.level;
        ClientLevel clientWorld = mc.level;

        float timeOfDay = Mth.clamp(Mth.cos(clientWorld.getTimeOfDay((float) event.getRenderPartialTicks()) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomemanager = world.getBiomeManager();
        Vec3 vector3d1 = event.getRenderer().getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vec3 baseColor = CubicSampler.gaussianSampleVec3(vector3d1, (x, y, z) -> clientWorld.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getFogColor()), timeOfDay));
        Vec3 waterFogColor = CubicSampler.gaussianSampleVec3(vector3d1, (x, y, z) -> clientWorld.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).getWaterFogColor()), timeOfDay));

        if (world.dimension() == ClinkerDimensions.OTHERSHORE) {
            Vec3 playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            float heightMultiplier = Mth.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1) * 0.25F;
            float deepCaveHeightMultiplier = Mth.clamp(MathUtils.mapRange(15, aquiferFogHeight, 0.125F, 0, (float) playerVecPos.y), 0, 1);
            float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY, true) * 0.75F;
            float interpolatedBlockLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.BLOCK, true) * 0.75F;

            Vector3f seafogColor = new Vector3f(0.60F, 0.57F, 0.54F);
            Vector3f darkFogColor = getDarknessFogColors();

            float red = (float) Mth.lerp(deepCaveHeightMultiplier, Mth.lerp(heightMultiplier, baseColor.x(), seafogColor.x()), waterFogColor.x());
            float green = (float) Mth.lerp(deepCaveHeightMultiplier, Mth.lerp(heightMultiplier, baseColor.y(), seafogColor.y()), waterFogColor.y());
            float blue = (float) Mth.lerp(deepCaveHeightMultiplier, Mth.lerp(heightMultiplier, baseColor.z(), seafogColor.z()), waterFogColor.z());

            if (event.getInfo().getFluidInCamera() == FogType.NONE) {
                RenderSystem.setShaderFogColor(Mth.lerp(interpolatedLight, red, darkFogColor.x()), Mth.lerp(interpolatedLight, green, darkFogColor.y()), Mth.lerp(interpolatedLight, blue, darkFogColor.z()));
                RenderSystem.setShaderFogStart(-8.0F);
                RenderSystem.setShaderFogEnd((RenderSystem.getShaderFogEnd() * 0.75F) - (5 * MathUtils.invert(calculateInterpolatedLight(world, playerPos, playerVecPos, LightLayer.SKY, true)) - (30 * Mth.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1))));
            } else if (event.getInfo().getFluidInCamera() != FogType.LAVA) {
                float[] fogColor = RenderSystem.getShaderFogColor();
                RenderSystem.setShaderFogColor(Mth.lerp(interpolatedBlockLight, fogColor[0], darkFogColor.x()), Mth.lerp(interpolatedBlockLight, fogColor[1], darkFogColor.y()), Mth.lerp(interpolatedBlockLight, fogColor[2], darkFogColor.z()));
            }
        }
    }

    public static Vector3f getDarknessFogColors() {
        float brightness = 0.25f;
        return new Vector3f(0.179f * brightness, 0.179f * brightness, 0.167f * brightness);
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

        Vector3d interpolationFactor = playerVecPos.subtract(new Vector3d(playerPos.x(), playerPos.y(), playerPos.z()));
        return MathUtils.triLerp(new Vector3f((float) interpolationFactor.x(), (float) interpolationFactor.y(), (float) interpolationFactor.z()), lightValueArray[0], lightValueArray[1], lightValueArray[2], lightValueArray[3], lightValueArray[4], lightValueArray[5], lightValueArray[6], lightValueArray[7]);
*/

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

    }

    private static float calculateInterpolatedLight(Level world, BlockPos playerPos, Vec3 playerVecPos, LightLayer lightType, boolean returnsNormalized) {
        if (!returnsNormalized) {
            return calculateInterpolatedLight(world, playerPos, playerVecPos, lightType);
        } else {
            return MathUtils.mapRange(0, 15, 1, 0, calculateInterpolatedLight(world, playerPos, playerVecPos, lightType));
        }
    }

    private static float getLight(Level worldIn, BlockPos posIn, LightLayer lightType) {
        return lightType != null ? worldIn.getBrightness(lightType, posIn) : worldIn.getBrightness(posIn);
    }
}