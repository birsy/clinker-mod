package birsy.clinker.common.item.food;

import birsy.clinker.core.Clinker;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class FoodBase extends Item
{
	public FoodBase(int hunger, float saturation, MobEffect effect, int time, int level, float probability)
	{
		super(new Item.Properties()
				.tab(Clinker.CLINKER_FOOD)
				.food(new FoodProperties.Builder()
						.nutrition(hunger)
						.saturationMod(saturation)
						.effect(() -> new MobEffectInstance(effect, time, level), probability)
						.build())
		);
	}

}
