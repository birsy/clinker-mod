package birsy.clinker.common.entity.lungwyrm.behaviors;

import birsy.clinker.common.entity.ai.memories.EnemyTracker;
import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import birsy.clinker.common.entity.projectile.LungWyrmChargeEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.util.MathUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.List;

public class SwoopAndFireChargeVolley extends ExtendedBehaviour<LungWyrmEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .usesMemory(MemoryModuleType.WALK_TARGET)
            .hasMemory(ClinkerMemoryModules.TRACKED_ENEMIES.get());
    double dirX, dirZ, startY, swoopHeight;
    boolean stoppedSwooping;
    int swoopStartTicks, ticksRunning;
    int currentIndex, chargesRemaining;

    public SwoopAndFireChargeVolley() {
        super();
        this.noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LungWyrmEntity entity) {
        return entity.joints.size() > 11;
    }

    @Override
    protected void start(LungWyrmEntity entity) {
        super.start(entity);
        List<LivingEntity> targets = BrainUtils.getMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get()).enemies();
        int targetSize = 1;
        if (targets == null || targets.isEmpty()) {
            Vec3 vec = entity.calculateViewVector(0, entity.yHeadRot);
            dirX = vec.x;
            dirZ = vec.z;
        } else {
            targetSize = targets.size();
            double avgX = 0, avgZ = 0;
            for (LivingEntity target : targets) {
                avgX += (target.getX() + entity.getRandom().triangle(0, 32)) / targetSize;
                avgZ += (target.getZ() + entity.getRandom().triangle(0, 32)) / targetSize;
            }
            dirX = avgX - entity.getX();
            dirZ = avgZ - entity.getZ();
            double length = Mth.length(dirX, dirZ);
            dirX /= length; dirZ /= length;
        }

        startY = entity.getY();
        // determine swoop height
        int validGroundDistances = 0;
        float averageGroundY = Float.MAX_VALUE, maxGroundY = 0F;
        for (LungWyrmEntity.LungWyrmJoint joint : entity.joints) {
            if (Double.isFinite(joint.groundY)) {
                maxGroundY = (float) Math.max(maxGroundY, joint.groundY);
                averageGroundY += (float) joint.groundY;
                validGroundDistances++;
            }
        }
        averageGroundY /= validGroundDistances;
        float swoopY = (float) Math.min(Math.max(Mth.lerp(0.5F, startY, averageGroundY), maxGroundY + 10), startY - 10);
        swoopHeight = startY - swoopY;
        swoopStartTicks = 100;

        ticksRunning = 0;
        stoppedSwooping = false;

        chargesRemaining = Mth.clamp(entity.getRandom().nextInt(4, 8) * targetSize, 4, 24);
        currentIndex = entity.getRandom().nextInt(1, (int) Math.min(entity.joints.size() * 0.3, entity.joints.size() - this.chargesRemaining));
    }

    @Override
    protected void tick(LungWyrmEntity entity) {
        super.tick(entity);
        ticksRunning++;

        float swoopTime = ticksRunning / 300F;
        if (swoopTime <= 1) {
            double targetSwoopHeight = startY - swoopHeight * Math.sin(Math.PI * swoopTime);
            float speed = (float) Mth.lerp(Math.sin(Math.PI * swoopTime), 0.2, 0.6);
            BrainUtils.setMemory(
                    entity, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(
                            new Vec3(entity.getX() + dirX * 3, targetSwoopHeight, entity.getZ() + dirZ * 3),
                            speed, 1
                    )
            );
            entity.getLookControl().lookTargetController
                    .lookAt(1000,
                            entity.getX() + dirX * 3,
                            entity.getEyeY() + entity.joints.getFirst().dY * 10,
                            entity.getZ() + dirZ * 3,
                            1F, 0.03F
                    );

            float roll = Mth.clampedMap(swoopTime, 0.0F, 0.8F, 0, 1);
            roll = MathUtils.ease(roll, MathUtils.EasingType.easeOutSine) * 360;
            entity.setTargetHeadRoll(roll);
        } else if (!stoppedSwooping) {
            stoppedSwooping = true;
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
            entity.setTargetHeadRoll(0);
        }

        if (ticksRunning > swoopStartTicks && chargesRemaining > 0 && currentIndex > 0 && currentIndex < entity.joints.size()) {
            for (int i = 0; i <= 1; i++) {
                if ((ticksRunning + i * 2) % 7 == 0) {
                    LungWyrmEntity.LungWyrmJoint joint = entity.joints.get(currentIndex);

                    Quaterniond rot = new Quaterniond();
                    Vector3d pos = new Vector3d(), dir = new Vector3d();
                    joint.getSegmentTransform(pos, dir, rot, 1.0F);

                    dir.set(i == 0 ? -1 : 1, 0.1, 0).normalize();
                    rot.transform(dir);
                    fire(entity, pos, dir);

                    if (i == 1) {
                        chargesRemaining--;
                        currentIndex += 2;
                    }
                }
            }
        }
    }

    void fire(LungWyrmEntity entity, Vector3d pos, Vector3d dir) {
        BlockPos targetPos;

        EnemyTracker tracker = BrainUtils.getMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get());
        if (tracker != null && !tracker.enemies().isEmpty()) {
            List<LivingEntity> targets = tracker.enemies();
            LivingEntity target = targets.get(entity.getRandom().nextInt(0, targets.size()));
            targetPos = target.getOnPos().offset(
                    (int) entity.getRandom().triangle(0, 24), -10,
                    (int) entity.getRandom().triangle(0, 24)
            );
        } else {
            int x = (int) (pos.x() + entity.getRandom().triangle(0, 64)),
                z = (int) (pos.z() + entity.getRandom().triangle(0, 64));
            int y = entity.level().getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 10;
            targetPos = new BlockPos(x, y, z);
        }

        LungWyrmChargeEntity charge = LungWyrmChargeEntity.create(entity.level());
        charge.setPos(pos.x(), pos.y(), pos.z());
        charge.setDeltaMovement(dir.x() * 0.5, dir.y() * 0.5, dir.z() * 0.5);
        charge.setTargetPos(targetPos);
        charge.setOwner(entity);
        entity.level().addFreshEntity(charge);

        entity.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 300F,
                Mth.lerp(entity.getRandom().nextFloat(), 0.5F, 0.7F)
        );
    }

    @Override
    protected boolean shouldKeepRunning(LungWyrmEntity entity) {
        return !stoppedSwooping || (chargesRemaining > 0 && currentIndex > 0 && currentIndex < entity.joints.size());
    }

    @Override
    protected void stop(LungWyrmEntity entity) {
        super.stop(entity);
        if (!stoppedSwooping) {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
            entity.setTargetHeadRoll(0);
        }
    }
}
