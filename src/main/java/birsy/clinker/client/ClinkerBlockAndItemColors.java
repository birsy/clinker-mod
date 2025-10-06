package birsy.clinker.client;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class ClinkerBlockAndItemColors {

    @SubscribeEvent
    public static void registerColorResolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        //event.register(SheetMossTintHandler.INSTANCE);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(SheetMossTintHandler.INSTANCE,
                ClinkerBlocks.SHEET_MOSS.get(),
                ClinkerBlocks.LONG_SHEET_MOSS.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(SheetMossTintHandler.INSTANCE,
                ClinkerBlocks.SHEET_MOSS.get(),
                ClinkerBlocks.LONG_SHEET_MOSS.get()
        );
    }
}
