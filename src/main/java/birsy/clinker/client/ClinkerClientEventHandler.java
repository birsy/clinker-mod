package birsy.clinker.client;

import birsy.clinker.client.render.page.PageAtlas;
import birsy.clinker.client.render.page.PageRenderer;
import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Arrays;

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class ClinkerClientEventHandler {
    private static final ResourceLocation TEST_PAGE_LOCATION = Clinker.resource("test_page");

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Minecraft.getInstance().getProfiler().push("clinker.drawPageAtlas");

            //PageRenderer.drawPageTestStuffHaha();
            PageAtlas.INSTANCE.update();
            // upload test atlas
                if (Minecraft.getInstance().getConnection() != null) {
                    Page.PageLayout testPage = Minecraft.getInstance().getConnection().registryAccess()
                            .registryOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY).get(TEST_PAGE_LOCATION)
                            .getLayout(Minecraft.getInstance().getLanguageManager().getSelected());
                    int[] coordinates = new int[2];
                    PageAtlas.INSTANCE.tryReserveLayoutLocation(testPage, 1, coordinates);
                }

            Minecraft.getInstance().getProfiler().pop();
        }
    }
}
