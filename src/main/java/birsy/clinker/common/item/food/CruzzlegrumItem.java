package birsy.clinker.common.item.food;


import java.util.List;

import javax.annotation.Nullable;

import birsy.clinker.core.Clinker;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class CruzzlegrumItem extends Item
{	
	public CruzzlegrumItem()
	{
		super(new Item.Properties()
				.group(Clinker.CLINKER_FOOD)
				.food(new Food.Builder()
						.hunger(1)
						.saturation(1)
						.meat()
						.effect(() -> new EffectInstance(Effects.NAUSEA, 300, 1), 0.75F)
						.build())
		);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if(entityLiving instanceof PlayerEntity) {
			
		}
		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add((new TranslationTextComponent("item.gnomeat.lore")).mergeStyle(TextFormatting.GRAY));
	}
}
