package birsy.clinker.common.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.entity.PartEntity;

import java.util.List;

public class SeaHagPartEntity extends PartEntity<SeaHagEntity> {
    private final EntityDimensions size;
    public final String name;

    public SeaHagPartEntity(SeaHagEntity parent, String name, float w, float h) {
        super(parent);
        this.name = name;
        this.size = EntityDimensions.scalable(w, h);
        this.refreshDimensions();
    }

    protected void defineSynchedData() {

    }
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public int getId() {
        return this.getParent().getId();
    }

    public boolean equals(Object pObject) {
        if (pObject instanceof Entity) {
            return ((Entity)pObject).getId() == this.getId();
        } else {
            return false;
        }
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }
    public boolean isPickable() {
        return true;
    }

    protected void collideWithNearbyEntities() {
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof SeaHagPartEntity && ((SeaHagPartEntity) entity).getParent() == parent) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return this.getParent().hurt(this, pSource, pAmount);
    }

    public boolean is(Entity pEntity) {
        return this == pEntity || this.getParent() == pEntity;
    }
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }
}
