package birsy.clinker.client.render.tileentity.model;

import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrockpotModel extends Model {
    public BirsyModelRenderer crockpotRotationJoint;
    public BirsyModelRenderer crockpotJoint;
    public BirsyModelRenderer crockpotTop;
    public BirsyModelRenderer crockpotSideSouth;
    public BirsyModelRenderer crockpotSideNorth;
    public BirsyModelRenderer crockpotSideEast;
    public BirsyModelRenderer crockpotSideWest;
    public BirsyModelRenderer crockpotHandle1;
    public BirsyModelRenderer crockpotHandle2;

    public CrockpotModel() {
        super(RenderType::getEntityCutout);
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.crockpotHandle1 = new BirsyModelRenderer(this, 0, 0);
        this.crockpotHandle1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crockpotHandle1.addBox(-1.5F, -4.5F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotHandle1.rotateAngleY = (float) Math.toRadians(45.0);
        this.crockpotHandle2 = new BirsyModelRenderer(this, 0, 0);
        this.crockpotHandle2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crockpotHandle2.addBox(-1.5F, -4.5F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotHandle2.rotateAngleY = (float) Math.toRadians(90.0);

        this.crockpotTop = new BirsyModelRenderer(this, 0, 25);
        this.crockpotTop.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.crockpotTop.addBox(-5.5F, 0.0F, -5.5F, 11.0F, 1.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotSideNorth = new BirsyModelRenderer(this, 0, 13);
        this.crockpotSideNorth.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crockpotSideNorth.addBox(-6.5F, 0.0F, 5.5F, 13.0F, 7.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotSideSouth = new BirsyModelRenderer(this, 0, 5);
        this.crockpotSideSouth.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crockpotSideSouth.addBox(-6.5F, 0.0F, -6.5F, 13.0F, 7.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotSideEast = new BirsyModelRenderer(this, 29, 0);
        this.crockpotSideEast.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crockpotSideEast.addBox(-6.5F, 0.0F, -5.5F, 1.0F, 7.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotSideWest = new BirsyModelRenderer(this, 41, 18);
        this.crockpotSideWest.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crockpotSideWest.addBox(5.5F, 0.0F, -5.5F, 1.0F, 7.0F, 11.0F, 0.0F, 0.0F, 0.0F);

        this.crockpotRotationJoint = new BirsyModelRenderer(this, 0, 0);
        this.crockpotRotationJoint.setRotationPoint(5.0F, 20.0F, 10.0F);
        this.crockpotRotationJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.crockpotJoint = new BirsyModelRenderer(this, 0, 0);
        this.crockpotJoint.setRotationPoint(-5.0F, 0.0F, -10.0F);
        this.crockpotJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        this.crockpotJoint.addChild(this.crockpotTop);
        this.crockpotHandle1.addChild(this.crockpotHandle2);
        this.crockpotTop.addChild(this.crockpotSideWest);
        this.crockpotTop.addChild(this.crockpotSideNorth);
        this.crockpotTop.addChild(this.crockpotHandle1);
        this.crockpotTop.addChild(this.crockpotSideSouth);
        this.crockpotTop.addChild(this.crockpotSideEast);
        this.crockpotRotationJoint.addChild(this.crockpotJoint);
    }

    public void setOpenAmount(float openAmount) {
        float factor = MathUtils.ease(openAmount, MathUtils.EasingType.easeOutBack);
        this.crockpotRotationJoint.rotateAngleX = Math.min(0.0F, MathHelper.lerp(factor, 0.0F, (float) Math.toRadians(-50.0F)));
        this.crockpotRotationJoint.rotateAngleY = Math.max(0.0F, MathHelper.lerp(factor, 0.0F, (float) Math.toRadians(10.0F)));
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        this.crockpotRotationJoint.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }
}
