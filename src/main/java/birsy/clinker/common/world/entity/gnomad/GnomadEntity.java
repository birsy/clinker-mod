package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquads;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GnomadEntity extends Monster implements InterpolatedSkeletonParent {
    public GnomadSquad squad;

    @OnlyIn(Dist.CLIENT)
    public Vec3 acceleration = Vec3.ZERO;
    private Vec3 deltaPosition = Vec3.ZERO, pDeltaPosition = Vec3.ZERO;
    private EntityDataAccessor<Byte> DATA_ANIMATION_FLAGS_ID;
    private EntityDataAccessor<Boolean> DATA_CARRYING_SUPPLIES_ID;

    public GnomadEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
        this.moveControl = new SmoothSwimmingMoveControl(this, 365, 25, 1000.0F, 1.0F, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.02D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(this.DATA_ANIMATION_FLAGS_ID, (byte) 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) updateAcceleration();

    }

    public void lookForSquadmatesAndCreateSquad() {
        if (this.squad != null) return;
        if (this.level() instanceof ServerLevel level) {
            List<GnomadEntity> nearbyGnomads = GnomadSquad.getNearbyGnomads(this, GnomadSquad.SQUAD_SEARCH_RADIUS, (gnomad) -> gnomad.squad == null);
            nearbyGnomads.add(this);
            level.getDataStorage().computeIfAbsent(GnomadSquads.factory(level), "GnomadSquads").createSquad(nearbyGnomads);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void updateAcceleration() {
        this.pDeltaPosition = new Vec3(this.deltaPosition.x, this.deltaPosition.y, this.deltaPosition.z);
        this.deltaPosition = this.getDeltaMovement();
        this.acceleration = this.pDeltaPosition.subtract(this.deltaPosition);
    }

    public void setSitting(boolean sitting) {
        if (sitting) {
            this.entityData.set(DATA_ANIMATION_FLAGS_ID, (byte) (this.entityData.get(DATA_ANIMATION_FLAGS_ID) | 0b1));
        } else {
            this.entityData.set(DATA_ANIMATION_FLAGS_ID, (byte) (this.entityData.get(DATA_ANIMATION_FLAGS_ID) & 0b11111110));
        }
    }

    public boolean isSitting() {
        return (this.entityData.get(DATA_ANIMATION_FLAGS_ID) & 0b1) > 0;
    }

    InterpolatedSkeleton skeleton;
    @Override
    @OnlyIn(Dist.CLIENT)
    public void setSkeleton(InterpolatedSkeleton skeleton) {
        this.skeleton = skeleton;
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public InterpolatedSkeleton getSkeleton() {
        return this.skeleton;
    }

}
