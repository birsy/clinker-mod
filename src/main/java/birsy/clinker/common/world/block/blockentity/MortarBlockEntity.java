package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MortarBlockEntity extends BlockEntity {
    public NonNullList<ItemStack> ingredients = NonNullList.withSize(4, ItemStack.EMPTY);
    public ItemStack result = ItemStack.EMPTY;
    float rotation, prevRotation;
    float recipeProgress, prevRecipeProgress;

    public MortarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClinkerBlockEntities.MORTAR.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag items = new CompoundTag();
        ContainerHelper.saveAllItems(items, this.ingredients, registries);
        tag.put("Items", items);
        tag.put("Result", result.saveOptional(registries));
        tag.putFloat("RecipeProgress", recipeProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        // i have no idea why this works and just loading it directly into ingredients doesn't?????
        // ugh whatever
        NonNullList<ItemStack> newIngredients = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag.getCompound("Items"), newIngredients, registries);
        this.ingredients = newIngredients;

        result = ItemStack.parseOptional(registries, tag.getCompound("Result"));
        recipeProgress = tag.getFloat("RecipeProgress");
    }

    public boolean addItem(@Nullable LivingEntity entity, ItemStack item) {
        if (item.isEmpty()) return false;
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) {
                if (level.isClientSide) return true;

                ingredients.set(i, item.consumeAndReturn(1, entity));
                markUpdated();
                return true;
            }
        }
        resetProgress();
        return false;
    }

    public ItemStack removeItemStack() {
        // find the last ingredient added
        ItemStack lastStack = ItemStack.EMPTY;
        int lastStackIndex = -1;
        for (int i = ingredients.size() - 1; i >= 0; i--) {
            ItemStack ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                lastStack = ingredient;
                lastStackIndex = i;
            }
        }
        if (lastStack.isEmpty()) return ItemStack.EMPTY;
        if (level.isClientSide()) return lastStack;
        resetProgress();
        ingredients.set(lastStackIndex, ItemStack.EMPTY);
        markUpdated();
        return lastStack;
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

    private void resetProgress() {
        this.recipeProgress = 0.0F;
        this.prevRecipeProgress = 0.0F;
    }

    public void grind() {
        this.recipeProgress++;
    }
}
