package birsy.clinker.common.block.blockentity;

import birsy.clinker.common.block.AmberBlock;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EmbeddedAmberBlockEntity extends BlockEntity {
    private CompoundTag embeddedEntityTag = null;
    private Entity embeddedEntity = null;
    private ItemStack embeddedItem = ItemStack.EMPTY;

    public EmbeddedAmberBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClinkerBlockEntities.EMBEDDED_AMBER.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("embeddedItem", embeddedItem.saveOptional(registries));
        if (embeddedEntityTag != null) tag.put("embeddedEntity", embeddedEntityTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        embeddedItem = ItemStack.parseOptional(registries, tag.getCompound("embeddedItem"));
        if (tag.contains("embeddedEntity", Tag.TAG_COMPOUND)) {
            embeddedEntityTag = tag.getCompound("embeddedEntity");
            if (level != null) embeddedEntity = EntityType.loadEntityRecursive(embeddedEntityTag, level, entity -> entity);
        } else {
            embeddedEntityTag = null;
            embeddedEntity = null;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (embeddedEntityTag != null && level != null)
            embeddedEntity = EntityType.loadEntityRecursive(embeddedEntityTag, level, entity -> entity);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        // make certain the entity doesn't exist anywhere...
        if (embeddedEntity != null)
            embeddedEntity.remove(Entity.RemovalReason.DISCARDED);
    }

    public void setEmbeddedEntity(@Nullable Entity entity) {
        this.markUpdated();
        if (entity == null) {
            this.embeddedEntity = null;
            this.embeddedEntityTag = null;
            return;
        }

        BlockPos pos = getBlockPos();
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5 + entity.getBbHeight() * 0.5, pos.getZ() + 0.5);
        entity.setYBodyRot(entity.getRandom().nextFloat() * 180);
        this.embeddedEntity = entity;
        this.embeddedEntityTag = embeddedEntity.save(embeddedEntityTag) ? embeddedEntityTag : null;
    }
    @Nullable
    public Entity getEmbeddedEntity() {
        return this.embeddedEntity;
    }

    public void setEmbeddedItem(ItemStack itemStack) {
        this.embeddedItem = itemStack.copy();
    }
    public ItemStack getEmbeddedItem() {
        return this.embeddedItem;
    }

    public void dropEmbeddedObjects() {
        if (level == null) return;
        BlockPos pos = getBlockPos();
        while (!embeddedItem.isEmpty()) {
            ItemStack stack = embeddedItem.split(level.random.nextIntBetweenInclusive(10, 32) + 5);
            double x = pos.getX() + level.random.triangle(0.5, 0.5),
                   y = pos.getY() + level.random.triangle(0.5, 0.5),
                   z = pos.getZ() + level.random.triangle(0.5, 0.5);
            ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
            itemEntity.setDeltaMovement(0, 0, 0);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
        if (embeddedEntity != null) {
            embeddedEntity.setPos(pos.getX() + 0.5, pos.getY() + 0.5 + embeddedEntity.getBbHeight() * 0.5, pos.getZ() + 0.5);
            level.addFreshEntity(embeddedEntity);
            // clear out an area for the entity to spawn!
            BlockPos.betweenClosedStream(embeddedEntity.getBoundingBox()).forEach(containedPos -> {
                BlockState state = level.getBlockState(containedPos);
                if (state.getBlock() instanceof AmberBlock) level.destroyBlock(pos, true, embeddedEntity);
            });
        }
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
