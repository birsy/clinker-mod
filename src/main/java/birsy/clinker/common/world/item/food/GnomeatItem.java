package birsy.clinker.common.world.item.food;


import birsy.clinker.core.Clinker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class GnomeatItem extends Item
{	
	public GnomeatItem()
	{
		super(new Item.Properties()
				.food(new FoodProperties.Builder()
						.nutrition(2)
						.saturationMod(2.0F)
						.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 0.75F)
						.meat()
						.build())
		);
	}


	
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		pTooltipComponents.add(Component.translatable("item.gnomeat.lore").withStyle(ChatFormatting.GRAY));
	}
}
