package birsy.clinker.client.render.world.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class VinePlantRenderer {
    public static void renderVine(Vec3 pos1, Vec3 pos2, PoseStack pMatrixStack, MultiBufferSource pBuffer) {
        pMatrixStack.pushPose();

        float x1 = (float)(pos1.x);
        float y1 = (float)(pos1.y);
        float z1 = (float)(pos1.z);
        float ropeWidth = 0.25F;

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = pMatrixStack.last().pose();

        float f4 = Mth.fastInvSqrt(x1 * x1 + z1 * z1) * ropeWidth / 2.0F;
        float f5 = z1 * f4;
        float f6 = x1 * f4;

        BlockPos blockpos = new BlockPos(pos1);
        BlockPos blockpos1 = new BlockPos(pos2);
        int blockLightLevel1 = 15;
        int blockLightLevel2 = 15;
        int skyLightLevel1 = 15;
        int skyLightLevel2 = 15;
        
        for(int ropeSegment = 0; ropeSegment <= 24; ++ropeSegment) {
            addVertexPair(vertexconsumer, matrix4f, x1, y1, z1, blockLightLevel1, blockLightLevel2, skyLightLevel1, skyLightLevel2, ropeWidth, ropeWidth, f5, f6, ropeSegment);
        }

        for(int ropeSegment = 24; ropeSegment >= 0; --ropeSegment) {
            addVertexPair(vertexconsumer, matrix4f, x1, y1, z1, blockLightLevel1, blockLightLevel2, skyLightLevel1, skyLightLevel2, ropeWidth, 0.0F, f5, f6, ropeSegment);
        }

        pMatrixStack.popPose();
    }

    private static void addVertexPair(VertexConsumer vertexconsumer, Matrix4f poseStack, float xIn, float yIn, float zIn, int blockLightLevel1, int blockLightLevel2, int skyLightLevel1, int skyLightLevel2, float p_174317_, float ropeYOffset, float ropeXOffset, float ropeZOffset, int ropeLength) {
        float ropeProgress = (float) ropeLength / 24.0F;

        int blockLight = (int)Mth.lerp(ropeProgress, (float)blockLightLevel1, (float)blockLightLevel2);
        int skyLight = (int)Mth.lerp(ropeProgress, (float)skyLightLevel1, (float)skyLightLevel2);
        int lightColor = LightTexture.pack(blockLight, skyLight);

        float r = 0.5F;
        float g = 0.4F;
        float b = 0.3F;
        
        float x = xIn * ropeProgress;
        float y = yIn > 0.0F ? yIn * ropeProgress * ropeProgress : yIn - yIn * (1.0F - ropeProgress) * (1.0F - ropeProgress);
        float z = zIn * ropeProgress;
        
        vertexconsumer.vertex(poseStack, x - ropeXOffset, y + ropeYOffset, z + ropeZOffset).color(r, g, b, 1.0F).uv2(lightColor).endVertex();
        vertexconsumer.vertex(poseStack, x + ropeXOffset, y + p_174317_ - ropeYOffset, z - ropeZOffset).color(r, g, b, 1.0F).uv2(lightColor).endVertex();
    }

    protected int getBlockLightLevel(Level level, BlockPos pPos) {
        return level.getBrightness(LightLayer.BLOCK, pPos);
    }
}
