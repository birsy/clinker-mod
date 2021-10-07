package birsy.clinker.common.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MoleGatorEntity extends MonsterEntity {
    public MoleGatorEntity(EntityType<? extends MoleGatorEntity> entityIn, World worldIn) {
        super(entityIn, worldIn);
    }

    public static class ChargeAttackGoal extends Goal {
        private final MoleGatorEntity gator;
        private Vector3d chargeDirection;
        private final float adjustmentSpeed;

        private int windupLength = 15;
        private int windupTicks;

        private boolean isAttacking;
        private int chargeLength = 40;
        private int chargeTicks;

        public ChargeAttackGoal(MoleGatorEntity gator, float adjustmentSpeed) {
            this.gator = gator;
            this.adjustmentSpeed = adjustmentSpeed;
        }

        @Override
        public boolean shouldExecute() {
            return false;
        }

        @Override
        public void startExecuting() {
            this.chargeLength = 20 + gator.rand.nextInt(25);
            this.windupLength = 15 + gator.rand.nextInt(5);
            this.chargeDirection = new Vector3d(this.gator.getLookVec().getX(), 0.0D, this.gator.getLookVec().getZ());
            super.startExecuting();
        }

        @Override
        public void tick() {
            super.tick();
            if (this.gator.getAttackTarget() != null) {
                LivingEntity target = this.gator.getAttackTarget();
                Vector3d targetVector = this.gator.getPositionVec().subtract(target.getPositionVec()).normalize();
                this.chargeDirection = new Vector3d(MathHelper.lerp(adjustmentSpeed, this.chargeDirection.getX(), targetVector.getX()),
                                                    MathHelper.lerp(adjustmentSpeed, this.chargeDirection.getY(), targetVector.getY()),
                                                    MathHelper.lerp(adjustmentSpeed, this.chargeDirection.getZ(), targetVector.getZ()));

                if (!this.isAttacking) {
                    this.windupTicks++;


                    if (this.windupTicks >= windupLength) {
                        this.isAttacking = true;
                    }
                } else {

                }
            }
        }
    }
}
