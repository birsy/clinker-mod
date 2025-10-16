package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.ai.LookTargetController;
import birsy.clinker.common.world.entity.ai.behaviors.StateMachineBehavior;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.List;
import java.util.Optional;

public class MogulCombatStateMachine extends StateMachineBehavior<GnomadMogulEntity> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_PRESENT)
    );

    public MogulCombatStateMachine() {
        super();
        this.initialState((entity) -> new StrafeState(entity));
        this.noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of();
    }

    private static class StrafeState implements StateMachineBehavior.State<GnomadMogulEntity> {
        final LookTargetController.LookTargetHandle headLookTarget, bodyLookTarget;

        protected StrafeState(GnomadMogulEntity entity) {
            this.headLookTarget = entity.getLookControl().lookTargetController
                    .createHandle(0.5F, 1);
            this.bodyLookTarget = entity.getBodyRotationControl().lookTargetController
                    .createHandle(0.05F, 1);

            Optional<Entity> nearestEntity = EntityRetrievalUtil.getNearestEntity(entity, 10);
            if (nearestEntity.isPresent()) {
                this.headLookTarget.setActive(true);
                this.headLookTarget.face(nearestEntity.get());
            } else {
                this.headLookTarget.setActive(false);
            }
            this.bodyLookTarget.face(0, entity.getYHeadRot());
        }

        @Override
        public void tick(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            // just walk from side to side
            entity.getMoveControl().strafe(0, (float) Math.sin(entity.tickCount / 50.0F) * 5);

            // face the mob
            Optional<Entity> nearestEntity = EntityRetrievalUtil.getNearestEntity(entity, 10);
            if (nearestEntity.isPresent()) {
                this.headLookTarget.setActive(true);
                this.headLookTarget.face(nearestEntity.get());
            } else {
                this.headLookTarget.setActive(false);
            }
            this.bodyLookTarget.face(0, entity.getYHeadRot());

            // sometimes do a little spin
            if (RandomUtil.oneInNChance(100))
                stateMachine.transition(new DoALittleTwirlState(entity));
        }

        @Override
        public void onExit(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            this.headLookTarget.remove(); this.bodyLookTarget.remove();
            entity.getMoveControl().strafe(0, 0);
        }
    }
    private static class DoALittleTwirlState implements StateMachineBehavior.State<GnomadMogulEntity> {
        final LookTargetController.LookTargetHandle headLookTarget, bodyLookTarget;
        float angle, progress = 0;

        protected DoALittleTwirlState(GnomadMogulEntity entity) {
            this.headLookTarget = entity.getLookControl().lookTargetController.createHandle(0.5F, 99);
            this.bodyLookTarget = entity.getBodyRotationControl().lookTargetController.createHandle(0.1F, 99);

            angle = entity.getYHeadRot();
            headLookTarget.face(Mth.cos(progress * Mth.DEG_TO_RAD * 5) * 45, angle);
            bodyLookTarget.face(0, angle);
        }

        @Override
        public void tick(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            float angleDelta = 5;
            angle = Mth.wrapDegrees(angle + angleDelta);
            progress += angleDelta;

            headLookTarget.face(Mth.cos(progress * Mth.DEG_TO_RAD * 5) * 45, angle);
            bodyLookTarget.face(0, angle);

            if (progress >= 360)
                stateMachine.transition(new StrafeState(entity));
        }

        @Override
        public void onExit(StateMachine<GnomadMogulEntity> stateMachine, GnomadMogulEntity entity) {
            headLookTarget.remove(); bodyLookTarget.remove();
        }
    }
}
