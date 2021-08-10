package birsy.clinker.common.item.food;


import birsy.clinker.core.Clinker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class CruzzlegrumItem extends Item
{	
	public CruzzlegrumItem()
	{
		super(new Item.Properties()
				.tab(Clinker.CLINKER_FOOD)
				.food(new FoodProperties.Builder()
						.nutrition(0)
						.saturationMod(0)
						.meat()
						.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 0.75F)
						.build())
		);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if(entityLiving instanceof Player) {
			//((PlayerEntity)entityLiving).getFoodStats().addStats(Random.nextInt(2), Random.nextInt(2));
		}
		return super.finishUsingItem(stack, worldIn, entityLiving);
	}
}
