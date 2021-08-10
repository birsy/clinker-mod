package birsy.clinker.client.render.entity.model;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.util.BirsyModelRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeadArmorModel<T extends LivingEntity> extends BipedModel<T> {
    private final EquipmentSlotType slot;
    private final LivingEntity entity;

    public BirsyModelRenderer armorRightPauldron;
    public BirsyModelRenderer armorHelmet;
    public BirsyModelRenderer armorHelmetTop;
    public BirsyModelRenderer armorRightHelmetFlap;
    public BirsyModelRenderer armorLeftHelmetFlap;
    public BirsyModelRenderer armorBackHelmetFlap;
    public BirsyModelRenderer armorChestplate;
    public BirsyModelRenderer armorFrontChestplateFlap;
    public BirsyModelRenderer armorBackChestplateFlap;
    public BirsyModelRenderer armorRightChestplateFlap;
    public BirsyModelRenderer armorLeftChestplateFlap;
    public BirsyModelRenderer armorLeftPauldron;

    private boolean isPiglin;
    private boolean isBaby;
    
    public LeadArmorModel(LivingEntity entityIn, EquipmentSlotType slotIn) {
        super(1.0F, 0.0F, 64, 64);
        this.slot = slotIn;
        this.isPiglin = entityIn instanceof AbstractPiglinEntity || entityIn instanceof ZombifiedPiglinEntity;
        this.isBaby = entityIn.isChild();
        this.entity = entityIn;
        
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.armorHelmet = new BirsyModelRenderer(this, 0, 0);
        this.armorHelmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armorHelmet.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.6F, 0.6F, 0.6F);

        this.armorHelmetTop = new BirsyModelRenderer(this, 32, 0);
        this.armorHelmetTop.setRotationPoint(0.0F, -8.6F, 0.0F);
        this.armorHelmetTop.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 2.0F, 6.0F, 0.6F, 0.1F, 0.6F);

        this.armorRightHelmetFlap = new BirsyModelRenderer(this, 46, 8);
        this.armorRightHelmetFlap.setRotationPoint(-4.6F, -5.25F, 0.0F);
        this.armorRightHelmetFlap.addBox(0.0F, 0.0F, -4.0F, 1.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.armorRightHelmetFlap.rotateAngleZ = (float) Math.toRadians(20);

        this.armorLeftHelmetFlap = new BirsyModelRenderer(this, 46, 8);
        this.armorLeftHelmetFlap.setRotationPoint(4.6F, -5.25F, 0.0F);
        this.armorLeftHelmetFlap.addBox(-1.0F, 0.0F, -4.0F, 1.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.armorLeftHelmetFlap.rotateAngleZ = (float) Math.toRadians(-20);

        this.armorBackHelmetFlap = new BirsyModelRenderer(this, 46, 20);
        this.armorBackHelmetFlap.setRotationPoint(0.0F, -5.25F, 4.6F);
        this.armorBackHelmetFlap.addBox(-4.0F, 0.0F, -1.0F, 8.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.armorBackHelmetFlap.rotateAngleX = (float) Math.toRadians(20);


        this.armorChestplate = new BirsyModelRenderer(this, 0, 16);
        this.armorChestplate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armorChestplate.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.0F, 1.0F, 1.0F);

        this.armorRightChestplateFlap = new BirsyModelRenderer(this, 24, 16);
        this.armorRightChestplateFlap.setRotationPoint(-4.6F, 9.0F, 0.0F);
        this.armorRightChestplateFlap.addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, 0.05F, 0.25F, 0.75F);
        this.armorRightChestplateFlap.rotateAngleZ = (float) Math.toRadians(15);

        this.armorLeftChestplateFlap = new BirsyModelRenderer(this, 24, 16);
        this.armorLeftChestplateFlap.setRotationPoint(4.6F, 9.0F, 0.0F);
        this.armorLeftChestplateFlap.addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, 0.05F, 0.25F, 0.75F);
        this.armorLeftChestplateFlap.rotateAngleZ = (float) Math.toRadians(-15);

        this.armorFrontChestplateFlap = new BirsyModelRenderer(this, 24, 25);
        this.armorFrontChestplateFlap.setRotationPoint(0.0F, 10.55F, -2.25F);
        this.armorFrontChestplateFlap.addBox(-2.0F, 0.0F, -0.5F, 4.0F, 5.0F, 1.0F, 0.8F, 0.0F, 0.0F);
        this.armorFrontChestplateFlap.rotateAngleX = (float) Math.toRadians(-10);

        this.armorBackChestplateFlap = new BirsyModelRenderer(this, 24, 25);
        this.armorBackChestplateFlap.mirror = true;
        this.armorBackChestplateFlap.setRotationPoint(0.0F, 10.55F, 2.25F);
        this.armorBackChestplateFlap.addBox(-2.0F, 0.0F, -0.5F, 4.0F, 5.0F, 1.0F, 0.8F, 0.0F, 0.0F);
        this.armorBackChestplateFlap.rotateAngleX = (float) Math.toRadians(10);

        this.armorLeftPauldron = new BirsyModelRenderer(this, 34, 23);
        this.armorLeftPauldron.mirror = true;
        this.armorLeftPauldron.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armorLeftPauldron.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.6F, 0.6F, 0.6F);

        this.armorRightPauldron = new BirsyModelRenderer(this, 34, 23);
        this.armorRightPauldron.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armorRightPauldron.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.6F, 0.6F, 0.6F);
        
        this.bipedLeftArm.addChild(this.armorLeftPauldron);
        this.bipedRightArm.addChild(this.armorRightPauldron);
        this.armorHelmet.addChild(this.armorRightHelmetFlap);
        this.armorHelmet.addChild(this.armorBackHelmetFlap);
        this.bipedBody.addChild(this.armorChestplate);
        this.armorHelmet.addChild(this.armorLeftHelmetFlap);
        this.armorChestplate.addChild(this.armorRightChestplateFlap);
        this.armorHelmet.addChild(this.armorHelmetTop);
        this.armorChestplate.addChild(this.armorFrontChestplateFlap);
        this.bipedHead.addChild(this.armorHelmet);
        this.armorChestplate.addChild(this.armorLeftChestplateFlap);
        this.armorChestplate.addChild(this.armorBackChestplateFlap);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        boolean flag = entity.getTicksElytraFlying() > 4;
        float f = 1.0F;
        if (flag) {
            f = (float) entity.getMotion().lengthSquared();
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        float bob = (MathHelper.cos(entity.limbSwing * 0.6662F * 1.5F) * 2.0F * entity.limbSwingAmount * 0.5F / f) * 0.15F;

        if (this.isSneak) {
            this.armorFrontChestplateFlap.rotationPointY = this.armorFrontChestplateFlap.defaultRotationPointY;
            this.armorFrontChestplateFlap.rotationPointY -= 1.0F;
        }
        this.armorFrontChestplateFlap.rotateAngleX = Math.min((float) Math.toRadians(-10), Math.min(this.bipedLeftLeg.rotateAngleX, this.bipedRightLeg.rotateAngleX)) - (this.bipedBody.rotateAngleX * 2);
        this.armorBackChestplateFlap.rotateAngleX = -(Math.min((float) Math.toRadians(-10), Math.min(this.bipedLeftLeg.rotateAngleX, this.bipedRightLeg.rotateAngleX))) - this.bipedBody.rotateAngleX;

        this.armorLeftChestplateFlap.rotateAngleZ = bob + (float) Math.toRadians(-15);
        this.armorRightChestplateFlap.rotateAngleZ = -bob + (float) Math.toRadians(15);

        if (this.slot == EquipmentSlotType.HEAD) {
        		if (isPiglin) {
        			matrixStack.scale(1.25F, 1.0F, 1.25F);
        		}
                matrixStack.push();
                this.armorHelmet.copyModelAngles(this.bipedHead);
                if (isBaby) {
                    matrixStack.scale(0.8F, 0.8F, 0.8F);
                    this.armorHelmet.setRotationPoint(0.0F, 15.0F, 0.0F);
                }
                this.armorHelmet.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                matrixStack.pop();
        }

        if (this.slot == EquipmentSlotType.CHEST) {
            matrixStack.push();
            this.armorChestplate.copyModelAngles(this.bipedBody);
            addParentRotation(this.armorLeftPauldron, this.bipedLeftArm);
            addParentRotation(this.armorRightPauldron, this.bipedRightArm);
            if (isBaby) {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                this.armorChestplate.setRotationPoint(0.0F, 24.0F, 0.0F);
                this.armorLeftPauldron.setRotationPoint(5.0F, 24.0F, 0.0F);
                this.armorRightPauldron.setRotationPoint(-5.0F, 24.0F, 0.0F);
            }
            this.armorLeftPauldron.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.armorRightPauldron.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.armorChestplate.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStack.pop();
        }
    }
    
    public void addParentRotation(BirsyModelRenderer modelRenderer, ModelRenderer modelRenderer1) {
    	modelRenderer.rotateAngleX = modelRenderer.rotateAngleX + modelRenderer1.rotateAngleX;
    	modelRenderer.rotateAngleY = modelRenderer.rotateAngleY + modelRenderer1.rotateAngleY;
    	modelRenderer.rotateAngleZ = modelRenderer.rotateAngleZ + modelRenderer1.rotateAngleZ;

        modelRenderer.rotationPointX = modelRenderer.rotationPointX + modelRenderer1.rotationPointX;
        modelRenderer.rotationPointY = modelRenderer.rotationPointY + modelRenderer1.rotationPointY;
        modelRenderer.rotationPointZ = modelRenderer.rotationPointZ + modelRenderer1.rotationPointZ;
    }
}
