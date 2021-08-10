package birsy.clinker.common.tileentity;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTileEntities;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class MitesoilDiffuserTileEntity extends BlockEntity implements TickableBlockEntity {
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

        if (this.getLevel().random.nextInt(100) == 0 && !this.bursting) {
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
            this.getLevel().playSound(null, this.getBlockPos().getX() + 0.5f, this.getBlockPos().getY() + 1.0f, this.getBlockPos().getZ() + 0.5f, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, 0.75f, 1.5f);
            Clinker.LOGGER.info("Explode sound! Desync issues?");
            this.shouldPlaySound = false;
        }
    }

    public void initiateBurst(int time) {
        if (!this.getLevel().getBlockState(worldPosition.above()).canOcclude() || !(this.getLevel().getBlockState(worldPosition.above()).getBlock() == ClinkerBlocks.MITESOIL_DIFFUSER.get())) {
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

            Random rand = this.getLevel().random;
            Vec3 position = new Vec3(
                    rand.nextBoolean() ? this.getBlockPos().getX() + 0.5 + MathUtils.bias(rand.nextFloat(), 0.5) : this.getBlockPos().getX() + 0.5 - MathUtils.bias(rand.nextFloat(), 0.5),
                    this.getBlockPos().getY() + 1 + MathUtils.bias(rand.nextFloat(), 0.5) * verticalHeight,
                    rand.nextBoolean() ? this.getBlockPos().getZ() + 0.5 + MathUtils.bias(rand.nextFloat(), 0.5) : this.getBlockPos().getZ() + 0.5 - MathUtils.bias(rand.nextFloat(), 0.5));

            this.getLevel().addParticle(ParticleTypes.ENTITY_EFFECT, position.x(), position.y(), position.z(), 10, 152, 10);
        }

        Vec3 position = new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        for (Entity entity : this.getLevel().getEntitiesOfClass(Entity.class, new AABB(position.add(-0.25, 1, -0.25), position.add(1.25, 3, 1.25)))) {
            final float velocity = 3;
            final float velocityMultiplier = 0.25f;

            entity.push(0, (velocity - entity.distanceToSqr(position.add(0.5, 1, 0.5))) * velocityMultiplier, 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getBurstTicks (float partialTicks) {
        return Mth.lerp(partialTicks, this.prevBurstTicks, this.burstTicks);
    }
}
