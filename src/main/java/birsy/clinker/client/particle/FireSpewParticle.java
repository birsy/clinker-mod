package birsy.clinker.client.particle;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class FireSpewParticle extends Particle {
    protected final TextureAtlasSprite sprite;
    final Vector3f velocity = new Vector3f(), pVelocity = new Vector3f();
    private static final Vector3f
            START_COLOR = new Vector3f(1, 1, 1),
            MID_COLOR = new Vector3f(1, 0.78F, 0.56F),
            END_COLOR = new Vector3f(1, 0.22F, 0);

    public FireSpewParticle(ClientLevel pLevel,
                            double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprite = pSprites.get(0, 10);
        this.lifetime = 20;
        this.velocity.set(pXSpeed, pYSpeed, pZSpeed);
        this.pVelocity.set(this.velocity);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.pVelocity.set(this.velocity);

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double nextX = this.x + this.velocity.x,
               nextY = this.y + this.velocity.y,
               nextZ = this.z + this.velocity.z;
        Vec3 startPos = new Vec3(this.x, this.y, this.z);
        Vec3 endPos = new Vec3(nextX, nextY, nextZ);
        BlockHitResult raycast = this.level.clip(
                new ClipContext(startPos, endPos, ClipContext.Block.VISUAL, ClipContext.Fluid.WATER, CollisionContext.empty())
        );

        if (raycast.getType() == HitResult.Type.BLOCK) {
            float raycastLength = Mth.sqrt((float) raycast.getLocation().distanceToSqr(this.x, this.y, this.z));
            float velocityLength = this.velocity.length();
            float remainingLength = velocityLength - raycastLength;

            Vec3i normal = raycast.getDirection().getNormal();
            float dot = this.velocity.dot(normal.getX(), normal.getY(), normal.getZ());
            //if (Math.abs(dot) < 0.1F) {
                dot *= 1.0F;
                this.velocity.sub(normal.getX() * dot, normal.getY() * dot, normal.getZ() * dot).normalize();

                nextX = raycast.getLocation().x() + this.velocity.x() * remainingLength + normal.getX()*0.1;
                nextY = raycast.getLocation().y() + this.velocity.y() * remainingLength + normal.getY()*0.1;
                nextZ = raycast.getLocation().z() + this.velocity.z() * remainingLength + normal.getZ()*0.1;

                this.velocity.mul(velocityLength);
            //}

        }

        this.velocity.mul(0.99F);

        this.setPos(nextX, nextY, nextZ);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void updateColor() {
        float offset = 0;
        float factor = (this.age + offset) / (this.lifetime);
        this.alpha = MathUtils.smoothstep(1.0F - factor);

        //factor = (float) Math.pow(factor, 0.5);
        float transitionFac = 0.1F;
        if (factor < transitionFac) {
            factor = factor / transitionFac;
            factor = MathUtils.smoothstep(factor);
            this.rCol = Mth.lerp(factor, START_COLOR.x(), MID_COLOR.x());
            this.gCol = Mth.lerp(factor, START_COLOR.y(), MID_COLOR.y());
            this.bCol = Mth.lerp(factor, START_COLOR.z(), MID_COLOR.z());
        } else {
            factor = (factor - transitionFac) / (1-transitionFac);
            factor = MathUtils.smoothstep(factor);
            this.rCol = Mth.lerp(factor, MID_COLOR.x(), END_COLOR.x());
            this.gCol = Mth.lerp(factor, MID_COLOR.y(), END_COLOR.y());
            this.bCol = Mth.lerp(factor, MID_COLOR.z(), END_COLOR.z());
        }
    }

    private final Vector3f vel = new Vector3f();
    private final Vector3f quadOffsetDir = new Vector3f();
    @Override
    public void render(VertexConsumer pBuffer, Camera pCamera, float pPartialTicks) {
        updateColor();
        this.pVelocity.lerp(this.velocity, pPartialTicks, vel).normalize();

        Vec3 cameraPos = pCamera.getPosition();
        float x = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - cameraPos.x());
        float y = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - cameraPos.y());
        float z = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - cameraPos.z());

        Vector3f forward = this.pVelocity.lerp(this.velocity, pPartialTicks, new Vector3f()).normalize();
        Vector3f directionToCamera = new Vector3f(x, y, z).normalize();
        Vector3f tangent = forward.cross(directionToCamera, new Vector3f()).normalize();
        forward.mul(vel.length());
        float backFactor = ((float) this.age / this.lifetime);
        float frontFactor = ((this.age - vel.length()*20) / this.lifetime);

        Vector3f[] verticies = new Vector3f[]{
                tangent.mul(-1, new Vector3f()).mul(0.3F * (1 / (1-frontFactor))).sub(forward),
                tangent.mul(-1, new Vector3f()).mul(0.3F * (1 / (1-backFactor))),//.add(forward),
                tangent.mul(1, new Vector3f()).mul(0.3F * (1 / (1-backFactor))),//.add(forward),
                tangent.mul(1, new Vector3f()).mul(0.3F * (1 / (1-frontFactor))).sub(forward)
        };


        for (Vector3f vertex : verticies) {
            vertex.add(x,y,z);
        }


        float f6 = this.sprite.getU0();
        float f4 = this.sprite.getV0();
        float f7 = (this.sprite.getU1() - this.sprite.getU0())*(14F/32F) + this.sprite.getU0();
        float f5 = (this.sprite.getV1() - this.sprite.getV0())*(21F/32F) + this.sprite.getV0();
        int j = LightTexture.FULL_BRIGHT;
        pBuffer.vertex(verticies[0].x(), verticies[0].y(), verticies[0].z())
                .uv(f7, f5)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j)
                .endVertex();
        pBuffer.vertex(verticies[1].x(), verticies[1].y(), verticies[1].z())
                .uv(f7, f4)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j)
                .endVertex();
        pBuffer.vertex(verticies[2].x(), verticies[2].y(), verticies[2].z())
                .uv(f6, f4)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j)
                .endVertex();
        pBuffer.vertex(verticies[3].x(), verticies[3].y(), verticies[3].z())
                .uv(f6, f5)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(j)
                .endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new FireSpewParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
