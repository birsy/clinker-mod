package birsy.clinker.client.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class CappinModelPart extends ModelPart {
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

    public CappinModelPart parent;
    public Map<String, CappinModelPart> childrenCMP;

    public CappinModelPart(List<Cube> pCubes, Map<String, ModelPart> pChildren) {
        super(pCubes, pChildren);
        Map<String, CappinModelPart> convertedChildren = new HashMap<>();
        pChildren.forEach((string, child) -> convertedChildren.put(string, fromModelPart(child)));
        this.childrenCMP = convertedChildren;
    }

    public void setScale(float x, float y, float z) {
        this.xScale = x;
        this.yScale = y;
        this.zScale = z;
    }

    @Override
    public void translateAndRotate(PoseStack pPoseStack) {
        translateAndRotateAndScale(pPoseStack);
    }

    public void translateAndRotateAndScale(PoseStack pPoseStack) {
        pPoseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);

        if (this.zRot != 0.0F) {
            pPoseStack.mulPose(Vector3f.ZP.rotation(this.zRot));
        }

        if (this.yRot != 0.0F) {
            pPoseStack.mulPose(Vector3f.YP.rotation(this.yRot));
        }

        if (this.xRot != 0.0F) {
            pPoseStack.mulPose(Vector3f.XP.rotation(this.xRot));
        }

        pPoseStack.scale(xScale, yScale, zScale);
    }

    public CappinModelPart getChild(String pName) {
        CappinModelPart modelPart = this.childrenCMP.get(pName);
        modelPart.setParent(this);
        if (modelPart == null) {
            throw new NoSuchElementException("Can't find part " + pName);
        } else {
            return modelPart;
        }
    }

    @Override
    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.childrenCMP.isEmpty()) {
                pPoseStack.pushPose();
                this.translateAndRotateAndScale(pPoseStack);
                this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

                for(CappinModelPart modelPart : this.childrenCMP.values()) {
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

    @Override
    public void visit(PoseStack pPoseStack, ModelPart.Visitor pVisitor) {
        this.visit(pPoseStack, pVisitor, "");
    }

    private void visit(PoseStack p_171313_, ModelPart.Visitor p_171314_, String p_171315_) {
        if (!this.cubes.isEmpty() || !this.childrenCMP.isEmpty()) {
            p_171313_.pushPose();
            this.translateAndRotate(p_171313_);
            PoseStack.Pose posestack$pose = p_171313_.last();

            for(int i = 0; i < this.cubes.size(); ++i) {
                p_171314_.visit(posestack$pose, p_171315_, i, this.cubes.get(i));
            }

            String s = p_171315_ + "/";
            this.childrenCMP.forEach((p_171320_, p_171321_) -> {
                p_171321_.visit(p_171313_, p_171314_, s + p_171320_);
            });
            p_171313_.popPose();
        }
    }

    public static PartDefinition initModelPart(CappinModelPart cube, PartDefinition parent, String pName, CubeListBuilder pCubes, PartPose pPartPose) {
        cube.xInit = pPartPose.x;
        cube.yInit = pPartPose.y;
        cube.zInit = pPartPose.z;
        cube.xRotInit = pPartPose.xRot;
        cube.yRotInit = pPartPose.yRot;
        cube.zRotInit = pPartPose.zRot;
        cube.xScaleInit = 1.0F;
        cube.yScaleInit = 1.0F;
        cube.zScaleInit = 1.0F;

        return parent.addOrReplaceChild(pName, pCubes,pPartPose);
    }

    public static CappinModelPart fromModelPart(ModelPart modelPart) {
        if (modelPart == null) {
            return null;
        } else {
            CappinModelPart part = new CappinModelPart(modelPart.cubes, modelPart.children);
            part.setPos(modelPart.x, modelPart.y, modelPart.z);
            part.setRotation(modelPart.xRot, modelPart.yRot, modelPart.zRot);
            part.setScale(1.0F, 1.0F, 1.0F);

            return part;
        }
    }

    public void setParent(CappinModelPart part) {
        this.parent = part;
    }

    public ModelPart getParent() {
        return this.parent;
    }
}
