package birsy.clinker.common.item.food;


import java.util.List;

import javax.annotation.Nullable;

import birsy.clinker.core.Clinker;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.effect.MobEffectInstance;

public class GnomeatItem extends Item
{	
	public GnomeatItem()
	{
		super(new Item.Properties()
				.tab(Clinker.CLINKER_FOOD)
				.food(new FoodProperties.Builder()
						.nutrition(2)
						.saturationMod(2)
						.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 0.75F)
						.build())
		);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add((new TranslatableComponent("item.gnomeat.lore")).withStyle(ChatFormatting.GRAY));
	}
}
