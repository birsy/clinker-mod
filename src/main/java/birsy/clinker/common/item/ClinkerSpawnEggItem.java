package birsy.clinker.common.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import net.minecraft.world.item.Item.Properties;

public class ClinkerSpawnEggItem extends SpawnEggItem
{
	protected static final List<ClinkerSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
	private final Lazy<? extends EntityType<?>> entityTypeSupplier;
	
	public ClinkerSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, int primaryColorIn, int secondaryColorIn, Properties builder)
	{
		super(null, primaryColorIn, secondaryColorIn, builder);
		this.entityTypeSupplier = Lazy.of(entityTypeSupplier);
		UNADDED_EGGS.add(this);
	}
	
	public static void initSpawnEggs()
	{
		final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "BY_ID");
		DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior()
			{
				@Override
				protected ItemStack execute(BlockSource source, ItemStack stack) 
				{
					Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
					EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
					type.spawn(source.getLevel(), stack, null, source.getPos(), MobSpawnType.DISPENSER, direction != Direction.UP, false);
					stack.shrink(1);
					return stack;
				}
			};
			
			for (final SpawnEggItem spawnEgg : UNADDED_EGGS)
			{
				EGGS.put(spawnEgg.getType(null), spawnEgg);
				DispenserBlock.registerBehavior(spawnEgg, dispenseBehavior);
			}
			UNADDED_EGGS.clear();
	}
	
	@Override
	public EntityType<?> getType(CompoundTag nbt)
	{
		return this.entityTypeSupplier.get();
	}
}
