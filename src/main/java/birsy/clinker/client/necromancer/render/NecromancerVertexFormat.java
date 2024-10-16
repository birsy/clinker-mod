package birsy.clinker.client.necromancer.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

public class NecromancerVertexFormat {
    public static final VertexFormatElement ELEMENT_BONE_INDEX = new VertexFormatElement(
            0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.GENERIC, 1
    );

    public static final VertexFormat SKINNED_ENTITY = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder()
                    .put("Position", ELEMENT_POSITION)
                    .put("Color", ELEMENT_COLOR)
                    .put("UV0", ELEMENT_UV0)
                    .put("Normal", ELEMENT_NORMAL)
                    .put("BoneIndex", ELEMENT_BONE_INDEX)
                    .build()
    );
}
