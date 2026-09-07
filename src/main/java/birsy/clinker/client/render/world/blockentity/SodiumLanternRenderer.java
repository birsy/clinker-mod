package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.ambience.AmbienceHandler;
import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.client.render.world.OthershoreFogLayerRenderer;
import birsy.clinker.client.render.world.OthershoreStormRenderHelper;
import birsy.clinker.common.block.blockentity.MortarBlockEntity;
import birsy.clinker.common.block.blockentity.SodiumLanternBlockEntity;
import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.print.attribute.standard.Destination;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class SodiumLanternRenderer<T extends SodiumLanternBlockEntity> implements BlockEntityRenderer<T> {
    public SodiumLanternRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        submitPositionForHalationRendering(blockEntity.getBlockPos().getCenter());
    }

    private static VertexBuffer halationVbo;
    private static final List<Vec3> HALATION_POSITIONS = new ArrayList<>();
    private static void submitPositionForHalationRendering(Vec3 pos) {
        HALATION_POSITIONS.add(pos);
    }
    @SubscribeEvent
    static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            if (halationVbo == null) {
                halationVbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
                halationVbo.bind();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.addVertex(0, 0, 0).setUv(0, 0);
                bufferBuilder.addVertex(0, 0, 0).setUv( 1, 0);
                bufferBuilder.addVertex(0, 0, 0).setUv( 1,  1);
                bufferBuilder.addVertex(0, 0, 0).setUv(0,  1);
                MeshData meshData = bufferBuilder.buildOrThrow();
                halationVbo.upload(meshData);
                VertexBuffer.unbind();
            }

            Vec3 cameraPos = event.getCamera().getPosition();
            Quaternionf cameraRotation = event.getCamera().rotation();

            OthershoreWeatherSystem weatherSystem = ClientOthershoreWeatherSystem.get();
            float stormIntensity = 0;
            if (weatherSystem != null) stormIntensity = OthershoreStormRenderHelper.getStormIntensity(cameraPos.y(), weatherSystem, event.getPartialTick().getGameTimeDeltaTicks());
            float surfaceFactor = AmbienceHandler.SURFACE_TRACKER.getAboveGroundFactor(event.getPartialTick().getGameTimeDeltaTicks());
            surfaceFactor = Mth.sqrt(surfaceFactor);
            stormIntensity *= surfaceFactor;

            // sort by distance
            HALATION_POSITIONS.sort(Comparator.<Vec3>comparingDouble(pos -> pos.distanceTo(cameraPos)).reversed());

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            Matrix4f projectionMatrix = event.getProjectionMatrix();

            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.HALATION);
            shader.bind();
            shader.bindSamplers(0);
            shader.setDefaultUniforms(VertexFormat.Mode.QUADS);
            shader.getUniformSafe("Color").setVector(1.00F, 0.65F, 0.20F, stormIntensity * 0.75F + 0.25F);
            shader.getUniformSafe("Radius").setFloat(8.0F);
            shader.getUniformSafe("ScreenResolution").setVector(AdvancedFbo.getMainFramebuffer().getWidth(), AdvancedFbo.getMainFramebuffer().getHeight());
            halationVbo.bind();
            poseStack.mulPose(cameraRotation.invert(new Quaternionf()));
            poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
            for (Vec3 lanternPosition : HALATION_POSITIONS) {
                poseStack.pushPose();
                poseStack.translate(lanternPosition.x(), lanternPosition.y(), lanternPosition.z());
                halationVbo.drawWithShader(poseStack.last().pose(), projectionMatrix, VeilRenderBridge.toShaderInstance(shader));
                poseStack.popPose();
            }
            VertexBuffer.unbind();
            ShaderProgram.unbind();
            poseStack.popPose();
            RenderSystem.defaultBlendFunc();
            HALATION_POSITIONS.clear();
        }
    }
}
