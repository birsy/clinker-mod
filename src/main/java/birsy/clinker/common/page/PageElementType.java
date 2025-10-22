package birsy.clinker.common.page;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public interface PageElementType<T extends PageElement> {
    MapCodec<T> codec();
}
