package birsy.clinker.client.necromancer.render;

import birsy.clinker.client.necromancer.RenderFactory;
import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class NecromancerEntityRenderer<T extends Entity & SkeletonParent, M extends Skeleton<T>> extends EntityRenderer<T> {
    final RenderFactory<T> renderFactory;
    final List<NecromancerEntityRenderLayer<T, M>> layers;

    protected NecromancerEntityRenderer(EntityRendererProvider.Context pContext, RenderFactory<T> renderFactory, float shadowRadius) {
        super(pContext);
        this.renderFactory = renderFactory;
        this.shadowRadius = shadowRadius;
        this.layers = new ArrayList<>();
    }

    public void addLayer(NecromancerEntityRenderLayer<T, M> layer) {
        this.layers.add(layer);
    }
    
    public void setupEntity(T entity) {
        renderFactory.setup(entity);
        Skeleton<T> skeleton = renderFactory.createSkeleton(entity);
        entity.setSkeleton(skeleton);
        entity.setAnimator(renderFactory.createAnimator(entity, skeleton));
    }

    public abstract Skin<M> getSkin(T parent);

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        poseStack.pushPose();
        float scale = 1.0F / 16.0F;
        poseStack.scale(scale, scale, scale);

        Minecraft minecraft = Minecraft.getInstance();
        boolean invisible = pEntity.isInvisible();
        boolean spectator = !invisible && !pEntity.isInvisibleTo(minecraft.player);
        boolean glowing = minecraft.shouldEntityAppearGlowing(pEntity);

        RenderType rendertype = this.getRenderType(pEntity, invisible, spectator, glowing);
        M skeleton = (M) pEntity.getSkeleton();
        Skin<M> skin = this.getSkin(pEntity);

        if (rendertype != null && skeleton != null && skin != null) {
            int packedOverlay = OverlayTexture.NO_OVERLAY;
            if (pEntity instanceof LivingEntity living) packedOverlay = LivingEntityRenderer.getOverlayCoords(living, 0);
            renderSkin(skeleton, skin, pEntity.tickCount, pPartialTicks, poseStack, pBuffer.getBuffer(rendertype), pPackedLight, packedOverlay, 1, 1, 1, 1);
        }

        if (!pEntity.isSpectator() && rendertype != null && skeleton != null) {
            for (NecromancerEntityRenderLayer<T, M> layer : this.layers) layer.render(poseStack, pBuffer, pPackedLight, pEntity, skeleton, pPartialTicks);
        }

        poseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
    }

    public void renderSkin(M skeleton, Skin<M> skin, int ticksExisted, float partialTicks, PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        skin.render(skeleton, ticksExisted, partialTicks, poseStack, consumer, packedLight, packedOverlay, r, g, b, a);
    }

    public abstract RenderType getRenderType(T entity, ResourceLocation texture);

    protected RenderType getRenderType(T pLivingEntity, boolean visible, boolean spectator, boolean glowing) {
        ResourceLocation texture = this.getTextureLocation(pLivingEntity);

        if (spectator) {
            return RenderType.itemEntityTranslucentCull(texture);
        }
        if (glowing) {
            return RenderType.outline(texture);
        }
        if (visible) {
            return this.getRenderType(pLivingEntity, texture);
        }

        return null;
    }
}
