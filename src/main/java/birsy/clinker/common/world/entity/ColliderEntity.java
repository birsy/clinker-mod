package birsy.clinker.common.world.entity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ColliderEntity<T extends Entity & CollisionParent> extends Entity implements Attackable {
    private T parentMob;

    private EntityDimensions colliderDimensions;
    private static final EntityDataAccessor<Float> DATA_WIDTH_ID = SynchedEntityData.defineId(ColliderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT_ID = SynchedEntityData.defineId(ColliderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID_ID = SynchedEntityData.defineId(ColliderEntity.class, EntityDataSerializers.INT);
    private LivingEntity lastHurtByMob;

    public ColliderEntity(EntityType<? extends ColliderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        //this.setInvulnerable(true);
        //this.noPhysics = true;
    }

    public static <T extends Entity & CollisionParent> ColliderEntity<T> create(T parent, float horizontalSize, float verticalSize) {
        return new ColliderEntity<T>(ClinkerEntities.COLLIDER.get(), parent.level()).setDimensions(horizontalSize, verticalSize).setParent(parent);
    }

    public ColliderEntity<T> setParent(T parent) {
        this.entityData.set(DATA_PARENT_ID_ID, parent.getId());
        this.parentMob = parent;
        this.updateParent();
        return this;
    }

    private void updateParent() {
        this.blocksBuilding = this.parentMob.blocksBuilding;
    }

    public T getParent() {
        if (parentMob == null || this.level().getEntity(this.entityData.get(DATA_PARENT_ID_ID)) != parentMob) {
            Entity newParentMob = this.level().getEntity(this.entityData.get(DATA_PARENT_ID_ID));
            if (newParentMob != null) {
                if (newParentMob instanceof Entity && newParentMob instanceof CollisionParent) {
                    this.parentMob = (T) newParentMob;
                    this.updateParent();
                } else {
                    Clinker.LOGGER.error("Something has gone terribly wrong. No valid entity exists with id {} for ColliderEntity!", this.entityData.get(DATA_PARENT_ID_ID));
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }

        return this.parentMob;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.parentMob == null || this.parentMob.isRemoved() || this.parentMob.level() != this.level()) {
                this.remove(RemovalReason.DISCARDED);
            }
        }

        if (this.canBeCollidedWith()) {
            this.pushEntities();
        }
    }

    public void setWidth(float pWidth) {
        this.entityData.set(DATA_WIDTH_ID, pWidth);
    }

    public float getWidth() {
        return this.entityData.get(DATA_WIDTH_ID);
    }

    public void setHeight(float pHeight) {
        this.entityData.set(DATA_HEIGHT_ID, pHeight);
    }

    public float getHeight() {
        return this.entityData.get(DATA_HEIGHT_ID);
    }

    public ColliderEntity<T> setDimensions(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
        return this;
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.scalable(this.getWidth(), this.getHeight());
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getDimensions();
    }

    @Override
    protected AABB makeBoundingBox() {
        return this.getDimensions().makeBoundingBox(this.position());
    }

    @Override
    public boolean isPickable() {
        if (this.getParent() == null) return super.isPickable();
        return this.getParent().isPickable();
    }

    @Override
    public ItemStack getPickResult() {
        if (this.getParent() == null) return ItemStack.EMPTY;
        return this.getParent().getPickResult();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.getParent() == null) return false;
        if (pSource.getEntity() != null && pSource.getEntity() instanceof LivingEntity livingEntity && !pSource.is(DamageTypeTags.NO_ANGER))
            this.lastHurtByMob = livingEntity;
        return this.getParent().hurt(this, pSource, pAmount);
    }

    @Override
    public void push(Entity pusher) {
        if (this.getParent() == null) return;
        this.getParent().push(this, pusher);
    }

    @Override
    public boolean isPushable() {
        if (this.getParent() == null) return super.isPushable();
        return this.getParent().isPushable();
    }

    @Override
    public boolean is(Entity pEntity) {
        if (this.parentMob == null) return super.is(pEntity);
        if (pEntity instanceof ColliderEntity<?> collider) return collider.parentMob == this.getParent();
        return this == pEntity || this.getParent() == pEntity;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        if (this.is(pEntity)) return false;
        return super.canCollideWith(pEntity);
    }

    @Override
    public boolean canBeCollidedWith() {
        if (this.getParent() == null) return super.canBeCollidedWith();
        return this.getParent().canBeCollidedWith();
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    // stolen from LivingEntity
    protected void pushEntities() {
        if (this.level().isClientSide()) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
        } else {
            List<Entity> nearbyEntities = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
            if (!nearbyEntities.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
                if (i > 0 && nearbyEntities.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;

                    for(Entity entity : nearbyEntities) {
                        if (!entity.isPassenger()) {
                            ++j;
                        }
                    }

                    if (j > i - 1) {
                        this.hurt(this.damageSources().cramming(), 6.0F);
                    }
                }

                for(Entity entity1 : nearbyEntities) {
                    this.doPush(entity1);
                }
            }
        }
    }


    protected void doPush(Entity p_20971_) {
        p_20971_.push(this);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_WIDTH_ID, 1.0F);
        this.entityData.define(DATA_HEIGHT_ID, 1.0F);
        this.entityData.define(DATA_PARENT_ID_ID, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (DATA_HEIGHT_ID.equals(pKey) || DATA_WIDTH_ID.equals(pKey)) {
            this.setBoundingBox(this.makeBoundingBox());
        }
        if (DATA_PARENT_ID_ID.equals(pKey)) {
            this.setBoundingBox(this.makeBoundingBox());
        }
    }
    @Override
    public @Nullable LivingEntity getLastAttacker() {
        return this.lastHurtByMob;
    }
}
