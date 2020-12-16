package birsy.clinker.client.render.layers;

import birsy.clinker.client.render.model.entity.TorAntModel;
import birsy.clinker.common.entity.passive.TorAntEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TorAntEyesLayer<T extends TorAntEntity, M extends TorAntModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Clinker.MOD_ID, "textures/entity/tor_ant/tor_ant_eyes.png"));

   public TorAntEyesLayer(IEntityRenderer<T, M> rendererIn) {
      super(rendererIn);
   }

   public RenderType getRenderType() {
      return RENDER_TYPE;
   }
}
