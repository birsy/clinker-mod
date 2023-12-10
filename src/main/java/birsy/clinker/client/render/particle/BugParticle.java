package birsy.clinker.client.render.particle;

import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class BugParticle extends Particle {
    protected TextureAtlasSprite sprite;
    protected static final FastNoiseLite noise;
    static { noise = new FastNoiseLite(); noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic); }

    protected Vec3 heading;
    protected Direction attachmentDirection;
    protected boolean attached;
    protected float seed;
    protected float distanceMoved;

    public BugParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.sprite = pSprites.get(pLevel.random);
        this.seed = pLevel.random.nextFloat();
        this.distanceMoved = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attached) {
            this.tickOnLand();
        } else {
            this.tickInAir();
        }
    }

    public abstract void tickOnLand();
    public abstract void tickInAir();

    protected Vec3 getRandomNoiseHeading(float hSpeed, float vSpeed) {
        float noiseY = this.seed * 256;
        Vec3 randomHeading = new Vec3(this.noise.GetNoise(this.age * hSpeed, noiseY + 128), this.noise.GetNoise(this.age * vSpeed, noiseY), this.noise.GetNoise(this.age * hSpeed,noiseY + 512));
        Vec3i normal = this.attachmentDirection.getNormal();
        if (this.attached) randomHeading = randomHeading.multiply(1 - normal.getX(), 1 - normal.getY(), 1 - normal.getZ());
        return randomHeading;
    }

    public void move(double pX, double pY, double pZ) {
        Vec3 pos = new Vec3(this.x, this.y, this.z);
        Vec3 nextPos = pos.add(pX, pY, pZ);
        BlockHitResult raycast = this.level.clip(new ClipContext(pos, nextPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, (Entity) null));

        this.distanceMoved += raycast.getLocation().distanceTo(pos);

        this.setBoundingBox(this.getBoundingBox().move(raycast.getLocation().subtract(pos)));
        this.setLocationFromBoundingbox();

        if (raycast.getType() == HitResult.Type.BLOCK && this.canCollide()) this.onCollide(raycast);
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera camera, float pPartialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        PoseStack posestack = new PoseStack();
        posestack.translate(x, y, z);
        renderBug(posestack, pBuffer, camera, pPartialTicks);
    }

    public abstract void renderBug(PoseStack posestack, VertexConsumer pBuffer, Camera camera, float pPartialTicks);

    public abstract boolean canCollide();

    public void onCollide(BlockHitResult raycast) {
        this.attachmentDirection = raycast.getDirection();
    }

    private void drawDoubleSidedQuad(VertexConsumer pBuffer, Matrix4f matrix, float maxX, float maxZ, float minX, float minZ, float maxU, float maxV, float minU, float minV, int packedLight) {
        pBuffer.vertex(matrix, maxX, 0, maxZ).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(matrix, maxX, 0, minZ).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(matrix, minX, 0, minZ).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(matrix, minX, 0, maxZ).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();

        pBuffer.vertex(matrix, minX, 0, maxZ).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(matrix, minX, 0, minZ).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(matrix, maxX, 0, minZ).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(matrix, maxX, 0, maxZ).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
    }
}
