package birsy.clinker.common.item;

import birsy.clinker.client.entity.item.LeadArmorRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LeadArmorItem extends ArmorItem {
    public LeadArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }
    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot equipmentSlot, ArmorMaterial.Layer layer, boolean innerModel) {
        return equipmentSlot == EquipmentSlot.CHEST || equipmentSlot == EquipmentSlot.HEAD ? LeadArmorRenderer.TEXTURE_LOCATION : null;
    }
}
