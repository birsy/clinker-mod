package birsy.clinker.common.page.elements;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.common.page.PageElement;
import birsy.clinker.common.page.PageElementTransform;
import birsy.clinker.common.page.PageElementType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerPageElementTypes;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.Colorc;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.List;

public class ImagePageElement extends PageElement {
    public static final MapCodec<ImagePageElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(element -> element.texture),
                    Codec.FLOAT.listOf(4, 4).optionalFieldOf("uv_coordinates", List.of(0.0F, 0.0F, 1.0F, 1.0F)).forGetter(element -> List.of(element.u1, element.v1, element.u2, element.v2)),
                    Color.ARGB_CODEC.optionalFieldOf("color", Color.WHITE).forGetter(element -> element.color),
                    PageElementTransform.CODEC.fieldOf("transform").forGetter(element -> element.transform)
            ).apply(instance, ImagePageElement::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ImagePageElement> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, element -> element.texture,
            ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list(4)), element -> List.of(element.u1, element.v1, element.u2, element.v2),
            ExtraByteBufCodecs.COLOR, element -> element.color,
            PageElementTransform.STREAM_CODEC, element -> element.transform,
            ImagePageElement::new
    );

    final ResourceLocation texture;
    final float u1, v1, u2, v2;
    final Colorc color;

    public ImagePageElement(ResourceLocation texture, List<Float> textureCoordinates, Colorc color, PageElementTransform transform) {
        this(texture, textureCoordinates.get(0), textureCoordinates.get(1), textureCoordinates.get(2), textureCoordinates.get(3), color, transform);
    }

    public ImagePageElement(ResourceLocation texture, float u1, float v1, float u2, float v2, Colorc color, PageElementTransform transform) {
        super(transform);
        this.texture = texture.getPath().startsWith("textures/") ? texture : texture.withPrefix("textures/page/");
        this.u1 = u1; this.v1 = v1;
        this.u2 = u2; this.v2 = v2;
        this.color = color;
    }

    public void drawToAtlas(MultiBufferSource bufferSource, Matrix4f matrix, int atlasOffsetX, int atlasOffsetY) {
        float halfWidth  = this.transform.width() * 0.5F,
              halfHeight = this.transform.height() * 0.5F;

        matrix.identity();
        matrix.translate(atlasOffsetX, atlasOffsetY, 0);
        matrix.translate(this.transform.x() + halfWidth, this.transform.y() + halfHeight, this.transform.renderOrder());
        matrix.rotateYXZ(0, 0, this.transform.rotation() * Mth.DEG_TO_RAD);

        RenderType renderType = VeilRenderType.get(ClinkerRenderTypes.PAGE, this.texture);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        vertexConsumer.addVertex(matrix, -halfWidth, -halfHeight, 0)
                      .setUv(this.u1, this.v1)
                      .setColor(this.color.argb());
        vertexConsumer.addVertex(matrix, -halfWidth,  halfHeight, 0)
                      .setUv(this.u1, this.v2)
                      .setColor(this.color.argb());
        vertexConsumer.addVertex(matrix,  halfWidth,  halfHeight, 0)
                      .setUv(this.u2, this.v2)
                      .setColor(this.color.argb());
        vertexConsumer.addVertex(matrix,  halfWidth, -halfHeight, 0)
                      .setUv(this.u2, this.v1)
                      .setColor(this.color.argb());
    }

    @Override
    public PageElementType<?> type() {
        return ClinkerPageElementTypes.IMAGE.get();
    }
}
