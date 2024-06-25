package birsy.clinker.common.world.entity.homunculoids;

import birsy.clinker.common.world.entity.ai.FollowBehindEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

public class PouncerHomunculoid extends HomunculoidEntity implements SmartBrainOwner<PouncerHomunculoid> {

    public PouncerHomunculoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void customServerAiStep() {
        this.tickBrain(this);
    }

    @Override
    public List<? extends ExtendedSensor<PouncerHomunculoid>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<PouncerHomunculoid> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<PouncerHomunculoid> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<PouncerHomunculoid>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new FollowBehindEntity<PouncerHomunculoid>()
                        .entityProvider((homunculoid) -> homunculoid.owner),
                new OneRandomBehaviour<> (
                        new SetRandomWalkTarget<>().setRadius(2),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))) );
    }

    @Override
    public BrainActivityGroup<PouncerHomunculoid> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }
}
