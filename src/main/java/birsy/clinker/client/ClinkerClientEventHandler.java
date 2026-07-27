package birsy.clinker.client;

import birsy.clinker.client.render.page.PageAtlas;
import birsy.clinker.client.render.world.OthershoreDimensionEffects;
import birsy.clinker.client.resource.CounterTransformOverrideResource;
import birsy.clinker.client.resource.localization.LongStringLocalizationReloader;
import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataComponents;
import birsy.clinker.core.registry.ClinkerItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class ClinkerClientEventHandler {
    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new LongStringLocalizationReloader());
        event.registerReloadListener(new CounterTransformOverrideResource.Reloader());
    }

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(Clinker.resource("othershore"), new OthershoreDimensionEffects());
    }

    @SubscribeEvent
    public static void onLevelTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().isPaused()) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.effects() instanceof OthershoreDimensionEffects othershoreDimensionEffects) {
            othershoreDimensionEffects.tick();
        }
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Minecraft.getInstance().getProfiler().push("clinker.drawPageAtlas");
            if (PageAtlas.INSTANCE != null) PageAtlas.INSTANCE.update();
            Minecraft.getInstance().getProfiler().pop();
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            if (level.effects() instanceof OthershoreDimensionEffects effects) {
                Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                effects.drawCloudsCustom(level,
                        event.getRenderTick(),
                        event.getPartialTick().getGameTimeDeltaPartialTick(false),
                        event.getPoseStack(),
                        camPos.x, camPos.y, camPos.z,
                        event.getModelViewMatrix(),
                        event.getProjectionMatrix()
                );
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent renderHandEvent) {
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity instanceof LivingEntity entity) {
            InteractionHand oppositeHand = renderHandEvent.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

            ItemStack handStack = renderHandEvent.getItemStack();
            ItemStack oppositeHandStack = entity.getItemInHand(oppositeHand);

            // don't draw hand if it's empty and we're holding a page in the other hand.
            if (handStack.isEmpty() && oppositeHandStack.is(ClinkerItems.PAGE)) {
                renderHandEvent.setCanceled(true);
                return;
            }

            // we're rendering the page
            if (handStack.is(ClinkerItems.PAGE)) {
                boolean holdingWithBothHands = oppositeHandStack.isEmpty();
                VertexConsumer consumer = renderHandEvent.getMultiBufferSource().getBuffer(RenderType.entityCutoutNoCull(PageAtlas.LOCATION));
                PoseStack stack = renderHandEvent.getPoseStack();
                stack.pushPose();
                stack.translate(0, 0, -2);
                stack.mulPose(Axis.XP.rotationDegrees(90));
                PoseStack.Pose pose = stack.last();

                if (PageAtlas.INSTANCE != null) {
                    float[] uvs = new float[4];
                    Page.PageLayout layout = handStack.get(ClinkerDataComponents.PAGE).page().value().getLayout(Minecraft.getInstance().getLanguageManager().getSelected());
                    PageAtlas.INSTANCE.tryReserveLayoutLocation(layout, 999, uvs);
                    float halfWidth = ((float) Page.PAGE_WIDTH / Page.PAGE_HEIGHT),
                          halfHeight = 1.0F;
                    consumer.addVertex(pose, -halfWidth, 0,  halfHeight).setColor(1F, 1F, 1F, 1F)
                            .setUv(uvs[0], 1 - uvs[3]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(renderHandEvent.getPackedLight()).setNormal(pose, 0, 1, 0);
                    consumer.addVertex(pose, -halfWidth, 0, -halfHeight).setColor(1F, 1F, 1F, 1F)
                            .setUv(uvs[0], 1 - uvs[1]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(renderHandEvent.getPackedLight()).setNormal(pose, 0, 1, 0);
                    consumer.addVertex(pose,  halfWidth, 0, -halfHeight).setColor(1F, 1F, 1F, 1F)
                            .setUv(uvs[2], 1 - uvs[1]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(renderHandEvent.getPackedLight()).setNormal(pose, 0, 1, 0);
                    consumer.addVertex(pose,  halfWidth, 0,  halfHeight).setColor(1F, 1F, 1F, 1F)
                            .setUv(uvs[2], 1 - uvs[3]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(renderHandEvent.getPackedLight()).setNormal(pose, 0, 1, 0);
                }

                stack.popPose();
            }
        }
    }
}
