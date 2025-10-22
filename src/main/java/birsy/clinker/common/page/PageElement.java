package birsy.clinker.common.page;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public abstract class PageElement {
    public static final Codec<PageElement> CODEC = ClinkerRegistries.PAGE_ELEMENT_TYPE_REGISTRY
            .byNameCodec()
            .dispatch(PageElement::type, PageElementType::codec);

    public final PageElementTransform transform;
    public PageElement(PageElementTransform transform) {
        this.transform = transform;
    }

    public abstract PageElementType<?> type();

    public void drawToAtlas(MultiBufferSource bufferSource, Matrix4f matrix, int atlasOffsetX, int atlasOffsetY) {
        float halfWidth  = this.transform.width() * 0.5F,
              halfHeight = this.transform.height() * 0.5F;

        matrix.identity();
        matrix.translate(atlasOffsetX, atlasOffsetY, 0);
        matrix.translate(this.transform.x() + halfWidth, this.transform.y() + halfHeight, this.transform.renderOrder());
        matrix.rotateYXZ(0, 0, this.transform.rotation());

        RenderType renderType = VeilRenderType.get(ClinkerRenderTypes.PAGE);
        if (renderType == null) return;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        vertexConsumer.addVertex(matrix, -halfWidth, -halfHeight, 0).setColor(0.0F, 0.0F, 0.0F, 0.5F);
        vertexConsumer.addVertex(matrix, -halfWidth,  halfHeight, 0).setColor(0.0F, 1.0F, 0.0F, 0.5F);
        vertexConsumer.addVertex(matrix,  halfWidth,  halfHeight, 0).setColor(1.0F, 1.0F, 0.0F, 0.5F);
        vertexConsumer.addVertex(matrix,  halfWidth, -halfHeight, 0).setColor(1.0F, 0.0F, 0.0F, 0.5F);
    }
}
