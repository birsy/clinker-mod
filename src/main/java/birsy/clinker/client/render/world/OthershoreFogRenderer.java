package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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

                final float heightMultiplier = MathHelper.clamp(mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1);

                final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos) * heightMultiplier;

                final float density = MathHelper.clamp((interpolatedLight - 3) * 0.25f / 13f, 0.02f, 1.0f);

                if (event.getInfo().getFluidState().isEmpty()) {
                    event.setCanceled(true);
                    event.setDensity(density);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFogColors(EntityViewRenderEvent.FogColors event)
    {
        if (event.getInfo().getRenderViewEntity() instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) event.getInfo().getRenderViewEntity();
            final World world = player.world;
            
            if (world.getDimensionKey() == ClinkerDimensions.OTHERSHORE) {
                Vector3d playerVecPos = player.getEyePosition((float) event.getRenderPartialTicks());
                BlockPos playerPos = new BlockPos(playerVecPos);

                final float heightMultiplier = MathHelper.clamp(mapRange(fogHeight, fogEnd, 1, 0, (float) playerVecPos.y), 0, 1);

                final float interpolatedLight = calculateInterpolatedLight(world, playerPos, playerVecPos) * heightMultiplier;

                final float density = MathHelper.clamp((interpolatedLight - 3) * 0.25f / 13f, 0.02f, 1.0f);

                if (event.getInfo().getFluidState().isEmpty()) {
                    float red = MathHelper.lerp(density, event.getRed(), 0.00f);
                    float green = MathHelper.lerp(density, event.getGreen(), 0.00f);
                    float blue = MathHelper.lerp(density, event.getBlue(), 0.56f);

                    event.setRed(red);
                    event.setGreen(green);
                    event.setBlue(blue);
                }
            }
        }
    }

    private static float calculateInterpolatedLight(World world, BlockPos playerPos, Vector3d playerVecPos) {
        float light = getLight(world, playerPos);

        float xLight = getLight(world, playerPos.add(1, 0, 0));
        float yLight = getLight(world, playerPos.add(0, 1, 0));
        float zLight = getLight(world, playerPos.add(0, 0, 1));

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

    private static float mapRange(float a1, float a2, float b1, float b2, float s) {
        return b1+(((s-a1) * (b2-b1))/(a2-a1));
    }

    private static float getLight(World worldIn, BlockPos posIn) {
        return (float) worldIn.getLightFor(LightType.SKY, posIn);
    }
}
