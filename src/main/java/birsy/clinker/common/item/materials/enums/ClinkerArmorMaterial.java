package birsy.clinker.common.item.materials.enums;

import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerItems;

public enum ClinkerArmorMaterial implements IArmorMaterial
{	
	SHELL(Clinker.MOD_ID + ":shell", 23, new int[] { 3, 6, 4, 2 }, 23, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 1.5F, () -> {return Ingredient.fromItems(ClinkerItems.CENTIPEDE_SHELL.get());}, 0.25F),
	LEAD(Clinker.MOD_ID + ":lead", 190, new int[] {2, 5, 6, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 4.0F, () -> {return Ingredient.fromItems(ClinkerItems.LEAD_INGOT.get());}, 0.05F),
	TRADITIONAL(Clinker.MOD_ID + ":traditional", 2, new int[] { 1, 2, 2, 1 }, 100, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, () -> {return Ingredient.fromItems(Items.GRAY_WOOL);}, 0.0F);
	
	private static final int[] MAX_DAMAGE_ARRAY = new int[] { 11, 16, 15, 13 };
	private final String name;
	private final int maxDamamgeFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final Supplier<Ingredient> repairMaterial;
	private final float knockbackResistance;
	
	ClinkerArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, Supplier<Ingredient> repairMaterial, float knockbackResistance)
	{
		this.name = name;
		this.maxDamamgeFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmountArray;
		this.enchantability = enchantability;
		this.soundEvent = soundEvent;
		this.toughness = toughness;
		this.repairMaterial = repairMaterial;
		this.knockbackResistance = knockbackResistance;
	}

	@Override
	public int getDurability(EquipmentSlotType slotIn) {
		return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamamgeFactor;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType slotIn) {
		return this.damageReductionAmountArray[slotIn.getIndex()];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getSoundEvent() {
		return this.soundEvent;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.get();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
