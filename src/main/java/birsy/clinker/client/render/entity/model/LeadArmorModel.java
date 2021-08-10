package birsy.clinker.client.render.entity.model;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.util.BirsyModelRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import LivingEntity;

@OnlyIn(Dist.CLIENT)
public class LeadArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    private final EquipmentSlot slot;
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
    
    public LeadArmorModel(LivingEntity entityIn, EquipmentSlot slotIn) {
        super(1.0F, 0.0F, 64, 64);
        this.slot = slotIn;
        this.isPiglin = entityIn instanceof AbstractPiglin || entityIn instanceof ZombifiedPiglin;
        this.isBaby = entityIn.isBaby();
        this.entity = entityIn;
        
        this.texWidth = 64;
        this.texHeight = 32;

        this.armorHelmet = new BirsyModelRenderer(this, 0, 0);
        this.armorHelmet.setPos(0.0F, 0.0F, 0.0F);
        this.armorHelmet.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.6F, 0.6F, 0.6F);

        this.armorHelmetTop = new BirsyModelRenderer(this, 32, 0);
        this.armorHelmetTop.setPos(0.0F, -8.6F, 0.0F);
        this.armorHelmetTop.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 2.0F, 6.0F, 0.6F, 0.1F, 0.6F);

        this.armorRightHelmetFlap = new BirsyModelRenderer(this, 46, 8);
        this.armorRightHelmetFlap.setPos(-4.6F, -5.25F, 0.0F);
        this.armorRightHelmetFlap.addBox(0.0F, 0.0F, -4.0F, 1.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.armorRightHelmetFlap.zRot = (float) Math.toRadians(20);

        this.armorLeftHelmetFlap = new BirsyModelRenderer(this, 46, 8);
        this.armorLeftHelmetFlap.setPos(4.6F, -5.25F, 0.0F);
        this.armorLeftHelmetFlap.addBox(-1.0F, 0.0F, -4.0F, 1.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.armorLeftHelmetFlap.zRot = (float) Math.toRadians(-20);

        this.armorBackHelmetFlap = new BirsyModelRenderer(this, 46, 20);
        this.armorBackHelmetFlap.setPos(0.0F, -5.25F, 4.6F);
        this.armorBackHelmetFlap.addBox(-4.0F, 0.0F, -1.0F, 8.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.armorBackHelmetFlap.xRot = (float) Math.toRadians(20);


        this.armorChestplate = new BirsyModelRenderer(this, 0, 16);
        this.armorChestplate.setPos(0.0F, 0.0F, 0.0F);
        this.armorChestplate.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.0F, 1.0F, 1.0F);

        this.armorRightChestplateFlap = new BirsyModelRenderer(this, 24, 16);
        this.armorRightChestplateFlap.setPos(-4.6F, 9.0F, 0.0F);
        this.armorRightChestplateFlap.addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, 0.05F, 0.25F, 0.75F);
        this.armorRightChestplateFlap.zRot = (float) Math.toRadians(15);

        this.armorLeftChestplateFlap = new BirsyModelRenderer(this, 24, 16);
        this.armorLeftChestplateFlap.setPos(4.6F, 9.0F, 0.0F);
        this.armorLeftChestplateFlap.addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, 0.05F, 0.25F, 0.75F);
        this.armorLeftChestplateFlap.zRot = (float) Math.toRadians(-15);

        this.armorFrontChestplateFlap = new BirsyModelRenderer(this, 24, 25);
        this.armorFrontChestplateFlap.setPos(0.0F, 10.55F, -2.25F);
        this.armorFrontChestplateFlap.addBox(-2.0F, 0.0F, -0.5F, 4.0F, 5.0F, 1.0F, 0.8F, 0.0F, 0.0F);
        this.armorFrontChestplateFlap.xRot = (float) Math.toRadians(-10);

        this.armorBackChestplateFlap = new BirsyModelRenderer(this, 24, 25);
        this.armorBackChestplateFlap.mirror = true;
        this.armorBackChestplateFlap.setPos(0.0F, 10.55F, 2.25F);
        this.armorBackChestplateFlap.addBox(-2.0F, 0.0F, -0.5F, 4.0F, 5.0F, 1.0F, 0.8F, 0.0F, 0.0F);
        this.armorBackChestplateFlap.xRot = (float) Math.toRadians(10);

        this.armorLeftPauldron = new BirsyModelRenderer(this, 34, 23);
        this.armorLeftPauldron.mirror = true;
        this.armorLeftPauldron.setPos(0.0F, 0.0F, 0.0F);
        this.armorLeftPauldron.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.6F, 0.6F, 0.6F);

        this.armorRightPauldron = new BirsyModelRenderer(this, 34, 23);
        this.armorRightPauldron.setPos(0.0F, 0.0F, 0.0F);
        this.armorRightPauldron.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.6F, 0.6F, 0.6F);
        
        this.leftArm.addChild(this.armorLeftPauldron);
        this.rightArm.addChild(this.armorRightPauldron);
        this.armorHelmet.addChild(this.armorRightHelmetFlap);
        this.armorHelmet.addChild(this.armorBackHelmetFlap);
        this.body.addChild(this.armorChestplate);
        this.armorHelmet.addChild(this.armorLeftHelmetFlap);
        this.armorChestplate.addChild(this.armorRightChestplateFlap);
        this.armorHelmet.addChild(this.armorHelmetTop);
        this.armorChestplate.addChild(this.armorFrontChestplateFlap);
        this.head.addChild(this.armorHelmet);
        this.armorChestplate.addChild(this.armorLeftChestplateFlap);
        this.armorChestplate.addChild(this.armorBackChestplateFlap);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        boolean flag = entity.getFallFlyingTicks() > 4;
        float f = 1.0F;
        if (flag) {
            f = (float) entity.getDeltaMovement().lengthSqr();
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        float bob = (Mth.cos(entity.animationPosition * 0.6662F * 1.5F) * 2.0F * entity.animationSpeed * 0.5F / f) * 0.15F;

        if (this.crouching) {
            this.armorFrontChestplateFlap.y = this.armorFrontChestplateFlap.defaultRotationPointY;
            this.armorFrontChestplateFlap.y -= 1.0F;
        }
        this.armorFrontChestplateFlap.xRot = Math.min((float) Math.toRadians(-10), Math.min(this.leftLeg.xRot, this.rightLeg.xRot)) - (this.body.xRot * 2);
        this.armorBackChestplateFlap.xRot = -(Math.min((float) Math.toRadians(-10), Math.min(this.leftLeg.xRot, this.rightLeg.xRot))) - this.body.xRot;

        this.armorLeftChestplateFlap.zRot = bob + (float) Math.toRadians(-15);
        this.armorRightChestplateFlap.zRot = -bob + (float) Math.toRadians(15);

        if (this.slot == EquipmentSlot.HEAD) {
        		if (isPiglin) {
        			matrixStack.scale(1.25F, 1.0F, 1.25F);
        		}
                matrixStack.pushPose();
                this.armorHelmet.copyFrom(this.head);
                if (isBaby) {
                    matrixStack.scale(0.8F, 0.8F, 0.8F);
                    this.armorHelmet.setPos(0.0F, 15.0F, 0.0F);
                }
                this.armorHelmet.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                matrixStack.popPose();
        }

        if (this.slot == EquipmentSlot.CHEST) {
            matrixStack.pushPose();
            this.armorChestplate.copyFrom(this.body);
            addParentRotation(this.armorLeftPauldron, this.leftArm);
            addParentRotation(this.armorRightPauldron, this.rightArm);
            if (isBaby) {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                this.armorChestplate.setPos(0.0F, 24.0F, 0.0F);
                this.armorLeftPauldron.setPos(5.0F, 24.0F, 0.0F);
                this.armorRightPauldron.setPos(-5.0F, 24.0F, 0.0F);
            }
            this.armorLeftPauldron.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.armorRightPauldron.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.armorChestplate.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStack.popPose();
        }
    }
    
    public void addParentRotation(BirsyModelRenderer modelRenderer, ModelPart modelRenderer1) {
    	modelRenderer.xRot = modelRenderer.xRot + modelRenderer1.xRot;
    	modelRenderer.yRot = modelRenderer.yRot + modelRenderer1.yRot;
    	modelRenderer.zRot = modelRenderer.zRot + modelRenderer1.zRot;

        modelRenderer.x = modelRenderer.x + modelRenderer1.x;
        modelRenderer.y = modelRenderer.y + modelRenderer1.y;
        modelRenderer.z = modelRenderer.z + modelRenderer1.z;
    }
}
