package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.common.world.block.FermentationBarrelBlock;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;


import java.util.ArrayList;
import java.util.List;

public class FermentationBarrelBlockEntity extends BlockEntity implements LidBlockEntity, IAlchemicalInfo {
    private final NonNullList<ItemStack> items = NonNullList.of(ItemStack.EMPTY, ItemStack.EMPTY);
    private LidController lidController = new LidController(0.05F, 0.07F);
    private boolean isFermenting;
    private int fermentationTime;
    private int recipeStartTicks;
    public boolean shouldUpdateBlockCollision = true;
    public FermentationBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ClinkerBlockEntities.FERMENTATION_BARREL.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Fermenting", this.isFermenting);
        tag.putInt("FermentationTime", this.fermentationTime);
        tag.putBoolean("Open", lidController.isOpen());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.isFermenting = tag.getBoolean("Fermenting");
        this.fermentationTime = tag.getInt("FermentationTime");
        lidController.setOpen(tag.getBoolean("Open"));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FermentationBarrelBlockEntity blockEntity) {
        blockEntity.setOpen(state.getValue(FermentationBarrelBlock.OPEN));
        blockEntity.lidController.tickLid();

        blockEntity.recipeStartTicks = Math.max(0, blockEntity.recipeStartTicks - 1);
        if (blockEntity.isFermenting) {
            blockEntity.fermentationTime++;
        }
    }

    public void addItem(ItemStack item) {
        items.add(item);
        recipeStartTicks += 50;
    }

    public void dropItem(int index) {
        if (index == -1) {
            for (ItemStack itemStack : items) {
                if (itemStack != ItemStack.EMPTY) {
                    ItemEntity itemEntity = new ItemEntity(this.getLevel(), this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, itemStack);
                    this.getLevel().addFreshEntity(itemEntity);
                    items.clear();
                }
            }
        } else {
            try {
                ItemStack itemStack = items.get(index);
                if (itemStack != ItemStack.EMPTY) {
                    ItemEntity itemEntity = new ItemEntity(this.getLevel(), this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1.1, this.getBlockPos().getZ() + 0.5, items.get(index));
                    items.remove(index);

                    this.getLevel().addFreshEntity(itemEntity);
                }
            } catch (Exception e) {
                Clinker.LOGGER.error("Attempted to drop non-existent item from invalid index!");
            }
        }
    }

    public void lidInteract() {
        if (this.lidController.isOpen()) {
            close();
        } else {
            open();
        }
        Clinker.LOGGER.info((this.lidController.isOpen() ? "Closed" : "Opened") + " the lid.");
    }

    public void setOpen(boolean open) {
        lidController.setOpen(open);
    }

    public void open() {
        lidController.setOpen(true);
    }

    public void close() {
        lidController.setOpen(false);
    }

    @Override
    public float getOpenNess(float pPartialTicks) {
        return lidController.getOpenness(pPartialTicks);
    }

    
    public List<? extends FormattedText> getText(BlockEntity blockEntity) {
        ArrayList<Component> text = new ArrayList<>();
        text.add(Component.translatable("block.clinker.fermentation_barrel").withStyle(ChatFormatting.DARK_GRAY));
        text.add(Component.literal("sync chest noise later"));
        return text;
    }

    
    public void renderCustomElements(BlockEntity blockEntity, PoseStack mStack, int x, int y, int width, int height) {

    }
}
