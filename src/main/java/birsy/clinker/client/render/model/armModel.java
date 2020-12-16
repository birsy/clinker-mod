package birsy.clinker.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * armModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class armModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer end;
    public BirsyModelRenderer upper;
    public BirsyModelRenderer lower;

    public armModel() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.upper = new BirsyModelRenderer(this, 0, 0);
        this.upper.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.upper.addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, -0.5F, 0.0F, 0.0F);
        this.lower = new BirsyModelRenderer(this, 0, 4);
        this.lower.setRotationPoint(8.0F, 0.0F, 0.0F);
        this.lower.addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, -0.5F, 0.0F, 0.0F);
        this.end = new BirsyModelRenderer(this, 0, 8);
        this.end.setRotationPoint(16.0F, 0.0F, 0.0F);
        this.end.addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, -0.5F, -0.25F, -0.25F);
        this.upper.addChild(this.lower);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.upper, this.end).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	resetParts(this.end, this.upper, this.lower);
    	bob(this.end, 2.0F, 2.0F, true, ageInTicks, 0.5F, true);
    	
    	//inverseKinematicsBase(this.upper, this.lower, this.end.modelRendererPosition);
    }
}
