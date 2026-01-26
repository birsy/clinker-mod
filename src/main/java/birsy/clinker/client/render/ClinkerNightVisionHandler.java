package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerNightVisionHandler {
    private static float nightVisionScale = 0.0F, previousNightVisionScale = 0.0F;

    @SubscribeEvent
    public static void tickNightVisionScale(ClientTickEvent.Post tick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isAddedToLevel()) return;
        previousNightVisionScale = nightVisionScale;
        nightVisionScale = Mth.lerp(0.05F, nightVisionScale, player.hasEffect(MobEffects.NIGHT_VISION) ? 1 : 0);
    }

    @SubscribeEvent
    public static void runNightVisionPipeline(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !player.isAddedToLevel()) return;

            if (nightVisionScale < 0.01F) return;

            float scale = Mth.lerp(event.getPartialTick().getGameTimeDeltaPartialTick(true), previousNightVisionScale, nightVisionScale);

            PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(ClinkerPostPipelines.BRIGHTNESS_CONTRAST_PIPELINE);
            pipeline.getOrCreateUniform("Brightness").setFloat(Mth.lerp(scale, 0, 1.5F));
            pipeline.getOrCreateUniform("Contrast").setFloat(Mth.lerp(scale, 1.0F, 4.0F));

            VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline);
        }
    }
}
