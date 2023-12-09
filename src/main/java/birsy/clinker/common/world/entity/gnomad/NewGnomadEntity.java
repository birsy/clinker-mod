package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.core.Clinker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NewGnomadEntity extends Monster implements InterpolatedSkeletonParent {
    @OnlyIn(Dist.CLIENT)
    public Vec3 acceleration = Vec3.ZERO;
    private Vec3 deltaPosition = Vec3.ZERO, pDeltaPosition = Vec3.ZERO;

    public NewGnomadEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
        this.moveControl = new SmoothSwimmingMoveControl(this, 365, 25, 1000.0F, 1.0F, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.2D, 0.001F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.02D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();
        updateAcceleration();
    }

    @OnlyIn(Dist.CLIENT)
    public void updateAcceleration() {
        this.pDeltaPosition = new Vec3(this.deltaPosition.x, this.deltaPosition.y, this.deltaPosition.z);
        this.deltaPosition = this.getDeltaMovement();
        this.acceleration = this.pDeltaPosition.subtract(this.deltaPosition);
        //Clinker.LOGGER.info(this.acceleration);
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
