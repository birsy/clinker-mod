package birsy.clinker.client.render.entity.layers;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.item.materials.ClinkerArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeArmorLayer<T extends GnomeEntity, M extends GnomeModel<T>, A extends GnomeModel<T>> extends RenderLayer<T, M> {
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
   private final A modelLeggings;
   private final A modelArmor;

   public GnomeArmorLayer(RenderLayerParent<T, M> entityRenderer, A lowerLayer, A upperLayer) {
      super(entityRenderer);
      this.modelLeggings = lowerLayer;
      this.modelArmor = upperLayer;
   }

   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
	   modelArmor.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	   modelLeggings.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	   this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.CHEST, packedLightIn, this.armorRenderFetcher(EquipmentSlot.CHEST));
	   this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.LEGS, packedLightIn, this.armorRenderFetcher(EquipmentSlot.LEGS));
	   this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.FEET, packedLightIn, this.armorRenderFetcher(EquipmentSlot.FEET));
	   this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlot.HEAD, packedLightIn, this.armorRenderFetcher(EquipmentSlot.HEAD));
   }

   private void renderArmor(PoseStack matrixStackIn, MultiBufferSource bufferIn, T entityIn, EquipmentSlot slotType, int packedLightIn, A modelIn) {
      ItemStack itemstack = entityIn.getItemBySlot(slotType);
      if (itemstack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (armoritem.getSlot() == slotType) {
            this.getParentModel().setModelAttributes(modelIn);
            this.setModelSlotVisible(modelIn, slotType);
            boolean flag1 = itemstack.hasFoil();
            if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) {
               int i = ((net.minecraft.world.item.DyeableLeatherItem)armoritem).getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               this.renderArmorMesh(matrixStackIn, bufferIn, packedLightIn, flag1, modelIn, f, f1, f2, this.getArmorResource(entityIn, itemstack, slotType, null));
               this.renderArmorMesh(matrixStackIn, bufferIn, packedLightIn, flag1, modelIn, 1.0F, 1.0F, 1.0F, this.getArmorResource(entityIn, itemstack, slotType, "overlay"));
            } else {
               this.renderArmorMesh(matrixStackIn, bufferIn, packedLightIn, flag1, modelIn, 1.0F, 1.0F, 1.0F, this.getArmorResource(entityIn, itemstack, slotType, null));
            }
         }
      }
   }
   
   protected void setModelSlotVisible(A modelIn, EquipmentSlot slotIn) {
	   modelIn.setVisible(false);
	   switch(slotIn) {
	   case HEAD:
		   break;
	   case CHEST:
		   modelIn.gnomeBody.visible = true;
		   modelIn.gnomeLeftArm.visible = true;
		   modelIn.gnomeRightArm.visible = true;
		   break;
	   case LEGS:
		   modelIn.gnomeOveralls.visible = true;
		   modelIn.gnomeRightLeg.visible = true;
		   modelIn.gnomeLeftLeg.visible = true;
		   break;
	   case FEET:
		   modelIn.gnomeRightLeg.visible = true;
		   modelIn.gnomeLeftLeg.visible = true;
	   default:
		   break;
	   }
   }
   
   private void renderArmorMesh(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean hasOverlay, A modelIn, float red, float green, float blue, ResourceLocation armorResource) {
      VertexConsumer ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(armorResource), false, hasOverlay);
      modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
   }

   private A armorRenderFetcher(EquipmentSlot slotIn) {
      return (A)(this.isLegSlot(slotIn) ? this.modelLeggings : this.modelArmor);
   }

   private boolean isLegSlot(EquipmentSlot slotIn) {
      return slotIn == EquipmentSlot.LEGS;
   }
   
   public static <A extends GnomeModel<?>> A getGnomeArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, A _default)
   {
	   A model;
	   if (itemStack.getItem() instanceof ClinkerArmorItem) {
		   model = ((ClinkerArmorItem) itemStack.getItem()).getGnomeArmorModel(entityLiving, itemStack, slot, _default);
       } else {
    	   model = null;
       }
       return model == null ? _default : model;
   }
   
   protected A getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
	      return getGnomeArmorModel(entity, itemStack, slot, model);
   }
   
   public static String getGnomeArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type)
   {
       String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
       return result != null ? result : _default;
   }

   public ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
      ArmorItem item = (ArmorItem)stack.getItem();
      String texture = item.getMaterial().getName();
      String domain = "clinker";
      int idx = texture.indexOf(':');
      if (idx != -1) {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }
      String s1 = String.format("%s:textures/models/gnome_armor/%s_layer_%d%s.png", domain, texture, (isLegSlot(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type));

      s1 = getGnomeArmorTexture(entity, stack, s1, slot, type);
      ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);

      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s1);
         ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
      }

      return resourcelocation;
   }
}