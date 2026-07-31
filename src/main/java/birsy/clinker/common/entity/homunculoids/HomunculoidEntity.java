package birsy.clinker.common.entity.homunculoids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class HomunculoidEntity extends PathfinderMob implements OwnableEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID_ID = SynchedEntityData.defineId(
            HomunculoidEntity.class, EntityDataSerializers.OPTIONAL_UUID
    );
    protected int timeRemaining;

    protected HomunculoidEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_UUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getOwnerUUID() != null) pCompound.putUUID("Owner", this.getOwnerUUID());
        pCompound.putInt("TimeRemaining", this.timeRemaining);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }
        if (uuid != null) {
            this.setOwnerUUID(uuid);
        }

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
    public PlayerTeam getTeam() {
        LivingEntity livingentity = this.getOwner();
        if (livingentity != null)
            return livingentity.getTeam();

        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        LivingEntity owner = this.getOwner();
        if (entity == owner)
            return true;
        if (entity instanceof OwnableEntity ownable && ownable.getOwner() == owner)
            return true;
        if (owner != null)
            return owner.isAlliedTo(entity);
        return super.isAlliedTo(entity);
    }

    @Override
    public void tick() {
        super.tick();

        this.timeRemaining--;
        if (this.timeRemaining < 0) {
            this.die(this.damageSources().dryOut());
        }

        // imprinting
        if (this.getOwner() == null) {
            Player nearestPlayer = EntityRetrievalUtil.getNearestPlayer(this, 10.0);
            if (nearestPlayer != null) {
                this.setOwnerUUID(nearestPlayer.getUUID());
            }
        }
    }

    protected boolean canTargetEntity(Entity entity) {
        if (entity instanceof OwnableEntity ownable) {
            if (this.getOwnerUUID() != null && ownable.getOwnerUUID() == this.getOwnerUUID())
                return false;
        }

        if (entity instanceof Mob mob)
            return mob.getTarget() == this.getOwner();

//        if (entity instanceof LivingEntity livingEntity)
//            return BrainUtils.getTargetOfEntity(livingEntity) == this.getOwner();
        return false;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.timeRemaining = this.getTotalTime();
    }

    protected int getTotalTime() {
        return 1200;
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID_ID, Optional.ofNullable(uuid));
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID_ID).orElse(null);
    }
}
