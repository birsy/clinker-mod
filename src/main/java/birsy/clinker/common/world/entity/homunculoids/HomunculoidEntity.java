package birsy.clinker.common.world.entity.homunculoids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class HomunculoidEntity extends PathfinderMob {
    protected LivingEntity owner;
    protected int timeRemaining;

    protected HomunculoidEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putUUID("OwnerUUID", this.owner.getUUID());
        pCompound.putInt("TimeRemaining", this.timeRemaining);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.level() instanceof ServerLevel slevel) this.owner = (LivingEntity) slevel.getEntity(pCompound.getUUID("OwnerUUID"));
        this.timeRemaining = pCompound.getInt("TimeRemaining");
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        SmoothGroundNavigation navigator = new SmoothGroundNavigation(this, pLevel);
        navigator.setCanOpenDoors(false);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    @Override
    public void tick() {
        super.tick();

        this.timeRemaining--;
        if (this.timeRemaining < 0) {
            this.die(this.damageSources().dryOut());
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.timeRemaining = this.getTotalTime();
    }

    protected int getTotalTime() {
        return 1200;
    }
}
