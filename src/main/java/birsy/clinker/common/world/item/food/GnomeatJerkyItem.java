package birsy.clinker.common.world.item.food;

import birsy.clinker.core.Clinker;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class GnomeatJerkyItem extends Item
{
	public GnomeatJerkyItem()
	{
		super(new Item.Properties()
				.food(new FoodProperties.Builder()
						.nutrition(5)
						.saturationMod(5.0F)
						.meat()
						.build())
		);
	}

}
