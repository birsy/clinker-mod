package birsy.clinker.client.render.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.ISkyRenderHandler;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class OthershoreDimensionRenderInfo extends DimensionRenderInfo {
    private final Minecraft mc = Minecraft.getInstance();

    public OthershoreDimensionRenderInfo() {
        super(256.0F, true, DimensionRenderInfo.FogType.NORMAL, false, false);
    }

    public Vector3d func_230494_a_(Vector3d p_230494_1_, float p_230494_2_) {
        return p_230494_1_.mul(p_230494_2_ * 0.94F + 0.06F, p_230494_2_ * 0.94F + 0.06F, p_230494_2_ * 0.91F + 0.09F);
    }

    public boolean func_230493_a_(int p_230493_1_, int p_230493_2_) {
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
