package birsy.clinker.common.tileentity;

import birsy.clinker.common.block.HeaterBlock;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class HeaterTileEntity extends TileEntity implements ITickableTileEntity, ISidedInventory {
    public int ageInTicks;
    private float prevHeat;
    public float heat;

    public boolean isCooking = true;

    public ItemStack fuel = ItemStack.EMPTY;

    public HeaterTileEntity() {
        super(ClinkerTileEntities.HEATER.get());
    }

    @Override
    public void tick() {
        this.ageInTicks++;
        this.prevHeat = this.heat;

        if (isCooking) {
            if (this.heat < 100) {
                this.heat++;
            }
        } else {
            if (this.heat > 0) {
                this.heat--;
            }
        }

        if (!this.world.isRemote()) {
            int blockHeat = (int) MathUtils.mapRange(0, 100, 0, 15, this.heat);
            BlockState state = world.getBlockState(this.pos);

            if (state.get(HeaterBlock.PROPERTY_HEAT) != blockHeat) {
                this.world.setBlockState(this.pos, state.with(HeaterBlock.PROPERTY_HEAT, blockHeat));
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 0) {
            HeaterSound heaterSound = new HeaterSound(this, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS);
            Minecraft.getInstance().getSoundHandler().playOnNextTick(heaterSound);
        }
        return super.receiveClientEvent(id, type);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeatOverlayStrength (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevHeat, this.heat);
    }


    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{1};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        if (itemStackIn.getItem() == this.fuel.getItem() || this.fuel == ItemStack.EMPTY) {
            return direction != Direction.DOWN && this.fuel.getCount() + itemStackIn.getCount() > itemStackIn.getMaxStackSize();
        } else {
            return false;
        }
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return fuel == ItemStack.EMPTY || fuel.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return fuel;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        this.fuel = new ItemStack(fuel.getItem(), fuel.getCount() - 1);
        return this.fuel;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        this.fuel = ItemStack.EMPTY;
        return this.fuel;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.fuel = stack;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        this.fuel = ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public class HeaterSound extends TickableSound {
        private final HeaterTileEntity block;
        private static final float maxVolume = 1.6F;
        private float fade;

        public HeaterSound(HeaterTileEntity blockIn, SoundEvent eventIn, SoundCategory categoryIn) {
            super(eventIn, categoryIn);
            this.block = blockIn;

            this.x = (float) block.getPos().getX();
            this.y = (float) block.getPos().getY();
            this.z = (float) block.getPos().getZ();
            this.repeat = true;
            this.repeatDelay = 0;

            this.volume = 0.0F;
            this.fade = 1.0F;
        }

        @Override
        public void tick() {
            if (!block.isRemoved()) {
                if (this.block.isCooking && this.fade < 1.0F) {
                    this.fade += 0.025F;
                } else if (this.fade > 0.0F){
                    this.fade -= 0.05F;
                }

                this.volume = this.fade * maxVolume;
            } else {
                this.finishPlaying();
            }
        }

        @Override
        public boolean shouldPlaySound() {
            return !block.isRemoved();
        }
    }
}
