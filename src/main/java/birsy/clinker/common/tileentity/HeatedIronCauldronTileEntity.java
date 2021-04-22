package birsy.clinker.common.tileentity;

import birsy.clinker.common.world.WorldUtil;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class HeatedIronCauldronTileEntity extends TileEntity implements ITickableTileEntity {
    public int ageInTicks;
    //Used for animation
    private float prevSpoonRotation;
    private float spoonRotation;

    //Used for actual calculation
    private float beforeSpoonRotation;
    private float nextSpoonRotation;
    private float spoonRotationInterpolationTicks;

    private float prevWaterLevel;
    private float waterLevel;

    private float prevHeat;
    private float heat;

    public boolean usable;

    /**
     * Forge fluid stuff!
     */
    public FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);


    public HeatedIronCauldronTileEntity() {
        super(ClinkerTileEntities.HEATED_IRON_CAULDRON.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        tank.readFromNBT(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        tank.writeToNBT(tag);
        return tag;
    }


    @Override
    public void tick() {
        ageInTicks++;
        this.prevHeat = this.heat;
        this.prevSpoonRotation = this.spoonRotation;
        this.prevWaterLevel = this.waterLevel;

        this.waterLevel = calculateWaterLevel();

        if (MathUtils.within(this.spoonRotation, this.nextSpoonRotation, 0.125f)) {
            this.spoonRotationInterpolationTicks++;
            this.spoonRotation = MathHelper.lerp(this.spoonRotationInterpolationTicks, beforeSpoonRotation, nextSpoonRotation);
        } else {
            this.spoonRotation = this.nextSpoonRotation;
            this.beforeSpoonRotation = this.nextSpoonRotation;
        }

        if (WorldUtil.getAmbientHeat(this.getWorld(), this.getPos()) > 30) {
            if (this.heat < 100) {
                this.heat++;
            }
        } else {
            if (this.heat > 0) {
                this.heat--;
            }
        }

        this.usable = this.heat >= 50;
    }

    public void rotateSpoon (float amount) {
        if (this.spoonRotation == this.nextSpoonRotation) {
            this.nextSpoonRotation = spoonRotation + amount;
        }
    }


    @OnlyIn(Dist.CLIENT)
    public float getVisualWaterLevel (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevWaterLevel, this.waterLevel);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeatOverlayStrength (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevHeat, this.heat);
    }

    @OnlyIn(Dist.CLIENT)
    public float getSpoonRotation (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevSpoonRotation, this.spoonRotation);
    }

    /**
     * Forge stuff relating to fluids. Only really need to work with water but I need everything for modpack makers to be happy.
     */

    private float calculateWaterLevel() {
        float tankFullness = tank.getFluidAmount() / 1000.0f;
        return tankFullness * 3;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(capability, facing);
    }
}
