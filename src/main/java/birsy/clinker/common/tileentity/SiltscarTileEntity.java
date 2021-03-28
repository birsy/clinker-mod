package birsy.clinker.common.tileentity;

import birsy.clinker.common.block.silt.SiltscarVineMouthBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SiltscarTileEntity extends TileEntity implements ITickableTileEntity {
    private float prevMouthAngle;
    private float mouthAngle;

    public float attackDelay;

    private ItemStack heldItem = ItemStack.EMPTY;
    private Entity heldEntity;
    private int heldEntityTicks;

    public SiltscarTileEntity() {
        super(null);
        //super(ClinkerTileEntities.SILTSCAR_MOUTH.get());
    }

    @Override
    public void tick() {
        prevMouthAngle = mouthAngle;

        BlockState blockState = this.getWorld().getBlockState(this.pos);
        if (blockState.getBlock() == null) { //ClinkerBlocks.SILTSCAR_VINE_MOUTH.get()) { {
            float mouthClosingSpeed = 2;
            if (blockState.get(SiltscarVineMouthBlock.MOUTH_CLOSED) && mouthAngle < 90) {
                mouthAngle =+ mouthClosingSpeed;
            } else if (blockState.get(SiltscarVineMouthBlock.MOUTH_CLOSED) && mouthAngle > 90) {
                mouthAngle =- mouthClosingSpeed;
            }

            if (this.getHeldEntity().getPosition() != this.getPos()) {
                this.setHeldEntity(null);
            } else {
                this.getHeldEntity().attackEntityFrom(new DamageSource("chomped").setDifficultyScaled(), 1.0f);
                this.heldEntityTicks++;
            }

            if (this.heldEntityTicks > 120) {
                ItemStack itemstack = this.getHeldItem();

                this.open(true, true);
                this.getWorld().setBlockState(pos, blockState.with(SiltscarVineMouthBlock.MOUTH_CLOSED, false).with(SiltscarVineMouthBlock.HOLDING_ITEM, false));

                double d0 = (double) (this.getWorld().rand.nextFloat() * 0.7F) + (double) 0.15F;
                double d1 = (double) (this.getWorld().rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                double d2 = (double) (this.getWorld().rand.nextFloat() * 0.7F) + (double) 0.15F;
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(this.getWorld(), (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                itementity.setDefaultPickupDelay();
                this.getWorld().addEntity(itementity);
            }
        }
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("Item", 10)) {
            this.setHeldItem(ItemStack.read(nbt.getCompound("Item")));
        }
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.getHeldItem().isEmpty()) {
            compound.put("HeldItem", this.getHeldItem().write(new CompoundNBT()));
        }
        return compound;
    }

    public ItemStack getHeldItem() {
        return this.heldItem;
    }

    public void setHeldItem(ItemStack item) {
        this.heldItem = item;
        this.markDirty();
    }


    public Entity getHeldEntity() {
        return this.heldEntity;
    }

    public void setHeldEntity(Entity entity) {
        this.heldEntity = entity;
        this.markDirty();
    }

    public void open(boolean item, boolean entity) {
        if (item) {
            this.setHeldItem(ItemStack.EMPTY);
        }

        if (entity) {
            this.setHeldEntity(null);
        }

        if (item || entity) {
            this.attackDelay = 60;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getMouthAngle(float partialTick) {
        return MathHelper.lerp(partialTick, prevMouthAngle, mouthAngle);
    }
}
