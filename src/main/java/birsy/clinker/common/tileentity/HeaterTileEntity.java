package birsy.clinker.common.tileentity;

import birsy.clinker.common.block.HeaterBlock;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class HeaterTileEntity extends BlockEntity implements TickableBlockEntity, WorldlyContainer {
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

        if (!this.level.isClientSide()) {
            int blockHeat = (int) MathUtils.mapRange(0, 100, 0, 15, this.heat);
            BlockState state = level.getBlockState(this.worldPosition);

            if (state.getValue(HeaterBlock.PROPERTY_HEAT) != blockHeat) {
                this.level.setBlockAndUpdate(this.worldPosition, state.setValue(HeaterBlock.PROPERTY_HEAT, blockHeat));
            }
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 0) {
            HeaterSound heaterSound = new HeaterSound(this, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS);
            Minecraft.getInstance().getSoundManager().queueTickingSound(heaterSound);
        }
        return super.triggerEvent(id, type);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeatOverlayStrength (float partialTicks) {
        return Mth.lerp(partialTicks, this.prevHeat, this.heat);
    }


    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{1};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        if (itemStackIn.getItem() == this.fuel.getItem() || this.fuel == ItemStack.EMPTY) {
            return direction != Direction.DOWN && this.fuel.getCount() + itemStackIn.getCount() > itemStackIn.getMaxStackSize();
        } else {
            return false;
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return fuel == ItemStack.EMPTY || fuel.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return fuel;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        this.fuel = new ItemStack(fuel.getItem(), fuel.getCount() - 1);
        return this.fuel;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        this.fuel = ItemStack.EMPTY;
        return this.fuel;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.fuel = stack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.fuel = ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public class HeaterSound extends AbstractTickableSoundInstance {
        private final HeaterTileEntity block;
        private static final float maxVolume = 1.6F;
        private float fade;

        public HeaterSound(HeaterTileEntity blockIn, SoundEvent eventIn, SoundSource categoryIn) {
            super(eventIn, categoryIn);
            this.block = blockIn;

            this.x = (float) block.getBlockPos().getX();
            this.y = (float) block.getBlockPos().getY();
            this.z = (float) block.getBlockPos().getZ();
            this.looping = true;
            this.delay = 0;

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
                this.stop();
            }
        }

        @Override
        public boolean canPlaySound() {
            return !block.isRemoved();
        }
    }
}
