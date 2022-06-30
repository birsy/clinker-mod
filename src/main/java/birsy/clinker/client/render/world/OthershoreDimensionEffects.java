package birsy.clinker.client.render.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.ISkyRenderHandler;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class OthershoreDimensionEffects extends DimensionSpecialEffects {
    private final Minecraft mc = Minecraft.getInstance();

    public OthershoreDimensionEffects() {
        super(256.0F, true, SkyType.NORMAL, false, false);
    }

    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return pFogColor.multiply(pBrightness * 0.94F + 0.06F, pBrightness * 0.94F + 0.06F, pBrightness * 0.91F + 0.09F);
    }

    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    //TODO: remake sky renderer
    @Nullable
    @Override
    public ISkyRenderHandler getSkyRenderHandler() {
        return new OthershoreSkyRenderer(mc);
    }
}
