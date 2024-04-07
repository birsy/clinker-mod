package birsy.clinker.common.world.entity.ordnance;

import birsy.clinker.client.render.particle.OrdnanceExplosionParticle;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundOrdnanceExplosionPacket;
import birsy.clinker.common.world.entity.ordnance.modifer.OrdnanceModifier;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class OrdnanceEntity extends Projectile {
    public final Set<OrdnanceModifier> modifiers = new HashSet<>();
    private static final EntityDataAccessor<ItemStack> DATA_ID_ORDNANCE_ITEM = SynchedEntityData.defineId(OrdnanceEntity.class, EntityDataSerializers.ITEM_STACK);

    @OnlyIn(Dist.CLIENT)
    private float spin, pSpin;

    public OrdnanceEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static OrdnanceEntity create(Level pLevel, double x, double y, double z, ItemStack ordnanceItem) {
        OrdnanceEntity entity = new OrdnanceEntity(ClinkerEntities.ORDNANCE.get(), pLevel);
        entity.setPos(x, y, z);
        entity.entityData.set(DATA_ID_ORDNANCE_ITEM, ordnanceItem);
        pLevel.addFreshEntity(entity);
        return entity;
    }

    public static OrdnanceEntity toss(Level pLevel, LivingEntity thrower, ItemStack ordnanceItem) {
        OrdnanceEntity entity = new OrdnanceEntity(ClinkerEntities.ORDNANCE.get(), pLevel);
        entity.setOwner(thrower);
        entity.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.8F, 0.0F);
        entity.setPos(thrower.getEyePosition().add(entity.getDeltaMovement().normalize()));
        return entity;
    }

    public static OrdnanceEntity fireAtPosition(ItemStack ordnanceItem, Level pLevel, Vec3 currentPosition, Vec3 targetPosition, int timeInTicks) {
        OrdnanceEntity entity = OrdnanceEntity.create(pLevel, currentPosition.x, currentPosition.y, currentPosition.z, ordnanceItem);
        double timeSquared = timeInTicks * timeInTicks;
        Vec3 delta = targetPosition.subtract(currentPosition);
        Vec3 acceleration = new Vec3(0, -0.024, 0);

        double velocityX = (delta.x - (0.5 * acceleration.x * timeSquared)) / (double)timeInTicks;
        double velocityY = (delta.y - (0.5 * acceleration.y * timeSquared)) / (double)timeInTicks;
        double velocityZ = (delta.z - (0.5 * acceleration.z * timeSquared)) / (double)timeInTicks;
        entity.setDeltaMovement(velocityX, velocityY, velocityZ);

        return entity;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_ORDNANCE_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_37075_) {
        super.addAdditionalSaveData(p_37075_);

        ItemStack itemstack = this.entityData.get(DATA_ID_ORDNANCE_ITEM);
        if (!itemstack.isEmpty()) {
            p_37075_.put("FireworksItem", itemstack.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_37073_) {
        super.readAdditionalSaveData(p_37073_);

        ItemStack itemstack = ItemStack.of(p_37073_.getCompound("FireworksItem"));
        if (!itemstack.isEmpty()) {
            this.entityData.set(DATA_ID_ORDNANCE_ITEM, itemstack);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.noPhysics) {
            Vec3 position = this.position();
            Vec3 velocity = this.getDeltaMovement();

            FluidState state = this.level().getFluidState(this.blockPosition());
            float drag = 0.99F;
            float gravity = -0.024F;
            if (!state.isEmpty()) {
                drag = 0.9F;
                gravity *= -0.1;
            }

            velocity = velocity.scale(drag);
            velocity = velocity.add(0, gravity, 0);

            Vec3 endPosition = position.add(velocity);

            BlockHitResult blockHitResult = this.level().clip(new ClipContext(position, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, position, endPosition, this.getBoundingBox().expandTowards(velocity).inflate(1.0D), this::canHitEntity);

            float bounceStrength = 0.2F;
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                Vec3 normal = new Vec3(blockHitResult.getDirection().getStepX(), blockHitResult.getDirection().getStepY(), blockHitResult.getDirection().getStepZ());
                if (normal.distanceTo(new Vec3(0, 1, 0)) < 0.01) {
                    //extra little force to prevent goofy bouncing
                    velocity = velocity.subtract(0, gravity, 0);
                }
                velocity = VectorUtils.reflect(normal, velocity).scale(bounceStrength);
                this.onHitBlock(blockHitResult);
            } else if (entityHitResult != null) {
                if (entityHitResult.getType() == HitResult.Type.ENTITY) {
                    velocity = velocity.scale(-bounceStrength * 0.2F);
                    this.onHitEntity(entityHitResult);
                }
            }

            this.setDeltaMovement(velocity);

            Vec3 finalPos = position.add(velocity);
            this.setPos(finalPos);
        }

        if (this.level().isClientSide) {
            this.updateSpin();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));
        this.playCollisionSound(1, 0.4F);

        pResult.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), 3.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));
        this.playCollisionSound(1, 0.6F);
    }

    public void playCollisionSound(float volumeMultiplier, float pitchMultiplier) {
        Vec3 pos = this.position().add(0, this.getBbHeight() * 0.5, 0);
        float speed = (float) this.getDeltaMovement().length();
        float pitch = (random.nextFloat() + 0.5F) * (speed * 4 + 0.8F) * 0.6F;
        float volume = 0.5F;
        if (speed < 0.03) return;
        this.level().playSound(null, pos.x(), pos.y(), pos.z(), ClinkerSounds.ORDNANCE_BOUNCE.get(), SoundSource.BLOCKS, volume * volumeMultiplier, pitch * pitchMultiplier);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateSpin() {
        this.pSpin = this.spin;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPosition = camera.getPosition();
        Vec3 directionToCamera = this.position().subtract(camPosition).normalize();
        //spin less when you're facing it, so the rotation looks more natural.
        Vec3 deltaMovement = this.getPosition(0).subtract(this.getPosition(1));
        Vec3 axisOfRotation = deltaMovement.normalize().cross(new Vec3(0, 1, 0));
        float angleBasedSpinMultiplier = (float) axisOfRotation.dot(directionToCamera);

        //the circumference divided by the amount moved, so it appears like it's rolling.
        float spinAmount = (float) (deltaMovement.length() / (Mth.TWO_PI * 6.0F));
        float spinInRadians = spinAmount * Mth.TWO_PI * 3;

        this.spin += spinInRadians * angleBasedSpinMultiplier;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            Entity entity = pSource.getDirectEntity();
            if (entity == null) return false;
            if (entity instanceof OrdnanceEntity) return false;
            if (!this.level().isClientSide) {
                Vec3 lookVector = entity.getLookAngle();
                this.setDeltaMovement(this.getDeltaMovement().add(lookVector.scale(0.5)));
                this.setOwner(entity);
                this.level().playSound(null, this.position().x(), this.position().y(), this.position().z(), SoundEvents.TRIDENT_HIT, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            this.markHurt();
            return true;
        }
    }

    public float getSpin(float partialTicks) {
        return Mth.lerp(partialTicks, this.pSpin, this.spin);
    }


    public void detonate() {
        for (OrdnanceModifier modifier : this.modifiers) {
            modifier.onDetonate();
        }
        createOrdnanceExplosion(this.position().add(0, this.getBbHeight() * 0.5, 0), this.level(), this.getOwner(), this);
        this.discard();
    }

    private static final Vec3[] PARTICLE_POINTS = MathUtils.generateSpherePoints(500);
    public static void createOrdnanceExplosion(Vec3 location, Level level, @Nullable Entity thrower, @Nullable OrdnanceEntity ordnance) {
        float radius = 5F;

        if (level.isClientSide) {
            for (Vec3 particlePoint : PARTICLE_POINTS) {
                Vec3 velocity = particlePoint.normalize();
                velocity = velocity.scale(radius + (level.random.nextGaussian() * 0.1F));

                Vector3f baseColor = new Vector3f(1.0f, 1.0f, 0.5f);
                Vector3f smokeColor =  new Vector3f(0.8f, 0.8f, 0.8f);

                level.addParticle(new OrdnanceExplosionParticle.Options(baseColor, smokeColor, Mth.abs((float)  level.random.nextGaussian()) * 3.0F), location.x(), location.y(), location.z(),
                        velocity.x(), velocity.y, velocity.z);
            }

            level.addParticle(ParticleTypes.FLASH, true, location.x(), location.y(), location.z(), 0, 0, 0);
            level.addParticle(ClinkerParticles.EXPLOSION_LIGHT.get(), true, location.x(), location.y(), location.z(), 0, 0, 0);

            level.playLocalSound(location.x(), location.y(), location.z(), ClinkerSounds.ORDNANCE_EXPLODE.get(), SoundSource.BLOCKS, 3F, Mth.lerp(level.random.nextFloat(), 0.4F, 0.6F), false);
            level.playLocalSound(location.x(), location.y(), location.z(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 4F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
            level.playLocalSound(location.x(), location.y(), location.z(), SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, SoundSource.BLOCKS, 0.1F, Mth.lerp(level.random.nextFloat(), 0.7F, 0.9F), false);
        } else {
            ClinkerPacketHandler.sendToClientsTrackingChunk(level.getChunkAt(BlockPos.containing(location)), new ClientboundOrdnanceExplosionPacket(location));
        }

        for (Entity entity : level.getEntities(ordnance, new AABB(location.subtract(radius, radius, radius), location.add(radius, radius, radius)))) {
            if (entity.distanceToSqr(location) > radius * radius) {
                continue;
            }
            float scaledDistance = (float) (1 - (entity.position().distanceTo(location) / radius));
            float damage = Mth.lerp(Mth.sqrt(scaledDistance), 15, 32);
            Vec3 knockback = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(location).normalize().scale(Mth.sqrt(scaledDistance));
            DamageSource source = entity.damageSources().explosion(ordnance, thrower);//
            entity.hurt(source, damage);
            if (entity instanceof LivingEntity l) {
                if (l.isDamageSourceBlocked(source)) {
                    knockback = knockback.scale(8); //3
                }
            }
            entity.setOnGround(false);
            entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
        }
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return true;
    }
    @Override
    public boolean isPickable() {
        return true;
    }
    @Override
    public float getPickRadius() {
        return 1.0F;
    }
    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }
}
