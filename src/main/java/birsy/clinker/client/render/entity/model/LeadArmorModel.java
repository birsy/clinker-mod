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
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeadArmorModel<T extends LivingEntity> extends BipedModel<T> {    
    public BirsyModelRenderer helmet;
    public BirsyModelRenderer chestplate;
    public BirsyModelRenderer leftShoulderPad;
    public BirsyModelRenderer rightShoulderPad;
    public BirsyModelRenderer leftHelmetFlap;
    public BirsyModelRenderer rightHelmetFlap;
    public BirsyModelRenderer upperHat;
    public BirsyModelRenderer cross;
    public BirsyModelRenderer leftChestplateFlap;
    public BirsyModelRenderer rightChestplateFlap;
    
    private static final Map<Integer, LeadArmorModel<? extends LivingEntity>> CACHE = new HashMap<>();
    private final EquipmentSlotType slot;
    private final byte entityIn;

    public LeadArmorModel(int entityIn) {
    	super(1.0F, 0.0F, 64, 64);
    	this.slot = EquipmentSlotType.values()[entityIn & 15];
    	this.entityIn = (byte) (entityIn >> 4);
    	
    	this.textureWidth = 64;
        this.textureHeight = 32;
        this.leftShoulderPad = new BirsyModelRenderer(this, 34, 16);
        this.leftShoulderPad.mirror = true;
        this.leftShoulderPad.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftShoulderPad.addBox(-0.75F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.3F, 0.3F, 0.3F);
        this.setRotateAngle(leftShoulderPad, 0.0F, 0.0F, -0.19198621771937624F);
        this.cross = new BirsyModelRenderer(this, 0, 0);
        this.cross.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cross.addBox(-1.5F, -14.0F, -0.5F, 3.0F, 4.0F, 1.0F, -0.25F, -0.25F, -0.25F);
        this.rightHelmetFlap = new BirsyModelRenderer(this, 0, 15);
        this.rightHelmetFlap.mirror = true;
        this.rightHelmetFlap.setRotationPoint(-4.5F, -3.5F, 0.0F);
        this.rightHelmetFlap.addBox(0.0F, -0.25F, -3.0F, 1.0F, 4.0F, 6.0F, 0.0F, -0.25F, 0.0F);
        this.setRotateAngle(rightHelmetFlap, 0.0F, 0.0F, 0.3490658503988659F);
        this.helmet = new BirsyModelRenderer(this, 0, 0);
        this.helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helmet.addBox(-4.5F, -8.1F, -4.5F, 9.0F, 6.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.rightShoulderPad = new BirsyModelRenderer(this, 34, 16);
        this.rightShoulderPad.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightShoulderPad.addBox(-3.75F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.3F, 0.3F, 0.3F);
        this.setRotateAngle(rightShoulderPad, 0.0F, 0.0F, 0.19198621771937624F);
        this.upperHat = new BirsyModelRenderer(this, 14, 15);
        this.upperHat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.upperHat.addBox(-2.5F, -10.75F, -2.5F, 5.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.leftChestplateFlap = new BirsyModelRenderer(this, 50, 16);
        this.leftChestplateFlap.setRotationPoint(4.25F, 11.0F, 0.0F);
        this.leftChestplateFlap.addBox(-1.0F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.5F);
        this.setRotateAngle(leftChestplateFlap, 0.0F, 0.0F, -0.3490658503988659F);
        this.chestplate = new BirsyModelRenderer(this, 36, 0);
        this.chestplate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.chestplate.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.6F, 0.6F, 0.6F);
        this.leftHelmetFlap = new BirsyModelRenderer(this, 0, 15);
        this.leftHelmetFlap.setRotationPoint(4.5F, -3.5F, 0.0F);
        this.leftHelmetFlap.addBox(-1.0F, -0.25F, -3.0F, 1.0F, 4.0F, 6.0F, 0.0F, -0.25F, 0.0F);
        this.setRotateAngle(leftHelmetFlap, 0.0F, 0.0F, -0.3490658503988659F);
        this.rightChestplateFlap = new BirsyModelRenderer(this, 50, 16);
        this.rightChestplateFlap.setRotationPoint(-4.25F, 11.0F, 0.0F);
        this.rightChestplateFlap.addBox(0.0F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.5F);
        this.setRotateAngle(rightChestplateFlap, 0.0F, 0.0F, 0.3490658503988659F);
        this.helmet.addChild(this.cross);
        this.helmet.addChild(this.rightHelmetFlap);
        this.helmet.addChild(this.upperHat);
        this.chestplate.addChild(this.leftChestplateFlap);
        this.helmet.addChild(this.leftHelmetFlap);
        this.chestplate.addChild(this.rightChestplateFlap);
    }
    
    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        boolean piglin = (this.entityIn & 2) > 0;
        boolean child = (this.entityIn & 4) > 0;

        if (this.slot == EquipmentSlotType.HEAD) {
        		if (piglin) {
        			matrixStack.scale(1.25F, 1.0F, 1.25F);
        		}
                matrixStack.push();
                this.helmet.copyModelAngles(this.bipedHead);
                if (child) {
                    matrixStack.scale(0.8F, 0.8F, 0.8F);
                    this.helmet.setRotationPoint(0.0F, 15.0F, 0.0F);
                }
                this.helmet.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                matrixStack.pop();
        }

        if (this.slot == EquipmentSlotType.CHEST) {
            matrixStack.push();
            this.chestplate.copyModelAngles(this.bipedBody);
            addParentRotation(this.leftShoulderPad, this.bipedLeftArm);
            addParentRotation(this.rightShoulderPad, this.bipedRightArm);
            if (child) {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                this.chestplate.setRotationPoint(0.0F, 24.0F, 0.0F);
                this.leftShoulderPad.setRotationPoint(5.0F, 24.0F, 0.0F);
                this.rightShoulderPad.setRotationPoint(-5.0F, 24.0F, 0.0F);
            }
            this.leftShoulderPad.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.rightShoulderPad.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.chestplate.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStack.pop();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <A extends BipedModel<?>> A getModel(EquipmentSlotType slot, LivingEntity entity) {
        boolean piglin = entity instanceof PiglinEntity || entity instanceof ZombifiedPiglinEntity;
        int entityFlag = (slot.ordinal() & 15) | (piglin ? 1 : 0) << 5 | (entity.isChild() ? 1 : 0) << 6;
        return (A) CACHE.computeIfAbsent(entityFlag, LeadArmorModel::new);
    }

    public void setRotateAngle(BirsyModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
        
        modelRenderer.defaultRotateAngleX = x;
        modelRenderer.defaultRotateAngleY = y;
        modelRenderer.defaultRotateAngleZ = z;
    }
    
    public void addParentRotation(BirsyModelRenderer modelRenderer, ModelRenderer modelRenderer1) {
    	modelRenderer.rotateAngleX = modelRenderer.defaultRotateAngleX;
		modelRenderer.rotateAngleY = modelRenderer.defaultRotateAngleY;
		modelRenderer.rotateAngleZ = modelRenderer.defaultRotateAngleZ;
    	
    	modelRenderer.rotateAngleX = modelRenderer.defaultRotateAngleX + modelRenderer1.rotateAngleX;
    	modelRenderer.rotateAngleY = modelRenderer.defaultRotateAngleY + modelRenderer1.rotateAngleY;
    	modelRenderer.rotateAngleZ = modelRenderer.defaultRotateAngleZ + modelRenderer1.rotateAngleZ;
    }
}
