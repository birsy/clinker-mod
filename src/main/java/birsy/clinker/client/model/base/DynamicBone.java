package birsy.clinker.client.model.base;

import birsy.clinker.core.util.SecondOrderDynamics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;

public class DynamicBone extends InterpolatedBone {
    float dX, dY, dZ;
    SecondOrderDynamics xDynamics, yDynamics, zDynamics;

    public DynamicBone(String identifier) {
        super(identifier);
    }

    protected void tick(float deltaTime) {
        this.pX = this.dX;
        this.pY = this.dY;
        this.pZ = this.dZ;
        this.pRotation.set(this.rotation);
        this.pXSize = this.xSize;
        this.pYSize = this.ySize;
        this.pZSize = this.zSize;

        this.dX = xDynamics.update(deltaTime, this.x);
        this.dY = yDynamics.update(deltaTime, this.y);
        this.dZ = zDynamics.update(deltaTime, this.z);
    }

    public void transform(PoseStack pPoseStack, float partialTick) {
        pPoseStack.translate(Mth.lerp(partialTick, pX, dX), Mth.lerp(partialTick, pY, dY), Mth.lerp(partialTick, pZ, dZ));
        pPoseStack.scale(Mth.lerp(partialTick, pXSize, xSize), Mth.lerp(partialTick, pYSize, ySize), Mth.lerp(partialTick, pZSize, zSize));
        this.currentRotation = pRotation.slerp(rotation, partialTick, currentRotation);
        this.currentRotation.normalize();
        pPoseStack.mulPose(this.currentRotation.toMojangQuaternion());
    }
}
