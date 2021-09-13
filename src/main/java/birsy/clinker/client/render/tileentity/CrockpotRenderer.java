package birsy.clinker.client.render.tileentity;

import birsy.clinker.client.render.tileentity.model.CrockpotModel;
import birsy.clinker.common.block.CrockpotBlock;
import birsy.clinker.common.tileentity.CrockpotTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class CrockpotRenderer<T extends CrockpotTileEntity> extends TileEntityRenderer<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/crockpot/crockpot.png");
    private final CrockpotModel crockpotModel;

    public CrockpotRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        crockpotModel = new CrockpotModel();
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        //Renders offset by 1/2 a block.
        matrixStackIn.translate(0.5, 0.5, 0.5);
        //It's also upside down for some reason?
        matrixStackIn.rotate(Vector3f.ZP.rotation((float) Math.PI));
        //Fixes it going a block down from the previous translation.
        matrixStackIn.translate(0, -1, 0);

        if (tileEntityIn.canOpen()) {
            Direction direction = tileEntityIn.openDirection;
            if (direction != Direction.UP) {
                crockpotModel.setOpenAmount(tileEntityIn.getOpenAmount(partialTicks));
                if (direction == Direction.EAST) {
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90));
                } else if (direction == Direction.WEST) {
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
                } else if (direction == Direction.NORTH) {
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
                }
            } else {
                crockpotModel.setOpenAmount(0.0F);
                matrixStackIn.translate(0, -0.5 * MathUtils.ease(tileEntityIn.getOpenAmount(partialTicks), MathUtils.EasingType.easeOutBack), 0);
            }
        }

        crockpotModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStackIn.pop();
    }
}
