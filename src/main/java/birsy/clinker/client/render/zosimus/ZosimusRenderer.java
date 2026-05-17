package birsy.clinker.client.render.zosimus;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

//@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class ZosimusRenderer {
    public static ZosimusRenderer INSTANCE = new ZosimusRenderer();

    private static final ResourceLocation TEXTURE = Clinker.resource("textures/gui/zosimus.png");
    ZosimusBodyPart root;
    public ZosimusRenderer() {
//        this.root = new ZosimusBodyPart(null, 0, 0, 0);
//        ZosimusBodyPart torso = new ZosimusCloak(this.root, 0, 0, 0);
//        ZosimusBodyPart neck = new ZosimusCollar(torso, 0, -0.9F, 0);
//        ZosimusHead head = new ZosimusHead(neck, 0, -0.25F, 0);
//        ZosimusBodyPart eyes = new ZosimusEyes(head);
    }

//    @SubscribeEvent
//    public static void guiRender(RenderGuiEvent.Post event) {
//        long ticksElapsed = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;
//        double partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
//        INSTANCE.render(event.getGuiGraphics().pose(), ticksElapsed + partialTick, partialTick);
//    }

    public void render(PoseStack pPoseStack, double tickTime, double partialTicks) {
        pPoseStack.pushPose();
        pPoseStack.scale(128, 128, 1);
        pPoseStack.translate(2, 2, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.root.yRot = (float) (tickTime * 0.1F);
        this.root.xRot = 0.0F;
        this.root.zRot = 0.0F;
        //this.root.render(bufferbuilder, pPoseStack);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        pPoseStack.popPose();
    }
}
