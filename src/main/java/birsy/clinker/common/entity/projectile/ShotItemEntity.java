package birsy.clinker.common.entity.projectile;

import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ShotItemEntity extends Projectile {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(ShotItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Vector3f> DATA_AXIS =
            SynchedEntityData.defineId(ShotItemEntity.class, EntityDataSerializers.VECTOR3);
    float rot = 0F, pRot = 0F;

    public ShotItemEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }
    public static ShotItemEntity shoot(Level level, LivingEntity thrower, ItemStack itemStack) {
        ShotItemEntity entity = new ShotItemEntity(ClinkerEntities.SHOT_ITEM.get(), level);
        entity.setOwner(thrower);
        entity.shootFromRotation(thrower,
                thrower.getXRot(), thrower.getYRot(),
                0.0F, 1.5F, 0.0F
        );
        Vec3 throwDirection = entity.getDeltaMovement().normalize();
        entity.setPos(thrower.getEyePosition().add(throwDirection));

        Vec3 axis = throwDirection.cross(new Vec3(0, 1, 0));
        entity.setAxis((float) axis.x(), (float) axis.y(), (float) axis.z());

        entity.setItem(itemStack);
        return entity;
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM, ItemStack.EMPTY);
        builder.define(DATA_AXIS, new Vector3f(0, 0, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (!this.getItem().isEmpty())
            compound.put("Item", this.getItem().save(this.registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Item", 10)) {
            CompoundTag compoundtag = compound.getCompound("Item");
            this.setItem(ItemStack.parse(this.registryAccess(), compoundtag).orElse(ItemStack.EMPTY));
        } else {
            this.setItem(ItemStack.EMPTY);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.pRot = this.rot;

            Vec3 moveDirection = this.getDeltaMovement().normalize();
            float speed = (float) this.getDeltaMovement().length();
            Vec3 axis = moveDirection.cross(new Vec3(0, 1, 0));
            float alignment = this.getAxis().dot((float) axis.x() * speed, (float) axis.y() * speed, (float) axis.z()) * speed;
            this.rot += alignment;
        }
    }

    @Override
    protected void onHit(HitResult result) {
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
        }
        result.getEntity().hurt(source, (float) damage);
    }

    public void convertToItemEntity() {
        if (this.level().isClientSide()) return;
        if (!this.getItem().isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(
                    this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()
            );
            this.level().addFreshEntity(itemEntity);
        }
        this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
        this.remove(RemovalReason.DISCARDED);
    }

    public ItemStack getItem() { return this.entityData.get(DATA_ITEM); }
    public void setItem(ItemStack stack) { this.entityData.set(DATA_ITEM, stack); }

    public Vector3f getAxis() { return this.entityData.get(DATA_AXIS); }
    public void setAxis(Vector3f axis) { this.entityData.set(DATA_AXIS, this.getAxis().set(axis)); }
    public void setAxis(float x, float y, float z) { this.entityData.set(DATA_AXIS, this.getAxis().set(x, y, z)); }
}
