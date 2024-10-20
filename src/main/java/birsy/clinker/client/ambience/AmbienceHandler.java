package birsy.clinker.client.ambience;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class AmbienceHandler {
    private static final SurfaceAmbience surfaceAmbience = new SurfaceAmbience(Minecraft.getInstance());

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        Minecraft.getInstance().getProfiler().push("clinker.ambienceTick");
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().isPaused() || !Minecraft.getInstance().level.isLoaded(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition())) {
            Minecraft.getInstance().getProfiler().pop();
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            surfaceAmbience.tick();
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    @SubscribeEvent
    public static void onDraw(RenderLevelStageEvent event) {
        Minecraft.getInstance().getProfiler().push("clinker.ambienceFrame");
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().isPaused() || !Minecraft.getInstance().level.isLoaded(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition())) {
            Minecraft.getInstance().getProfiler().pop();
            return;
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {

        }
        Minecraft.getInstance().getProfiler().pop();
    }
}
