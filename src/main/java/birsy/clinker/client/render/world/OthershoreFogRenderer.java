package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OthershoreFogRenderer {
    private static final int fogHeight = 48;
    private static final int fogEnd = fogHeight + 30;



    @SubscribeEvent
    public static void onRenderFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        if (event.getInfo().getRenderViewEntity() instanceof PlayerEntity)
        {
            final PlayerEntity player = (PlayerEntity) event.getInfo().getRenderViewEntity();
            final World world = player.world;
            
            if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
                Vector3d playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
                BlockPos playerPos = new BlockPos(playerVecPos);

                final float heightMultiplier = MathHelper.clamp(MathUtils.mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1);

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
    }

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event)
    {
        if (event.getInfo().getRenderViewEntity() instanceof PlayerEntity)
        {
            final PlayerEntity player = (PlayerEntity) event.getInfo().getRenderViewEntity();
            final World world = player.world;

            if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
                Vector3d playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
                BlockPos playerPos = new BlockPos(playerVecPos);

                final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos, LightType.SKY, true);

                float brightness = 0.25f;
                Vector3f fogColor = new Vector3f(0.179f * brightness, 0.179f * brightness, 0.167f * brightness);

                event.setRed  (MathHelper.lerp(interpolatedLight, event.getRed(),   fogColor.getX()));
                event.setGreen(MathHelper.lerp(interpolatedLight, event.getGreen(), fogColor.getY()));
                event.setBlue (MathHelper.lerp(interpolatedLight, event.getBlue(),  fogColor.getZ()));
            }
        }
    }

    private static float calculateInterpolatedLight(World world, BlockPos playerPos, Vector3d playerVecPos, LightType lightType) {
        float light;
        float xLight;
        float yLight;
        float zLight;

        if (lightType != null) {
            light = getLight(world, playerPos, lightType);

            xLight = getLight(world, playerPos.add(1, 0, 0), lightType);
            yLight = getLight(world, playerPos.add(0, 1, 0), lightType);
            zLight = getLight(world, playerPos.add(0, 0, 1), lightType);
        } else {
            light = world.getLight(playerPos);

            xLight = world.getLight(playerPos.add(1, 0, 0));
            yLight = world.getLight(playerPos.add(0, 1, 0));
            zLight = world.getLight(playerPos.add(0, 0, 1));
        }

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
        return (float) worldIn.getLightFor(lightType, posIn);
    }
}
