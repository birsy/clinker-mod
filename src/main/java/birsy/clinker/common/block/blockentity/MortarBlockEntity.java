package birsy.clinker.common.block.blockentity;

import birsy.clinker.common.alchemy.recipe.MortarAndPestleRecipe;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRecipes;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MortarBlockEntity extends BlockEntity {
    public ItemStack result = ItemStack.EMPTY;
    boolean checkedRecipe = false;
    int totalRecipeTime = Integer.MAX_VALUE;

    public NonNullList<ItemStack> ingredients = NonNullList.withSize(4, ItemStack.EMPTY);

    float angularVelocity = 0;
    float rotation, prevRotation;
    int recipeProgress, prevRecipeProgress;
    int grindTicks = 0;

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
        tag.putInt("RecipeProgress", recipeProgress);
        tag.putInt("TotalRecipeTime", totalRecipeTime);
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
        recipeProgress = tag.getInt("RecipeProgress");
        totalRecipeTime = tag.getInt("TotalRecipeTime");
    }

    public boolean addItem(@Nullable LivingEntity entity, ItemStack item) {
        // can't put items in if we're currently doing a recipe
        if (this.isInProgress()) return false;
        if (item.isEmpty()) return false;

        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) {
                if (level.isClientSide) return true;

                ingredients.set(i, item.consumeAndReturn(1, entity));
                resetRecipe();
                markUpdated();
                return true;
            }
        }
        resetRecipe();
        return false;
    }

    public ItemStack removeItemStack() {
        // if it's done, return the result!
        if (this.isCompleted()) {
            ItemStack result = this.result;
            // since we're done, all of our ingredients are "used up"
            // todo: support actual stacks of ingredients
            for (int i = 0; i < this.ingredients.size(); i++)
                this.ingredients.set(i, ItemStack.EMPTY);

            resetRecipe();
            markUpdated();
            return result;
        }

        // can't take items out if we're currently doing a recipe
        if (this.isInProgress()) return ItemStack.EMPTY;

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
        resetRecipe();
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

    private void resetRecipe() {
        this.result = ItemStack.EMPTY;
        this.checkedRecipe = false;
        this.recipeProgress = 0;
        this.prevRecipeProgress = 0;
    }

    public void findRecipe() {
        if (this.level == null) return;
        if (!this.result.isEmpty()) return;

        // checkedRecipe ensures that we only search for new recipes if we've added / removed an item.
        if (this.checkedRecipe) return;

        List<ItemStack> truncatedList = new ArrayList<>(4);
        for (ItemStack ingredient : this.ingredients)
            if (!ingredient.isEmpty()) truncatedList.add(ingredient);

        MortarAndPestleRecipe.Input input = new MortarAndPestleRecipe.Input(truncatedList);
        Optional<RecipeHolder<MortarAndPestleRecipe>> optional = this.level.getRecipeManager().getRecipeFor(
                ClinkerRecipes.Types.MORTAR_AND_PESTLE.get(),
                input, level
        );
        this.result = optional
                .map(RecipeHolder::value)
                .map(e -> e.assemble(input, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
        this.totalRecipeTime = optional
                .map(RecipeHolder::value)
                .map(recipe -> recipe.grindTime)
                .orElse(Integer.MAX_VALUE);
        this.checkedRecipe = true;
        this.markUpdated();
    }

    public void grind() {
        findRecipe();
        this.recipeProgress++;
        this.grindTicks = 3;
    }

    public boolean isInProgress() {
        return !this.result.isEmpty() && this.recipeProgress > 0;
    }

    public boolean isCompleted() {
        return !this.result.isEmpty() && this.recipeProgress >= this.totalRecipeTime;
    }

    public void spawnProgressParticles() {
        if (this.level == null) return;
        int particleCount = Math.min((int) this.angularVelocity, 6);
        if (particleCount == 0) return;
        for (int i = 0; i < particleCount; i++) {
            boolean shouldBeResult = level.getRandom().nextFloat() < ((float) this.recipeProgress / this.totalRecipeTime);

            ItemStack item;
            if (shouldBeResult) {
                item = this.result;
            } else {
                int totalIngredients = 0;
                for (int j = 0; j < this.ingredients.size(); j++) {
                    if (this.ingredients.get(j).isEmpty()) break;
                    totalIngredients ++;
                }
                if (totalIngredients == 0) continue;
                item = this.ingredients.get(level.getRandom().nextInt(totalIngredients));
            }

            if (item.isEmpty()) continue;
            BlockPos pos = this.getBlockPos();
            level.addParticle(
                    new ItemParticleOption(ParticleTypes.ITEM, item),
                    pos.getX() + Mth.lerp(level.getRandom().nextFloat(), 0.25, 0.75), pos.getY() + 0.25,
                    pos.getZ() + Mth.lerp(level.getRandom().nextFloat(), 0.25, 0.75),
                    0, 0, 0
            );
        }
    }

    public static void tickCommon(Level level, BlockPos pos, BlockState state, MortarBlockEntity entity) {
        entity.rotation += entity.angularVelocity;
        entity.angularVelocity = Mth.approach(entity.angularVelocity, entity.grindTicks > 0 ? 12 : 0, 1);
        entity.grindTicks--;
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, MortarBlockEntity entity) {
        entity.prevRotation = entity.rotation;
        entity.prevRecipeProgress = entity.recipeProgress;

        tickCommon(level, pos, state, entity);
        entity.spawnProgressParticles();
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, MortarBlockEntity entity) {
        tickCommon(level, pos, state, entity);
    }

    public float getRotation(float partialTicks) {
        return Mth.lerp(partialTicks, prevRotation, rotation);
    }

    public float getRecipeProgress(float partialTicks) {
        return Math.clamp(Mth.lerp(partialTicks, prevRecipeProgress, recipeProgress) / this.totalRecipeTime, 0, 1);
    }
}
