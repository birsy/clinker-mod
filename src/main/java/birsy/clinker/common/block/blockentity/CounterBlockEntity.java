package birsy.clinker.common.block.blockentity;

import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CounterBlockEntity extends BlockEntity {
    public static final int SLOT_SIDE_LENGTH = 4;
    public static final float SLOT_SIZE = 1.0F / SLOT_SIDE_LENGTH;

    public NonNullList<ItemStack> items = NonNullList.withSize(SLOT_SIDE_LENGTH * SLOT_SIDE_LENGTH, ItemStack.EMPTY);
    public NonNullList<Float> itemRotations = NonNullList.withSize(items.size(), 0.0F);

    public CounterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClinkerBlockEntities.COUNTER.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);

        CompoundTag rotations = new CompoundTag(itemRotations.size());
        for (int i = 0; i < itemRotations.size(); i++) rotations.putFloat("" + i, itemRotations.get(i));
        tag.put("ItemRotations", rotations);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        // i have no idea why this works and just loading it directly into ingredients doesn't?
        // whatever!!
        NonNullList<ItemStack> newItems = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, newItems, registries);

        CompoundTag rotations = tag.getCompound("ItemRotations");
        for (int i = 0; i < itemRotations.size(); i++)
            itemRotations.set(i, rotations.getFloat("" + i));

        this.items = newItems;
    }

    public static int getIndex(int slotX, int slotZ) {
        if (slotX >= SLOT_SIDE_LENGTH || slotX < 0 || slotZ >= SLOT_SIDE_LENGTH || slotZ < 0)
            return -1;
        return slotX * SLOT_SIDE_LENGTH + slotZ;
    }

    public static int getSlot(double axis) {
        return (int) Math.floor(Mth.frac(axis) * 4);
    }

    private boolean isClient() {
        return this.getLevel() != null && this.getLevel().isClientSide();
    }

    private boolean isSlotObstructed(int slotX, int slotZ) {
        Level level = this.getLevel();
        if (level == null) return false;

        BlockPos obstructingPos = this.getBlockPos().above();
        BlockState obstructingState = level.getBlockState(obstructingPos);
        VoxelShape collisionShape = obstructingState.getCollisionShape(level, obstructingPos);
        if (collisionShape.isEmpty()) return false;

        double x = (slotX + 0.5) * SLOT_SIZE, y = 0,
               z = (slotZ + 0.5) * SLOT_SIZE;
        double xzRadius = SLOT_SIZE * 0.2, height = SLOT_SIZE * 0.5;
        AABB slotAABB = new AABB(x - xzRadius, y, z - xzRadius, x + xzRadius, y + height, z + xzRadius);
        for (AABB collisionShapeSegment : collisionShape.toAabbs()) {
            if (slotAABB.intersects(collisionShapeSegment))
                return true;
        }

        return false;
    }

    public void updateSlotsFromObstruction() {
        Level level = this.getLevel();
        if (level == null || level.isClientSide()) return;
        for (int x = 0; x < SLOT_SIDE_LENGTH; x++) {
            for (int z = 0; z < SLOT_SIDE_LENGTH; z++) {
                if (isSlotObstructed(x, z)) {
                    spawnItem(x, z, removeItem(x, z));
                }
            }
        }
    }

    public boolean spawnItem(int slotX, int slotZ, ItemStack item) {
        if (item.isEmpty()) return false;

        Level level = this.getLevel();
        if (level == null) return false;

        int index = getIndex(slotX, slotZ);
        if (index == -1) return false;

        double x = this.getBlockPos().getX() + (slotX + 0.5) * SLOT_SIZE,
               y = this.getBlockPos().getY() + 1,
               z = this.getBlockPos().getZ() + (slotZ + 0.5) * SLOT_SIZE;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, item);
        itemEntity.setDeltaMovement(
                level.random.triangle(0.0F, 0.1F),
                level.random.triangle(0.2F, 0.1F),
                level.random.triangle(0.0F, 0.1F)
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
        return true;
    }

    public boolean addItem(@Nullable LivingEntity entity, ItemStack item, int slotX, int slotZ) {
        if (item.isEmpty()) return false;
        int index = getIndex(slotX, slotZ);
        if (index == -1) return false;

        ItemStack currentItem = items.get(index);
        int remainingStackSize = currentItem.getMaxStackSize() - currentItem.getCount();
        if (currentItem.isStackable() && currentItem.is(item.getItem()) && remainingStackSize > 0) {
            if (this.isClient()) return true;
            int amountToAdd = Math.min(item.getCount(), remainingStackSize);
            item.shrink(amountToAdd);
            currentItem.grow(amountToAdd);
            return true;
        }
        if (!currentItem.isEmpty()) return false;

        if (isSlotObstructed(slotX, slotZ)) return false;

        if (this.isClient()) return true;

        items.set(index, item.copyAndClear());
        // compute new rotation
        if (entity != null) {
            double slotPosX = this.getBlockPos().getX() + slotX * SLOT_SIZE + 0.5 * SLOT_SIZE,
                   slotPosZ = this.getBlockPos().getZ() + slotZ * SLOT_SIZE + 0.5 * SLOT_SIZE;
            Vec3 eyePos = entity.getEyePosition();
            double dX = eyePos.x - slotPosX, dZ = eyePos.z - slotPosZ;
            double length = Mth.length(dX, dZ);
            if (length > 0) {
                dX /= length; dZ /= length;
                float newRotation = (float) Mth.atan2(-dZ, dX) - Mth.HALF_PI;
                itemRotations.set(index, newRotation);
            }
        } else {
            itemRotations.set(index, 0.0F);
        }
        markUpdated();
        return true;
    }

    public ItemStack removeItem(int slotX, int slotZ) {
        int index = getIndex(slotX, slotZ);
        if (index == -1) return ItemStack.EMPTY;
        ItemStack item = items.get(index);
        if (this.isClient()) return item;
        markUpdated();
        return item.copyAndClear();
    }

    private void markUpdated() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
