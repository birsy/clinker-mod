package birsy.clinker.common.tileentity;

import birsy.clinker.common.block.CrockpotBlock;
import birsy.clinker.core.registry.ClinkerTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrockpotTileEntity extends TileEntity implements ITickableTileEntity {
    public float ageInTicks;

    public boolean open;
    private float openAmount;
    private float prevOpenAmount;

    public Direction openDirection;

    public CrockpotTileEntity() {
        super(ClinkerTileEntities.CROCKPOT.get());
        //this.openDirection = calculateOpenDirection();
    }

    @Override
    public void tick() {
        this.ageInTicks++;
        this.updateOpenAmount();
    }
    
    private void updateOpenAmount() {
        prevOpenAmount = openAmount;

        if (open && openAmount < 1) {
            openAmount = Math.min(1.0F, openAmount + 0.08F);
        } else if (!open && openAmount > 0) {
            openAmount = Math.max(0.0F, openAmount - 0.14F);
        }
    }

    private Direction calculateOpenDirection() {
        Direction defaultDirection = this.getBlockState().get(CrockpotBlock.FACING).getOpposite();
        Direction[] directionPriority = {defaultDirection, defaultDirection.rotateY(), defaultDirection.rotateY().getOpposite(), Direction.UP};

        for (Direction direction : directionPriority) {
            if (!this.getWorld().getBlockState(this.getPos().offset(direction)).isSolid()) {
                return direction;
            }
        }

        return Direction.DOWN;
    }

    public boolean canOpen() {
        return openDirection != Direction.DOWN;
    }

    public boolean open() {
        if (this.prevOpenAmount == 0 && this.openAmount == 0) {
            this.openDirection = this.calculateOpenDirection();
        }
        if (this.canOpen() && !this.open) {
            this.open = true;
            return true;
        }

        return false;
    }

    public boolean close() {
        if (this.open) {
            this.open = false;
            return true;
        }

        return false;
    }

    public boolean switchOpenState() {
        if (this.open) {
            return this.close();
        } else {
            return this.open();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getOpenAmount(float partialTick) {
        return MathHelper.lerp(partialTick, prevOpenAmount, openAmount);
    }
}
