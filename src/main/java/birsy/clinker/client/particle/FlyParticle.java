package birsy.clinker.client.particle;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class FlyParticle extends TextureSheetParticle {
    private static final FastNoiseLite noise;
    static { noise = new FastNoiseLite(); noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic); }

    private Vector3f movementDirection = new Vector3f(),
                     pMovementDirection = new Vector3f();
    private final double seed;

    protected FlyParticle(ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(spriteSet);

        this.lifetime = 10 * 20;
        this.hasPhysics = true;
        this.friction = 0.5F;
        this.gravity = 0.1F;

        this.rCol = 0.10F;
        this.gCol = 0.11F;
        this.bCol = 0.09F;

        this.seed = level.random.nextDouble();
    }

    @Override
    public void tick() {
        double xB = this.x, yB = this.y, zB = this.z;

        super.tick();
        float updateSpeed = (float) ((seed * 10000) + age * 5.0F);
        float speed = 0.2F;//Mth.map(noise.GetNoise(updateSpeed, updateSpeed, updateSpeed), -1F, 1F, 0.1F, 0.25F);
        Vector3d movement = new Vector3d(noise.GetNoise(updateSpeed, 0), noise.GetNoise(updateSpeed, updateSpeed), noise.GetNoise(0, updateSpeed)).mul(speed);
        this.move(movement.x, movement.y, movement.z);
        double xA = this.x, yA = this.y, zA = this.z;

        this.pMovementDirection.set(this.movementDirection);
        this.movementDirection.lerp(new Vector3f((float) (xB - xA), (float) (yB - yA), (float) (zB - zA)), 0.1F);
    }

    private final Vector3f forward = new Vector3f(), directionToCamera = new Vector3f(), tangent = new Vector3f();
    private final Vector3f v0 = new Vector3f(), v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f();
    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        float fadeFactor = (this.age + partialTick) / (this.lifetime + 1);
        fadeFactor *= fadeFactor * fadeFactor * fadeFactor;
        fadeFactor = 1.0F - fadeFactor;

        Vec3 camPos = camera.getPosition();
        float rX = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
        float rY = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
        float rZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

        this.forward.set(this.pMovementDirection).lerp(this.movementDirection, partialTick);
        float length = forward.length();
        this.forward.normalize();

        this.directionToCamera.set(rX, rY, rZ);
        this.directionToCamera.normalize();

        this.tangent.set(
                        this.forward.y() * this.directionToCamera.z() - this.forward.z() * this.directionToCamera.y(),
                        this.forward.z() * this.directionToCamera.x() - this.forward.x() * this.directionToCamera.z(),
                        this.forward.x() * this.directionToCamera.y() - this.forward.y() * this.directionToCamera.x()
                );
        this.tangent.normalize();

        float forwardScale = Math.max(1.0F, length * 40.0F);
        this.forward.mul(forwardScale);

        this.v0.set(this.tangent).mul(-1.0F).sub(this.forward);
        this.v1.set(this.tangent).mul(-1.0F).add(this.forward);
        this.v2.set(this.tangent).add(this.forward);
        this.v3.set(this.tangent).sub(this.forward);

        float scale = 0.02F
                * Mth.clampedMap(age + partialTick, 0, 20, 0, 1)
                * Mth.clampedMap(age + partialTick, lifetime - 20, lifetime, 1, 0);
        this.v0.mul(scale).add(rX, rY, rZ);
        this.v1.mul(scale).add(rX, rY, rZ);
        this.v2.mul(scale).add(rX, rY, rZ);
        this.v3.mul(scale).add(rX, rY, rZ);

        float u0 = this.getU0(), u1 = this.getU1(), v0 = this.getV0(), v1 = this.getV1();
        int packedLight = this.getLightColor(partialTick);
        consumer.addVertex(this.v0.x(), this.v0.y(), this.v0.z())
                .setUv(u1, v1)
                .setColor(this.rCol, this.gCol, this.bCol, fadeFactor)
                .setLight(packedLight);
        consumer.addVertex(this.v1.x(), this.v1.y(), this.v1.z())
                .setUv(u1, v0)
                .setColor(this.rCol, this.gCol, this.bCol, fadeFactor)
                .setLight(packedLight);
        consumer.addVertex(this.v2.x(), this.v2.y(), this.v2.z())
                .setUv(u0, v0)
                .setColor(this.rCol, this.gCol, this.bCol, fadeFactor)
                .setLight(packedLight);
        consumer.addVertex(this.v3.x(), this.v3.y(), this.v3.z())
                .setUv(u0, v1)
                .setColor(this.rCol, this.gCol, this.bCol, fadeFactor)
                .setLight(packedLight);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    
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
            FlyParticle flyParticle = new FlyParticle(level, this.sprite, x, y, z, d0, d1, d2);
            return flyParticle;
        }
    }
}
