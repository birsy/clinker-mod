package birsy.clinker.client.render.world;

import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableAttachment;
import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import birsy.clinker.common.world.level.interactable.manager.InteractableManager;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import org.joml.Vector3d;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InteractableRenderer {
    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
//        if (InteractableManager.seenInteractable.isPresent()) {
//            if (!InteractableManager.seenInteractable.get().hasOutline) return;
//            event.setCanceled(InteractableManager.seenInteractable.get().hasOutline);
//        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            Minecraft minecraft = Minecraft.getInstance();
            EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();

            if (entityRenderDispatcher.shouldRenderHitBoxes()) {
                ClientInteractableManager manager = (ClientInteractableManager) InteractableAttachment.getInteractableManagerForLevel(minecraft.level);

                PoseStack stack = event.getPoseStack();
                stack.pushPose();
                Vec3 camPos = minecraft.gameRenderer.getMainCamera().getPosition();
                stack.translate(-camPos.x, -camPos.y, -camPos.z);

                for (Interactable interactable : manager.storage.getInteractables()) {
                    boolean isSeenInteractable = false;
                    if (manager.getSeenInteractable().isPresent()) isSeenInteractable = interactable == manager.getSeenInteractable().get();

                    stack.pushPose();
                    Vector3d position = interactable.getPosition(event.getPartialTick());
                    stack.translate(position.x, position.y, position.z);

                    DebugRenderUtil.renderBox(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.LINES),
                            interactable.getBounds().move(-position.x, -position.y, -position.z), 1F, 1F, 1F, 0.5F);

                    stack.mulPose(new Quaternionf(interactable.getOrientation(event.getPartialTick())));

                    DebugRenderUtil.renderBox(event.getPoseStack(), event.getLevelRenderer().renderBuffers.bufferSource().getBuffer(RenderType.LINES),
                            interactable.size.x() * -0.5F, interactable.size.y() * -0.5F, interactable.size.z() * -0.5F,
                            interactable.size.x() * 0.5F,  interactable.size.y() * 0.5F,  interactable.size.z() * 0.5F,
                            1.0F,  isSeenInteractable ? 1.0F : 0.5F, 0.5F, 1.0F);
                    stack.popPose();
                }
                stack.popPose();
            }
        }
    }
}
