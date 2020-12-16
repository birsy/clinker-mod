package birsy.clinker.core.util;

import birsy.clinker.client.render.entity.CopterGnomadRenderer;
import birsy.clinker.client.render.entity.GnomadAxemanRenderer;
import birsy.clinker.client.render.entity.GnomadShamanRenderer;
import birsy.clinker.client.render.entity.GnomeBratRenderer;
import birsy.clinker.client.render.entity.GnomeRenderer;
import birsy.clinker.client.render.entity.HyenaRenderer;
import birsy.clinker.client.render.entity.SnailRenderer;
import birsy.clinker.client.render.entity.TorAntRenderer;
import birsy.clinker.client.render.entity.WitchBrickRenderer;
import birsy.clinker.client.render.entity.WitherRevenantRenderer;
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
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.SNAIL.get(), SnailRenderer::new);
		
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOME.get(), GnomeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOME_BRAT.get(), GnomeBratRenderer::new);
		
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOMAD_AXEMAN.get(), GnomadAxemanRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOMAD_SHAMAN.get(), GnomadShamanRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.COPTER_GNOMAD.get(), CopterGnomadRenderer::new);
		
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.HYENA.get(), HyenaRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.TOR_ANT.get(), TorAntRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.WITHER_REVENANT.get(), WitherRevenantRenderer::new);
		
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.WITCH_BRICK.get(), WitchBrickRenderer::new);
	}
	
	@SubscribeEvent
	public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
		ClinkerSpawnEggItem.initSpawnEggs();
	}
}
