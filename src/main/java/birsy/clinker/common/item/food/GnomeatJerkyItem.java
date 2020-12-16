package birsy.clinker.common.item.food;

import birsy.clinker.core.Clinker;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class GnomeatJerkyItem extends Item
{
	public GnomeatJerkyItem()
	{
		super(new Item.Properties()
				.group(Clinker.CLINKER_FOOD)
				.food(new Food.Builder()
						.hunger(5)
						.saturation(5.0F)
						.build())
		);
	}

}
