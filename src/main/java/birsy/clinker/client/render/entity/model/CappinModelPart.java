package birsy.clinker.client.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

    public CappinModelPart(List<Cube> pCubes, Map<String, ModelPart> pChildren) {
        super(pCubes, pChildren);
    }

    public void setScale(float x, float y, float z) {
        this.xScale = x;
        this.yScale = y;
        this.zScale = z;
    }

    public void translateAndRotateAndScale(PoseStack pPoseStack) {
        pPoseStack.scale(xScale, yScale, zScale);
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
    }

    public ModelPart getChild(String pName) {
        ModelPart modelpart = this.children.get(pName);
        if (modelpart == null) {
            throw new NoSuchElementException("Can't find part " + pName);
        } else {
            return modelpart;
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
        CappinModelPart part = new CappinModelPart(modelPart.cubes, modelPart.children);
        part.setPos(modelPart.x, modelPart.y, modelPart.z);
        part.setRotation(modelPart.xRot, modelPart.yRot, modelPart.zRot);
        part.setScale(1.0F, 1.0F, 1.0F);

        return part;
    }
}
