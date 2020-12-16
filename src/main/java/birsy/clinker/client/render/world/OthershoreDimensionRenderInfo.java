package birsy.clinker.client.render.world;

import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OthershoreDimensionRenderInfo extends DimensionRenderInfo {

    public OthershoreDimensionRenderInfo() {
    	super(150.0F, true, DimensionRenderInfo.FogType.NORMAL, false, false);
    }

    public Vector3d func_230494_a_(Vector3d p_230494_1_, float p_230494_2_) {
       return p_230494_1_;
    }

    public boolean func_230493_a_(int p_230493_1_, int p_230493_2_) {
       return true;
    }   
}
