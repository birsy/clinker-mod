package birsy.clinker.client.render.layers;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.entity.GnomeModel2;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.item.materials.ClinkerArmorItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomeArmorLayer<T extends GnomeEntity, M extends GnomeModel2<T>, A extends GnomeModel2<T>> extends LayerRenderer<T, M> {
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
   private final A modelLeggings;
   private final A modelArmor;

   public GnomeArmorLayer(IEntityRenderer<T, M> entityRenderer, A lowerLayer, A upperLayer) {
      super(entityRenderer);
      this.modelLeggings = lowerLayer;
      this.modelArmor = upperLayer;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlotType.CHEST, packedLightIn, this.armorRenderFetcher(EquipmentSlotType.CHEST));
      this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlotType.LEGS, packedLightIn, this.armorRenderFetcher(EquipmentSlotType.LEGS));
      this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlotType.FEET, packedLightIn, this.armorRenderFetcher(EquipmentSlotType.FEET));
      this.renderArmor(matrixStackIn, bufferIn, entitylivingbaseIn, EquipmentSlotType.HEAD, packedLightIn, this.armorRenderFetcher(EquipmentSlotType.HEAD));
   }

   private void renderArmor(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityIn, EquipmentSlotType slotType, int packedLightIn, A modelIn) {
      ItemStack itemstack = entityIn.getItemStackFromSlot(slotType);
      if (itemstack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (armoritem.getEquipmentSlot() == slotType) {
            this.getEntityModel().setModelAttributes(modelIn);
            this.setModelSlotVisible(modelIn, slotType);
            boolean flag1 = itemstack.hasEffect();
            if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) {
               int i = ((net.minecraft.item.IDyeableArmorItem)armoritem).getColor(itemstack);
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
   
   protected void setModelSlotVisible(A modelIn, EquipmentSlotType slotIn) {
	   modelIn.setVisible(false);
	   switch(slotIn) {
	   case HEAD:
		   break;
	   case CHEST:
		   modelIn.gnomeBody.showModel = true;
		   modelIn.gnomeLeftArm.showModel = true;
		   modelIn.gnomeRightArm.showModel = true;
		   break;
	   case LEGS:
		   modelIn.gnomeOveralls.showModel = true;
		   modelIn.gnomeRightLeg.showModel = true;
		   modelIn.gnomeLeftLeg.showModel = true;
		   break;
	   case FEET:
		   modelIn.gnomeRightLeg.showModel = true;
		   modelIn.gnomeLeftLeg.showModel = true;
	   default:
		   break;
	   }
   }
   
   private void renderArmorMesh(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean hasOverlay, A modelIn, float red, float green, float blue, ResourceLocation armorResource) {
      IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(bufferIn, RenderType.getArmorCutoutNoCull(armorResource), false, hasOverlay);
      modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
   }

   private A armorRenderFetcher(EquipmentSlotType slotIn) {
      return (A)(this.isLegSlot(slotIn) ? this.modelLeggings : this.modelArmor);
   }

   private boolean isLegSlot(EquipmentSlotType slotIn) {
      return slotIn == EquipmentSlotType.LEGS;
   }
   
   public static <A extends GnomeModel2<?>> A getGnomeArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType slot, A _default)
   {
	   A model;
	   if (itemStack.getItem() instanceof ClinkerArmorItem) {
		   model = ((ClinkerArmorItem) itemStack.getItem()).getGnomeArmorModel(entityLiving, itemStack, slot, _default);
       } else {
    	   model = null;
       }
       return model == null ? _default : model;
   }
   
   protected A getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlotType slot, A model) {
	      return getGnomeArmorModel(entity, itemStack, slot, model);
   }
   
   public static String getGnomeArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlotType slot, String type)
   {
       String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
       return result != null ? result : _default;
   }

   public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, @Nullable String type) {
      ArmorItem item = (ArmorItem)stack.getItem();
      String texture = item.getArmorMaterial().getName();
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