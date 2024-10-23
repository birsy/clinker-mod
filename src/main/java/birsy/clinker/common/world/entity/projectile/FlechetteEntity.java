package birsy.clinker.common.world.entity.projectile;

import birsy.clinker.common.world.alchemy.effects.ChainLightningHandler;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FlechetteEntity extends Projectile implements IEntityWithComplexSpawn {
    private static final EntityDataAccessor<Quaternionf> DATA_ORIENTATION = SynchedEntityData.defineId(FlechetteEntity.class, EntityDataSerializers.QUATERNION);

    protected OrdnanceEffects effects = OrdnanceEffects.DEFAULT_EFFECT_PARAMS;
    final Quaternionf currentOrientation = new Quaternionf(), previousOrientation = new Quaternionf();

    boolean stuck = false;
    float stuckDistance = 0.0F;
    final Vector3f stuckDirection = new Vector3f(0, -1, 0);

    public static final int MAX_LIFETIME = 20 * 20;

    public FlechetteEntity(EntityType<? extends FlechetteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // networking & serialization
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.effects.serialize(new CompoundTag()));
    }
    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.effects = OrdnanceEffects.deserialize(buffer.readNbt());
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        effects.serialize(pCompound);
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.effects = OrdnanceEffects.deserialize(pCompound);
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ORIENTATION, new Quaternionf());
    }

    // tick
    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > MAX_LIFETIME) {
            this.discard();
            return;
        }
        if (!this.noPhysics) this.tickPhysics();
        if (this.level().isClientSide()) this.tickClient();
    }

    protected void tickPhysics() {
        Vec3 position = this.position();
        Vec3 velocity = this.getDeltaMovement();

        if (this.stuck) {
            this.entityData.set(DATA_ORIENTATION, new Quaternionf().rotationTo(
                    0, 1, 0,
                    this.stuckDirection.x,
                    this.stuckDirection.y,
                    this.stuckDirection.z
            ));

            // check if we should be unstuck
            BlockHitResult blockHitResult = this.level().clip(
                    new ClipContext(
                            position,
                            position.add(this.stuckDirection.x * this.stuckDistance, this.stuckDirection.y * this.stuckDistance, this.stuckDirection.z * this.stuckDistance),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this
                    )
            );
            if (blockHitResult.getType() == HitResult.Type.MISS) this.stuck = false;
            return;
        }

        float drag = this.getDrag();
        float gravity = this.getGravity();

        velocity = velocity.scale(drag);
        velocity = velocity.add(0, gravity, 0);
        this.setDeltaMovement(velocity);

        if (this.getDeltaMovement().length() > 0.005) {
            float vLength = (float) this.getDeltaMovement().length();
            this.entityData.set(DATA_ORIENTATION, new Quaternionf().rotationTo(
                    0, 1, 0,
                    (float) this.getDeltaMovement().x / vLength,
                    (float) this.getDeltaMovement().y / vLength,
                    (float) this.getDeltaMovement().z / vLength
            ));
            if (this.level().isClientSide()) {
                Vector3f particlePos = new Vector3f(0, -5.5F/16.0F, 0);
                this.getOrientation(1.0F).transform(particlePos);
                this.level().addParticle(ParticleTypes.ENCHANTED_HIT,
                        this.getX() + particlePos.x, this.getY() + particlePos.y, this.getZ() + particlePos.z,
                        0, 0, 0
                );
            }
        }

        Vec3 endPosition = position.add(velocity);

        BlockHitResult blockHitResult = this.level().clip(new ClipContext(position, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, position, endPosition, this.getBoundingBox().expandTowards(velocity).inflate(1.0D), this::canHitEntity);

        // only register whichever hit is closer...
        boolean hitBlock = blockHitResult.getType() == HitResult.Type.BLOCK;
        boolean hitEntity = entityHitResult != null;
        if (hitBlock && hitEntity) {
            boolean isBlockCloser = blockHitResult.getLocation().distanceTo(position) < entityHitResult.getLocation().distanceTo(position);
            hitBlock = isBlockCloser;
            hitEntity = !isBlockCloser;
        }

        if (hitBlock) {
            this.onHitBlock(blockHitResult);
        } else if (hitEntity) {
            this.onHitEntity(entityHitResult);
        }

        Vec3 finalPos = position.add(this.getDeltaMovement());
        this.setPos(finalPos);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));

        Entity entity = pResult.getEntity();
        if (!this.canHitEntity(entity)) {
            if (this.canCollideWith(entity)) this.setDeltaMovement(this.getDeltaMovement().scale(-1));
            return;
        }
        // todo: custom damage type
        entity.hurt(entity.damageSources().source(DamageTypes.ARROW, this.getOwner(), this), 4.0F);
        if (entity instanceof LivingEntity livingentity) {
            if (!this.level().isClientSide) livingentity.setArrowCount(livingentity.getArrowCount() + 1);
            for (MobEffectInstance effect : this.effects.potion().getEffects()) livingentity.addEffect(effect, this.getOwner());
            if (this.effects.electrified()) ChainLightningHandler.shock(this, livingentity);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, null));

        if (this.effects.touchType() == OrdnanceEffects.TouchType.BOUNCE) {
            Vec3 normal = new Vec3(pResult.getDirection().getStepX(), pResult.getDirection().getStepY(), pResult.getDirection().getStepZ());
            this.setDeltaMovement( VectorUtils.reflect(normal, this.getDeltaMovement()) );
        } else {
            this.stuck = true;
            this.stuckDirection.set(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).normalize();

            float length = 0.15F;
            Vec3 position = pResult.getLocation().subtract( this.stuckDirection.x() * length, this.stuckDirection.y() * length, this.stuckDirection.z() * length);

            this.stuckDistance = length + 0.1F;
            this.setDeltaMovement(0, 0, 0);
            this.setPos(position);
        }
    }

    protected float getGravity() {
        float gravity = -0.024F;
        if (this.isUnderWater()) gravity *= -0.1F;
        return gravity;
    }
    protected float getDrag() {
        float drag = 0.99F;
        if (this.isUnderWater()) drag = 0.9F;
        return drag;
    }

    protected void tickClient() {
        this.previousOrientation.set(this.currentOrientation);
        this.currentOrientation.set(this.entityData.get(DATA_ORIENTATION));
    }

    final Quaternionf returnOrientation = new Quaternionf();
    public Quaternionf getOrientation(float partialTicks) {
        return this.previousOrientation.slerp(this.currentOrientation, partialTicks, returnOrientation).normalize();
    }
}
