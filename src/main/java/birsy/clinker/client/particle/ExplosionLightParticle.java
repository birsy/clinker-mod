package birsy.clinker.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;


public class ExplosionLightParticle extends Particle {
    private final PointLight light;

    protected ExplosionLightParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 4;
        this.hasPhysics = false;
        this.gravity = 0.0F;

        this.light = new PointLight();
        this.light.setPosition(x, y, z);
        this.light.setRadius(0);
        light.setColor(0, 0, 0);
        VeilRenderSystem.renderer().getLightRenderer().addLight(this.light);
    }

    @Override
    public void remove() {
        super.remove();
        if (this.light != null) VeilRenderSystem.renderer().getLightRenderer().removeLight(this.light);
    }

    @Override
    public void render(VertexConsumer p_107261_, Camera p_107262_, float partialTicks) {
        if (this.light == null) return;

        float brightness = (0.6F - ((float)this.age + partialTicks - 1.0F) * 0.25F * 0.5F) * 10;
        light.setColor(brightness, brightness * 0.9F, brightness * 0.5F);

        float radius = Math.max(0, 7.1F * Mth.sin(((float)this.age + partialTicks - 1.0F) * 0.25F * (float) Math.PI));
        light.setRadius(radius * 3);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet spriteSet) {}

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ExplosionLightParticle explosionLightParticle = new ExplosionLightParticle(level, x, y, z);
            return explosionLightParticle;
        }
    }
}
