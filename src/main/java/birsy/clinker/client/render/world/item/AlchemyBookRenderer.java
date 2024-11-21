package birsy.clinker.client.render.world.item;

import birsy.clinker.client.render.BasicModelPart;
import birsy.clinker.common.world.item.AlchemyBookItem;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AlchemyBookRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    public static final AlchemyBookModel bookModel = new AlchemyBookModel();
    public static final ResourceLocation bookTexture = new ResourceLocation(Clinker.MOD_ID, "textures/book/book_cover.png");

    //TODO: mixin to the player renderer so the arms hold the book properly.
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        ItemStack mainHandItem = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = event.getEntity().getItemInHand(InteractionHand.OFF_HAND);
        if (mainHandItem.getItem() instanceof AlchemyBookItem || offHandItem.getItem() instanceof AlchemyBookItem) {
            PoseStack stack = event.getPoseStack();
            float scale = 0.225F;
            stack.pushPose();
            stack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(event.getPartialTick(), event.getEntity().yBodyRotO, event.getEntity().yBodyRot)));
            //stack.mulPose(Axis.YP.rotationDegrees(180));
            float bob = Mth.lerp(event.getPartialTick(), event.getEntity().oBob, event.getEntity().bob) * Mth.sin(Mth.lerp(event.getPartialTick(), event.getEntity().walkDistO, event.getEntity().walkDist) * 5);
            stack.translate(0, 0.9 + (bob * 0.5), 0.5);
            stack.mulPose(Axis.XP.rotationDegrees(-110));
            stack.scale(scale, scale, scale);
            AlchemyBookModel model = new AlchemyBookModel();
            model.bookSpine.yRot = -(float)(Math.PI / 2);
            model.bookSpine.x = 24.5F;
            model.renderToBuffer(stack, event.getMultiBufferSource().getBuffer(RenderType.entitySolid(bookTexture)), event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            stack.popPose();
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getItemStack().getItem() instanceof AlchemyBookItem) {
            event.setCanceled(true);
            PoseStack stack = event.getPoseStack();
            float xRot = event.getInterpolatedPitch(); //Minecraft.getInstance().cameraEntity.getViewXRot(event.getPartialTick());
            float yVRot = Minecraft.getInstance().cameraEntity.getViewYRot(event.getPartialTick());
            float yRot = yVRot;
            if (Minecraft.getInstance().cameraEntity instanceof LivingEntity) {
                yRot = Mth.lerp(mc.getPartialTick(), ((LivingEntity) Minecraft.getInstance().cameraEntity).yBodyRotO, ((LivingEntity) Minecraft.getInstance().cameraEntity).yBodyRot);
            }
            float scale = 0.5F;
            double distance = 2.0;
            float lookAtFactor = Mth.clamp(MathUtils.mapRange(0, 75, 0.2F, 1, xRot), 0, 1);

            Vec3 position = Vec3.directionFromRotation(xRot, yVRot);
            position = position.multiply(0, 1, 0).add(0, 0, 1).normalize().scale(distance);

            stack.pushPose();
            stack.mulPose(Axis.YP.rotationDegrees(yVRot));
            stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(0.9F, -yRot, -yVRot)));

            stack.mulPose(Axis.YP.rotationDegrees(180.0F));
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            stack.scale(scale, scale, scale);
            stack.translate(0, 1.8 * event.getEquipProgress(), 0);

            stack.translate(Mth.lerp(lookAtFactor, position.x(), 0), Mth.lerp(lookAtFactor, position.y() + 2, 0), Mth.lerp(lookAtFactor, position.z(), distance));

            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.mulPose(Axis.XP.rotationDegrees(90));

            stack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(lookAtFactor, -xRot, -90)));
            stack.scale(0.45F, 0.45F, 0.45F);

            //Renders the player's hands.
            if (!mc.player.isInvisible()) {
                stack.pushPose();
                HumanoidArm mainHandSide = mc.options.mainHand().get();
                ItemStack mainHandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
                ItemStack offHandItem = mc.player.getItemInHand(InteractionHand.OFF_HAND);
                ItemStack rightArmItem = mainHandSide == HumanoidArm.RIGHT ? mainHandItem : offHandItem;
                ItemStack leftArmItem = mainHandSide == HumanoidArm.RIGHT ? offHandItem : mainHandItem;

                if ((rightArmItem == ItemStack.EMPTY && mainHandSide != HumanoidArm.RIGHT) || rightArmItem.getItem() instanceof AlchemyBookItem) {
                    renderHand(stack, event.getMultiBufferSource(), event.getPackedLight(), HumanoidArm.RIGHT);
                }
                if ((leftArmItem == ItemStack.EMPTY && mainHandSide != HumanoidArm.LEFT) || leftArmItem.getItem() instanceof AlchemyBookItem) {
                    renderHand(stack, event.getMultiBufferSource(), event.getPackedLight(), HumanoidArm.LEFT);
                }

                stack.popPose();
            }
            stack.mulPose(Axis.XP.rotationDegrees(40 * (MathUtils.ease(event.getEquipProgress(), MathUtils.EasingType.easeInOutBack))));

            bookModel.bookSpine.yRot = -(float)(Math.PI / 2);
            bookModel.bookSpine.x = 24.5F;
            bookModel.renderToBuffer(stack, event.getMultiBufferSource().getBuffer(RenderType.entitySolid(bookTexture)), event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

            stack.popPose();
        }
    }

    public static void renderHand(PoseStack stack, MultiBufferSource buffer, int light, HumanoidArm side) {
        RenderSystem.setShaderTexture(0, mc.player.getSkin().texture());
        PlayerRenderer playerrenderer = (PlayerRenderer)mc.getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(mc.player);
        stack.pushPose();

        float sideSign = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float scale = 5.2F;
        stack.scale(scale, scale, scale);
        stack.translate(0.0, 1.1, 0.4F);
        //stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        stack.mulPose(Axis.XP.rotationDegrees(45.0F));
        stack.mulPose(Axis.XP.rotationDegrees(180.0F));
        stack.mulPose(Axis.ZP.rotationDegrees(sideSign * -30.0F));
        stack.translate(sideSign * -0.3F, 0, 0);

        if (side == HumanoidArm.RIGHT) {
            playerrenderer.renderRightHand(stack, buffer, light, mc.player);
        } else {
            playerrenderer.renderLeftHand(stack, buffer, light, mc.player);
        }

        stack.popPose();
    }

    public static class AlchemyBookModel extends Model {
        private final BasicModelPart bookSpine;
        private final BasicModelPart bookCover;
        private final BasicModelPart bookBack;
        private final BasicModelPart bookPages;

        public AlchemyBookModel() {
            super(RenderType::entitySolid);
            this.bookSpine = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(102, 0).mirror().addBox(-6.5F, -32.0F, 0.0F, 13.0F, 64.0F, 2.0F, new CubeDeformation(-0.05F, 0.0F, 0.05F)).mirror(false), 256, 256), PartPose.offset(0.0F, 0.0F, 0.0F));//CappinModelPart.fromModelPart(root.getChild("bookSpine"));

            this.bookCover = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(0, 66).addBox(-49.0F, -32.0F, 0.0F, 49.0F, 64.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), 256, 256), PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
            this.bookCover.setParent(this.bookSpine);

            this.bookBack = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -32.0F, 0.0F, 49.0F, 64.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), 256, 256), PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
            this.bookBack.setParent(this.bookSpine);

            this.bookPages = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(132, 0).addBox(-47.0F, -31.0F, -4.5F, 47.0F, 62.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), 256, 256), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 1.5708F, 0.0F));
            this.bookPages.setParent(this.bookSpine);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            bookSpine.render(poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
