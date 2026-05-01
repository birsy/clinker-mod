package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerNightVisionHandler {
    private static float nightVisionScale = 0.0F, previousNightVisionScale = 0.0F;

    @SubscribeEvent
    public static void initNightVisionScale(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() == Minecraft.getInstance().player) {
            boolean hasNightVision =  Minecraft.getInstance().player.hasEffect(MobEffects.NIGHT_VISION);
            nightVisionScale = hasNightVision ? 1 : 0;
            previousNightVisionScale = nightVisionScale;
        }
    }

    @SubscribeEvent
    public static void tickNightVisionScale(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isAddedToLevel()) return;

        previousNightVisionScale = nightVisionScale;

        boolean hasNightVision =  player.hasEffect(MobEffects.NIGHT_VISION);
        if (Minecraft.getInstance().isPaused()) return;
        nightVisionScale = Mth.lerp(0.05F, nightVisionScale, hasNightVision ? 1 : 0);
    }

    @SubscribeEvent
    public static void runNightVisionPipeline(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !player.isAddedToLevel()) return;

            if (nightVisionScale < 0.01F) return;

            float scale = Mth.lerp(event.getPartialTick().getGameTimeDeltaPartialTick(true), previousNightVisionScale, nightVisionScale);

            PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(ClinkerPostPipelines.BRIGHTNESS_CONTRAST_PIPELINE);
            pipeline.getUniformSafe("Brightness").setFloat(Mth.lerp(scale, 0, 1.5F));
            pipeline.getUniformSafe("Contrast").setFloat(Mth.lerp(scale, 1.0F, 4.0F));

            VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline);
        }
    }
}
