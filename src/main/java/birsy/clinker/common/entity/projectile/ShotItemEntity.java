package birsy.clinker.common.entity.projectile;

import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ShotItemEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Vector3f> DATA_AXIS =
            SynchedEntityData.defineId(ShotItemEntity.class, EntityDataSerializers.VECTOR3);
    float rot = 0F, pRot = 0F;

    public ShotItemEntity(EntityType<? extends ShotItemEntity> entityType, Level level) {
        super(entityType, level);
        this.rot = level.random.nextFloat() * 3.141592F;
        this.pRot = this.rot;
    }
    public static ShotItemEntity create(Level level, LivingEntity thrower, ItemStack itemStack) {
        ShotItemEntity entity = new ShotItemEntity(ClinkerEntities.SHOT_ITEM.get(), level);
        entity.setOwner(thrower);
        Vec3 axis = thrower.calculateViewVector(0, thrower.getYRot()).cross(new Vec3(0, 1, 0));
        entity.setAxis((float) axis.x(), (float) axis.y(), (float) axis.z());
        entity.setItem(itemStack);
        return entity;
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_AXIS, new Vector3f(0, 0, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("AxisX", this.getAxis().x);
        compound.putFloat("AxisY", this.getAxis().y);
        compound.putFloat("AxisZ", this.getAxis().z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        float axisX = compound.getFloat("AxisX"), axisY = compound.getFloat("AxisY"), axisZ = compound.getFloat("AxisZ");
        this.setAxis(axisX, axisY, axisZ);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.pRot = this.rot;

            Vec3 moveDirection = this.getDeltaMovement().normalize();
            float speed = (float) this.getDeltaMovement().length();
            Vec3 axis = moveDirection.cross(new Vec3(0, 1, 0));
            if (moveDirection.x == 0 && moveDirection.y == 0) {
                this.rot -= speed;
            } else {
                float alignment = this.getAxis().dot((float) axis.x() * speed, (float) axis.y() * speed, (float) axis.z()) * speed;
                this.rot -= alignment;
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        convertToItemEntity();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        double damage = 1;
        DamageSource source = this.damageSources().mobProjectile(this,
                this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null
        );
        // todo: make this more robust
        if (!this.getItem().isEmpty()) {
            ItemStack stack = this.getItem();
            Item item = stack.getItem();

            if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
                ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
                damage = modifiers.compute(damage, EquipmentSlot.MAINHAND);
            }

            damage += item.getAttackDamageBonus(result.getEntity(), (float) damage, source);
            // its considered a CRIT!
            damage *= 1.5F;

            // this is so dumb
            if (item instanceof Equipable equipable && result.getEntity() instanceof Mob mob) {
                EquipmentSlot slot = equipable.getEquipmentSlot();
                if (mob.getItemBySlot(slot).isEmpty()) {
                    mob.equipItemIfPossible(stack);
                    this.discard();
                }
            }
        }

        result.getEntity().hurt(source, (float) damage);
    }

    public void convertToItemEntity() {
        if (this.level().isClientSide()) return;
        if (this.isRemoved() || this.getRemovalReason() != null) return;
        if (!this.getItem().isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(
                    this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()
            );
            this.level().addFreshEntity(itemEntity);
        }
        this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
        this.remove(RemovalReason.DISCARDED);
    }

    public Vector3f getAxis() { return this.entityData.get(DATA_AXIS); }
    public void setAxis(Vector3f axis) { this.entityData.set(DATA_AXIS,  new Vector3f(axis)); }
    public void setAxis(float x, float y, float z) { this.entityData.set(DATA_AXIS, new Vector3f(x, y, z)); }

    public float getRoll(float partialTick) { return Mth.lerp(partialTick, pRot, rot); }

    @Override protected Item getDefaultItem() { return Items.ARROW; }
}
