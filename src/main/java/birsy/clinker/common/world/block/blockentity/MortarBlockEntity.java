package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MortarBlockEntity extends BlockEntity {
    int lastAddedIndex = -1;
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
        ContainerHelper.saveAllItems(tag, this.ingredients, registries);
        tag.put("Result", result.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, this.ingredients, registries);
        result = ItemStack.parseOptional(registries, tag.getCompound("Result"));
    }

    public boolean addItem(ItemStack item) {
        if (item.isEmpty()) return false;
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) {
                ingredients.set(i, item.copyWithCount(1));
                lastAddedIndex = i;
                item.shrink(1);
                return true;
            }
        }
        return false;
    }

    public ItemStack removeItemStack() {
        if (lastAddedIndex <= -1) return ItemStack.EMPTY;
        ItemStack ingredient = ingredients.get(lastAddedIndex);
        ingredients.set(lastAddedIndex, ItemStack.EMPTY);
        lastAddedIndex--;
        return ingredient;
    }

    public void progress() {
        this.prevRecipeProgress = this.recipeProgress;
        this.recipeProgress++;
    }
}
