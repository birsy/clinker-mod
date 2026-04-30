package birsy.clinker.common.world.entity;

import birsy.clinker.client.entity.leggy.LeggyAnimator;
import birsy.clinker.client.entity.leggy.LeggySkeleton;
import birsy.clinker.client.entity.slabcrab.SlabCrabAnimator;
import birsy.clinker.client.entity.slabcrab.SlabCrabSkeleton;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GiantLeggyCritterEntity extends GroundLocomotionEntity implements SkeletonParent<GiantLeggyCritterEntity, LeggySkeleton> {
    public LegManager legManager;

    public GiantLeggyCritterEntity(EntityType<? extends GroundLocomotionEntity> entityType, Level level) {
        super(entityType, level);

        double socketY = 0.5, idealY = -0.2;

        double legLength = 3, idealLength = 0.6 * legLength;

        this.legManager = new LegManager(this, 0.15F);

        int[] stepGroups = {5, 3, 1, 4, 2};
        for (int i = 0; i < stepGroups.length; i++) {
            double angle = (i / 5F) * Math.PI * 2 + Mth.PI;
            double x = Math.sin(angle), z = Math.cos(angle);

            legManager.addLeg(stepGroups[i] - 1,
                    x * 0.5, socketY, z * 0.5,
                    x * idealLength, idealY, z * idealLength,
                    legLength
            );
        }
    }

    // don't
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    @Override
    public void tick() {
        super.tick();
        legManager.tick();
        float speedFactor = 0.0F;
        int legCount = legManager.legCount();
        for (int i = 0; i < legCount; i++) {
            speedFactor += legManager.getLeg(i).state == LegManager.Leg.State.ATTACHED ? 1 : 0;
        }
        speedFactor /= legCount;
        this.speedModifier = speedFactor;
    }

    LeggySkeleton skeleton;
    LeggyAnimator animator;
    @Override public void setSkeleton(LeggySkeleton skeleton) { this.skeleton = skeleton; }
    @Override public LeggySkeleton getSkeleton() { return this.skeleton; }
    @Override public void setAnimator(Animator<GiantLeggyCritterEntity, LeggySkeleton> animator) { this.animator = (LeggyAnimator) animator; }
    @Override public LeggyAnimator getAnimator() { return animator; }
}
