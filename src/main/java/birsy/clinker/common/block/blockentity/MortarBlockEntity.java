package birsy.clinker.common.block.blockentity;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MortarBlockEntity extends BlockEntity {
    public NonNullList<ItemStack> ingredients = NonNullList.withSize(4, ItemStack.EMPTY);
    public ItemStack result = ItemStack.EMPTY;
    int totalRecipeTime = 100;

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

    public void spawnProgressParticles() {
        if (this.level == null) return;
        int particleCount = Math.min((int) this.angularVelocity, 6);
        if (particleCount == 0) return;
        for (int i = 0; i < particleCount; i++) {
            boolean shouldBeResult = level.getRandom().nextFloat() > ((float) this.recipeProgress / this.totalRecipeTime);
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

    private void resetProgress() {
        this.recipeProgress = 0;
        this.prevRecipeProgress = 0;
    }

    public void grind() {
        this.recipeProgress++;
        this.grindTicks = 3;
    }

    public float getRotation(float partialTicks) {
        return Mth.lerp(partialTicks, prevRotation, rotation);
    }
}
