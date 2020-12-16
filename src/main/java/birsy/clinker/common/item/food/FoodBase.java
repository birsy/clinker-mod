package birsy.clinker.common.item.food;

import birsy.clinker.core.Clinker;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class FoodBase extends Item
{
	public FoodBase(int hunger, float saturation, Effect effect, int time, int level, float probability)
	{
		super(new Item.Properties()
				.group(Clinker.CLINKER_FOOD)
				.food(new Food.Builder()
						.hunger(hunger)
						.saturation(saturation)
						.effect(() -> new EffectInstance(effect, time, level), probability)
						.build())
		);
	}

}
