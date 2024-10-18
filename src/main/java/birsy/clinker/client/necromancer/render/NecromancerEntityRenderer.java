package birsy.clinker.client.necromancer.render;

import birsy.clinker.client.necromancer.animate.Bone;
import birsy.clinker.client.necromancer.animate.Skeleton;
import birsy.clinker.client.necromancer.animate.SkeletonOwner;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class NecromancerEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/placeholder.png");
    private static final float[] finalBoneTransforms = new float[Skeleton.MAX_BONES * 4 * 4]; // maximum of 2 bones
    public static Skin skin;
    public static Skeleton skeleton;

    public NecromancerEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    // TODO REMOVE THIS
    public static void buildSkinAndSkeletonTemporary() {
        SkinBuilder skinBuilder = new SkinBuilder(64, 32);
        skinBuilder.cube(-8, -8, -8, 16, 16, 16, 0, 0, 1);
        skinBuilder.cube(2, -3, -3, 16, 16, 16, 0, 0, 1);
        skin = skinBuilder.build();

        skeleton = new Skeleton()
                .addRoot(new Bone(0, 0, 0)
                        .addChild(new Bone(0, 16, 0))
                );
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        DebugRenderUtil.renderCube(pPoseStack, pBuffer.getBuffer(RenderType.LINES), 0.5F, 0.5F, 0.5F, 1.0F);
        if (skeleton == null || skin == null) return;

        pPoseStack.scale(1.0F/16.0F,1.0F/16.0F,1.0F/16.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.tickCount + pPartialTick));
        for (Bone root : this.skeleton.roots()) {
            //root.precalculatePoseSpaceMatricies();
        }
        for (int i = 0; i < this.skeleton.boneByIndex().size(); i++) {
            //this.skeleton.boneByIndex().get(i).transformMatrix
            new Matrix4f()
                    .get(this.finalBoneTransforms, i * 4 * 4);
        }
       // Clinker.LOGGER.info(Arrays.toString(this.finalBoneTransforms));


        RenderSystem.setShader(ClinkerShaders::getSkinnedEntityShader);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, TEXTURE);
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();

        ShaderInstance shader = RenderSystem.getShader();
        Matrix3f cameraRot = Axis.YP.rotationDegrees(pEntity.tickCount + pPartialTick).get(new Matrix3f());
        shader.safeGetUniform("NormalMatt").set(cameraRot);
        shader.safeGetUniform("LightmapCoordinates").set(15, 15);
        shader.safeGetUniform("OverlayCoordinates").set(0, OverlayTexture.v(false));
        //shader.safeGetUniform("BoneTransforms").set(this.finalBoneTransforms);
        skin.draw(pPoseStack);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return TEXTURE;
    }
}
