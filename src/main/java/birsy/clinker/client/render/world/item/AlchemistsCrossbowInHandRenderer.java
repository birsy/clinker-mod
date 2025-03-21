package birsy.clinker.client.render.world.item;

import birsy.clinker.common.world.item.AlchemistsCrossbowItem;
import birsy.clinker.common.world.item.components.LoadedItemStack;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public static float getPullPercentage(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (entity == null) return 0.0F;
        boolean loaded = !AlchemistsCrossbowItem.getLoadedItems(stack).isEmpty();
        boolean firing = AlchemistsCrossbowItem.isFiring(stack);
        boolean using = entity.isUsingItem();
        if (using && !firing) {
            // loading
            Clinker.LOGGER.info(Math.clamp(entity.getTicksUsingItem() / (float) AlchemistsCrossbowItem.ITEM_LOAD_TIME, 0, 1));
            return Math.clamp(entity.getTicksUsingItem() / (float) AlchemistsCrossbowItem.ITEM_LOAD_TIME, 0, 1);
        } else if (firing) {
            // firing
            int ticksBetweenShots = AlchemistsCrossbowItem.TICKS_BETWEEN_SHOTS;
            float fireTime = 1 - ((entity.getTicksUsingItem() % ticksBetweenShots) / (float) ticksBetweenShots);
            float reloadTime = 0.2F;
            if (fireTime < reloadTime) {
                return MathUtils.mapRange(0.0F, reloadTime, 0.0F, 1.0F, fireTime);
            } else {
                return MathUtils.mapRange(reloadTime, 1.0F, 1.0F, 0.0F, fireTime);
            }
        } else if (loaded) {
            // loaded
            return 1.0F;
        }
        return 0.0F;
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity)) return;

        LivingEntity player = (LivingEntity) Minecraft.getInstance().cameraEntity;
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ItemStack crossbow = event.getItemStack();
        if (crossbow.getItem() instanceof AlchemistsCrossbowItem) {
            HumanoidArm arm = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            float direction = arm == HumanoidArm.RIGHT ? -1.0F : 1.0F;

            PoseStack poseStack = new PoseStack();
            float partialTicks = event.getPartialTick();

            poseStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));
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
            poseStack.translate(0, -0.55 - equipProgress, 1);
            poseStack.mulPose(Axis.XP.rotationDegrees(80));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            //poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());

            BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer()
                    .getModel(crossbow, player.level(), player, player.getId());
            Minecraft.getInstance().getItemRenderer()
                    .render(crossbow, ItemDisplayContext.FIXED, arm == HumanoidArm.LEFT, poseStack,
                            event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, bakedmodel);

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
            }
            event.setCanceled(true);
            return;
        }

        InteractionHand oppositeHand = event.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        crossbow = player.getItemInHand(oppositeHand);
        if (crossbow.getItem() instanceof AlchemistsCrossbowItem) {
            boolean loaded = !AlchemistsCrossbowItem.getLoadedItems(crossbow).isEmpty();
            boolean firing = AlchemistsCrossbowItem.isFiring(crossbow);
            if ((loaded || firing) && !(player.getUseItemRemainingTicks() > 0 && !firing)) {
                event.setCanceled(true);
            }
        }
    }
}
