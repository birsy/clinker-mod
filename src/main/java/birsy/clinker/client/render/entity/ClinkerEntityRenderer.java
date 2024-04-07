package birsy.clinker.client.render.entity;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;

public abstract class ClinkerEntityRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    protected static final ResourceLocation PLACEHOLDER_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/entity/placeholder.png");;

    public ClinkerEntityRenderer(EntityRendererProvider.Context context, M model, float shadowSize) {
        super(context, model, shadowSize);
    }
    @Override
    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
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

        float entityXRotation = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        if (pEntity.getPose() == Pose.SLEEPING) {
            Direction direction = pEntity.getBedOrientation();
            if (direction != null) {
                float eyeHeight = pEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                pMatrixStack.translate((float)(-direction.getStepX()) * eyeHeight, 0.0D, (float)(-direction.getStepZ()) * eyeHeight);
            }
        }

        float rotationBob = this.getBob(pEntity, pPartialTicks);
        this.setupRotations(pEntity, pMatrixStack, rotationBob, bodyRotation, pPartialTicks);
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(pEntity, pMatrixStack, pPartialTicks);
        pMatrixStack.translate(0.0D, -1.501F, 0.0D);
        float animationSpeed = 0.0F;
        float animationAmount = 0.0F;
        if (!shouldSit && pEntity.isAlive()) {
            animationSpeed = pEntity.walkAnimation.speed(pPartialTicks);
            animationAmount = pEntity.walkAnimation.speed(pPartialTicks) - pEntity.walkAnimation.speed(pPartialTicks) * (1.0F - pPartialTicks);
            if (pEntity.isBaby()) {
                animationAmount *= 3.0F;
            }

            if (animationSpeed > 1.0F) {
                animationSpeed = 1.0F;
            }
        }

        this.model.setupAnim(pEntity, animationAmount, animationSpeed, rotationBob, neckRotation, entityXRotation);
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
                renderlayer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, animationAmount, animationSpeed, pPartialTicks, rotationBob, neckRotation, entityXRotation);
            }
        }

        pMatrixStack.popPose();

        var renderNameTagEvent = new net.neoforged.neoforge.client.event.RenderNameTagEvent(pEntity, pEntity.getDisplayName(), this, pMatrixStack, pBuffer, pPackedLight, pPartialTicks);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(renderNameTagEvent);
        if (renderNameTagEvent.getResult() != net.neoforged.bus.api.Event.Result.DENY && (renderNameTagEvent.getResult() == net.neoforged.bus.api.Event.Result.ALLOW || this.shouldShowName(pEntity))) {
            this.renderNameTag(pEntity, renderNameTagEvent.getContent(), pMatrixStack, pBuffer, pPackedLight);
        }
    }
}
