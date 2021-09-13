package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
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
    public static void onRenderFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        final Entity player = event.getInfo().getRenderViewEntity();
        final World world = player.world;

        if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
            Vector3d playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            final float heightMultiplier = MathHelper.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1) * 0.125F;
            final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightType.SKY) * heightMultiplier;
            final float density = MathHelper.clamp((interpolatedLight - 3) * 0.25f / 13f, 0.02f, 1.0f);

            //Make it a little foggier when it's darker....
            final float darknessMultiplier = 1 + calculateInterpolatedLight(world, playerPos, playerVecPos, LightType.SKY, true);
            if (event.getInfo().getFluidState().isEmpty()) {
                event.setCanceled(true);
                event.setDensity(density * darknessMultiplier);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event)
    {
        final Entity player = event.getInfo().getRenderViewEntity();
        final World world = player.world;
        final ClientWorld clientWorld = mc.world;

        float f12 = MathHelper.clamp(MathHelper.cos(clientWorld.func_242415_f((float) event.getRenderPartialTicks()) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomemanager = world.getBiomeManager();
        Vector3d vector3d1 = event.getRenderer().getActiveRenderInfo().getProjectedView().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vector3d baseColor = CubicSampler.func_240807_a_(vector3d1, (x, y, z) -> clientWorld.getDimensionRenderInfo().func_230494_a_(Vector3d.unpack(biomemanager.getBiomeAtPosition(x, y, z).getFogColor()), f12));
        Vector3d waterFogColor = CubicSampler.func_240807_a_(vector3d1, (x, y, z) -> clientWorld.getDimensionRenderInfo().func_230494_a_(Vector3d.unpack(biomemanager.getBiomeAtPosition(x, y, z).getWaterFogColor()), f12));

        if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
            Vector3d playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
            BlockPos playerPos = new BlockPos(playerVecPos);

            final float heightMultiplier = MathHelper.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1) * 0.25F;
            final float deepCaveHeightMultiplier = MathHelper.clamp(MathUtils.mapRange(15, aquiferFogHeight, 0.125F, 0, (float) playerVecPos.y), 0, 1);
            final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightType.SKY, true);
            final float interpolatedBlockLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightType.BLOCK, true);


            Vector3f seafogColor = new Vector3f(0.60F, 0.57F, 0.54F);
            Vector3f darkFogColor = getDarknessFogColors();

            float red = (float) MathHelper.lerp(deepCaveHeightMultiplier, MathHelper.lerp(heightMultiplier, baseColor.getX(), seafogColor.getX()), waterFogColor.getX());
            float green = (float) MathHelper.lerp(deepCaveHeightMultiplier, MathHelper.lerp(heightMultiplier, baseColor.getY(), seafogColor.getY()), waterFogColor.getY());
            float blue = (float) MathHelper.lerp(deepCaveHeightMultiplier, MathHelper.lerp(heightMultiplier, baseColor.getZ(), seafogColor.getZ()), waterFogColor.getZ());

            if (event.getInfo().getFluidState().isEmpty()) {
                event.setRed  (MathHelper.lerp(interpolatedLight, red, darkFogColor.getX()));
                event.setGreen(MathHelper.lerp(interpolatedLight, green, darkFogColor.getY()));
                event.setBlue (MathHelper.lerp(interpolatedLight, blue, darkFogColor.getZ()));
            } else {
                event.setRed  (MathHelper.lerp(interpolatedBlockLight, event.getRed(),   darkFogColor.getX()));
                event.setGreen(MathHelper.lerp(interpolatedBlockLight, event.getGreen(), darkFogColor.getY()));
                event.setBlue (MathHelper.lerp(interpolatedBlockLight, event.getBlue(),  darkFogColor.getZ()));
            }
        }
    }

    public static Vector3f getDarknessFogColors() {
        float brightness = 0.25f;
        return new Vector3f(0.179f * brightness, 0.179f * brightness, 0.167f * brightness);
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        final Entity player = event.getInfo().getRenderViewEntity();
        final World world = player.world;

        if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
        }
    }

    private static float calculateInterpolatedLight(World world, BlockPos playerPos, Vector3d playerVecPos, LightType lightType) {
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

        Vector3d interpolationFactor = playerVecPos.subtract(new Vector3d(playerPos.getX(), playerPos.getY(), playerPos.getZ()));
        return MathUtils.triLerp(new Vector3f((float) interpolationFactor.getX(), (float) interpolationFactor.getY(), (float) interpolationFactor.getZ()), lightValueArray[0], lightValueArray[1], lightValueArray[2], lightValueArray[3], lightValueArray[4], lightValueArray[5], lightValueArray[6], lightValueArray[7]);
*/

        float light = getLight(world, playerPos, lightType);

        float xLight = getLight(world, playerPos.add(1, 0, 0), lightType);
        float yLight = getLight(world, playerPos.add(0, 1, 0), lightType);
        float zLight = getLight(world, playerPos.add(0, 0, 1), lightType);


        //Corrects light levels in tight areas. Assumes that solid blocks have the same light level as the block the player is currently in.
        if (world.getBlockState(playerPos.add(1, 0, 0)).isSolid()) {
            xLight = light;
        }
        if (world.getBlockState(playerPos.add(0, 1, 0)).isSolid()) {
            yLight = light;
        }
        if (world.getBlockState(playerPos.add(0, 0, 1)).isSolid()) {
            zLight = light;
        }

        Vector3d interpolationFactor = playerVecPos.subtract(new Vector3d(playerPos.getX(), playerPos.getY(), playerPos.getZ()));

        xLight = (float) MathHelper.lerp(interpolationFactor.x, light, xLight);
        yLight = (float) MathHelper.lerp(interpolationFactor.y, light, yLight);
        zLight = (float) MathHelper.lerp(interpolationFactor.z, light, zLight);

        //return (float) MathHelper.lerp(interpolationFactor.z, MathHelper.lerp(interpolationFactor.y, MathHelper.lerp(interpolationFactor.x, light, xLight), yLight), zLight);
        return ((((xLight + zLight) - light) + yLight) - light);

    }

    private static float calculateInterpolatedLight(World world, BlockPos playerPos, Vector3d playerVecPos, LightType lightType, boolean returnsNormalized) {
        if (!returnsNormalized) {
            return calculateInterpolatedLight(world, playerPos, playerVecPos, lightType);
        } else {
            return MathUtils.mapRange(0, 15, 1, 0, calculateInterpolatedLight(world, playerPos, playerVecPos, lightType));
        }
    }

    private static float getLight(World worldIn, BlockPos posIn, LightType lightType) {
        return lightType != null ? worldIn.getLightFor(lightType, posIn) : worldIn.getLight(posIn);
    }
}
