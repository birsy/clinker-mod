package birsy.clinker.client.entity;

import birsy.clinker.common.world.entity.mold.MoldCell;
import birsy.clinker.common.world.entity.mold.MoldEntity;
import birsy.clinker.core.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class MoldRenderer extends EntityRenderer<MoldEntity> {
    private final BlockRenderDispatcher dispatcher;
    private static final Quaternionf[] rotations = Util.make(() -> {
        Quaternionf[] array = new Quaternionf[Direction.values().length];
        int i = 0;
        for (Direction value : Direction.values()) {
            array[i++] = value.getRotation();
        }
        return array;
    });

    public MoldRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(MoldEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        int rot = 0;
        float time = pEntity.tickCount + pPartialTick;
        pPoseStack.pushPose();
        boolean first = true;
        for (MoldCell cell : pEntity.parts.values()) {
            int transitionBulbs = 3;
            for (MoldCell child : cell.children) {
                for (int i = 0; i < transitionBulbs; i++) {
                    pPoseStack.pushPose();

                    float factor = ((float)i) / ((float)transitionBulbs);
                    Vec3 position = cell.position(pPartialTick).lerp(child.position(pPartialTick), factor).subtract(pEntity.position());

                    float depth = cell.depth + factor;
                    float depthFactor = depth / pEntity.maxDepth;
                    float scaleMultiplierFactor = (cell.getCurrentEnergy() / (float)pEntity.startingEnergy);
                    if (first) scaleMultiplierFactor = 0;
                    scaleMultiplierFactor *= scaleMultiplierFactor;
                    float scaleMultiplier = (float) Mth.lerp(scaleMultiplierFactor, 1,
                            1 + 0.3F * MathUtil.smoothMinExpo(Mth.sin(time * 0.15F - depth * 4), -0.5, -0.5));

                    float scaleFactor = factor * factor * (3.0F - 2.0F * factor);
                    float partRadius = cell.getRadius(pPartialTick);
                    if (first) partRadius += 0.5 * (Mth.sin(time * 0.05F) + 1) * 0.5F;
                    float radius = Mth.lerp(scaleFactor, partRadius, child.getRadius(pPartialTick)) * scaleMultiplier;

                    pPoseStack.translate(position.x(), position.y(), position.z());
                    pPoseStack.scale(radius, radius, radius);
                    pPoseStack.mulPose(rotations[rot++ % rotations.length]);
                    pPoseStack.translate(-0.5, -0.5, -0.5);

                    dispatcher.renderSingleBlock(Blocks.CRIMSON_HYPHAE.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pEntity, 0.0F), ModelData.EMPTY, RenderType.entityCutout(this.getTextureLocation(pEntity)));
                    pPoseStack.popPose();
                }
            }
            first = false;
        }
        pPoseStack.popPose();
//        VertexConsumer lines = pBuffer.getBuffer(RenderType.LINES);
//        for (MoldCell cell : pEntity.parts) {
//            float b1 = 1 - (cell.depth / ((float)pEntity.maxDepth));
//            Vec3 cellPos = cell.position(pPartialTick).subtract(pEntity.getPosition(pPartialTick));
//            DebugRenderUtil.renderSphere(pPoseStack, lines, 16, cell.getRadius(pPartialTick) * 0.5F, cellPos.x(), cellPos.y(), cellPos.z(), b1, b1, b1, 1);
//
//            for (MoldCell child : cell.children) {
//                float b2 = 1 - (child.depth / ((float)pEntity.maxDepth));
//
//                Vec3 childPos = child.position(pPartialTick).subtract(pEntity.getPosition(pPartialTick));
//
//                DebugRenderUtil.renderLine(pPoseStack, lines, cellPos, childPos, b1, b1, b1, 1, b2, b2, b2, 1);
//            }
//
//            Vec3 attachmentPoint = new Vec3(cellPos.x() + Mth.clamp(cell.attachmentPoint.vector.x() * 30, -0.5, 0.5),
//                    cellPos.y() + Mth.clamp(cell.attachmentPoint.vector.y() * 30, -0.5, 0.5),
//                    cellPos.z() + Mth.clamp(cell.attachmentPoint.vector.z() * 30, -0.5, 0.5));
//            DebugRenderUtil.renderLine(pPoseStack, lines, cellPos, attachmentPoint, cell.attachmentPoint.isInner ? 0 : 1, cell.attachmentPoint.isEdge ? 1 : 0, cell.attachmentPoint.isInner ? 1 : 0, 1);
//            DebugRenderUtil.renderSphere(pPoseStack, lines, 8, 0.1F, attachmentPoint.x(), attachmentPoint.y(), attachmentPoint.z(), cell.attachmentPoint.isInner ? 0 : 1, cell.attachmentPoint.isEdge ? 1 : 0, cell.attachmentPoint.isInner ? 1 : 0, 1);
//        }

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MoldEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
