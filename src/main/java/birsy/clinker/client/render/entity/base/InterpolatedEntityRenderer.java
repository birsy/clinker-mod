package birsy.clinker.client.render.entity.base;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.client.model.base.ModelFactory;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class InterpolatedEntityRenderer<T extends LivingEntity & InterpolatedSkeletonParent, M extends InterpolatedSkeleton> extends EntityRenderer<T> {
    protected final ModelFactory modelFactory;
    List<EntityRenderLayer<T, M>> layers = Lists.newArrayList();

    protected InterpolatedEntityRenderer(EntityRendererProvider.Context pContext, ModelFactory modelFactory, float shadowRadius) {
        super(pContext);
        this.modelFactory = modelFactory;
        this.shadowRadius = shadowRadius;
    }

    public void setupModelFactory() {}

    public final void createModel(T parent) {
        this.setupModelFactory();
        parent.setSkeleton(this.modelFactory.create());
    }

    public final boolean addLayer(EntityRenderLayer<T, M> layer) {
        return this.layers.add(layer);
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();

        this.setupRotations(pEntity, pMatrixStack, pEntity.tickCount + pPartialTicks, pPartialTicks);

        Minecraft minecraft = Minecraft.getInstance();
        boolean invisible = pEntity.isInvisible();
        boolean isSpectatorTransparent = !invisible && !pEntity.isInvisibleTo(minecraft.player);
        boolean glowing = minecraft.shouldEntityAppearGlowing(pEntity);
        RenderType rendertype = this.getRenderType(pEntity, invisible, isSpectatorTransparent, glowing);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
            int packedOverlay = LivingEntityRenderer.getOverlayCoords(pEntity, 0);

            pEntity.getSkeleton().render(pPartialTicks, pMatrixStack, vertexconsumer, pPackedLight, packedOverlay, 1.0F, 1.0F, 1.0F, isSpectatorTransparent ? 0.15F : 1.0F);
        }

        if (!pEntity.isSpectator()) {
            for(EntityRenderLayer<T, M> layer : this.layers) {
                layer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, pPartialTicks);
            }
        }

        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public abstract RenderType getRenderType(T entity);

    protected RenderType getRenderType(T pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        ResourceLocation resourcelocation = this.getTextureLocation(pLivingEntity);
        if (pTranslucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (pBodyVisible) {
            return this.getRenderType(pLivingEntity);
        } else {
            return pGlowing ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pPartialTicks) {
        if (pEntityLiving.deathTime > 0) {
            float deathTime = ((float)pEntityLiving.deathTime + pPartialTicks - 1.0F) / 20.0F * 1.6F;
            deathTime = Mth.sqrt(deathTime);
            if (deathTime > 1.0F) {
                deathTime = 1.0F;
            }
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(deathTime * this.getFlipDegrees(pEntityLiving)));
        } else if (pEntityLiving.isAutoSpinAttack()) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F - pEntityLiving.getXRot()));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(((float)pEntityLiving.tickCount + pPartialTicks) * -75.0F));
        } else if (LivingEntityRenderer.isEntityUpsideDown(pEntityLiving)) {
            pMatrixStack.translate(0.0D, pEntityLiving.getBbHeight() + 0.1F, 0.0D);
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }
        if (pEntityLiving.isFullyFrozen()) {
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.cos(pEntityLiving.tickCount * 3.25F) * Mth.PI * 0.4F));
        }
    }

    protected float getFlipDegrees(T entity) {
        return 90.0F;
    }

}
