package birsy.clinker.common.entity.homunculoids;

import birsy.clinker.common.entity.ai.behaviors.AttackWithOwner;
import birsy.clinker.common.entity.ai.behaviors.FollowBehindEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class SpitterHomunculoid extends HomunculoidEntity implements SmartBrainOwner<SpitterHomunculoid>, RangedAttackMob {
    public SpitterHomunculoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        // placeholder - copied from snow golem
        // todo: make this an actual custom projectile
        // idea: maybe something sticky that slows entities down?

        Arrow arrow = new Arrow(this.level(), this, Items.ARROW.getDefaultInstance(), null);//new Snowball(this.level(), this);
        double targetX = target.getX(),
               targetY = target.getY() + target.getEyeHeight(),
               targetZ = target.getZ();

        arrow.shoot(targetX - arrow.getX(),
                    targetY - arrow.getY(),
                    targetZ - arrow.getZ(),
              1.6F, 6.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<ExtendedSensor<SpitterHomunculoid>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<SpitterHomunculoid> getCoreTasks() { // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<SpitterHomunculoid> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<SpitterHomunculoid>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new AttackWithOwner<>(),
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()
                ),         // Set a random look target
                new FollowBehindEntity<SpitterHomunculoid>()
                        .entityProvider(OwnableEntity::getOwner)
        );
    }

    @Override
    public BrainActivityGroup<SpitterHomunculoid> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new StayWithinDistanceOfAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableRangedAttack<>(0)); // Melee attack the target if close enough
    }
}
