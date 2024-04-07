package birsy.clinker.common.world.entity.salamander;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;

public class SalamanderPartEntity extends PartEntity<NewSalamanderEntity> {
    protected NewSalamanderEntity.SalamanderSegment segmentParent;
    private static final EntityDimensions size = EntityDimensions.scalable(1, 1);

    public SalamanderPartEntity(NewSalamanderEntity parent, NewSalamanderEntity.SalamanderSegment segment) {
        super(parent);
        this.segmentParent = segment;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.getParent().getPickResult();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        return this.getParent().hurt(damageSource, amount);
    }

    @Override
    public void push(double x, double y, double z) {
        super.push(x, y, z);
        this.segmentParent.push(x, y, z);
    }
    
    @Override
    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDimensions getDimensions(Pose p_31023_) {
        return size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    // data is entirely handled by the parent.
    @Override
    protected void defineSynchedData() {}
    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {}
}
