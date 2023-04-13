package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.client.render.entity.model.MudScarabModel;
import birsy.clinker.common.world.entity.MudScarabEntity;
import birsy.clinker.common.world.entity.salamander.AbstractSalamanderPartEntity;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class NewSalamanderRenderer extends EntityRenderer<NewSalamanderEntity> {
    private static final ResourceLocation SALAMANDER_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/salamander/salamander_body/salamander_body.png");
    private static final ResourceLocation CIRCLE_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/debug/circle.png");

    public NewSalamanderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(NewSalamanderEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        Vec3 pos = pEntity.getPosition(pPartialTick);
        pPoseStack.translate(-pos.x, -pos.y + 0.5, -pos.z);
        int amount = 0;
        for (NewSalamanderEntity.SalamanderJoint joint : pEntity.joints) {
            float brightness = 1 - (amount / (float)pEntity.joints.size());
            DebugRenderUtil.renderCircle(pPoseStack, pBuffer.getBuffer(RenderType.LINES), 10, (float)joint.radius, joint.position.x, joint.position.y, joint.position.z, joint.isHead ? 1.0F : 0.2F, 0.2F, 0.8F * brightness, 1.0F);
            amount ++;
        }

        boolean color1 = true;
        for (NewSalamanderEntity.SalamanderSegment segment : pEntity.segments) {
            Vec3 pos1 = segment.joint1.position;
            Vec3 pos2 = segment.joint2.position;
            DebugRenderUtil.renderLine(pPoseStack, pBuffer.getBuffer(RenderType.LINES), pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), 1, color1 ? 1 : 0, segment.isHead() ? 1 : 0, 1);
            color1 = !color1;
        }
        pPoseStack.popPose();
    }
    
    @Override
    public ResourceLocation getTextureLocation(NewSalamanderEntity entity) {
        return SALAMANDER_TEXTURE;
    }
}

