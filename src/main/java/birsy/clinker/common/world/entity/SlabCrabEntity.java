package birsy.clinker.common.world.entity;

import birsy.clinker.common.world.entity.ai.GroundLookControl;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import java.util.List;

public class SlabCrabEntity extends GroundLocomoteEntity implements SmartBrainOwner<SlabCrabEntity> {
    public SlabCrabEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    // ai
    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<ExtendedSensor<SlabCrabEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<SlabCrabEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<SlabCrabEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<AiTestEntity>(
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    // make them act like a solid
    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    @Override
    public void push(Entity entity) {
        // don't
    }

    public static class CrabLookControl extends GroundLookControl {
        public CrabLookControl(SlabCrabEntity pMob) {
            super(pMob);
        }

        public SlabCrabEntity getEntity() {
            return (SlabCrabEntity) this.mob;
        }

        @Override
        public void tick() {
            SlabCrabEntity me = this.getEntity();
            
            float desiredYAngle = this.getYRotD().orElse(me.yBodyRot);
            float desiredXAngle = this.getXRotD().orElse(0.0F);

            float lerpFactor = this.rotationLerpSpeed.value();

            me.yHeadRot = rotateTowards(me.yHeadRot, Mth.rotLerp(lerpFactor, me.yHeadRot, desiredYAngle), 5);
            me.setXRot(   rotateTowards(me.getXRot(), Mth.rotLerp(lerpFactor, me.getXRot(), desiredXAngle), 5));

            this.clampHeadRotationToBody();
        }
    }
}
