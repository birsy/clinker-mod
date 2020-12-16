package birsy.clinker.common.item.materials;

import birsy.clinker.client.render.model.entity.GnomeModel2;
import birsy.clinker.core.Clinker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ClinkerArmorItem extends ArmorItem
{
	public ClinkerArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot) {
		super(materialIn, slot, new Item.Properties().group(Clinker.CLINKER_TOOLS));
	}
	
	public String getGnomeArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return null;
	}
	
	public <A extends GnomeModel2<?>> A getGnomeArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
		return null;
	}
}
