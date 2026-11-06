package birsy.clinker.client.entity.item;

import birsy.clinker.common.item.AlchemistsCrossbowItem;
import birsy.clinker.common.item.components.CrossbowState;
import birsy.clinker.common.item.components.LoadedItemStack;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

import static birsy.clinker.common.item.AlchemistsCrossbowItem.*;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class AlchemistsCrossbowRenderer implements IClientItemExtensions {
    private static final float[] RANDOM_FLOATS = Util.make(() -> {
        RandomSource random = RandomSource.create(80085);
        float[] array = new float[64];
        for (int i = 0; i < array.length; i++)
            array[i] = random.nextFloat() * 2.0F - 1.0F;
        return array;
    });

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack crossbow) {
        CrossbowState crossbowState = getCrossbowState(crossbow);
        switch (crossbowState) {
            case FIRING, LOADED -> {
                boolean isThisMe = entity == Minecraft.getInstance().player;
                if (!isThisMe || !Minecraft.getInstance().options.getCameraType().isFirstPerson())
                    return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            case LOADING -> {
                //return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
        }
        return IClientItemExtensions.super.getArmPose(entity, hand, crossbow);
    }

    public static float getPullPercentage(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (entity == null) return 0.0F;
        CrossbowState crossbowState = getCrossbowState(stack);

        switch (crossbowState) {
            case LOADING -> {
                return Math.clamp(entity.getTicksUsingItem() / (float) AlchemistsCrossbowItem.ITEM_LOAD_TIME, 0, 1);
            }
            case FIRING -> {
                LoadedItemStack loadedItemStack = AlchemistsCrossbowItem.getLoadedItems(stack);
                if (loadedItemStack.lastCount() <= 0) return 0.0F;
                return (float)loadedItemStack.stack().getCount() / loadedItemStack.lastCount();
            }
            case LOADED -> { return 1.0F; }
            default -> { return 0.0F; }
        }
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity)) return;
        ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();

        LivingEntity player = (LivingEntity) Minecraft.getInstance().cameraEntity;
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        InteractionHand hand = event.getHand(),
                oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        HumanoidArm arm = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();

        ItemStack mainHandItem = event.getItemStack();
        ItemStack oppositeHandItem = player.getItemInHand(oppositeHand);

        PoseStack poseStack = event.getPoseStack();
        float partialTicks = event.getPartialTick(),
              swingProgress = event.getSwingProgress(),
              equipProgress = event.getEquipProgress();

        if (mainHandItem.getItem() instanceof AlchemistsCrossbowItem) {
            ItemStack crossbow = mainHandItem;
            boolean hasRepeater = AlchemistsCrossbowItem.hasRepeater(crossbow);

            CrossbowState crossbowState = getCrossbowState(crossbow);
            LoadedItemStack loadedItemStack = AlchemistsCrossbowItem.getLoadedItems(crossbow);

            if (crossbowState == CrossbowState.STANDBY) return;

            boolean leftHanded = arm == HumanoidArm.LEFT;
            float direction = leftHanded ? -1 : 1;

            poseStack.pushPose();

            if (player.isUsingItem()) equipProgress = 0.0F;

            // loading animation stuff
            boolean loading = crossbowState == CrossbowState.LOADING && !(isFull(crossbow) || oppositeHandItem.isEmpty());

            float useTime = (player.getTicksUsingItem() + partialTicks);
            float itemLoadProgress = useTime / AlchemistsCrossbowItem.ITEM_LOAD_TIME;
            float pullBackFactor = Math.clamp(itemLoadProgress, 0, 1);
            float handAnimationTime = pullBackFactor, stretchAmount = pullBackFactor;
            boolean handReturning = false;
            if (hasRepeater && itemLoadProgress >= 1.0F) {
                float returnTime = 0.2F;
                pullBackFactor = itemLoadProgress % 1.0F;
                handReturning = pullBackFactor < returnTime;
                handAnimationTime = handReturning ?
                        Mth.clampedMap(pullBackFactor, 0F, returnTime, 1F, 0F) :
                        Mth.clampedMap(pullBackFactor, returnTime, 1F, 0F, 1F);
                stretchAmount = pullBackFactor < (returnTime * 0.2F) ?
                        Mth.clampedMap(pullBackFactor, 0F, returnTime * 0.2F, 1F, 0F) :
                        Mth.clampedMap(pullBackFactor, returnTime * 0.2F, 1F, 0F, 1F);
            }
            handAnimationTime = MathUtils.ease(handAnimationTime, MathUtils.EasingType.easeInSine);

            if (loading) {
                itemInHandRenderer.applyItemArmTransform(poseStack, arm, equipProgress);
                poseStack.translate(direction * -0.4785682F, -0.094387F, 0.05731531F);

                poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
                poseStack.mulPose(Axis.YP.rotationDegrees(direction * 65.3F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(direction * -9.785F));

                // item wobble
                if (pullBackFactor > 0.1F) {
                    float wobble = Mth.sin(itemLoadProgress * 20.0F) * 1F;
                    poseStack.translate(0.0F, wobble * (pullBackFactor - 0.1F) * 0.004F, 0.0F);
                }

                poseStack.mulPose(Axis.YN.rotationDegrees(direction * 45.0F));
                poseStack.pushPose();
                poseStack.scale(1.0F, 1.0F, 1.0F + stretchAmount * 0.2F);
            } else {
                boolean noAmmo = loadedItemStack.isEmpty();
                float recoil = crossbowState == CrossbowState.FIRING ?
                        Mth.sin((useTime % TICKS_BETWEEN_SHOTS) * Mth.HALF_PI) : 0;
                float xOffset = -0.4F * Mth.sin(Mth.sqrt(swingProgress) * Mth.PI),
                      yOffset =  0.2F * Mth.sin(Mth.sqrt(swingProgress) * Mth.TWO_PI),
                      zOffset = -0.2F * Mth.sin(swingProgress * Mth.PI) + (noAmmo ? 0 : recoil * 0.02F);
                poseStack.translate(direction * xOffset, yOffset, zOffset);
                itemInHandRenderer.applyItemArmTransform(poseStack, arm, equipProgress);
                itemInHandRenderer.applyItemArmAttackTransform(poseStack, arm, swingProgress);
                if ((crossbowState == CrossbowState.LOADED || crossbowState == CrossbowState.FIRING) && swingProgress < 0.001F) {
                    poseStack.translate(direction * -0.641864F, 0.0F, 0.0F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(direction * 10.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(direction * (noAmmo ? recoil * 1F : 0F)));
                }
                poseStack.pushPose();
            }

            BakedModel crossbowModel = Minecraft.getInstance().getItemRenderer()
                    .getModel(crossbow, player.level(), player, 0);
            Minecraft.getInstance().getItemRenderer()
                    .render(crossbow,
                            leftHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                            leftHanded,
                            poseStack,
                            event.getMultiBufferSource(),
                            event.getPackedLight(),
                            OverlayTexture.NO_OVERLAY,
                            crossbowModel
                    );

            // draw the loaded items, too
            if (!loadedItemStack.isEmpty()) {
                poseStack.pushPose();

                poseStack.translate(.079F * direction, .23F, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(-45F + -90F + -10F * direction));
                poseStack.mulPose(Axis.XP.rotationDegrees(90F));
                poseStack.scale(0.6F, 0.6F, 0.6F);
                BakedModel ammoModel = Minecraft.getInstance().getItemRenderer()
                        .getModel(loadedItemStack.stack(), player.level(), player, 0);
                float j = 1;
                float itemOffset = -0.03F;
                for (int i = 0; i < Math.min(loadedItemStack.stack().getCount(), 3); i++) {
                    poseStack.pushPose();

                    poseStack.translate(0, 0,  i * itemOffset);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(j * 5));
                    j *= -1;
                    Minecraft.getInstance().getItemRenderer()
                            .render(loadedItemStack.stack(),
                                    ItemDisplayContext.FIXED,
                                    leftHanded,
                                    poseStack,
                                    event.getMultiBufferSource(),
                                    event.getPackedLight(),
                                    OverlayTexture.NO_OVERLAY, ammoModel
                            );

                    poseStack.popPose();
                }
                if (loadedItemStack.stack().getCount() > 3) {
                    for (int i = 3; i < Math.min(loadedItemStack.stack().getCount(), 12); i++) {
                        poseStack.pushPose();
                        float randomA = RANDOM_FLOATS[(i * 4 + 0) % RANDOM_FLOATS.length],
                              randomB = RANDOM_FLOATS[(i * 4 + 1) % RANDOM_FLOATS.length],
                              randomC = RANDOM_FLOATS[(i * 4 + 2) % RANDOM_FLOATS.length],
                              randomD = RANDOM_FLOATS[(i * 4 + 3) % RANDOM_FLOATS.length];
                        float xOffset = randomA * 0.07F, zOffset = randomB * 0.07F;
                        float verticalOffset = (randomC * 0.5F + 0.5F) * 3 * itemOffset;
                        float rotation = randomD * 3.5F;
                        poseStack.translate(xOffset, zOffset, verticalOffset);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));

                        Minecraft.getInstance().getItemRenderer()
                                .render(loadedItemStack.stack(),
                                        ItemDisplayContext.FIXED,
                                        leftHanded,
                                        poseStack,
                                        event.getMultiBufferSource(),
                                        event.getPackedLight(),
                                        OverlayTexture.NO_OVERLAY,
                                        ammoModel
                                );

                        poseStack.popPose();
                    }
                }
                poseStack.popPose();
            }
            poseStack.popPose();

            // render hands while loading
            if (loading && localPlayer != null) {
                poseStack.pushPose();
                PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(localPlayer);

                float verticalOffset = handReturning ? Mth.sin(handAnimationTime * Mth.PI) * 0.1F : 0;
                float horizontalOffset = 1.0F - handAnimationTime;
                poseStack.translate(-0.5F * direction, 0.2F + verticalOffset, 0.3F - horizontalOffset * 0.25F);

                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(90 - 10 * direction));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90 * direction));

                poseStack.mulPose(Axis.YP.rotationDegrees(90 - 20 * direction));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-10 * direction));

                // approximate arm pivot
                poseStack.scale(0.7F, 0.7F, 0.7F);
                poseStack.translate(-direction * (6F / 16F), 0, 0F);

                if (arm == HumanoidArm.RIGHT)
                     playerRenderer.renderLeftHand(poseStack, event.getMultiBufferSource(), event.getPackedLight(), localPlayer);
                else playerRenderer.renderRightHand(poseStack, event.getMultiBufferSource(), event.getPackedLight(), localPlayer);

                // render hand
                if (!oppositeHandItem.isEmpty()) {
                    poseStack.translate(0.5F * direction, 0.7F, 0);
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                    BakedModel oppositeHandItemModel = Minecraft.getInstance().getItemRenderer()
                            .getModel(oppositeHandItem, player.level(), player, 0);
                    Minecraft.getInstance().getItemRenderer()
                            .render(oppositeHandItem,
                                    arm != HumanoidArm.LEFT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                                    arm != HumanoidArm.LEFT, poseStack,
                                    event.getMultiBufferSource(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, oppositeHandItemModel);
                }

                poseStack.popPose();
            }
            poseStack.popPose();

            event.setCanceled(true);
            return;
        }

        if (oppositeHandItem.getItem() instanceof AlchemistsCrossbowItem) {
            CrossbowState crossbowState = getCrossbowState(oppositeHandItem);
            if (crossbowState == CrossbowState.LOADING || crossbowState == CrossbowState.LOADED || crossbowState == CrossbowState.FIRING) {
                event.setCanceled(true);
            }
        }
    }
}
