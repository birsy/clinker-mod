package birsy.clinker.client.render.world.sky;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.util.Mth;

public class OthershoreSkyRenderer2 {
    VertexBuffer[] lowerCloudVbos;
    VertexBuffer[] lowerStarVbos;

    void rebuild(int renderRadiusInBlocks) {
        int minRadius = renderRadiusInBlocks;
        int maxRadius = 500;
        int layerWidth = 30;

        int layerCount = (maxRadius - minRadius) / layerWidth;
        if (lowerCloudVbos != null) for (VertexBuffer vbo : lowerCloudVbos) vbo.close();
        lowerCloudVbos = new VertexBuffer[layerCount];
        if (lowerStarVbos != null) for (VertexBuffer vbo : lowerStarVbos) vbo.close();
        lowerStarVbos = new VertexBuffer[layerCount];

        for (int i = 0; i < layerCount; i++) {
            float factor = i / (layerCount - 1.0F);
            float radius = Mth.lerp(factor, minRadius, maxRadius);

        }
    }
}
