package birsy.clinker.client.render.particle;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BugParticle extends TextureSheetParticle {
    protected final SpriteSet sprites;
    private float jitter;
    private float speed;
    private Vec3 desiredDirection;
    private Vec3 pDesiredDirection;
    private Vec3 movementPlane;
    protected BugParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.sprites = pSprites;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 0.75F;
        this.lifetime = 60 + this.random.nextInt(12);
        this.jitter = (float) this.random.nextGaussian() * 0.001F;
        this.speed = (float) ((this.random.nextGaussian() * 0.5F) + 0.5F) * 0.005F;
        Direction.Axis axis = Direction.Axis.getRandom(this.random);
        this.movementPlane = new Vec3(axis == Direction.Axis.X ? 0 : 1, axis == Direction.Axis.Y ? 0 : 1, axis == Direction.Axis.Z ? 0 : 1);
        this.desiredDirection = new Vec3((this.random.nextFloat() * 2.0) - 1.0, (this.random.nextFloat() * 2.0) - 1.0, (this.random.nextFloat() * 2.0) - 1.0).normalize();
        this.pDesiredDirection = this.desiredDirection;
        this.roll = (float) (this.random.nextGaussian() * Math.PI * 2.0F);
        this.oRoll = this.roll;
        this.pickSprite(pSprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.pDesiredDirection = this.desiredDirection;
        this.desiredDirection = this.desiredDirection.add(new Vec3((this.random.nextFloat() * 2.0) - 1.0, (this.random.nextFloat() * 2.0) - 1.0, (this.random.nextFloat() * 2.0) - 1.0).scale(0.01F)).normalize();
        Vec3 velocity = this.desiredDirection.scale(speed * ((this.random.nextGaussian() * 0.5F) + 0.5F));
        this.move(velocity.x(), velocity.y(), velocity.z());
        this.move(this.random.nextFloat() * this.jitter * 2 - this.jitter, this.random.nextFloat() * this.jitter * 2 - this.jitter, this.random.nextFloat() * this.jitter * 2 - this.jitter);
        this.roll = (float) (this.roll + (this.random.nextGaussian() * 0.01F));
        BlockState block = this.level.getBlockState(new BlockPos(this.x, this.y, this.z));
        //Clinker.LOGGER.info(block.getRegistryName().toString());
        if (block.isAir()) {
            //this.gravity = 1.0F;
        } else {
            this.gravity = 0.0F;
        }
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new BugParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
