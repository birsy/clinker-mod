package birsy.clinker.common.world.entity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;

public class ColliderEntity<T extends Entity & CollisionParent> extends LivingEntity {
    private T parentMob;
    private EntityDimensions colliderDimensions;
    private static final EntityDataAccessor<Float> DATA_HORIZONTAL_SIZE_ID = SynchedEntityData.defineId(ColliderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_VERTICAL_SIZE_ID = SynchedEntityData.defineId(ColliderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID_ID = SynchedEntityData.defineId(ColliderEntity.class, EntityDataSerializers.INT);

    @OnlyIn(Dist.CLIENT)
    float clientHorizontalSize = 1.0F, clientVerticalSize = 1.0F;

    public ColliderEntity(EntityType<? extends ColliderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static <T extends Entity & CollisionParent> ColliderEntity create(T parent, float horizontalSize, float verticalSize) {
        ColliderEntity entity = new ColliderEntity(ClinkerEntities.COLLIDER.get(), parent.level());//.setDimensions(horizontalSize, verticalSize).setParent(parent);
        return entity;
    }

//    public ColliderEntity setDimensions(float horizontal, float vertical) {
//        this.entityData.set(DATA_HORIZONTAL_SIZE_ID, horizontal);
//        this.entityData.set(DATA_VERTICAL_SIZE_ID, vertical);
//
//        this.colliderDimensions = new EntityDimensions(horizontal, vertical, false);
//        return this;
//    }
//
//    public ColliderEntity setParent(T parent) {
//        this.parentMob = parent;
//        this.entityData.set(DATA_PARENT_ID_ID, parent.getId());
//
//        return this;
//    }
//
//    public T getParent() {
//        if (parentMob == null) {
//            Entity newParentMob = this.level().getEntity(this.entityData.get(DATA_PARENT_ID_ID));
//            if (newParentMob != null) {
//                if (newParentMob instanceof Entity && newParentMob instanceof CollisionParent) this.parentMob = (T) newParentMob;
//                else throw new ClassCastException("Invalid entity type of entity with id " + this.entityData.get(DATA_PARENT_ID_ID) + " for ColliderEntity!");
//            }
//        }
//
//        return this.parentMob;
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//        if (!this.level().isClientSide) {
//            if (this.parentMob == null || this.parentMob.isRemoved() || this.parentMob.level() != this.level()) {
//                this.remove(RemovalReason.DISCARDED);
//            } else {
//                this.entityData.set(DATA_PARENT_ID_ID, this.parentMob.getId());
//            }
//        } else {
//            if (this.clientHorizontalSize != this.entityData.get(DATA_HORIZONTAL_SIZE_ID) || this.clientVerticalSize != this.entityData.get(DATA_VERTICAL_SIZE_ID)) {
//                this.clientHorizontalSize = this.entityData.get(DATA_HORIZONTAL_SIZE_ID);
//                this.clientVerticalSize = this.entityData.get(DATA_VERTICAL_SIZE_ID);
//                this.colliderDimensions = new EntityDimensions(clientHorizontalSize, clientVerticalSize, false);
//            }
//        }
//    }
//
//    @Override
//    public EntityDimensions getDimensions(Pose pPose) {
//        return super.getDimensions(pPose);
//        //return this.colliderDimensions;
//    }
//
//    @Override
//    public boolean isPickable() {
//        if (this.getParent() == null) return false;
//        return this.getParent().isPickable();
//    }
//
//    @Override
//    public ItemStack getPickResult() {
//        if (this.getParent() == null) return ItemStack.EMPTY;
//        return this.getParent().getPickResult();
//    }
//
//    @Override
//    public boolean hurt(DamageSource pSource, float pAmount) {
//        if (this.getParent() == null) return false;
//        return this.getParent().hurt(this, pSource, pAmount);
//    }
//
//    @Override
//    public void push(Entity pusher) {
//        if (this.getParent() == null) return;
//        this.getParent().push(this, pusher);
//    }
//
//    @Override
//    public boolean isPushable() {
//        if (this.getParent() == null) return true;
//        return this.getParent().isPushable();
//    }
//
//    @Override
//    public boolean is(Entity pEntity) {
//        if (pEntity instanceof ColliderEntity<?> collider) return collider.parentMob == this.getParent();
//        return this == pEntity || this.getParent() == pEntity;
//    }
//
//    @Override
//    public boolean canCollideWith(Entity pEntity) {
//        if (pEntity == this.getParent()) return false;
//        return super.canCollideWith(pEntity);
//    }
//
//    @Override
//    public boolean shouldBeSaved() {
//        return false;
//    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_HORIZONTAL_SIZE_ID, 1.0F);
        this.entityData.define(DATA_VERTICAL_SIZE_ID, 1.0F);
        this.entityData.define(DATA_PARENT_ID_ID, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    public Iterable<ItemStack> getArmorSlots() { return List.of(ItemStack.EMPTY); }
    public ItemStack getItemBySlot(EquipmentSlot pSlot) { return ItemStack.EMPTY; }
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {}
    public HumanoidArm getMainArm() {return HumanoidArm.LEFT;}




}
