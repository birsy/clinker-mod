package birsy.clinker.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ZosimusRenderer {
    final ZosimusBodyPart root;
    final ZosimusCube torso, head, hat;
    final ZosimusTube neck;

    public ZosimusRenderer() {
        this.root = new ZosimusBodyPart(null, 0, 0,0);

        this.torso = new ZosimusCube(root, 0, 0.5F, 0, 1.0F, 0.5F, 0.5F);
        ZosimusCube shoulderL = new ZosimusCube(torso, -1.25F, 0, 0, 0.25F, 0.25F, 0.25F);
        ZosimusCube shoulderR = new ZosimusCube(torso, 1.25F, 0, 0, 0.25F, 0.25F, 0.25F);

        this.neck = new ZosimusTube(torso,
                0, torso.height - 0.25F, 0.0F,
                0, torso.height + 0.5F, 0.0F,
                0.25F);
        this.head = new ZosimusCube(neck, 0, -0.25F, 0, 0.8F, 0.8F, 0.75F);
        this.hat = new ZosimusCube(head, 0, head.height + 0.2F, 0, 0.7F, 0.7F, 0.65F);
    }

    public void render(PoseStack pPoseStack, double tickTime, float partialTicks) {
        pPoseStack.pushPose();
        pPoseStack.scale(32, 32, 32);
        pPoseStack.translate(3, 3, 0);
        this.root.xRot = 0;
        this.root.yRot = (float) (tickTime * 0.1F) % Mth.TWO_PI;
        this.root.render(pPoseStack);
        pPoseStack.popPose();
    }

    private static class ZosimusCube extends ZosimusBodyPart {
        float width, height, depth;

        public ZosimusCube(@Nullable ZosimusBodyPart parent, float x, float y, float z, float width, float height, float depth) {
            super(parent, x, y, z);

            this.width = width;
            this.height = height;
            this.depth = depth;
        }

        @Override
        protected void draw(PoseStack stack) {
            Matrix4f matrix = stack.last().pose();
            float xRotation = this.getTotalXRot();
            float renderWidth = Mth.lerp(Mth.abs(Mth.sin(xRotation)), this.width, this.depth);
            drawSkinBox(matrix,
                    renderWidth * -1.0F, height * -1.0F,
                    renderWidth,  height);
        }
    }

    private static class ZosimusTube extends ZosimusBodyPart {
        float width;
        float x2, y2, z2;
        public ZosimusTube(@Nullable ZosimusRenderer.ZosimusBodyPart parent, float x1, float y1, float z1, float x2, float y2, float z2, float width) {
            super(parent, x1, y1, z1);
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.width = width;
        }

        @Override
        protected void draw(PoseStack stack) {
            Matrix4f matrix = stack.last().pose();
            float tx = this.getTransformedX(this.x, this.y, this.z),
                  ty = this.getTransformedY(this.x, this.y, this.z);
            drawLineSegment(matrix,
                    0, 0,
                    this.getTransformedX(this.x2, this.y2, this.z2) - tx,
                    this.getTransformedY(this.x2, this.y2, this.z2) - ty,
                    this.width);
        }

        @Override
        protected void render(PoseStack stack) {
            stack.pushPose();
            this.transformPoseStack(stack);
//            this.draw(stack);
//            float tx = this.getTransformedX(this.x, this.y, this.z),
//                  ty = this.getTransformedY(this.x, this.y, this.z);
//            stack.translate(this.getTransformedX(this.x2, this.y2, this.z2) - tx,
//                            -(this.getTransformedY(this.x2, this.y2, this.z2) - ty),
//                        0);
            for (ZosimusBodyPart child : this.children) child.render(stack);
            stack.popPose();
        }
    }

    private static class ZosimusBodyPart {
        final ZosimusBodyPart parent;
        float x, y, z;
        float xRot, yRot, tilt;
        protected final List<ZosimusBodyPart> children = new ArrayList<>();
        protected final Quaternionf transformQuat = new Quaternionf();
        protected final Vector3f transformPos = new Vector3f();

        public ZosimusBodyPart(@Nullable ZosimusBodyPart parent, float x, float y, float z) {
            this.parent = parent;
            if (parent != null) this.parent.children.add(this);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getTransformedX(float tx, float ty, float tz) {
            if (parent == null) return tx;

            this.transformPos.set(tx, ty, tz);
            this.parent.setRotationQuaternion().transform(this.transformPos);
            return transformPos.x;
        }

        public float getTransformedY(float tx, float ty, float tz) {
            if (parent == null) return ty;

            this.transformPos.set(tx, ty, tz);
            this.parent.setRotationQuaternion().transform(this.transformPos);
            return transformPos.y;
        }

        public float getTotalXRot() {
            if (parent == null) return this.xRot;
            return this.xRot + parent.getTotalXRot();
        }

        public float getTotalYRot() {
            if (parent == null) return this.yRot;
            return this.yRot + parent.getTotalYRot();
        }

        private Quaternionf setRotationQuaternion() {
            this.transformQuat.set(0, 0, 0, 1)
                    .rotateLocalY(this.getTotalYRot())
                    .rotateLocalX(this.getTotalXRot());
            return this.transformQuat;
        }

        protected void transformPoseStack(PoseStack stack) {
            stack.mulPose(Axis.ZP.rotation(this.tilt));
            stack.translate(this.getTransformedX(this.x, this.y, this.z), -this.getTransformedY(this.x, this.y, this.z), 0);
        }

        protected void render(PoseStack stack) {
            stack.pushPose();
            this.transformPoseStack(stack);
            this.draw(stack);
            for (ZosimusBodyPart child : this.children) child.render(stack);
            stack.popPose();
        }

        protected void draw(PoseStack stack) {

        }
    }

    private static void drawColoredBox(Matrix4f matrix, float x1, float y1, float x2, float y2, float zOffset, float r, float g, float b, float a) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, x1, y2, zOffset).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, zOffset).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, x2, y1, zOffset).color(r, g, b, a).endVertex();
        bufferbuilder.vertex(matrix, x1, y1, zOffset).color(r, g, b, a).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    private static void drawSkinBox(Matrix4f matrix, float x1, float y1, float x2, float y2) {
        drawColoredBox(matrix, x1, y1, x2, y2, 0.0F, 0, 0, 0, 1);
    }

    private static void drawPseudo3DSkinBox(Matrix4f matrix, float x, float y, float width, float depth, float height, float rot) {
        float renderWidth = Mth.abs(Mth.cos(rot) * width) + Mth.abs(Mth.sin(rot) * depth);
        drawSkinBox(matrix,
                renderWidth * -0.5F + x, height * -0.5F + y,
                renderWidth * 0.5F + x,  height * 0.5F + y);
    }

    private static void drawLineSegment(Matrix4f matrix, float x1, float y1, float x2, float y2, float width) {
        float neckX = (x1 - x2), neckY = (y1 - y2);
        float length = Mth.sqrt(neckX*neckX + neckY*neckY);
        float widthOffsetX = (-neckY / length) * width*0.5f, widthOffsetY = (neckX / length) * width*0.5f;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, x1 - widthOffsetX, y1 - widthOffsetY, 0).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, x1 + widthOffsetX, y1 + widthOffsetY, 0).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, x2 + widthOffsetX, y2 + widthOffsetY, 0).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, x2 - widthOffsetX, y2 - widthOffsetY, 0).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
