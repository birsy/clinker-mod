package birsy.clinker.client.render.util;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ModelPose<T extends Entity, M extends EntityModel<T>> {
    private T entity;
    private M model;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;

    private List<ModelPosePart> poseParts;

    public ModelPose(M modelIn) {
        this.model = modelIn;
    }

    public void addPosePart(ModelPosePart posePart) {
        poseParts.add(posePart);
    }

    public void sortPoses() {
        poseParts.sort((o1, o2) -> Float.compare(o1.priority, o2.priority));
    }

    public void build() {
        sortPoses();
    }

    public void setRotationAngles(T entityIn, float limbSwingIn, float limbSwingAmountIn, float ageInTicksIn, float netHeadYawIn, float headPitchIn) {
        this.entity = entityIn;
        this.limbSwing = limbSwingIn;
        this.limbSwingAmount = limbSwingAmountIn;
        this.ageInTicks = ageInTicksIn;
        this.netHeadYaw = netHeadYawIn;
        this.headPitch = headPitchIn;

        poseParts.forEach(posePart -> {posePart.setRotationAngles(entityIn, limbSwingIn, limbSwingAmountIn, ageInTicksIn, netHeadYawIn, headPitchIn);});
    }

    public void apply(float factor) {
        poseParts.forEach(posePart -> posePart.applyTransformations(factor));
    }

    public static class ModelPosePart<T extends Entity> implements IPosablePart {
        public final float priority;
        private final BirsyModelRenderer modelRenderer;

        private T entity;
        private float limbSwing;
        private float limbSwingAmount;
        private float ageInTicks;
        private float netHeadYaw;
        private float headPitch;

        public float rotationPointX;
        public float rotationPointY;
        public float rotationPointZ;
        public float rotateAngleX;
        public float rotateAngleY;
        public float rotateAngleZ;
        public float scaleX;
        public float scaleY;
        public float scaleZ;

        public ModelPosePart(BirsyModelRenderer modelRendererIn, float priorityIn) {
            this.modelRenderer = modelRendererIn;
            this.priority = priorityIn;
        }

        public void setRotationAngles(T entityIn, float limbSwingIn, float limbSwingAmountIn, float ageInTicksIn, float netHeadYawIn, float headPitchIn) {
            this.entity = entityIn;
            this.limbSwing = limbSwingIn;
            this.limbSwingAmount = limbSwingAmountIn;
            this.ageInTicks = ageInTicksIn;
            this.netHeadYaw = netHeadYawIn;
            this.headPitch = headPitchIn;
        }

        public void setTransformations(T entityIn, float limbSwingIn, float limbSwingAmountIn, float ageInTicksIn, float netHeadYawIn, float headPitchIn) {
            this.resetTransformations(true, true);
        }

        public void resetTransformations(boolean copyBase, boolean usesDefault) {
            if (copyBase) {
                this.scaleX = !usesDefault ? modelRenderer.scaleX : modelRenderer.defaultScaleX;
                this.scaleY = !usesDefault ? modelRenderer.scaleY : modelRenderer.defaultScaleY;
                this.scaleZ = !usesDefault ? modelRenderer.scaleZ : modelRenderer.defaultScaleZ;

                this.rotateAngleX = !usesDefault ? modelRenderer.rotateAngleX : modelRenderer.defaultRotateAngleX;
                this.rotateAngleY = !usesDefault ? modelRenderer.rotateAngleY : modelRenderer.defaultRotateAngleY;
                this.rotateAngleZ = !usesDefault ? modelRenderer.rotateAngleZ : modelRenderer.defaultRotateAngleZ;

                this.rotationPointX = !usesDefault ? modelRenderer.rotationPointX : modelRenderer.defaultRotationPointX;
                this.rotationPointY = !usesDefault ? modelRenderer.rotationPointY : modelRenderer.defaultRotationPointY;
                this.rotationPointZ = !usesDefault ? modelRenderer.rotationPointZ : modelRenderer.defaultRotationPointZ;
            } else {
                this.scaleX = !usesDefault ? 1.0F : modelRenderer.defaultScaleX;
                this.scaleY = !usesDefault ? 1.0F : modelRenderer.defaultScaleY;
                this.scaleZ = !usesDefault ? 1.0F : modelRenderer.defaultScaleZ;

                this.rotateAngleX = !usesDefault ? 0.0F : modelRenderer.defaultRotateAngleX;
                this.rotateAngleY = !usesDefault ? 0.0F : modelRenderer.defaultRotateAngleY;
                this.rotateAngleZ = !usesDefault ? 0.0F : modelRenderer.defaultRotateAngleZ;

                this.rotationPointX = modelRenderer.defaultRotationPointX;
                this.rotationPointY = modelRenderer.defaultRotationPointY;
                this.rotationPointZ = modelRenderer.defaultRotationPointZ;
            }
        }

        public void applyTransformations(float factor) {
            this.setTransformations(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            modelRenderer.scaleX = MathHelper.lerp(modelRenderer.scaleX, this.scaleX, factor);
            modelRenderer.scaleX = MathHelper.lerp(modelRenderer.scaleY, this.scaleY, factor);
            modelRenderer.scaleX = MathHelper.lerp(modelRenderer.scaleZ, this.scaleZ, factor);
            modelRenderer.rotationPointX = MathHelper.lerp(modelRenderer.rotationPointX, this.rotationPointX, factor);
            modelRenderer.rotationPointX = MathHelper.lerp(modelRenderer.rotationPointY, this.rotationPointY, factor);
            modelRenderer.rotationPointX = MathHelper.lerp(modelRenderer.rotationPointZ, this.rotationPointZ, factor);
            modelRenderer.rotateAngleX = MathHelper.lerp(modelRenderer.rotateAngleX, this.rotateAngleX, factor);
            modelRenderer.rotateAngleX = MathHelper.lerp(modelRenderer.rotateAngleY, this.rotateAngleY, factor);
            modelRenderer.rotateAngleX = MathHelper.lerp(modelRenderer.rotateAngleZ, this.rotateAngleZ, factor);
        }


        public float getRotationPointX() {
            return this.rotationPointX;
        }
        public float getRotationPointY() {
            return this.rotationPointY;
        }
        public float getRotationPointZ() {
            return this.rotationPointZ;
        }

        public float getRotateAngleX() {
            return this.rotateAngleX;
        }
        public float getRotateAngleY() {
            return this.rotationPointY;
        }
        public float getRotateAngleZ() {
            return this.rotateAngleZ;
        }

        public float getScaleX() {
            return this.scaleX;
        }
        public float getScaleY() {
            return this.scaleY;
        }
        public float getScaleZ() {
            return this.scaleZ;
        }


        public void setRotationPointX(float value) {
            this.rotationPointX = value;
        }
        public void setRotationPointY(float value) {
            this.rotationPointY = value;
        }
        public void setRotationPointZ(float value) {
            this.rotationPointZ = value;
        }

        public void setRotateAngleX(float value) {
            this.rotateAngleX = value;
        }
        public void setRotateAngleY(float value) {
            this.rotateAngleY = value;
        }
        public void setRotateAngleZ(float value) {
            this.rotateAngleZ = value;
        }

        public void setScaleX(float value) {
            this.scaleX = value;
        }
        public void setScaleY(float value) {
            this.scaleY = value;
        }
        public void setScaleZ(float value) {
            this.scaleZ = value;
        }
    }
}
