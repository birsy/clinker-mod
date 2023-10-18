package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.block.blockentity.CounterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.phys.Vec3;

public class CounterRenderer<T extends CounterBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    public CounterRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Workstation station = pBlockEntity.workstation;
        Vec3 blockPos = new Vec3(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
//        if (station != null) {
//
//        }


//        int items = 0;
//        for (PhysicalItem item : pBlockEntity.workstation.getItems()) {
//            pPoseStack.pushPose();
//            float scale = 0.5F;
//            Transform transform = item.getTransform(pPartialTick);
//            Vec3 position = transform.getPosition().subtract(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
//            pPoseStack.translate(position.x, position.y, position.z);
//            pPoseStack.scale(scale, scale, scale);
//            pPoseStack.mulPose(transform.getOrientation().toMojangQuaternion());
//            //pPoseStack.mulPose(Vector3f.YP.rotationDegrees(items * 3312.53829F));
//
//            int lightColor = 15728880;
//            if (pBlockEntity.getLevel() != null) {
//                lightColor = LevelRenderer.getLightColor(pBlockEntity.getLevel(), new BlockPos(item.getCenterOfMass().x(), item.getCenterOfMass().y(), item.getCenterOfMass().z()));
//            }
//
//            itemRenderer.renderStatic(item.asItemStack(), ItemTransforms.TransformType.FIXED, lightColor, pPackedOverlay, pPoseStack, pBufferSource, items + ItemTransforms.TransformType.FIXED.ordinal());
//
//            pPoseStack.popPose();
//            items++;
//        }

       /*for (IBody body : pBlockEntity.workstation.bodies) {
            if (body instanceof ICollidable cBody) {
                if (cBody.getCollisionShape() instanceof MeshCollisionShape shape) {
                    for (Vec3 vertex1 : shape.vertices) {
                        for (Vec3 vertex2 : shape.vertices) {
                            Vec3 v1 = shape.applyTransform(vertex1);
                            Vec3 v2 = shape.applyTransform(vertex2);
                            renderLine(pPoseStack, pBufferSource.getBuffer(RenderType.LINES),
                                    v1.subtract(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ()),
                                    v2.subtract(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ()),
                                    1.0F, 1.0F, 1.0F, 0.1F);
                        }
                    }
                }
                //AABB box = cBody.getCollisionShape().getBounds().move(pBlockEntity.getBlockPos().multiply(-1));
                //LevelRenderer.renderLineBox(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), box, 1.0F, 1.0F, 1.0F, 0.1F);
            }
        }*/
    }

    public static void renderLine(PoseStack pPoseStack, VertexConsumer pConsumer, Vec3 min, Vec3 max, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();
        float minX = (float)min.x;
        float minY = (float)min.y;
        float minZ = (float)min.z;
        float maxX = (float)max.x;
        float maxY = (float)max.y;
        float maxZ = (float)max.z;

        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
