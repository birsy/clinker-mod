package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.ai.behaviors.StateMachineBehavior;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.List;

public class MogulCombatStateMachine extends StateMachineBehavior<GnomadMogulEntity> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_PRESENT)
    );

    public MogulCombatStateMachine() {
        super();
        this.initialState((entity) -> new StrafeState());
        this.noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of();
    }

    private static class StrafeState implements StateMachineBehavior.State<GnomadMogulEntity> {
        @Override
        public void tick(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            // just walk from side to side
            entity.getMoveControl().strafe(0, (float) Math.sin(entity.tickCount / 20.0F) * 3);
            if (RandomUtil.oneInNChance(100)) {
                stateMachine.transition(new DoALittleTwirlState());
            }
        }

        @Override
        public void onExit(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            entity.getMoveControl().strafe(0, 0);
        }
    }
    private static class DoALittleTwirlState implements StateMachineBehavior.State<GnomadMogulEntity> {
        double angle, progress = 0;

        @Override
        public void onEnter(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            angle = entity.getYHeadRot();
            entity.getLookControl().rotationLerpSpeed.pushModifier("twirl", 999, 0.2F);
            entity.getBodyRotationControl().rotationLerpSpeed.pushModifier("twirl", 999, 1.0F);
            Clinker.LOGGER.info("initial angle {}", angle);
        }

        @Override
        public void tick(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            float angleDelta = 5;
            angle = Mth.wrapDegrees(angle + angleDelta);
            progress += angleDelta;
            double lookTargetX = entity.getX() + Math.cos((angle + 90) * Mth.DEG_TO_RAD) * 2,
                   lookTargetZ = entity.getZ() + Math.sin((angle + 90) * Mth.DEG_TO_RAD) * 2;
            entity.getLookControl().setLookAt(lookTargetX, entity.getEyeY(), lookTargetZ, angleDelta, angleDelta);
            entity.yBodyRot = (float) angle;
            if (progress >= 360)
                stateMachine.transition(new StrafeState());
        }

        @Override
        public void onExit(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            entity.getLookControl().rotationLerpSpeed.popModifier("twirl");
            entity.getBodyRotationControl().rotationLerpSpeed.popModifier("twirl");
            Clinker.LOGGER.info("ending angle {}", entity.getYHeadRot());
        }
    }
}
