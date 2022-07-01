package birsy.clinker.client.render.entity.model.base;

import birsy.clinker.core.util.SecondOrderDynamics;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ComplexModelPart {
    public SecondOrderDynamics xPosDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics yPosDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics zPosDynamics = new SecondOrderDynamics();
    public float x;
    public float y;
    public float z;
    public float xInit;
    public float yInit;
    public float zInit;

    public SecondOrderDynamics xRotDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics yRotDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics zRotDynamics = new SecondOrderDynamics();
    public float xRot;
    public float yRot;
    public float zRot;
    public float xRotInit;
    public float yRotInit;
    public float zRotInit;

    public SecondOrderDynamics xScaleDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics yScaleDynamics = new SecondOrderDynamics();
    public SecondOrderDynamics zScaleDynamics = new SecondOrderDynamics();
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public float xScaleInit = 1.0F;
    public float yScaleInit = 1.0F;
    public float zScaleInit = 1.0F;

    public boolean mirror;
    public boolean visible = true;
    public boolean skipDraw;

    public int u;
    public int v;
    public int textureWidth;
    public int textureHeight;

    private long pNanoTime;

    public Map<String, Cube> cubes;
    public ComplexModelPart parent;
    public List<ComplexModelPart> children = new ArrayList<>();

    public final ModelSkeleton modelSkeleton;

    public ComplexModelPart(ModelSkeleton skeleton, int u, int v) {
        this.u = u;
        this.v = v;
        this.modelSkeleton = skeleton;
    }
    public void addCube(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
        cubes.put("Cube" + cubes.size(), new Cube(u, v, minX, minY, minZ, sizeX, sizeY, sizeZ, expansionX, expansionY, expansionZ, mirror, textureWidth, textureHeight));
    }
    public void addCube(String name, float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
        cubes.put(name, new Cube(u, v, minX, minY, minZ, sizeX, sizeY, sizeZ, expansionX, expansionY, expansionZ, mirror, textureWidth, textureHeight));
    }
    public void setInitialPosition(float x, float y, float z) {
        this.x = x;
        this.xInit = x;
        this.y = y;
        this.yInit = y;
        this.z = z;
        this.zInit = z;
    }
    public void setInitialRotation(float x, float y, float z) {
        this.xRot = x;
        this.xRotInit = x;
        this.yRot = y;
        this.yRotInit = y;
        this.zRot = z;
        this.zRotInit = z;
    }
    public void setInitialScale(float x, float y, float z) {
        this.xScale = x;
        this.xScaleInit = x;
        this.yScale = y;
        this.yScaleInit = y;
        this.zScale = z;
        this.zScaleInit = z;
    }
    public void createDynamics(float frequency, float damping, float response) {
        this.xPosDynamics.createDynamics(frequency, damping, response, this.x);
        this.yPosDynamics.createDynamics(frequency, damping, response, this.y);
        this.zPosDynamics.createDynamics(frequency, damping, response, this.z);
        this.xRotDynamics.createDynamics(frequency, damping, response, this.xRot);
        this.yRotDynamics.createDynamics(frequency, damping, response, this.yRot);
        this.zRotDynamics.createDynamics(frequency, damping, response, this.zRot);
        this.xScaleDynamics.createDynamics(frequency, damping, response, this.xScale);
        this.yScaleDynamics.createDynamics(frequency, damping, response, this.yScale);
        this.zScaleDynamics.createDynamics(frequency, damping, response, this.zScale);
    }

    public void resetPose() {
        this.x = xInit;
        this.y = yInit;
        this.z = zInit;
        this.xRot = xRotInit;
        this.yRot = yRotInit;
        this.zRot = zRotInit;
        this.xScale = xScaleInit;
        this.yScale = yScaleInit;
        this.zScale = zScaleInit;
    }

    public void setScale(float x, float y, float z) {
        this.xScale = x;
        this.yScale = y;
        this.zScale = z;
    }

    public void translateAndRotateAndScale(PoseStack pPoseStack) {
        pPoseStack.translate(this.xPosDynamics.getValue() / 16.0F, this.yPosDynamics.getValue() / 16.0F, this.zPosDynamics.getValue() / 16.0F);

        if (this.zRot != 0.0F) {
            pPoseStack.mulPose(Vector3f.ZP.rotation((float) this.zRotDynamics.getValue()));
        }

        if (this.yRot != 0.0F) {
            pPoseStack.mulPose(Vector3f.YP.rotation((float) this.yRotDynamics.getValue()));
        }

        if (this.xRot != 0.0F) {
            pPoseStack.mulPose(Vector3f.XP.rotation((float) this.xRotDynamics.getValue()));
        }

        pPoseStack.scale((float) xScaleDynamics.getValue(), (float) yScaleDynamics.getValue(), (float) zScaleDynamics.getValue());
    }

    // Run every frame, updates the dynamics. Should be able to handle time-steps of any value!
    // With some impacts on accuracy, of course - but no system instability.
    public void update() {
        long nanoTime = System.nanoTime();
        double deltaTime = (double)(System.nanoTime() - pNanoTime) / (double)1000000000;
        this.pNanoTime = pNanoTime;

        xPosDynamics.update(deltaTime, x);
        yPosDynamics.update(deltaTime, y);
        zPosDynamics.update(deltaTime, z);

        xRotDynamics.update(deltaTime, xRot);
        yRotDynamics.update(deltaTime, yRot);
        zRotDynamics.update(deltaTime, zRot);

        xScaleDynamics.update(deltaTime, xScale);
        yScaleDynamics.update(deltaTime, yScale);
        zScaleDynamics.update(deltaTime, zScale);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        update();
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                pPoseStack.pushPose();
                this.translateAndRotateAndScale(pPoseStack);
                this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

                for(ComplexModelPart modelPart : this.children) {
                    modelPart.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                }

                pPoseStack.popPose();
            }
        }
    }

    private void compile(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.cubes.forEach((name, cube) -> {
            cube.compile(pPose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        });
    }

    public void setParent(ComplexModelPart part) {
        this.parent = part;
        part.children.add(this);
    }

    public ComplexModelPart getParent() {
        return this.parent;
    }

    public void getGlobalTransForm(PoseStack matrixStack) {
        ComplexModelPart parent = this.parent;
        if (parent != null) {
            parent.getGlobalTransForm(matrixStack);
        }
        this.translateAndRotateAndScale(matrixStack);
    }

    public Vec3 getWorldPos(Entity entity, float partialTick) {
        PoseStack matrixStack = new PoseStack();
        Vec3 position = entity.getPosition(partialTick);
        matrixStack.translate(position.x(), position.y(), position.z());
        matrixStack.mulPose(new Quaternion(0, -entity.getViewYRot(partialTick) + 180, 0, true));
        matrixStack.scale(-1, -1, 1);
        matrixStack.translate(0, -1.5f, 0);
        this.getGlobalTransForm(matrixStack);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();

        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.transform(matrix4f);
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

    @OnlyIn(Dist.CLIENT)
    public static class Cube {
        private final ComplexModelPart.Polygon[] polygons;
        public float minX;
        public float minY;
        public float minZ;
        public float maxX;
        public float maxY;
        public float maxZ;

        public Cube(int u, int v, float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ, boolean mirrored, float textureWidth, float textureHeight) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = minX + sizeX;
            this.maxY = minY + sizeY;
            this.maxZ = minZ + sizeZ;
            this.polygons = new ComplexModelPart.Polygon[6];
            float f = minX + sizeX;
            float f1 = minY + sizeY;
            float f2 = minZ + sizeZ;
            minX -= expansionX;
            minY -= expansionY;
            minZ -= expansionZ;
            f += expansionX;
            f1 += expansionY;
            f2 += expansionZ;
            if (mirrored) {
                float f3 = f;
                f = minX;
                minX = f3;
            }

            ComplexModelPart.Vertex vertex0 = new ComplexModelPart.Vertex(minX, minY, minZ, 0.0F, 0.0F);
            ComplexModelPart.Vertex vertex1 = new ComplexModelPart.Vertex(f, minY, minZ, 0.0F, 8.0F);
            ComplexModelPart.Vertex vertex2 = new ComplexModelPart.Vertex(f, f1, minZ, 8.0F, 8.0F);
            ComplexModelPart.Vertex vertex3 = new ComplexModelPart.Vertex(minX, f1, minZ, 8.0F, 0.0F);
            ComplexModelPart.Vertex vertex4 = new ComplexModelPart.Vertex(minX, minY, f2, 0.0F, 0.0F);
            ComplexModelPart.Vertex vertex5 = new ComplexModelPart.Vertex(f, minY, f2, 0.0F, 8.0F);
            ComplexModelPart.Vertex vertex6 = new ComplexModelPart.Vertex(f, f1, f2, 8.0F, 8.0F);
            ComplexModelPart.Vertex vertex7 = new ComplexModelPart.Vertex(minX, f1, f2, 8.0F, 0.0F);
            float f4 = (float)u;
            float f5 = (float)u + sizeZ;
            float f6 = (float)u + sizeZ + sizeX;
            float f7 = (float)u + sizeZ + sizeX + sizeX;
            float f8 = (float)u + sizeZ + sizeX + sizeZ;
            float f9 = (float)u + sizeZ + sizeX + sizeZ + sizeX;
            float f10 = (float)v;
            float f11 = (float)v + sizeZ;
            float f12 = (float)v + sizeZ + sizeY;
            this.polygons[2] = new ComplexModelPart.Polygon(new ComplexModelPart.Vertex[]{vertex5, vertex4, vertex0, vertex1}, f5, f10, f6, f11, textureWidth, textureHeight, mirrored, Direction.DOWN);
            this.polygons[3] = new ComplexModelPart.Polygon(new ComplexModelPart.Vertex[]{vertex2, vertex3, vertex7, vertex6}, f6, f11, f7, f10, textureWidth, textureHeight, mirrored, Direction.UP);
            this.polygons[1] = new ComplexModelPart.Polygon(new ComplexModelPart.Vertex[]{vertex0, vertex4, vertex7, vertex3}, f4, f11, f5, f12, textureWidth, textureHeight, mirrored, Direction.WEST);
            this.polygons[4] = new ComplexModelPart.Polygon(new ComplexModelPart.Vertex[]{vertex1, vertex0, vertex3, vertex2}, f5, f11, f6, f12, textureWidth, textureHeight, mirrored, Direction.NORTH);
            this.polygons[0] = new ComplexModelPart.Polygon(new ComplexModelPart.Vertex[]{vertex5, vertex1, vertex2, vertex6}, f6, f11, f8, f12, textureWidth, textureHeight, mirrored, Direction.EAST);
            this.polygons[5] = new ComplexModelPart.Polygon(new ComplexModelPart.Vertex[]{vertex4, vertex5, vertex6, vertex7}, f8, f11, f9, f12, textureWidth, textureHeight, mirrored, Direction.SOUTH);
        }

        public void compile(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            Matrix4f matrix4f = pPose.pose();
            Matrix3f matrix3f = pPose.normal();

            for(ComplexModelPart.Polygon modelpart$polygon : this.polygons) {
                Vector3f vector3f = modelpart$polygon.normal.copy();
                vector3f.transform(matrix3f);
                float xNormal = vector3f.x();
                float yNormal = vector3f.y();
                float zNormal = vector3f.z();

                for(ComplexModelPart.Vertex vertex : modelpart$polygon.vertices) {
                    float xPos = vertex.pos.x() / 16.0F;
                    float yPos = vertex.pos.y() / 16.0F;
                    float zPos = vertex.pos.z() / 16.0F;
                    Vector4f vertexPosition = new Vector4f(xPos, yPos, zPos, 1.0F);
                    vertexPosition.transform(matrix4f);
                    pVertexConsumer.vertex(vertexPosition.x(), vertexPosition.y(), vertexPosition.z(), pRed, pGreen, pBlue, pAlpha, vertex.u, vertex.v, pPackedOverlay, pPackedLight, xNormal, yNormal, zNormal);
                }
            }

        }
    }
    @OnlyIn(Dist.CLIENT)
    static class Polygon {
        public final ComplexModelPart.Vertex[] vertices;
        public final Vector3f normal;

        public Polygon(ComplexModelPart.Vertex[] p_104362_, float p_104363_, float p_104364_, float p_104365_, float p_104366_, float p_104367_, float p_104368_, boolean p_104369_, Direction p_104370_) {
            this.vertices = p_104362_;
            float f = 0.0F / p_104367_;
            float f1 = 0.0F / p_104368_;
            p_104362_[0] = p_104362_[0].remap(p_104365_ / p_104367_ - f, p_104364_ / p_104368_ + f1);
            p_104362_[1] = p_104362_[1].remap(p_104363_ / p_104367_ + f, p_104364_ / p_104368_ + f1);
            p_104362_[2] = p_104362_[2].remap(p_104363_ / p_104367_ + f, p_104366_ / p_104368_ - f1);
            p_104362_[3] = p_104362_[3].remap(p_104365_ / p_104367_ - f, p_104366_ / p_104368_ - f1);
            if (p_104369_) {
                int i = p_104362_.length;

                for(int j = 0; j < i / 2; ++j) {
                    ComplexModelPart.Vertex vertex = p_104362_[j];
                    p_104362_[j] = p_104362_[i - 1 - j];
                    p_104362_[i - 1 - j] = vertex;
                }
            }

            this.normal = p_104370_.step();
            if (p_104369_) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }

        }
    }
    @OnlyIn(Dist.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float pX, float pY, float pZ, float pU, float pV) {
            this(new Vector3f(pX, pY, pZ), pU, pV);
        }

        public ComplexModelPart.Vertex remap(float pU, float pV) {
            return new ComplexModelPart.Vertex(this.pos, pU, pV);
        }

        public Vertex(Vector3f pPos, float pU, float pV) {
            this.pos = pPos;
            this.u = pU;
            this.v = pV;
        }
    }
}
