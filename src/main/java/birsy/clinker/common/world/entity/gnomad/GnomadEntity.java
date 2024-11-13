package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquadTask;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquads;
import birsy.clinker.common.world.entity.gnomad.squad.RestWithFriendsTask;
import birsy.clinker.core.Clinker;
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
import net.neoforged.api.distmarker.OnlyIn;
import net.tslat.smartbrainlib.api.SmartBrainOwner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public abstract class GnomadEntity extends GroundLocomoteEntity implements Enemy, InterpolatedSkeletonParent {
    public GnomadSquad squad;
    public List<GnomadSquadTask> activeTasks = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANIMATION_FLAGS_ID, (byte) 0);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level() instanceof ServerLevel level) GnomadSquads.getInstance(level).createSquad(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            updateAcceleration();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void updateAcceleration() {
        Vec3 pDeltaPosition = new Vec3(this.deltaPosition.x, this.deltaPosition.y, this.deltaPosition.z);
        this.deltaPosition = this.getDeltaMovement();
        this.acceleration = pDeltaPosition.subtract(this.deltaPosition);
    }

    public void setSitting(boolean sitting) {
        if (sitting) {
            Clinker.LOGGER.info("sit!");
            this.entityData.set(DATA_ANIMATION_FLAGS_ID, (byte) (this.entityData.get(DATA_ANIMATION_FLAGS_ID) | 0b1));
        } else {
            this.entityData.set(DATA_ANIMATION_FLAGS_ID, (byte) (this.entityData.get(DATA_ANIMATION_FLAGS_ID) & 0b11111110));
        }
    }

    public boolean isSitting() {
        return (this.entityData.get(DATA_ANIMATION_FLAGS_ID) & 0b1) > 0;
    }

    public void ceaseTaskOfType(Class<GnomadSquadTask> taskType) {
        Iterator<GnomadSquadTask> taskIterator = this.activeTasks.iterator();
        while (taskIterator.hasNext()) {
            GnomadSquadTask task = taskIterator.next();
            if (taskType.isInstance(task)) {
                task.leave(this);
                taskIterator.remove();
            }
        }
    }

    public void ceaseTask(GnomadSquadTask task) {
        task.leave(this);
        this.activeTasks.remove(task);
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
