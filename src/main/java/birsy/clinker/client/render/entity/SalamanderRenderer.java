package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.AbstractSalamanderModel;
import birsy.clinker.common.entity.Salamander.AbstractSalamanderPartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public abstract class SalamanderRenderer<T extends AbstractSalamanderPartEntity, M extends AbstractSalamanderModel<T>> extends ClinkerEntityRenderer<T, M> {
    public SalamanderRenderer(EntityRendererProvider.Context context, M model) {
        super(context, model, 0.5F);
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();

        this.model.attackTime = this.getAttackAnim(pEntity, pPartialTicks);

        boolean shouldSit = pEntity.isPassenger() && (pEntity.getVehicle() != null && pEntity.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = pEntity.isBaby();
        float bodyRotationX = pEntity.getBodyRotX(pPartialTicks);
        float bodyRotationY = pEntity.getBodyRotY(pPartialTicks);
        float headRotationX = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        float headRotationY = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
        float neckRotationX = headRotationX - bodyRotationX;
        float neckRotationY = headRotationY - bodyRotationY;
        if (shouldSit && pEntity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)pEntity.getVehicle();
            bodyRotationX = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            bodyRotationY = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            neckRotationX = headRotationX - bodyRotationX;
            neckRotationY = headRotationY - bodyRotationY;
            float wrappedNeckRotationX = Mth.wrapDegrees(neckRotationX);
            float wrappedNeckRotationY = Mth.wrapDegrees(neckRotationY);
            if (wrappedNeckRotationX < -85.0F) {
                wrappedNeckRotationX = -85.0F;
            }
            if (wrappedNeckRotationY < -85.0F) {
                wrappedNeckRotationY = -85.0F;
            }

            if (wrappedNeckRotationX >= 85.0F) {
                wrappedNeckRotationX = 85.0F;
            }
            if (wrappedNeckRotationY >= 85.0F) {
                wrappedNeckRotationY = 85.0F;
            }

            bodyRotationX = headRotationX - wrappedNeckRotationX;
            bodyRotationY = headRotationY - wrappedNeckRotationY;
            if (wrappedNeckRotationX * wrappedNeckRotationX > 2500.0F) {
                bodyRotationY += wrappedNeckRotationX * 0.2F;
            }
            if (wrappedNeckRotationY * wrappedNeckRotationY > 2500.0F) {
                bodyRotationY += wrappedNeckRotationY * 0.2F;
            }

            neckRotationX = headRotationX - bodyRotationX;
            neckRotationY = headRotationY - bodyRotationY;
        }

        model.setBodyRotation(bodyRotationX, bodyRotationY);

        if (pEntity.getPose() == Pose.SLEEPING) {
            Direction direction = pEntity.getBedOrientation();
            if (direction != null) {
                float eyeHeight = pEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                pMatrixStack.translate((float)(-direction.getStepX()) * eyeHeight, 0.0D, (float)(-direction.getStepZ()) * eyeHeight);
            }
        }

        float rotationBob = this.getBob(pEntity, pPartialTicks);
        this.setupRotations(pEntity, pMatrixStack, rotationBob, 0.0F, pPartialTicks);
        //double originHeight = 8.0 / 16.0;
        //pMatrixStack.translate(0, originHeight, 0);
        //pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(-bodyRotationX));
        //pMatrixStack.translate(0, -originHeight, 0);

        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(pEntity, pMatrixStack, pPartialTicks);
        pMatrixStack.translate(0.0D, -1.501F, 0.0D);
        float animationSpeed = 0.0F;
        float animationAmount = 0.0F;
        if (!shouldSit && pEntity.isAlive()) {
            animationSpeed = Mth.lerp(pPartialTicks, pEntity.animationSpeedOld, pEntity.animationSpeed);
            animationAmount = pEntity.animationPosition - pEntity.animationSpeed * (1.0F - pPartialTicks);
            if (pEntity.isBaby()) {
                animationAmount *= 3.0F;
            }

            if (animationSpeed > 1.0F) {
                animationSpeed = 1.0F;
            }
        }

        this.model.setupAnim(pEntity, animationAmount, animationSpeed, rotationBob, neckRotationY, neckRotationX);
        this.model.prepareMobModel(pEntity, animationAmount, animationSpeed, pPartialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        boolean isBodyVisible = this.isBodyVisible(pEntity);
        boolean isSpectator = !isBodyVisible && !pEntity.isInvisibleTo(minecraft.player);
        boolean isGlowing = minecraft.shouldEntityAppearGlowing(pEntity);
        RenderType rendertype = this.getRenderType(pEntity, isBodyVisible, isSpectator, isGlowing);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
            int i = getOverlayCoords(pEntity, this.getWhiteOverlayProgress(pEntity, pPartialTicks));
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, i, 1.0F, 1.0F, 1.0F, isSpectator ? 0.15F : 1.0F);
        }

        if (!pEntity.isSpectator()) {
            for(RenderLayer<T, M> renderlayer : this.layers) {
                renderlayer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, animationAmount, animationSpeed, pPartialTicks, rotationBob, neckRotationY, neckRotationX);
            }
        }

        pMatrixStack.popPose();

        net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(pEntity, pEntity.getDisplayName(), this, pMatrixStack, pBuffer, pPackedLight, pPartialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(pEntity))) {
            this.renderNameTag(pEntity, renderNameplateEvent.getContent(), pMatrixStack, pBuffer, pPackedLight);
        }


    }
}
