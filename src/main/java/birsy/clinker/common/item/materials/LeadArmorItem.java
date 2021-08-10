package birsy.clinker.common.item.materials;

import birsy.clinker.client.render.entity.model.LeadArmorModel;
import birsy.clinker.common.item.materials.enums.ClinkerArmorMaterial;
import birsy.clinker.core.Clinker;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LeadArmorItem extends ArmorItem
{
	public LeadArmorItem(EquipmentSlot slotIn) {
		super(ClinkerArmorMaterial.LEAD, slotIn, new Item.Properties().tab(Clinker.CLINKER_TOOLS));
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return Clinker.MOD_ID + ":textures/models/armor/lead_armor.png";
	}
	
	public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
		return (A) new LeadArmorModel<>(entityLiving, armorSlot);
	}
}
