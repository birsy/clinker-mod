package birsy.clinker.common.entity.lungwyrm.behaviors;

import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import birsy.clinker.common.entity.projectile.LungWyrmChargeEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.List;

public class FireChargeVolley extends ExtendedBehaviour<LungWyrmEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .hasMemory(ClinkerMemoryModules.TRACKED_ENEMIES.get());
    int ticksRunning;
    int currentIndex, chargesRemaining;

    public FireChargeVolley() {
        super();
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
        this.ticksRunning = 0;
        this.chargesRemaining = entity.getRandom().nextInt(2, 6);
        this.currentIndex = entity.getRandom().nextInt(1, Math.min(12, entity.joints.size() - this.chargesRemaining));
    }

    @Override
    protected void tick(LungWyrmEntity entity) {
        super.tick(entity);
        ticksRunning++;

        for (int i = 0; i <= 1; i++) {
            if ((ticksRunning + i * 2) % 5 == 0) {
                LungWyrmEntity.LungWyrmJoint joint = entity.joints.get(currentIndex);

                Quaterniond rot = new Quaterniond();
                Vector3d pos = new Vector3d(), dir = new Vector3d();
                joint.getSegmentTransform(pos, dir, rot, 1.0F);

                dir.set(i == 0 ? -1 : 1, 0.1, 0).normalize();
                rot.transform(dir);
                fire(entity, pos, dir);

                if (i == 1) {
                    chargesRemaining--;
                    currentIndex++;
                }
            }
        }
    }

    void fire(LungWyrmEntity entity, Vector3d pos, Vector3d dir) {
        List<LivingEntity> targets = BrainUtils.getMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get()).enemies();
        LivingEntity target = targets.get(entity.getRandom().nextInt(0, targets.size()));
        BlockPos targetPos = target.getOnPos().offset(
                (int) entity.getRandom().triangle(0, 24), -10,
                (int) entity.getRandom().triangle(0, 24)
        );

        LungWyrmChargeEntity charge = LungWyrmChargeEntity.create(entity.level());
        charge.setPos(pos.x(), pos.y(), pos.z());
        charge.setDeltaMovement(dir.x(), dir.y(), dir.z());
        charge.setTargetPos(targetPos);
        charge.setOwner(entity);
        entity.level().addFreshEntity(charge);

        entity.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 300F,
                Mth.lerp(entity.getRandom().nextFloat(), 0.5F, 0.7F)
        );
    }

    @Override
    protected boolean shouldKeepRunning(LungWyrmEntity entity) {
        return chargesRemaining > 0 && currentIndex > 0 && currentIndex < entity.joints.size() && BrainUtils.hasMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get());
    }
}
