package birsy.clinker.common.tileentity;

import birsy.clinker.common.block.silt.SiltscarVineMouthBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTileEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SiltscarTileEntity extends BlockEntity implements TickableBlockEntity {
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

        BlockState blockState = this.getLevel().getBlockState(this.worldPosition);
        if (blockState.getBlock() == null) { //ClinkerBlocks.SILTSCAR_VINE_MOUTH.get()) { {
            float mouthClosingSpeed = 2;
            if (blockState.getValue(SiltscarVineMouthBlock.MOUTH_CLOSED) && mouthAngle < 90) {
                mouthAngle =+ mouthClosingSpeed;
            } else if (blockState.getValue(SiltscarVineMouthBlock.MOUTH_CLOSED) && mouthAngle > 90) {
                mouthAngle =- mouthClosingSpeed;
            }

            if (this.getHeldEntity().blockPosition() != this.getBlockPos()) {
                this.setHeldEntity(null);
            } else {
                this.getHeldEntity().hurt(new DamageSource("chomped").setScalesWithDifficulty(), 1.0f);
                this.heldEntityTicks++;
            }

            if (this.heldEntityTicks > 120) {
                ItemStack itemstack = this.getHeldItem();

                this.open(true, true);
                this.getLevel().setBlockAndUpdate(worldPosition, blockState.setValue(SiltscarVineMouthBlock.MOUTH_CLOSED, false).setValue(SiltscarVineMouthBlock.HOLDING_ITEM, false));

                double d0 = (double) (this.getLevel().random.nextFloat() * 0.7F) + (double) 0.15F;
                double d1 = (double) (this.getLevel().random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                double d2 = (double) (this.getLevel().random.nextFloat() * 0.7F) + (double) 0.15F;
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(this.getLevel(), (double) worldPosition.getX() + d0, (double) worldPosition.getY() + d1, (double) worldPosition.getZ() + d2, itemstack1);
                itementity.setDefaultPickUpDelay();
                this.getLevel().addFreshEntity(itementity);
            }
        }
    }

    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);
        if (nbt.contains("Item", 10)) {
            this.setHeldItem(ItemStack.of(nbt.getCompound("Item")));
        }
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        if (!this.getHeldItem().isEmpty()) {
            compound.put("HeldItem", this.getHeldItem().save(new CompoundTag()));
        }
        return compound;
    }

    public ItemStack getHeldItem() {
        return this.heldItem;
    }

    public void setHeldItem(ItemStack item) {
        this.heldItem = item;
        this.setChanged();
    }


    public Entity getHeldEntity() {
        return this.heldEntity;
    }

    public void setHeldEntity(Entity entity) {
        this.heldEntity = entity;
        this.setChanged();
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
        return Mth.lerp(partialTick, prevMouthAngle, mouthAngle);
    }
}
