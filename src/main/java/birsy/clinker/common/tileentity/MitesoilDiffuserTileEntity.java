package birsy.clinker.common.tileentity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class MitesoilDiffuserTileEntity extends TileEntity implements ITickableTileEntity {
    private boolean bursting;
    private boolean shouldPlaySound;

    public int burstLength = 0;
    private int prevBurstTicks = 0;
    private int burstTicks = 0;

    public MitesoilDiffuserTileEntity() {
        super(ClinkerTileEntities.MITESOIL_DIFFUSER.get());
    }

    @Override
    public void tick() {
        this.prevBurstTicks = this.burstTicks;

        if (this.getWorld().rand.nextInt(100) == 0 && !this.bursting) {
            initiateBurst(30);
        }

        if (this.bursting) {
            if (this.burstTicks < burstLength) {
                burstTicks++;
            } else {
                this.burst();
            }
        }

        if (this.shouldPlaySound) {
            this.getWorld().playSound(null, this.getPos().getX() + 0.5f, this.getPos().getY() + 1.0f, this.getPos().getZ() + 0.5f, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 0.75f, 1.5f);
            Clinker.LOGGER.info("Explode sound! Desync issues?");
            this.shouldPlaySound = false;
        }
    }

    public void initiateBurst(int time) {
        if (!this.getWorld().getBlockState(pos.up()).isSolid() || !(this.getWorld().getBlockState(pos.up()).getBlock() == ClinkerBlocks.MITESOIL_DIFFUSER.get())) {
            this.bursting = true;
            this.burstLength = time;
        }
    }

    private void burst() {
        this.bursting = false;
        this.burstTicks = 0;

        this.shouldPlaySound = true;
        for (int i = 0; i < 56; i++) {
            final float verticalHeight = 3.0f;

            Random rand = this.getWorld().rand;
            Vector3d position = new Vector3d(
                    rand.nextBoolean() ? this.getPos().getX() + 0.5 + MathUtils.bias(rand.nextFloat(), 0.5) : this.getPos().getX() + 0.5 - MathUtils.bias(rand.nextFloat(), 0.5),
                    this.getPos().getY() + 1 + MathUtils.bias(rand.nextFloat(), 0.5) * verticalHeight,
                    rand.nextBoolean() ? this.getPos().getZ() + 0.5 + MathUtils.bias(rand.nextFloat(), 0.5) : this.getPos().getZ() + 0.5 - MathUtils.bias(rand.nextFloat(), 0.5));

            this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, position.getX(), position.getY(), position.getZ(), 10, 152, 10);
        }

        Vector3d position = new Vector3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        for (Entity entity : this.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position.add(-0.25, 1, -0.25), position.add(1.25, 3, 1.25)))) {
            final float velocity = 3;
            final float velocityMultiplier = 0.25f;

            entity.addVelocity(0, (velocity - entity.getDistanceSq(position.add(0.5, 1, 0.5))) * velocityMultiplier, 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getBurstTicks (float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevBurstTicks, this.burstTicks);
    }
}
