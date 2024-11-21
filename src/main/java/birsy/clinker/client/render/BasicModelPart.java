package birsy.clinker.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;

import java.util.*;

@Deprecated
public class BasicModelPart {
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public float xInit;
    public float yInit;
    public float zInit;
    public float xRotInit;
    public float yRotInit;
    public float zRotInit;
    public float xScaleInit = 1.0F;
    public float yScaleInit = 1.0F;
    public float zScaleInit = 1.0F;

    public boolean visible = true;
    public boolean skipDraw;

    public BasicModelPart parent;
    public List<BasicModelPart> children = new ArrayList<>();
    public List<ModelPart.Cube> cubes;

    public BasicModelPart(List<ModelPart.Cube> pCubes, PartPose pose) {
        this(pose, pCubes);
    }

    public BasicModelPart(PartPose pose, List<ModelPart.Cube> pCubes) {
        this(pCubes);

        this.x = pose.x;
        this.y = pose.y;
        this.z = pose.z;
        this.xRot = pose.xRot;
        this.yRot = pose.yRot;
        this.zRot = pose.zRot;
        initializePose();
    }

    public BasicModelPart(List<ModelPart.Cube> pCubes) {
        this.cubes = pCubes;
    }


    public void setScale(float x, float y, float z) {
        this.xScale = x;
        this.yScale = y;
        this.zScale = z;
    }

    public void initializePose() {
        this.xInit = this.x;
        this.yInit = this.y;
        this.zInit = this.z;
        this.xRotInit = this.xRot;
        this.yRotInit = this.yRot;
        this.zRotInit = this.zRot;
        this.xScaleInit = this.xScale;
        this.yScaleInit = this.yScale;
        this.zScaleInit = this.zScale;
    }

    public void translateAndRotateAndScale(PoseStack pPoseStack) {
        pPoseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);

        if (this.zRot != 0.0F) {
            pPoseStack.mulPose(Axis.ZP.rotation(this.zRot));
        }

        if (this.yRot != 0.0F) {
            pPoseStack.mulPose(Axis.YP.rotation(this.yRot));
        }

        if (this.xRot != 0.0F) {
            pPoseStack.mulPose(Axis.XP.rotation(this.xRot));
        }

        pPoseStack.scale(xScale, yScale, zScale);
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

                for(BasicModelPart modelPart : this.children) {
                    modelPart.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                }

                pPoseStack.popPose();
            }
        }
    }

    private void compile(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        for(ModelPart.Cube cube : this.cubes) {
            cube.compile(pPose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }

    public void setParent(BasicModelPart part) {
        this.parent = part;
        part.children.add(this);
    }

    public BasicModelPart getParent() {
        return this.parent;
    }

    public static final List<ModelPart.Cube> toCubeList(CubeListBuilder defList, int textWidth, int textHeight) {
        List<ModelPart.Cube> list = new ArrayList<>();
        for (CubeDefinition cubeDefinition : defList.getCubes()) {
            list.add(cubeDefinition.bake(textWidth, textHeight));
        }
        return list;
    }

    public void getGlobalTransForm(PoseStack matrixStack) {
        BasicModelPart parent = this.parent;
        if (parent != null) {
            parent.getGlobalTransForm(matrixStack);
        }
        this.translateAndRotateAndScale(matrixStack);
    }
}
