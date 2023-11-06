package birsy.clinker.client.render.entity.model.base;

import birsy.clinker.core.util.SecondOrderDynamics;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.*;

//TODO: fix second order dynamics being shared between entities, somehow :P
@OnlyIn(Dist.CLIENT)
public class DynamicModelPart {
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

    public boolean translationalDynamicsEnabled = false;
    public boolean rotationalDynamicsEnabled = false;
    public boolean scalarDynamicsEnabled = false;

    public boolean mirror;
    public boolean visible = true;
    public boolean skipDraw;

    public int u;
    public int v;
    public float textureWidth;
    public float textureHeight;

    private static final Minecraft mc = Minecraft.getInstance();
    
    //public Map<String, Cube> cubes;
    public DynamicModelPart parent;
    public List<DynamicModelPart> children = new ArrayList<>();

    public final DynamicModel modelSkeleton;
    public Map<String, ModelPart.Cube> cubes;

    public DynamicModelPart(DynamicModel skeleton, int u, int v) {
        this.u = u;
        this.v = v;
        this.modelSkeleton = skeleton;
        this.modelSkeleton.addPart(this);
        this.cubes = new HashMap<>();
    }

    public void addCube(float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
        this.addCube("Cube_" + cubes.size(), minX, minY, minZ, sizeX, sizeY, sizeZ, expansionX, expansionY, expansionZ);
    }

    public void addCube(String name, float minX, float minY, float minZ, float sizeX, float sizeY, float sizeZ, float expansionX, float expansionY, float expansionZ) {
        ModelPart.Cube cube = new ModelPart.Cube(this.u, this.v, minX, minY, minZ, sizeX, sizeY, sizeZ, expansionX, expansionY, expansionZ, this.mirror, this.textureWidth, this.textureHeight, new HashSet<>(List.of(Direction.values())));
        cubes.put(name, cube);
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
        this.translationalDynamicsEnabled = true;
        this.xRotDynamics.createDynamics(frequency, damping, response, this.xRot);
        this.yRotDynamics.createDynamics(frequency, damping, response, this.yRot);
        this.zRotDynamics.createDynamics(frequency, damping, response, this.zRot);
        this.rotationalDynamicsEnabled = true;
        this.xScaleDynamics.createDynamics(frequency, damping, response, this.xScale);
        this.yScaleDynamics.createDynamics(frequency, damping, response, this.yScale);
        this.zScaleDynamics.createDynamics(frequency, damping, response, this.zScale);
        this.scalarDynamicsEnabled = true;
    }

    public void setDynamics(float frequency, float damping, float response) {
        this.xPosDynamics.setDynamics(frequency, damping, response);
        this.yPosDynamics.setDynamics(frequency, damping, response);
        this.zPosDynamics.setDynamics(frequency, damping, response);
        this.xRotDynamics.setDynamics(frequency, damping, response);
        this.yRotDynamics.setDynamics(frequency, damping, response);
        this.zRotDynamics.setDynamics(frequency, damping, response);
        this.xScaleDynamics.setDynamics(frequency, damping, response);
        this.yScaleDynamics.setDynamics(frequency, damping, response);
        this.zScaleDynamics.setDynamics(frequency, damping, response);
    }

    public void setTranslationalDynamics(boolean enabled) {
        this.xPosDynamics.enabled = enabled;
        this.yPosDynamics.enabled = enabled;
        this.zPosDynamics.enabled = enabled;
        this.translationalDynamicsEnabled = enabled;
    }

    public void setRotationalDynamics(boolean enabled) {
        this.xRotDynamics.enabled = enabled;
        this.yRotDynamics.enabled = enabled;
        this.zRotDynamics.enabled = enabled;
        this.rotationalDynamicsEnabled = enabled;
    }

    public void setScalarDynamics(boolean enabled) {
        this.xScaleDynamics.enabled = enabled;
        this.yScaleDynamics.enabled = enabled;
        this.zScaleDynamics.enabled = enabled;
        this.scalarDynamicsEnabled = enabled;
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
        if (translationalDynamicsEnabled) {
            pPoseStack.translate(this.xPosDynamics.getValue() / 16.0F, this.yPosDynamics.getValue() / 16.0F, this.zPosDynamics.getValue() / 16.0F);
        } else {
            pPoseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        }

        if (this.rotationalDynamicsEnabled) {
            if (this.zRotDynamics.getValue() != 0.0F) {
                pPoseStack.mulPose(Axis.ZP.rotation(this.zRotDynamics.getValue()));
            }

            if (this.yRotDynamics.getValue() != 0.0F) {
                pPoseStack.mulPose(Axis.YP.rotation(this.yRotDynamics.getValue()));
            }

            if (this.xRotDynamics.getValue() != 0.0F) {
                pPoseStack.mulPose(Axis.XP.rotation(this.xRotDynamics.getValue()));
            }
        } else {
            if (this.zRot != 0.0F) {
                pPoseStack.mulPose(Axis.ZP.rotation(this.zRot));
            }

            if (this.yRot != 0.0F) {
                pPoseStack.mulPose(Axis.YP.rotation(this.yRot));
            }

            if (this.xRot != 0.0F) {
                pPoseStack.mulPose(Axis.XP.rotation(this.xRot));
            }
        }

        if (this.scalarDynamicsEnabled) {
            pPoseStack.scale(xScaleDynamics.getValue(), yScaleDynamics.getValue(), zScaleDynamics.getValue());
        } else {
            pPoseStack.scale(xScale, yScale, zScale);
        }

    }

    // Run every frame, updates the dynamics. Should be able to handle time-steps of any value!
    // With some impacts on accuracy, of course - but no system instability.
    public void update(float timeMultiplier) {
        if (!mc.isPaused()) {
            float deltaTime = (mc.getDeltaFrameTime() / 20.0F) * timeMultiplier;

            if (translationalDynamicsEnabled) {
                xPosDynamics.update(deltaTime, x);
                yPosDynamics.update(deltaTime, y);
                zPosDynamics.update(deltaTime, z);
            }

            if (rotationalDynamicsEnabled) {
                xRotDynamics.update(deltaTime, xRot);
                yRotDynamics.update(deltaTime, yRot);
                zRotDynamics.update(deltaTime, zRot);
            }

            if (scalarDynamicsEnabled) {
                xScaleDynamics.update(deltaTime, xScale);
                yScaleDynamics.update(deltaTime, yScale);
                zScaleDynamics.update(deltaTime, zScale);
            }

            //Clinker.LOGGER.info("Pos: "   + xPosDynamics.getValue() + ", "   + yPosDynamics.getValue() + ", "   + zPosDynamics.getValue() + ", ");
            //Clinker.LOGGER.info("Rot: "   + xRotDynamics.getValue() + ", "   + yRotDynamics.getValue() + ", "   + zRotDynamics.getValue() + ", ");
            //Clinker.LOGGER.info("Scale: " + xScaleDynamics.getValue() + ", " + yScaleDynamics.getValue() + ", " + zScaleDynamics.getValue() + ", ");

            for (DynamicModelPart child : this.children) {
                child.update();
            }
        }
    }

    public void update() {
        this.update(1.0F);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                pPoseStack.pushPose();
                this.translateAndRotateAndScale(pPoseStack);
                this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

                for(DynamicModelPart part : this.children) {
                    part.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                }

                pPoseStack.popPose();
            }
        }
    }

    private void compile(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.cubes.forEach((name, cube) -> cube.compile(pPose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
    }

    public void setParent(DynamicModelPart part) {
        this.parent = part;
        part.children.add(this);
    }

    public DynamicModelPart getParent() {
        return this.parent;
    }

    public void addChild(DynamicModelPart part) {
        part.parent = this;
        this.children.add(part);
    }

    public void getGlobalTransForm(PoseStack matrixStack) {
        DynamicModelPart parent = this.parent;
        if (parent != null) {
            parent.getGlobalTransForm(matrixStack);
        }
        this.translateAndRotateAndScale(matrixStack);
    }

    public Vec3 getWorldPos(Entity entity, float partialTick) {
        PoseStack matrixStack = new PoseStack();
        Vec3 position = entity.getPosition(partialTick);
        matrixStack.translate(position.x(), position.y(), position.z());
        //matrixStack.mulPose(new Quaternion(0, -entity.getViewYRot(partialTick) + 180, 0, true));
        matrixStack.scale(-1, -1, 1);
        matrixStack.translate(0, -1.5f, 0);
        this.getGlobalTransForm(matrixStack);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();

        Vector4f vec = new Vector4f(0, 0, 0, 1);
        matrix4f.transform(vec);
        return new Vec3(vec.x(), vec.y(), vec.z());
    }
}
