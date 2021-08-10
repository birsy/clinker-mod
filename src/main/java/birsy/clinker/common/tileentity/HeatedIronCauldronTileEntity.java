package birsy.clinker.common.tileentity;

import birsy.clinker.common.world.WorldUtil;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.util.MathUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class HeatedIronCauldronTileEntity extends TileEntity implements ITickableTileEntity {
    boolean debug = Clinker.devmode;
    
    private final Random rand = new Random();
    public int ageInTicks;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    public NonNullList<float[]> itemRotations = NonNullList.withSize(4, new float[2]);
    public int mostRecentItemIndex = 0;

    //Measured in degrees per tick.
    private final float maxStirSpeed = 50.0F;
    private float stirSpeed;
    //Measured in degrees.
    private float prevSpoonRotation;
    private float spoonRotation;
    /* Keeps track of the amount of time spun for each speed. Will be used for recipes to require certain spinning speeds.
     * Index 0 keeps track of the total time the player has been stiring.
     * Indexes 1-3 keep track of slow, medium, and quick stir time. */
    private int[] stirTimeTable = new int[4];

    private float prevCauldronShakeAmount;
    private float cauldronShakeAmount;

    private float prevWaterLevel;
    private float waterLevel;

    private float prevHeat;
    public float heat;

    /**
     * Forge fluid stuff!
     */
    public FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);


    public HeatedIronCauldronTileEntity() {
        super(ClinkerTileEntities.HEATED_IRON_CAULDRON.get());
        this.itemRotations.set(0, new float[]{0, 0});
        this.itemRotations.set(1, new float[]{90, 90});
        this.itemRotations.set(2, new float[]{180, 180});
        this.itemRotations.set(3, new float[]{270, 270});
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
        this.prevWaterLevel = this.waterLevel;
        this.prevCauldronShakeAmount = this.cauldronShakeAmount;

        this.waterLevel = calculateWaterLevel();
        this.cauldronShakeAmount = calculateCauldronShakeAmount();

        this.stirSpeed = Math.max(this.stirSpeed - 0.25F, 0);
        updateStirRotations();

        if (WorldUtil.getAmbientHeat(this.getWorld(), this.getPos()) > 30) {
            if (this.heat < 100) {
                this.heat++;
            }
        } else {
            if (this.heat > 0) {
                this.heat--;
            }
        }

        this.updateStirTimes();
    }

    private void updateStirRotations() {
        /* Sets the previous rotation for partialTick nonsense. */
        this.prevSpoonRotation = this.spoonRotation;
        for (int index = 0; index < 4; index++) {
            this.itemRotations.get(index)[1] = this.itemRotations.get(index)[0];
        }

        /* Updates the rotations for each item as well as the items contained within.
         * I did some funny math to the item's rotation so that they stir faster while the spoon is near them, and slower while it's further away.
         * Don't really need to understand exactly what I did - just know it works ;) */
        this.spoonRotation += this.stirSpeed;
        for (int index = 0; index < 4; index++) {
            float itemMultiplier = 1.00F;
            //float distanceFromSpoon = Math.abs((this.spoonRotation % 360) - (this.itemRotations.get(index)[0] % 360));
            //itemMultiplier *= MathUtils.mapRange(0.0F, 1.0F, 0.75F,1.0F, -1.0F * ((distanceFromSpoon / 360.0F) - 1.0F));

            this.itemRotations.get(index)[0] = this.itemRotations.get(index)[0] + (this.stirSpeed * itemMultiplier);
        }
    }

    private void updateStirTimes() {
        float minStirSpeed = 5.0F;
        float interval = (maxStirSpeed - minStirSpeed) / 3.0F;
        /* Index of "stir speeds."
         * [0] is the lowest stir speed considered "slow" - five.
         * [1] is the lowest stir speed considered "medium".
         * [2] is the lowest stir speed considered "quick". */
        float[] stirSpeedTable = {minStirSpeed, interval + minStirSpeed, (interval * 2) + minStirSpeed, maxStirSpeed};
        int marginOfErrorTicks = 65;
        int highestStirSpeedIndex = 1;

        if (this.stirSpeed > minStirSpeed) {
            this.stirTimeTable[0]++;
        }

        //Updates the stir times for each index. If it's greater than the lowest stir speed for a particular speed, but less than the lowest stir speed for the next speed up, then it increases.
        for (int i = 1; i < stirTimeTable.length; i++) {
            int speedIndex = i - 1;
            if (stirTimeTable[i] > stirSpeedTable[speedIndex] && stirTimeTable[i] < stirSpeedTable[speedIndex + 1]) {
                stirTimeTable[i]++;
                if (i != highestStirSpeedIndex && stirTimeTable[i] > marginOfErrorTicks) {
                    for (int validIndex : MathUtils.getValidIndexes(stirTimeTable, i)) {
                        stirTimeTable[validIndex] = 0;
                    }
                }
            } else if (i != highestStirSpeedIndex) {
                stirTimeTable[i] = 0;
            }
        }

        for (int i = 1; i < stirTimeTable.length; i++) {
            highestStirSpeedIndex = stirSpeedTable[i] > stirSpeedTable[highestStirSpeedIndex] ? i : highestStirSpeedIndex;
        }
    }

    public void stir() {
        if (this.stirSpeed < this.maxStirSpeed) {
            this.stirSpeed = Math.min(this.stirSpeed + 1, this.maxStirSpeed);
        }
    }

    private float calculateCauldronShakeAmount() {
        float minShakeSpeed = this.maxStirSpeed - 20.0F;
        if (this.stirSpeed > minShakeSpeed) {
            return MathUtils.mapRange(minShakeSpeed, this.maxStirSpeed, 0, 1, this.stirSpeed);
        } else {
            return 0;
        }
    }

    private float calculateStirSoundPitch() {
        if (this.stirSpeed < 15.0F) {
            return MathUtils.mapRange(0, 15.0F, 0.5F, 1, this.stirSpeed);
        } else {
            return 1;
        }
    }

    public void addItem(ItemEntity itemEntity) {
        int emptySlot = -1;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) == ItemStack.EMPTY) {
                emptySlot = i;
            }
        }

        if (emptySlot != -1) {
            inventory.set(emptySlot, itemEntity.getItem());
            this.mostRecentItemIndex = emptySlot;
            
            for (int i = 0; i < 10; i++) {
                this.getWorld().addParticle(ParticleTypes.POOF, itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ(), 0, rand.nextFloat() + 2, 0);
            }
            this.getWorld().playSound(null, itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ(), SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.BLOCKS, 1.0F, 1.0F);

            itemEntity.remove();

            if (this.debug) {
                StringBuilder cauldronContents = new StringBuilder();
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i) == ItemStack.EMPTY) {
                        cauldronContents.append(inventory.get(i).getItem().getName().getString()).append(", ");
                    }
                }

                Clinker.LOGGER.info("Cauldron contains " + cauldronContents);
            }
        }
    }

    public void dropItems(int index, @Nullable LivingEntity entity) {
        if (index == -1) {
            for (ItemStack itemStack : inventory) {
                if (itemStack != ItemStack.EMPTY) {
                    ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5, itemStack);
                    this.getWorld().addEntity(itemEntity);
                }
            }
        } else {
            try {
                ItemStack itemStack = inventory.get(index);
                if (itemStack != ItemStack.EMPTY) {
                    ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getPos().getX() + 0.5, this.getPos().getY() + 1.1, this.getPos().getZ() + 0.5, inventory.get(index));
                    inventory.set(index, ItemStack.EMPTY);

                    /*
                    if (entity != null) {
                        double xMotion = entity.getPosX() - itemEntity.getPosX();
                        double yMotion = entity.getPosY() - itemEntity.getPosY();
                        double zMotion = entity.getPosZ() - itemEntity.getPosZ();
                        itemEntity.setMotion(xMotion * 0.1D, yMotion * 0.1D + Math.sqrt(Math.sqrt(xMotion * xMotion + yMotion * yMotion + zMotion * zMotion)) * 0.08D, zMotion * 0.1D);
                    }
                     */
                    
                    this.getWorld().addEntity(itemEntity);
                }
            } catch(Exception e) {
                Clinker.LOGGER.error("Attempted to drop non-existent item from invalid index!");
            }
        }
    }

    public int getLastItemInInventory() {
        if (inventory.get(this.mostRecentItemIndex) == ItemStack.EMPTY || this.mostRecentItemIndex == -1) {
            for (int i = inventory.size() - 1; i > 0; i--) {
                if (inventory.get(i) != ItemStack.EMPTY) {
                    return i;
                }
            }
            return -1;
        } else {
            return this.mostRecentItemIndex;
        }
    }

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
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
    public float getItemRotations (int index, float partialTicks) {
        return MathHelper.lerp(partialTicks, this.itemRotations.get(index)[1], this.itemRotations.get(index)[0]);
    }

    @OnlyIn(Dist.CLIENT)
    public float getSpoonRotation (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevSpoonRotation, this.spoonRotation);
    }

    @OnlyIn(Dist.CLIENT)
    public float getCauldronShakeAmount (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevCauldronShakeAmount, this.cauldronShakeAmount);
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
