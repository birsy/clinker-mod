package birsy.clinker.client.ambience;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerWorld;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class AmbienceHandler {
    public static final SurfaceAmbience SURFACE_AMBIENCE_HANDLER = new SurfaceAmbience(Minecraft.getInstance());

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        Minecraft.getInstance().getProfiler().push("clinker.ambienceTick");

        boolean shouldUpdate = shouldUpdate();
        SURFACE_AMBIENCE_HANDLER.tick(shouldUpdate);

        Minecraft.getInstance().getProfiler().pop();
    }

    @SubscribeEvent
    public static void onDraw(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Minecraft.getInstance().getProfiler().push("clinker.ambienceFrame");
            boolean shouldUpdate = shouldUpdate();
            Minecraft.getInstance().getProfiler().pop();
        }
    }

    private static boolean shouldUpdate() {
        return Minecraft.getInstance().level != null &&
               Minecraft.getInstance().level.dimension() == ClinkerWorld.OTHERSHORE &&
               Minecraft.getInstance().level.isLoaded(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition()) &&
               !Minecraft.getInstance().isPaused();
    }
}
