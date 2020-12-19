package birsy.clinker.common.item.food;


import birsy.clinker.core.Clinker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class CruzzlegrumItem extends Item
{	
	public CruzzlegrumItem()
	{
		super(new Item.Properties()
				.group(Clinker.CLINKER_FOOD)
				.food(new Food.Builder()
						.hunger(0)
						.saturation(0)
						.meat()
						.effect(() -> new EffectInstance(Effects.NAUSEA, 300, 1), 0.75F)
						.build())
		);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if(entityLiving instanceof PlayerEntity) {
			//((PlayerEntity)entityLiving).getFoodStats().addStats(Random.nextInt(2), Random.nextInt(2));
		}
		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}
}
