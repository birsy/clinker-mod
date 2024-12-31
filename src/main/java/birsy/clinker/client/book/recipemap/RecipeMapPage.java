package birsy.clinker.client.book.recipemap;

import birsy.clinker.client.render.Canvas;
import birsy.clinker.client.render.ClinkerFramebuffers;
import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.DebugRenderUtil;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class RecipeMapPage {
    ImmutableValueGraph<MatterNode, RecipeRequirements> nodeGraph;
    float x = 0, y = 0, zoom = 1;
    private Canvas canvas;

    public RecipeMapPage() {
        ImmutableValueGraph.Builder<MatterNode, RecipeRequirements> builder =
                ValueGraphBuilder.directed()
                .allowsSelfLoops(true)
                .immutable();

        MatterNode lead = new MatterNode("Lead", 0, 0);
        MatterNode mercury = new MatterNode("Mercury", 5, 5);
        builder.addNode(lead)
               .addNode(mercury)
               .putEdgeValue(lead, mercury, new RecipeRequirements("Powder", 100.0F));

        this.nodeGraph = builder.build();
    }

    public void draw() {
        if (canvas == null) {
            AdvancedFbo frameBuffer = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(ClinkerFramebuffers.RECIPE_MAP);
            this.canvas = new Canvas(frameBuffer);
        }

        canvas.beginDraw();

        PoseStack poseStack = canvas.poseStack;
        poseStack.pushPose();
        //poseStack.scale(-1, -1, -1);
        VertexConsumer consumer = Canvas.getBuffer(ClinkerRenderTypes.canvasTextured(TextureAtlas.LOCATION_BLOCKS));

        float minX = 0, maxX = minX + 512;
        float minY = 0, maxY = minY + 512;
        PoseStack.Pose pose = poseStack.last();
        consumer.addVertex(pose, minX, minY, 0).setUv(0F, 1F).setColor(1F,1F,1F,1F);
        consumer.addVertex(pose, maxX, minY, 0).setUv(1F, 1F).setColor(1F,1F,1F,1F);
        consumer.addVertex(pose, maxX, maxY, 0).setUv(1F, 0F).setColor(1F,1F,1F,1F);
        consumer.addVertex(pose, minX, maxY, 0).setUv(0F, 0F).setColor(1F,1F,1F,1F);

        poseStack.scale(64, 64, 64);
        Font font = Minecraft.getInstance().font;
        DebugRenderer.renderFloatingText(poseStack, Canvas.BUFFER_SOURCE, "HELLO ASS", 0, 0, 0, 0xFFFFFFFF);
        font.drawInBatch(
                "Ahh, my spoon.", 0.0F, 1.0F, 0xFFFFFFFF,
                false, poseStack.last().pose(), Canvas.BUFFER_SOURCE, Font.DisplayMode.NORMAL,
                0, LightTexture.FULL_BRIGHT
        );
        font.drawInBatch(
                "HELLO ASS", 0.0F, -1.0F, 0xFFFFFFFF,
                false, poseStack.last().pose(), Canvas.BUFFER_SOURCE, Font.DisplayMode.NORMAL,
                0, LightTexture.FULL_BRIGHT
        );
        poseStack.popPose();
        canvas.finishDraw();
    }

    record MatterNode(String name, float x, float y) {}
    public record RecipeRequirements(@Nullable String catalyst, float heat) { }
}
