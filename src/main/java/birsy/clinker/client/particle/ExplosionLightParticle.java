package birsy.clinker.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;


public class ExplosionLightParticle extends Particle {
    private final LightRenderHandle<PointLightData> light;

    protected ExplosionLightParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 4;
        this.hasPhysics = false;
        this.gravity = 0.0F;

        PointLightData lightData = new PointLightData();
        lightData.setPosition(x, y, z);
        lightData.setRadius(0);
        lightData.setColor(0, 0, 0);
        this.light = VeilRenderSystem.renderer().getLightRenderer().addLight(lightData);
        this.light.markDirty();
    }

    @Override
    public void remove() {
        super.remove();
        if (this.light != null) this.light.free();
    }

    @Override
    public void render(VertexConsumer p_107261_, Camera p_107262_, float partialTicks) {
        if (this.light == null) return;

        float brightness = (0.6F - ((float)this.age + partialTicks - 1.0F) * 0.25F * 0.5F) * 10;
        light.getLightData().setColor(brightness, brightness * 0.9F, brightness * 0.5F);

        float radius = Math.max(0, 7.1F * Mth.sin(((float)this.age + partialTicks - 1.0F) * 0.25F * (float) Math.PI));
        light.getLightData().setRadius(radius * 3);
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
