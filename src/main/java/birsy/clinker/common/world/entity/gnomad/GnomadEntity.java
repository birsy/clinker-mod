package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.GnomadSquads;
import birsy.clinker.core.Clinker;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;


import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public abstract class GnomadEntity extends GroundLocomoteEntity implements Enemy {
    public GnomadSquad squad;

    
    public Vec3 acceleration = Vec3.ZERO;
    private Vec3 deltaPosition = Vec3.ZERO;
    private static final EntityDataAccessor<Byte> DATA_ANIMATION_FLAGS_ID = SynchedEntityData.defineId(GnomadEntity.class, EntityDataSerializers.BYTE);

    public GnomadEntity(EntityType<? extends GnomadEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.7D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIMATION_FLAGS_ID, (byte) 0);
    }
    

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (this.level() instanceof ServerLevel level) GnomadSquads.getInstance(level).createSquad(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            updateAcceleration();
        }
    }

    
    public void updateAcceleration() {
        Vec3 pDeltaPosition = new Vec3(this.deltaPosition.x, this.deltaPosition.y, this.deltaPosition.z);
        this.deltaPosition = this.getDeltaMovement();
        this.acceleration = pDeltaPosition.subtract(this.deltaPosition);
    }

    public void setSitting(boolean sitting) {
        if (sitting) {
            Clinker.LOGGER.info("{} sits!", DebugEntityNameGenerator.getEntityName(this));
            this.entityData.set(DATA_ANIMATION_FLAGS_ID, (byte) (this.entityData.get(DATA_ANIMATION_FLAGS_ID) | 0b1));
        } else {
            this.entityData.set(DATA_ANIMATION_FLAGS_ID, (byte) (this.entityData.get(DATA_ANIMATION_FLAGS_ID) & 0b11111110));
        }
    }

    public boolean isSitting() {
        return (this.entityData.get(DATA_ANIMATION_FLAGS_ID) & 0b1) > 0;
    }
}
