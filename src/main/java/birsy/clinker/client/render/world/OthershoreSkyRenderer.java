package birsy.clinker.client.render.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class OthershoreSkyRenderer {
    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/environment/star.png");
    private Minecraft mc;
    public OthershoreSkyRenderer(Minecraft mc) {
        this.mc = mc;
    }

    private static void buildSkyDisc(BufferBuilder pBuilder, float y) {
        float f1 = 512.0F;
        float f = Math.signum(y) * f1;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        pBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        pBuilder.vertex(0.0D, y, 0.0D).endVertex();

        for(int angle = -180; angle <= 180; angle += 45) {
            pBuilder.vertex(f * Mth.cos((float) angle * ((float) Math.PI / 180F)), y, f1 * Mth.sin((float) angle * ((float) Math.PI / 180F))).endVertex();
        }

        pBuilder.end();
    }
}
