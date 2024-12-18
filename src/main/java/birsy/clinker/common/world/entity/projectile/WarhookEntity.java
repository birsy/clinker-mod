package birsy.clinker.common.world.entity.projectile;

import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.util.VectorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WarhookEntity extends Projectile {
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(WarhookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ID_REELING = SynchedEntityData.defineId(WarhookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ID_HASOWNER = SynchedEntityData.defineId(WarhookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> ID_OWNERPOS = SynchedEntityData.defineId(WarhookEntity.class, EntityDataSerializers.VECTOR3);
    public Entity cachedOwner;

    private ItemStack item = new ItemStack(ClinkerItems.MOGUL_WARHOOK.get());
    private Entity hookedEntity;
    private boolean hasHookedEntity = false;
    boolean hasTouchedGround = false;

    
    private Vec3 direction = Vec3.ZERO, pDirection = Vec3.ZERO;
    
    private Vec3 ownerPos, pOwnerPos;
    
    public float roll, pRoll = 0;

    public WarhookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static WarhookEntity toss(Level pLevel, LivingEntity thrower, ItemStack stack) {
        WarhookEntity entity = new WarhookEntity(ClinkerEntities.WARHOOK.get(), pLevel);
        entity.item = stack;
        entity.setOwner(thrower);
        entity.setPos(thrower.getEyePosition().add(entity.getDeltaMovement().normalize()));
        entity.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 1, 0.0F);

        return entity;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ID_FOIL, false);
        builder.define(ID_REELING, false);
        builder.define(ID_HASOWNER, false);
        builder.define(ID_OWNERPOS, new Vector3f(0, 0, 0));
    }

    boolean embedded = false;
    Vec3 embedDirection = Vec3.ZERO;

    public void tick() {
        super.tick();

        Vec3 position = this.position();
        Vec3 velocity = this.getDeltaMovement();

        FluidState state = this.level().getFluidState(this.blockPosition());
        float drag = 0.99F;
        float gravity = -0.024F;
        if (!state.isEmpty()) {
            drag = 0.9F;
            gravity *= -0.1;
        }
        if ((this.tickCount < 2 * 20 && !hasTouchedGround) || this.embedded) gravity = 0;

        velocity = velocity.scale(drag).add(0, gravity, 0);

        Vec3 endPosition = position.add(velocity);

        BlockHitResult blockHitResult = this.level().clip(new ClipContext(position, embedded ? position.add(embedDirection.scale(1.0F / 16.0F)) : endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, position, endPosition, this.getBoundingBox().expandTowards(velocity).inflate(1.0D), this::canHitEntity);

        if (blockHitResult.getType() == HitResult.Type.BLOCK || blockHitResult.isInside()) {
            embedded = true;
            embedDirection = position.subtract(endPosition).normalize();
            endPosition = blockHitResult.getLocation().subtract(embedDirection.scale(1.0F / 16.0F));
            velocity = Vec3.ZERO;
        }

        this.pDirection = this.direction;
        if (velocity.length() > 0.01) {
            this.direction = velocity.normalize();
        }

        if (this.level().isClientSide()) {
            if (this.hasOwner()) {
                this.pOwnerPos = this.ownerPos;
                this.ownerPos = new Vec3(this.entityData.get(ID_OWNERPOS));
            }
        } else {
            Entity owner = this.getOwner();
            if (owner != null) {
                if (owner instanceof LivingEntity e) {
                    float rotation = (float) Math.toRadians(e.yBodyRot);
                    Vec3 offsetVector = new Vec3(-Math.cos(rotation), 0, -Math.sin(rotation)).scale(e.getBbWidth() * 0.5).add(0, e.getBbHeight() * 0.5, 0);
                    this.entityData.set(ID_OWNERPOS, e.position().add(offsetVector).toVector3f());
                }

            }
        }

        this.setPos(endPosition);
        this.setDeltaMovement(velocity);
    }

    @Override
    public void setOwner(@Nullable Entity pOwner) {
        super.setOwner(pOwner);
        this.entityData.set(ID_HASOWNER, true);
    }

    public boolean hasOwner() {
        return this.entityData.get(ID_HASOWNER);
    }

    public Vec3 getOwnerPos(float partialTick) {
        if (this.ownerPos == null) return this.getPosition(partialTick);
        if (this.pOwnerPos == null) this.pOwnerPos = this.ownerPos;
        return this.pOwnerPos.lerp(this.ownerPos, partialTick);
    }

    public Vec3 getDirection(float partialTick) {
        return VectorUtil.slerp(this.pDirection, this.direction, partialTick).normalize();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.direction = new Vec3(pCompound.getDouble("TailX"), pCompound.getDouble("TailY"), pCompound.getDouble("TailZ"));
        this.pDirection = this.direction;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putDouble("TailX", this.direction.x());
        pCompound.putDouble("TailY", this.direction.y());
        pCompound.putDouble("TailZ", this.direction.z());
    }
}
