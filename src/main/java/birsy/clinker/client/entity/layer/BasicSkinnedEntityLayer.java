package birsy.clinker.client.entity.layer;

import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerSkinEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BasicSkinnedEntityLayer<T extends Entity & SkeletonParent<T, M>, M extends Skeleton> extends NecromancerSkinEntityRenderLayer<T, M> {
    final Function<T, RenderType> renderTypeProvider;
    final Function<T, Skin> skinProvider;
    TriConsumer<T, M, NecromancerRenderer> configuration = (entity, skeleton, renderer) -> {};

    public BasicSkinnedEntityLayer(NecromancerEntityRenderer<T, M> renderer,
                                   Function<T, RenderType> renderTypeProvider, Function<T, Skin> skinProvider) {
        super(renderer);
        this.renderTypeProvider = renderTypeProvider;
        this.skinProvider = skinProvider;
    }

    public BasicSkinnedEntityLayer<T, M> preRender(TriConsumer<T, M, NecromancerRenderer> configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    protected void renderSkin(T parent, M skeleton, Skin skin, RenderType renderType, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        if (parent instanceof LivingEntity livingEntity) {
            boolean hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
            if (hurt) renderer.setOverlay(OverlayTexture.pack(0.0F, hurt));
        }
        configuration.accept(parent, skeleton, renderer);
        super.renderSkin(parent, skeleton, skin, renderType, renderer, matrixStack, packedLight, partialTicks);
    }

    @Override public @Nullable RenderType getRenderType(T parent) { return renderTypeProvider.apply(parent); }
    @Override public @Nullable Skin getSkin(T parent) { return skinProvider.apply(parent); }
}
