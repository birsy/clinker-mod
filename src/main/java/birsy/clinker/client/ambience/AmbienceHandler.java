package birsy.clinker.client.ambience;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerWorld;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class AmbienceHandler {
    public static final SurfaceAmbience SURFACE_AMBIENCE_HANDLER = new SurfaceAmbience(Minecraft.getInstance());

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (shouldntUpdate()) return;

        Minecraft.getInstance().getProfiler().push("clinker.ambienceTick");
        if (event.phase == TickEvent.Phase.END) {
            SURFACE_AMBIENCE_HANDLER.tick();
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    @SubscribeEvent
    public static void onDraw(RenderLevelStageEvent event) {
        if (shouldntUpdate()) return;

        Minecraft.getInstance().getProfiler().push("clinker.ambienceFrame");
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    private static boolean shouldntUpdate() {
        return Minecraft.getInstance().level == null ||
               Minecraft.getInstance().level.dimension() != ClinkerWorld.OTHERSHORE ||
              !Minecraft.getInstance().level.isLoaded(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition()) ||
               Minecraft.getInstance().isPaused();
    }
}
