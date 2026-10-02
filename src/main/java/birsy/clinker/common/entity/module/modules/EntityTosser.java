package birsy.clinker.common.entity.module.modules;

import birsy.clinker.common.entity.module.Module;
import birsy.clinker.common.entity.module.ModuleHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface EntityTosser<E extends Entity & ModuleHolder<E> & EntityTosser<E>> {
    EntityTosser.EntityTosserModule<E> tossModule();

    class EntityTosserModule<E extends Entity & ModuleHolder<E> & EntityTosser<E>> extends Module<E> {
        public static final int NONE = -1;

        private final EntityDataAccessor<Integer> heldIdData;
        private final Vec3 holdOffset;
        private final int riderSeats;
        @Nullable private UUID heldEntityUUID;

        public EntityTosserModule(E self, EntityDataAccessor<Integer> heldIdData, Vec3 holdOffset, int riderSeats) {
            super(self);
            this.heldIdData = heldIdData;
            this.holdOffset = holdOffset;
            this.riderSeats = riderSeats;
        }

        @Nullable
        public Entity getHeldEntity() {
            Entity potentiallyHeldEntity = self.level().getEntity(self.getEntityData().get(heldIdData));
            if (potentiallyHeldEntity == null) return null;
            if (potentiallyHeldEntity.getVehicle() != self) return null;
            return potentiallyHeldEntity;
        }
        public boolean isHoldingEntity() {
            return getHeldEntity() != null;
        }

        public boolean grab(Entity target) {
            if (self.level().isClientSide) return false;
            if (self == target) return false;

            if (isHoldingEntity()) drop();
            if (!target.startRiding(self, true)) return false;
            self.getEntityData().set(heldIdData, target.getId());
            return true;
        }

        public void drop() {
            Entity held = getHeldEntity();
            if (held == null || self.level().isClientSide) return;
            Vec3 pos = findDropPos(held);
            detach(held);
            held.dismountTo(pos.x, pos.y, pos.z);
        }

        public void toss(Vec3 velocity) {
            Entity held = getHeldEntity();
            if (held == null || self.level().isClientSide) return;
            detach(held);

            if (held instanceof Projectile projectile) {
                projectile.shoot(velocity.x(), velocity.y(), velocity.z(), (float) velocity.length(), 0.1F);
                return;
            }

            held.setDeltaMovement(velocity);
            held.hurtMarked = true; // makes the server send the velocity to clients
        }

        private void detach(Entity held) {
            self.getEntityData().set(heldIdData, NONE);
            held.removeVehicle();
        }

        private Vec3 findDropPos(Entity held) {
            // drop them half a block away
            double reach = (self.getBbWidth() + held.getBbWidth()) / 4 + 0.5;
            Vec3 forward = Vec3.directionFromRotation(0, self.getYRot()).scale(reach);
            Vec3 desiredDropPos = self.getPosition(1.0F).add(forward);
            Vec3 desiredDropDelta = desiredDropPos.subtract(held.position());

            Vec3 dropDelta = Entity.collideBoundingBox(held, desiredDropDelta, held.getBoundingBox(), held.level(), List.of());
            return held.position().add(dropDelta);
        }

        @Override
        public void defineSyncedData(SynchedEntityData.Builder builder) {
            builder.define(heldIdData, NONE);
        }
        @Override
        public void save(CompoundTag tag) {
            Entity heldEntity = getHeldEntity();
            if (heldEntity != null) tag.putUUID("HeldEntity", heldEntity.getUUID());
        }
        @Override
        public void load(CompoundTag tag) {
            if (tag.hasUUID("HeldEntity")) heldEntityUUID = tag.getUUID("HeldEntity");
        }
        @Override
        public void tick() {
            // basically, initialize this properly from saved data.
            if (heldEntityUUID == null || self.level().isClientSide) return;
            for (Entity passenger : self.getPassengers()) {
                if (passenger.getUUID().equals(heldEntityUUID)) {
                    self.getEntityData().set(heldIdData, passenger.getId());
                    break;
                }
            }
            heldEntityUUID = null;
        }
        @Override
        public boolean canAddPassenger(Entity passenger) {
            int riders = self.getPassengers().size() - (isHoldingEntity() ? 1 : 0);
            return riders < riderSeats;
        }
        @Override
        public boolean positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
            if (passenger != getHeldEntity()) return false;
            Vec3 pos = self.position().add(holdOffset.yRot(-self.getYRot() * Mth.DEG_TO_RAD));
            moveFunction.accept(passenger, pos.x, pos.y, pos.z);
            return true;
        }
    }
}
