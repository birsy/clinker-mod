package birsy.clinker.common.blockentity;

import birsy.clinker.common.block.FermentationBarrelBlock;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FermentationBarrelBlockEntity extends BlockEntity implements LidBlockEntity {
    private final NonNullList<ItemStack> items = NonNullList.of(ItemStack.EMPTY, ItemStack.EMPTY);
    private LidController lidController = new LidController(0.05F, 0.07F);
    private boolean isFermenting;
    private int fermentationTime;
    private int recipeStartTicks;
    public boolean shouldUpdateBlockCollision = true;
    public FermentationBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ClinkerBlockEntities.FERMENTATION_BARREL.get(), pWorldPosition, pBlockState);
    }

    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putBoolean("Fermenting", this.isFermenting);
        tag.putInt("FermentationTime", this.fermentationTime);
        tag.putBoolean("Open", lidController.isOpen());

        return tag;
    }

    public void load(CompoundTag tag) {
        super.load(tag);
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
}
