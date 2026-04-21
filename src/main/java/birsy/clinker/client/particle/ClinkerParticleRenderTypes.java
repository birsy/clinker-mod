package birsy.clinker.client.particle;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class ClinkerParticleRenderTypes {
    public static final ParticleRenderType CHAIN_LIGHTNING = new ParticleRenderType() {
        private static final ResourceLocation TEXTURE = Clinker.resource("textures/particle/chain_lightning.png");
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(false); // no writing depth
            ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.PARTICLE_CHAIN_LIGHTNING);
            shader.setDefaultUniforms(VertexFormat.Mode.QUADS);

            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE); // additive blending
            RenderSystem.disableCull(); // no backface culling
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() { return "clinker:CHAIN_LIGHTNING"; }
    };

    public static final ParticleRenderType BLOSSOM_BUG = new ParticleRenderType() {
        private static final ResourceLocation TEXTURE = Clinker.resource("textures/particle/blossom_bug.png");
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(false); // no writing depth
            ShaderProgram shader = VeilRenderSystem.setShader(ClinkerShaders.PARTICLE_BLOSSOM_BUG); // blossom bug shader
            if (shader != null) {
                shader.getUniform("ScreenResolution").setVector(AdvancedFbo.getMainFramebuffer().getWidth(), AdvancedFbo.getMainFramebuffer().getHeight());
                shader.setSampler("DiffuseDepthSampler", AdvancedFbo.getMainFramebuffer().getDepthTextureAttachment().getId());
            }
            RenderSystem.setShaderTexture(0, TEXTURE); // particle texture
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE); // additive blending
            RenderSystem.disableCull(); // no backface culling
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() { return "clinker:BLOSSOM_BUG"; }
    };
}
