package birsy.clinker.common.world.entity.mold;

import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MoldEntity extends LivingEntity {
    public int startingEnergy;
    public int maxDepth;

    MoldCell root;
    public Int2ObjectMap<MoldCell> parts;
    protected AtomicInteger currentId = new AtomicInteger(0);
    Set<BlockPos> occupiedSpaces;

    
    public AABB renderAABB;
    public MoldEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.parts = new Int2ObjectArrayMap<>();
        this.occupiedSpaces = new HashSet<>();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.renderAABB = new AABB(this.blockPosition());
        this.setPos(Math.floor(this.getX()) + 0.5F, Math.floor(this.getY()), Math.floor(this.getZ()) + 0.5F);

        if (this.root == null) {
            this.startingEnergy = 48;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.startingEnergy);
            this.root = new MoldCell(this, MoldCell.AttachmentPoint.DOWN_FACE, this.startingEnergy);
        }

    }

    public void addMoldCell(MoldCell cell) {
        this.parts.put(cell.id, cell);
        if (this.level().isClientSide()) {
            this.renderAABB.minmax(new AABB(cell.pos));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.root.energy <= 0) {
            this.remove(RemovalReason.KILLED);
        }
        this.root.tick();
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return super.hurt(p_21016_, p_21017_);

//        if (this.isInvulnerableTo(p_21016_)) {
//            return false;
//        } else if (this.level().isClientSide) {
//            return false;
//        } else if (this.isDeadOrDying()) {
//            return false;
//        } else if (p_21016_.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
//            return false;
//        }
//        this.root.hurt(p_21016_, p_21017_);
//        return true;
    }

    public boolean hurt(MoldPartEntity part, DamageSource source, float amount) {
        return this.hurt(source, amount);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.renderAABB;
    }

    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag cells = new CompoundTag();
        this.root.serialize(cells, 0);
        tag.put("Cells", cells);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Cells")) {
            CompoundTag cells = tag.getCompound("Cells");
            Clinker.LOGGER.info(cells.toString());
            this.currentId.set(cells.size() + 1);
            this.root = new MoldCell(this, tag.getCompound("Cell" + 0));
            for (int i = 1; i < cells.size(); i++) {
                CompoundTag cellTag = tag.getCompound("Cell" + i);
                MoldCell child = new MoldCell(this, cellTag);
            }
        }

    }

    // can't be moved around.
    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    @Override
    public void push(Entity p_21294_) {}
    @Override
    public boolean isNoGravity() {
        return true;
    }
    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {}
    @Override
    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }
    @Override
    public void setDeltaMovement(Vec3 p_149804_) {
    }

    // all of these are required for living entities for whatever reason
    @Override
    public Iterable<ItemStack> getArmorSlots() {return Collections.singleton(ItemStack.EMPTY);}
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {return ItemStack.EMPTY;}
    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack item) {}
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public static class MoldPartEntity extends PartEntity<MoldEntity> {
        private final EntityDimensions size;

        public MoldPartEntity(MoldEntity entity) {
            super(entity);
            this.size = EntityDimensions.scalable(1, 1);
            this.refreshDimensions();
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag p_31025_) {
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag p_31028_) {
        }

        @Override
        public boolean isPickable() {
            return true;
        }

        @Nullable
        @Override
        public ItemStack getPickResult() {
            return this.getParent().getPickResult();
        }

        @Override
        public boolean hurt(DamageSource damageSource, float amount) {
            return this.isInvulnerableTo(damageSource) ? false : this.getParent().hurt(this, damageSource, amount);
        }

        @Override
        public boolean is(Entity entity) {
            return this == entity || this.getParent() == entity;
        }

        @Override
        public EntityDimensions getDimensions(Pose p_31023_) {
            return this.size;
        }

        @Override
        public boolean shouldBeSaved() {
            return false;
        }
    }
}
