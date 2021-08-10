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
public class OthershoreDimensionRenderInfo extends DimensionSpecialEffects {
    private final Minecraft mc = Minecraft.getInstance();

    public OthershoreDimensionRenderInfo() {
        super(256.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
    }

    public Vec3 getBrightnessDependentFogColor(Vec3 p_230494_1_, float p_230494_2_) {
        return p_230494_1_.multiply(p_230494_2_ * 0.94F + 0.06F, p_230494_2_ * 0.94F + 0.06F, p_230494_2_ * 0.91F + 0.09F);
    }

    public boolean isFoggyAt(int p_230493_1_, int p_230493_2_) {
        return false;
    }


    @Nullable
    @Override
    public ISkyRenderHandler getSkyRenderHandler() {
        return new OthershoreSkyRenderer(mc);
    }

    @Nullable
    @Override
    public ICloudRenderHandler getCloudRenderHandler() {
        return new OthershoreCloudRenderer(mc);
    }
}
