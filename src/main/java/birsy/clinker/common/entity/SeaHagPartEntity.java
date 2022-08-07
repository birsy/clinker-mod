package birsy.clinker.common.entity;

import birsy.clinker.core.Clinker;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.List;

public class SeaHagPartEntity extends PartEntity<SeaHagEntity> {
    private final EntityDimensions size;
    public final String name;
    private final SeaHagEntity parent;

    public SeaHagPartEntity(SeaHagEntity parent, String name, float w, float h) {
        super(parent);
        this.parent = parent;
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
    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }
    public boolean isPickable() {
        return true;
    }

    protected void collideWithNearbyEntities() {
        List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof SeaHagPartEntity && ((SeaHagPartEntity) entity).getParent() == parent) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return this.getParent().hurt(this, pSource, pAmount);
    }

    @Override
    public SeaHagEntity getParent() {
        return this.parent;
    }

    public boolean is(Entity pEntity) {
        return this == pEntity || this.getParent() == pEntity;
    }
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }
}
