package birsy.clinker.client.render.particle;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class FireflyParticle extends TextureSheetParticle {
    private static final FastNoiseLite noise;
    static { noise = new FastNoiseLite(); noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic); }

    private final Quaternionf rotation = new Quaternionf();
    private Vector3f movementDirection = new Vector3f(), pMovementDirection = new Vector3f();

    private PointLight light;

    private final double seed;

    protected FireflyParticle(ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.setSize(0.01F, 0.01F);
        this.pickSprite(spriteSet);
        this.lifetime = 10* 20;
        this.hasPhysics = true;
        this.friction = 0.5F;
        this.gravity = 0.1F;

        this.rCol = level.random.nextFloat();
        this.gCol = level.random.nextFloat();
        this.bCol = level.random.nextFloat();

        if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
            this.light = new PointLight();
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light);
            this.updateLight(0.0F);
        }

        this.seed = level.random.nextDouble();
    }

    @Override
    public void remove() {
        super.remove();
        if (VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()) {
            if (this.light != null) VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
        }
    }

    @Override
    public void tick() {
        double xB = this.x;
        double yB = this.y;
        double zB = this.z;

        super.tick();
        float updateSpeed = (float) ((seed * 10000) + age * 2.0F);
        float speed = (float) MathUtils.mapRange(-1F, 1F, 0.1F, 1.0F, noise.GetNoise(updateSpeed, updateSpeed, updateSpeed));
        Vector3d movement = new Vector3d(noise.GetNoise(updateSpeed, 0), noise.GetNoise(updateSpeed, updateSpeed) * 0.1, noise.GetNoise(0, updateSpeed)).mul(speed);
        this.move(movement.x, movement.y, movement.z);
        double xA = this.x;
        double yA = this.y;
        double zA = this.z;

        this.pMovementDirection.set(this.movementDirection);
        this.movementDirection.lerp(new Vector3f((float) (xB - xA), (float) (yB - yA), (float) (zB - zA)), 0.1F);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        float fadeFactor = (this.age + partialTick) / (this.lifetime + 1);
        fadeFactor *= fadeFactor * fadeFactor * fadeFactor;
        fadeFactor = 1 - fadeFactor;

        this.updateLight(partialTick);

        Vec3 cameraPosition = camera.getPosition();
        float rX = (float)(Mth.lerp(partialTick, this.xo, this.x) - cameraPosition.x());
        float rY = (float)(Mth.lerp(partialTick, this.yo, this.y) - cameraPosition.y());
        float rZ = (float)(Mth.lerp(partialTick, this.zo, this.z) - cameraPosition.z());

        Vector3f forward = this.pMovementDirection.lerp(this.movementDirection, partialTick, new Vector3f());
        float length = forward.length();
        forward = forward.normalize();
        Vector3f directionToCamera = new Vector3f(rX, rY, rZ).normalize();
        Vector3f tangent = forward.cross(directionToCamera, new Vector3f()).normalize();

        forward = forward.mul(Math.max(3, length * 40));

        Vector3f[] verticies = new Vector3f[]{
                tangent.mul(-1, new Vector3f()).sub(forward),
                tangent.mul(-1, new Vector3f()).add(forward),
                tangent.add(forward, new Vector3f()),
                tangent.sub(forward, new Vector3f())
        };

        for (Vector3f vertex : verticies) {
            vertex.mul(0.02F);
            vertex.add(rX, rY, rZ);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int packedLight = this.getLightColor(partialTick);
        consumer.vertex(verticies[0].x(), verticies[0].y(), verticies[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, fadeFactor).uv2(packedLight).endVertex();
        consumer.vertex(verticies[1].x(), verticies[1].y(), verticies[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, fadeFactor).uv2(packedLight).endVertex();
        consumer.vertex(verticies[2].x(), verticies[2].y(), verticies[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, fadeFactor).uv2(packedLight).endVertex();
        consumer.vertex(verticies[3].x(), verticies[3].y(), verticies[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, fadeFactor).uv2(packedLight).endVertex();
    }

    private void updateLight(float partialTick) {
        float fadeFactor = (this.age + partialTick) / (this.lifetime + 1);
        fadeFactor *= fadeFactor * fadeFactor * fadeFactor;
        fadeFactor = 1 - fadeFactor;
        this.light.setPosition(Mth.lerp(partialTick, this.xo, this.x), Mth.lerp(partialTick, this.yo, this.y), Mth.lerp(partialTick, this.zo, this.z));
        float brightness = 2.0F;
        this.light.setColor(this.rCol * brightness, this.gCol * brightness, this.bCol * brightness);
        this.light.setRadius(4.0F * fadeFactor);
    }

    @Override
    protected int getLightColor(float partialTicks) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            RandomSource randomsource = level.random;
            double d0 = randomsource.nextGaussian() * 1.0E-6F;
            double d1 = randomsource.nextGaussian() * 1.0E-4F;
            double d2 = randomsource.nextGaussian() * 1.0E-6F;
            FireflyParticle fireflyParticle = new FireflyParticle(level, this.sprite, x, y, z, d0, d1, d2);
            return fireflyParticle;
        }
    }
}
