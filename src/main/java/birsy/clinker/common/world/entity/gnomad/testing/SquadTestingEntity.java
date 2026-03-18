package birsy.clinker.common.world.entity.gnomad.testing;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.common.world.entity.ai.behaviors.LocomotorLookAtTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.ActiveSquadTasksSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.PostedSquadTasksSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.NearestSupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class SquadTestingEntity<E extends SquadTestingEntity<E>> extends GroundLocomotionEntity implements SquadMember<E>, SmartBrainOwner<E> {
    private Squad squad;

    public SquadTestingEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.STEP_HEIGHT, 1.1D);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain((E)this);
    }

    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>((E)this);
    }

    @Override
    public List<? extends ExtendedSensor<E>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new SquadSensor<>(),
                new PostedSquadTasksSensor<>(),
                new ActiveSquadTasksSensor<>(),
                new NearestSupplyDepotSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<E> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new SetAttackTarget<>(false)
                        .targetFinder(mob -> EntityRetrievalUtil.getNearestPlayer(mob, 32.0F)),
                new LocomotorLookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.serializeSquad(nbt);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.deserializeSquad(nbt);
    }

    @Override
    public @Nullable Squad getSquad() { return squad; }
    @Override
    public void setSquad(@Nullable Squad newSquad) {
        if (this.squad != null) this.squad.removeMember(this);
        if (newSquad != null) newSquad.addMember(this);
        this.squad = newSquad;
    }
}
