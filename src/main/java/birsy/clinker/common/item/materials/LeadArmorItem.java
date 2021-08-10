package birsy.clinker.common.item.materials;

import birsy.clinker.client.render.entity.model.LeadArmorModel;
import birsy.clinker.common.item.materials.enums.ClinkerArmorMaterial;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LeadArmorItem extends ArmorItem
{
	public LeadArmorItem(EquipmentSlotType slotIn) {
		super(ClinkerArmorMaterial.LEAD, slotIn, new Item.Properties().group(Clinker.CLINKER_TOOLS));
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return Clinker.MOD_ID + ":textures/models/armor/lead_armor.png";
	}
	
	public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
		return (A) new LeadArmorModel<>(entityLiving, armorSlot);
	}
}
