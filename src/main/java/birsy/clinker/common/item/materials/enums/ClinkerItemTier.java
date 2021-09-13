package birsy.clinker.common.item.materials.enums;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Supplier;

import birsy.clinker.core.registry.ClinkerItems;;

public enum ClinkerItemTier implements IItemTier
{
	LEAD(2, 10430, 6.0F, 2.0F, 0, () -> {return Ingredient.fromItems(ClinkerItems.LEAD_INGOT.get());});
	
	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final Supplier<Ingredient> repairMaterial;
	
	ClinkerItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial)
	{
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairMaterial = repairMaterial;
	}
	
	@Override
	public int getMaxUses() {
		return maxUses;
	}

	@Override
	public float getEfficiency() {
		return efficiency;
	}

	@Override
	public float getAttackDamage() {
		return attackDamage;
	}

	@Override
	public int getHarvestLevel() {
		return harvestLevel;
	}

	@Override
	public int getEnchantability() {
		return enchantability;
	}

	@Override
	public Ingredient getRepairMaterial() {
		return repairMaterial.get();
	}

}
