package birsy.clinker.client.render.entity;

import birsy.clinker.common.entity.GnomadAxemanEntity;
import birsy.clinker.common.entity.IVelocityTilt;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;

public abstract class ClinkerEntityRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    public ClinkerEntityRenderer(EntityRendererProvider.Context context, M model, float shadowSize) {
        super(context, model, shadowSize);
    }
    @Override
    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        if (pEntityLiving instanceof IVelocityTilt) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(((IVelocityTilt) pEntityLiving).getPitch(pPartialTicks)));
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(((IVelocityTilt) pEntityLiving).getRoll(pPartialTicks)));
        }
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        this.model.attackTime = this.getAttackAnim(pEntity, pPartialTicks);

        boolean shouldSit = pEntity.isPassenger() && (pEntity.getVehicle() != null && pEntity.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = pEntity.isBaby();
        float bodyRotation = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        float headRotation = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
        float neckRotation = headRotation - bodyRotation;
        if (shouldSit && pEntity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)pEntity.getVehicle();
            bodyRotation = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            neckRotation = headRotation - bodyRotation;
            float wrappedNeckRotation = Mth.wrapDegrees(neckRotation);
            if (wrappedNeckRotation < -85.0F) {
                wrappedNeckRotation = -85.0F;
            }

            if (wrappedNeckRotation >= 85.0F) {
                wrappedNeckRotation = 85.0F;
            }

            bodyRotation = headRotation - wrappedNeckRotation;
            if (wrappedNeckRotation * wrappedNeckRotation > 2500.0F) {
                bodyRotation += wrappedNeckRotation * 0.2F;
            }

            neckRotation = headRotation - bodyRotation;
        }

        float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        if (pEntity.getPose() == Pose.SLEEPING) {
            Direction direction = pEntity.getBedOrientation();
            if (direction != null) {
                float f4 = pEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                pMatrixStack.translate((double)((float)(-direction.getStepX()) * f4), 0.0D, (double)((float)(-direction.getStepZ()) * f4));
            }
        }

        float rotationBob = this.getBob(pEntity, pPartialTicks);
        this.setupRotations(pEntity, pMatrixStack, rotationBob, bodyRotation, pPartialTicks);
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(pEntity, pMatrixStack, pPartialTicks);
        pMatrixStack.translate(0.0D, (double)-1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && pEntity.isAlive()) {
            f8 = Mth.lerp(pPartialTicks, pEntity.animationSpeedOld, pEntity.animationSpeed);
            f5 = pEntity.animationPosition - pEntity.animationSpeed * (1.0F - pPartialTicks);
            if (pEntity.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.model.setupAnim(pEntity, f5, f8, rotationBob, neckRotation, f6);
        this.model.prepareMobModel(pEntity, f5, f8, pPartialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(pEntity);
        boolean flag1 = !flag && !pEntity.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(pEntity);
        RenderType rendertype = this.getRenderType(pEntity, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
            int i = getOverlayCoords(pEntity, this.getWhiteOverlayProgress(pEntity, pPartialTicks));
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
        }

        if (!pEntity.isSpectator()) {
            for(RenderLayer<T, M> renderlayer : this.layers) {
                renderlayer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, f5, f8, pPartialTicks, rotationBob, neckRotation, f6);
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
