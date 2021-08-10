package birsy.clinker.core.util;

import birsy.clinker.client.render.entity.*;
import birsy.clinker.common.entity.monster.ShoggothBodyEntity;
import birsy.clinker.common.item.ClinkerSpawnEggItem;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event)
	{
		ClinkerEntities.clientSetup();
	}
	
	@SubscribeEvent
	public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
		ClinkerSpawnEggItem.initSpawnEggs();
	}
}
