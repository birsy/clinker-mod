package birsy.clinker.common.world.item.materials.enums;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerItems;

import java.util.function.Supplier;

public enum ClinkerArmorMaterial implements ArmorMaterial
{	
	SHELL(Clinker.MOD_ID + ":shell", 23, new int[] { 3, 6, 4, 2 }, 23, SoundEvents.ARMOR_EQUIP_TURTLE, 1.5F, () -> {return Ingredient.of(ClinkerItems.CENTIPEDE_SHELL.get());}, 0.25F),
	LEAD(Clinker.MOD_ID + ":lead", 190, new int[] {2, 5, 6, 2}, 0, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, () -> {return Ingredient.of(ClinkerItems.LEAD_INGOT.get());}, 0.05F),
	TRADITIONAL(Clinker.MOD_ID + ":traditional", 2, new int[] { 1, 2, 2, 1 }, 100, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> {return Ingredient.of(Items.GRAY_WOOL);}, 0.0F);
	
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
	public int getDurabilityForSlot(EquipmentSlot slotIn) {
		return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamamgeFactor;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slotIn) {
		return this.damageReductionAmountArray[slotIn.getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.soundEvent;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Ingredient getRepairIngredient() {
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
