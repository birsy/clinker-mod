package birsy.clinker.client.render.world.item;

import birsy.clinker.common.world.item.AlchemistsCrossbowItem;
import birsy.clinker.common.world.item.components.CrossbowState;
import birsy.clinker.common.world.item.components.LoadedItemStack;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class AlchemistsCrossbowInHandRenderer {
    float handMoveDown = 0;
    public static float getPullPercentage(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (entity == null) return 0.0F;
        CrossbowState crossbowState = AlchemistsCrossbowItem.getCrossbowState(stack);

        switch (crossbowState) {
            case LOADING -> {
                return Math.clamp(entity.getTicksUsingItem() / (float) AlchemistsCrossbowItem.ITEM_LOAD_TIME, 0, 1);
            }
            case FIRING -> {
                LoadedItemStack loadedItemStack = AlchemistsCrossbowItem.getLoadedItems(stack);
                return (float)loadedItemStack.stack().getCount() / loadedItemStack.lastCount();
            }
            case LOADED -> { return 1.0F; }
            default -> { return 0.0F; }
        }
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity)) return;

        LivingEntity player = (LivingEntity) Minecraft.getInstance().cameraEntity;
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ItemStack crossbow = event.getItemStack();
        boolean hasRepeater = AlchemistsCrossbowItem.hasRepeater(crossbow);

        InteractionHand oppositeHand = event.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack oppositeHandItem = player.getItemInHand(oppositeHand);
        if (crossbow.getItem() instanceof AlchemistsCrossbowItem) {
            CrossbowState crossbowState = AlchemistsCrossbowItem.getCrossbowState(crossbow);
            if (crossbowState == CrossbowState.STANDBY) return;
            HumanoidArm arm = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            float direction = arm == HumanoidArm.LEFT ? -1 : 1;
            PoseStack poseStack = new PoseStack();
            float partialTicks = event.getPartialTick();

            poseStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));

            poseStack.pushPose();
            // view bobbing
            if (localPlayer != null) {
                float playerStep = localPlayer.walkDist - localPlayer.walkDistO;
                float stepSize = -(localPlayer.walkDist + playerStep * partialTicks);
                float viewBob = Mth.lerp(partialTicks, localPlayer.oBob, localPlayer.bob);

                Quaternionf bobXRotation = Axis.XP.rotationDegrees(Math.abs(Mth.cos(stepSize * (float) Math.PI - 0.2f) * viewBob) * 5f);
                poseStack.mulPose(bobXRotation.conjugate());
                Quaternionf bobZRotation = Axis.ZP.rotationDegrees(Mth.sin(stepSize * (float) Math.PI) * viewBob * 3f);
                poseStack.mulPose(bobZRotation.conjugate());
                poseStack.translate(-Mth.sin(stepSize * (float) Math.PI) * viewBob * 0.5f, -Math.abs(Mth.cos(stepSize * (float) Math.PI) * viewBob), 0f);

                float f2 = Mth.lerp(event.getPartialTick(), localPlayer.xBobO, localPlayer.xBob);
                float f3 = Mth.lerp(event.getPartialTick(), localPlayer.yBobO, localPlayer.yBob);
                poseStack.mulPose(Axis.XN.rotationDegrees((localPlayer.getViewXRot(partialTicks) - f2) * 0.1F));
                poseStack.mulPose(Axis.YP.rotationDegrees((localPlayer.getViewYRot(partialTicks) - f3) * 0.1F));
            }
            float equipProgress = event.getEquipProgress();
            if (player.isUsingItem()) {
                equipProgress = 0;
            }
            poseStack.translate(0, - equipProgress, 0);

            // render hands while loading
            if (localPlayer != null && crossbowState == CrossbowState.LOADING) {
                int offset = 4;
                float animTime = ((localPlayer.getTicksUsingItem() - offset + partialTicks) / AlchemistsCrossbowItem.ITEM_LOAD_TIME) % 1.0F;
                float pullBackFactor = animTime;
                if (!hasRepeater) {
                    pullBackFactor = Math.clamp((localPlayer.getTicksUsingItem() + partialTicks) / (AlchemistsCrossbowItem.ITEM_LOAD_TIME), 0, 1);
                } else {
                    float returnTime = 0.8F;
                    if (animTime < returnTime) {
                        pullBackFactor = MathUtils.mapRange(0F, returnTime, 0F, 1F, pullBackFactor);
                    } else {
                        pullBackFactor = MathUtils.mapRange(returnTime, 1F, 1F, 0F, pullBackFactor);
                        pullBackFactor = MathUtils.ease(pullBackFactor, MathUtils.EasingType.easeInBack);
                    }
                    if (localPlayer.getTicksUsingItem() < offset) {
                        pullBackFactor = 0;
                    }
                }

                RenderSystem.setShaderTexture(0, localPlayer.getSkin().texture());
                PlayerRenderer playerrenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(localPlayer);
                poseStack.pushPose();
                poseStack.translate(direction * 0.1F, -0.8, 0.7);
                poseStack.translate(pullBackFactor * 0.16F * direction, 0, pullBackFactor * -0.4F);
                poseStack.mulPose(Axis.YP.rotationDegrees(30 * direction));
                poseStack.mulPose(Axis.XP.rotationDegrees(40));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90 * direction));

                LoadedItemStack ammo = AlchemistsCrossbowItem.getLoadedItems(crossbow);
                float ammoOffset = -Math.min(3, 3) / 16.0F;
                poseStack.translate(0, ammoOffset, ammoOffset);

                if (arm != HumanoidArm.RIGHT) {
                    playerrenderer.renderRightHand(poseStack, event.getMultiBufferSource(), event.getPackedLight(), localPlayer);
                } else {
                    playerrenderer.renderLeftHand(poseStack, event.getMultiBufferSource(), event.getPackedLight(), localPlayer);
                }

                poseStack.pushPose();

                poseStack.translate(0.2 * direction, 0.7, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90 * direction));
                poseStack.mulPose(Axis.XP.rotationDegrees(-20));
                poseStack.mulPose(Axis.YP.rotationDegrees(20 * direction));
                poseStack.translate(0.0, 0.1, -0.05);
                poseStack.scale(0.8F,0.8F,0.8F);
                BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer()
                        .getModel(player.getItemInHand(oppositeHand), player.level(), player, player.getId());
                Minecraft.getInstance().getItemRenderer()
                        .render(crossbow,
                                arm != HumanoidArm.LEFT ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                                arm != HumanoidArm.LEFT, poseStack,
                                event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, bakedmodel);

                poseStack.popPose();

                poseStack.popPose();
            }



            if (crossbowState == CrossbowState.LOADING) { //crossbowState == CrossbowState.LOADING
                poseStack.translate(direction * -0.25F, -0.55, 1.2);
                poseStack.mulPose(Axis.XP.rotationDegrees(80));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45 + direction * 25));

            } else {
                poseStack.translate(0, -0.55, 1);
                poseStack.mulPose(Axis.XP.rotationDegrees(80));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            }


            BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer()
                    .getModel(crossbow, player.level(), player, player.getId());
            Minecraft.getInstance().getItemRenderer()
                    .render(crossbow, ItemDisplayContext.FIXED, arm == HumanoidArm.LEFT, poseStack,
                            event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, bakedmodel);
            // render items loaded into the crossbow
            LoadedItemStack loadedItemStack = AlchemistsCrossbowItem.getLoadedItems(crossbow);
            if (!loadedItemStack.isEmpty()) {
                poseStack.scale(0.8F, 0.8F, 0.8F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                BakedModel ammoModel = Minecraft.getInstance().getItemRenderer()
                        .getModel(loadedItemStack.stack(), player.level(), player, player.getId());
                float j = 1;
                for (int i = 0; i < Math.min(loadedItemStack.stack().getCount(), 3); i++) {
                    poseStack.pushPose();
                    poseStack.translate(0, 0,  -(i + 1) / 16.0F);
                    j *= -1;
                    poseStack.mulPose(Axis.ZP.rotationDegrees(j * 5));
                    Minecraft.getInstance().getItemRenderer()
                            .render(loadedItemStack.stack(), ItemDisplayContext.FIXED, arm == HumanoidArm.LEFT, poseStack,
                                    event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, ammoModel);
                    poseStack.popPose();
                }
                if (loadedItemStack.stack().getCount() > 3) {
                    for (int i = 3; i < Math.min(loadedItemStack.stack().getCount(), 12); i++) {
                        poseStack.pushPose();

                        float terribleRandomA = Mth.frac(((i-3) / 8.0F) * Mth.PI * 2048.0F);
                        float terribleRandomB = Mth.frac(((i-3) / 8.0F) * Mth.PI * 2048.0F * 2);
                        float terribleRandomC = Mth.frac(((i-3) / 8.0F) * Mth.PI * 2048.0F * 3);

                        float diagonalOffset = Mth.lerp(terribleRandomA, -0.07F, 0.07F);
                        float verticalOffset = Mth.lerp(terribleRandomB, -(1.0F / 16.0F), -(3.0F / 16.0F));
                        poseStack.translate(diagonalOffset, diagonalOffset, verticalOffset);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(terribleRandomC * 7));
                        Minecraft.getInstance().getItemRenderer()
                                .render(loadedItemStack.stack(), ItemDisplayContext.FIXED, arm == HumanoidArm.LEFT, poseStack,
                                        event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, ammoModel);
                        poseStack.popPose();
                    }
                }
            }
            poseStack.pushPose();


            event.setCanceled(true);
            return;
        }

        crossbow = oppositeHandItem;
        if (crossbow.getItem() instanceof AlchemistsCrossbowItem) {
            CrossbowState crossbowState = AlchemistsCrossbowItem.getCrossbowState(crossbow);
            if (crossbowState == CrossbowState.LOADING || crossbowState == CrossbowState.LOADED || crossbowState == CrossbowState.FIRING) {
                event.setCanceled(true);
            }
        }
    }
}
