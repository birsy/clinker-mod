package birsy.clinker.common.item.materials;

import birsy.clinker.client.render.entity.model.GnomeModel;
import birsy.clinker.core.Clinker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ClinkerArmorItem extends ArmorItem
{
	public ClinkerArmorItem(ArmorMaterial materialIn, EquipmentSlot slot) {
		super(materialIn, slot, new Item.Properties().tab(Clinker.CLINKER_TOOLS));
	}
	
	public String getGnomeArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return null;
	}
	
	public <A extends GnomeModel<?>> A getGnomeArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
		return null;
	}
}
