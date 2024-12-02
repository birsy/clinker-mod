package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ZosimusRenderer {
    private static final ResourceLocation TEXTURE = Clinker.resource("textures/gui/zosimus.png");
    ZosimusBodyPart root;
    public ZosimusRenderer() {
        this.root = new ZosimusBodyPart(null, 0, 0, 0);
        ZosimusBodyPart torso = new ZosimusCloak(this.root, 0, 0, 0);
        ZosimusBodyPart neck = new ZosimusCollar(torso, 0, -0.9F, 0);
        ZosimusHead head = new ZosimusHead(neck, 0, -0.25F, 0);
        ZosimusBodyPart eyes = new ZosimusEyes(head);
    }

    public void render(PoseStack pPoseStack, double tickTime, float partialTicks) {
        pPoseStack.pushPose();
        pPoseStack.scale(128, 128, 1);
        pPoseStack.translate(2, 2, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.root.yRot = (float) (tickTime * 0.1F);//Mth.cos((float) (tickTime * 0.08F));
        this.root.xRot = 0.5F;//Mth.sin((float) (tickTime * 0.1F)) * 0.5F;
        this.root.tilt = 0.0F;
        this.root.render(bufferbuilder, pPoseStack);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        pPoseStack.popPose();
    }

    private static class ZosimusEyes extends ZosimusBodyPart {
        public ZosimusEyes(@Nullable ZosimusHead parent) {
            super(parent, 0, 0, 0);
        }

        ZosimusHead getParent() {
            return (ZosimusHead) this.parent;
        }

        @Override
        protected void transformPoseStack(PoseStack stack) {
            super.transformPoseStack(stack);
        }

        @Override
        protected void draw(BufferBuilder bufferbuilder, PoseStack stack) {
            super.draw(bufferbuilder, stack);
            float drawX = this.x;
            float drawY = this.y - getParent().getHeadHeight()*0.5F;

            float drawYRot = Mth.wrapDegrees(this.getTotalYRot() * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
            float xOffset = drawYRot;
            drawX += xOffset * getParent().getHeadWidth()*0.5F;

            float drawXRot = Mth.wrapDegrees(this.getTotalXRot() * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
            float yOffset = drawXRot;
            float factor = drawYRot / Mth.HALF_PI;
            drawY += Mth.lerp(factor * factor, yOffset, 0.0F) * getParent().getHeadHeight()*0.5F;

            Matrix4f matrix = stack.last().pose();
            drawEye(matrix, bufferbuilder, false, false, drawX - 0.15F, drawY);
            drawEye(matrix, bufferbuilder, true, false, drawX + 0.15F, drawY);

            drawEye(matrix, bufferbuilder, false, true, drawX - 0.15F, drawY);
            drawEye(matrix, bufferbuilder, true, true, drawX + 0.15F, drawY);
        }

        private void drawEye(Matrix4f matrix, BufferBuilder bufferbuilder, boolean mirrored, boolean pupil, float eyeX, float eyeY) {
            ZosimusHead head = this.getParent();
            float headWidth = head.getHeadWidth() * 0.5F;
            float headHeight = head.getHeadHeight();

            float eyeWidth = 0.17F, eyeHeight = 0.17F * 0.48F;
            float x1 = -eyeWidth * 0.5F + eyeX, y1 = -eyeHeight * 0.5F + eyeY;
            float x2 =  eyeWidth * 0.5F + eyeX, y2 =  eyeHeight * 0.5F + eyeY;

            x1 = Mth.clamp(x1, -headWidth, headWidth);
            y1 = Mth.clamp(y1, -headHeight, 0);
            x2 = Mth.clamp(x2, -headWidth, headWidth);
            y2 = Mth.clamp(y2, -headHeight, 0);

            float pupilVOffset = pupil ? -(25.0F / 128.0F) : 0;
            float u1 = 28.0F/128.0F, v1 = 25.0F/128.0F + pupilVOffset,
                  u2 = 72.0F/128.0F, v2 = 50.0F/128.0F + pupilVOffset;
            if (mirrored) {
                float temp = u1;
                u1 = u2;
                u2 = temp;
            }
            bufferbuilder.addVertex(matrix, x1, y2, 1)
                    .setColor(181, 138, 88, 255).setUv(u1, v2);
            bufferbuilder.addVertex(matrix, x2, y2, 1)
                    .setColor(181, 138, 88, 255).setUv(u2, v2);
            bufferbuilder.addVertex(matrix, x2, y1, 1)
                    .setColor(181, 138, 88, 255).setUv(u2, v1);
            bufferbuilder.addVertex(matrix, x1, y1, 1)
                    .setColor(181, 138, 88, 255).setUv(u1, v1);
        }
    }

    private static class ZosimusHead extends ZosimusBodyPart {

        public ZosimusHead(@Nullable ZosimusBodyPart parent, float x, float y, float z) {
            super(parent, x, y, z);
        }

        protected float getHeadWidth() {
            return Mth.lerp(Mth.sin(this.getTotalYRot() * 2), 0.61F, 0.61F * 0.9F);
        }

        protected float getHeadHeight() {
            return Mth.lerp(Mth.sin(this.getTotalXRot() * 2), 0.56F, 0.61F * 0.9F);
        }

        @Override
        protected void draw(BufferBuilder bufferbuilder, PoseStack stack) {
            super.draw(bufferbuilder, stack);
            float headWidth = this.getHeadWidth()*0.5F;
            float headHeight = this.getHeadHeight();
            Matrix4f matrix = stack.last().pose();
            bufferbuilder.addVertex(matrix, -headWidth, 0, 0)
                    .setColor(1F,1F,1F,1F).setUv(1F, 1F);
            bufferbuilder.addVertex(matrix, headWidth, 0, 0)
                    .setColor(1F,1F,1F,1F).setUv(1F, 1F);
            bufferbuilder.addVertex(matrix, headWidth, -headHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1F, 1F);
            bufferbuilder.addVertex(matrix, -headWidth, -headHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1F, 1F);
        }
    }

    private static class ZosimusCollar extends ZosimusBodyPart {
        public ZosimusCollar(ZosimusBodyPart parent, float x, float y, float z) {
            super(parent, x, y, z);
        }

        @Override
        protected void draw(BufferBuilder bufferbuilder, PoseStack stack) {
            float neckWidth = 0.47F;
            float neckHeight = neckWidth * (14.0F / 34.0F);
            float neckHeightOffset = neckWidth * (12.0F / 34.0F);
            float x1 = -neckWidth*0.5F, y1 = -neckHeight;
            float x2 = neckWidth*0.5F, y2 = neckHeightOffset;
            float u1 = (28.0F / 128.0F), v1 = (78.0F / 128.0F);
            float u2 = (62.0F / 128.0F), v2 = (104.0F / 128.0F);

            Matrix4f matrix = stack.last().pose();
            bufferbuilder.addVertex(matrix, x1, y2, 0)
                    .setColor(1F,1F,1F,1F).setUv(u1, v2);
            bufferbuilder.addVertex(matrix, x2, y2, 0)
                    .setColor(1F,1F,1F,1F).setUv(u2, v2);
            bufferbuilder.addVertex(matrix, x2, y1, 0)
                    .setColor(1F,1F,1F,1F).setUv(u2, v1);
            bufferbuilder.addVertex(matrix, x1, y1, 0)
                    .setColor(1F,1F,1F,1F).setUv(u1, v1);

            float clamping = 1 - Mth.clamp(this.getTotalYRot() / Mth.HALF_PI, 0, 1);
            float xRot = this.getTotalXRot();
            y1 = -neckHeight - Mth.sin(xRot) * neckWidth * 0.5F * clamping;
            y2 = -neckHeight + Mth.sin(xRot) * neckWidth * 0.5F * clamping;
            if (y1 > y2) {
                float temp = y1;
                y1 = y2;
                y2 = temp;
            }
            bufferbuilder.addVertex(matrix, x1, y2, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, x2, y2, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, x2, y1, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, x1, y1, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);

//            bufferbuilder.addVertex(matrix, x1, y2, zOffset)
//                    .setColor(1F,1F,1F,1F).setUv(u1, v2);
//            bufferbuilder.addVertex(matrix, x2, y2, zOffset)
//                    .setColor(1F,1F,1F,1F).setUv(u2, v2);
//            bufferbuilder.addVertex(matrix, x2, y1, zOffset)
//                    .setColor(1F,1F,1F,1F).setUv(u2, v1);
//            bufferbuilder.addVertex(matrix, x1, y1, zOffset)
//                    .setColor(1F,1F,1F,1F).setUv(u1, v1);
        }
    }

    private static class ZosimusCloak extends ZosimusBodyPart {
        private float leftElbowOffset, rightElbowOffset;
        public ZosimusCloak(@Nullable ZosimusBodyPart parent, float x, float y, float z) {
            super(parent, x, y, z);
        }

        @Override
        protected void draw(BufferBuilder bufferbuilder, PoseStack stack) {
            float shoulderWidth = 1.0F*0.5F;
            float shoulderHeight = 0.8F;
            float neckHeight = shoulderHeight + 0.1F;
            float cloakWidth = 1.35F*0.5F;
            float cloakHeight = 0.0F;

            float depthMultiplier = 0.3F;
            float yRotation = this.getTotalYRot();
            float rotationMultiplier = MathUtil.smoothAbs(Mth.cos(yRotation), 0.1F) * 1
                                     + MathUtil.smoothAbs(Mth.sin(yRotation), 0.1F) * depthMultiplier;
            float rotatedCloakWidthL = MathUtil.smoothAbs(Mth.cos(yRotation), 0.1F) * (cloakWidth - leftElbowOffset)
                    + MathUtil.smoothAbs(Mth.sin(yRotation), 0.1F) * (cloakWidth * depthMultiplier);
            float rotatedCloakWidthR = MathUtil.smoothAbs(Mth.cos(yRotation), 0.1F) * (cloakWidth + rightElbowOffset)
                    + MathUtil.smoothAbs(Mth.sin(yRotation), 0.1F) * (cloakWidth * depthMultiplier);

            float xRotation = this.getTotalXRot();
            float rotatedShoulderHeight = MathUtil.smoothAbs(Mth.cos(xRotation), 0.1F) * (shoulderHeight)
                    + MathUtil.smoothAbs(Mth.sin(xRotation), 0.1F) * (shoulderWidth * depthMultiplier);
            float rotatedNeckHeight = MathUtil.smoothAbs(Mth.cos(xRotation), 0.1F) * (neckHeight)
                    + MathUtil.smoothAbs(Mth.sin(xRotation), 0.1F) * (shoulderWidth * depthMultiplier);

            Matrix4f matrix = stack.last().pose();
            // left side
            bufferbuilder.addVertex(matrix, -rotatedCloakWidthL, -cloakHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, 0, -cloakHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, 0, -rotatedNeckHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, -shoulderWidth*rotationMultiplier, -rotatedShoulderHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            // right side
            bufferbuilder.addVertex(matrix, 0, -cloakHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, rotatedCloakWidthR, -cloakHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, shoulderWidth*rotationMultiplier, -rotatedShoulderHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex(matrix, 0, -rotatedNeckHeight, 0)
                    .setColor(1F,1F,1F,1F).setUv(1.0F, 1.0F);
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
            if (parent == null) return this.xRot * Mth.cos(this.getTotalYRot());
            return this.xRot * Mth.cos(this.getTotalYRot()) + parent.getTotalXRot();
        }

        public float getTotalYRot() {
            if (parent == null) return this.yRot;
            return this.yRot + parent.getTotalYRot();
        }

//        xRot = 90
//        if (yRot == 0) tilt = 0
//        if (yRot == 90) tilt = 90
//        if (yRot == -90) tilt = -90;

        public float getTotalTilt() {
            if (parent == null) return this.tilt;
            return this.tilt + Mth.sin(this.getTotalYRot()) * this.parent.xRot;
        }

        private Quaternionf setRotationQuaternion() {
            this.transformQuat.set(0, 0, 0, 1)
                    .rotateLocalY(this.getTotalYRot())
                    .rotateLocalX(this.getTotalXRot());
            return this.transformQuat;
        }

        protected void transformPoseStack(PoseStack stack) {
            stack.translate(this.getTransformedX(this.x, this.y, this.z), this.getTransformedY(this.x, this.y, this.z), 0);
            stack.mulPose(Axis.ZP.rotation(this.getTotalTilt()));
        }

        protected void render(BufferBuilder bufferbuilder, PoseStack stack) {
            stack.pushPose();
            this.transformPoseStack(stack);
            this.draw(bufferbuilder, stack);
            for (ZosimusBodyPart child : this.children) child.render(bufferbuilder, stack);
            stack.popPose();
        }

        protected void draw(BufferBuilder bufferbuilder, PoseStack stack) {

        }
    }

    private static void drawColoredBox(Matrix4f matrix, float x1, float y1, float x2, float y2, float zOffset, float r, float g, float b, float a) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex(matrix, x1, y2, zOffset).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x2, y2, zOffset).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x2, y1, zOffset).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x1, y1, zOffset).setColor(r, g, b, a);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
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
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex(matrix, x1 - widthOffsetX, y1 - widthOffsetY, 0).setColor(0.0F, 0.0F, 0.0F, 1.0F);
        bufferbuilder.addVertex(matrix, x1 + widthOffsetX, y1 + widthOffsetY, 0).setColor(0.0F, 0.0F, 0.0F, 1.0F);
        bufferbuilder.addVertex(matrix, x2 + widthOffsetX, y2 + widthOffsetY, 0).setColor(0.0F, 0.0F, 0.0F, 1.0F);
        bufferbuilder.addVertex(matrix, x2 - widthOffsetX, y2 - widthOffsetY, 0).setColor(0.0F, 0.0F, 0.0F, 1.0F);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
}
