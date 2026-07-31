package birsy.clinker.common.entity.ai.behaviors;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.GroupBehaviour;
import net.tslat.smartbrainlib.object.SBLShufflingList;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public class DecisionBehaviour<E extends LivingEntity> extends GroupBehaviour<E> {
    public static final int CANCEL = -1;

    private final Function<E, Integer> decider;
    private boolean shouldInterrupt = false;

    public static <E extends LivingEntity> DecisionBehaviour<E> condition(Predicate<E> decider, ExtendedBehaviour<? super E> a, ExtendedBehaviour<? super E> b) {
        return new DecisionBehaviour<>(
                (e) -> decider.test(e) ? 0 : 1,
                a, b
        );
    }
    public static <E extends LivingEntity> DecisionBehaviour<E> condition(Predicate<E> decider, ExtendedBehaviour<? super E> a) {
        return new DecisionBehaviour<>(e -> decider.test(e) ? 0 : CANCEL, a);
    }

    @SafeVarargs
    public DecisionBehaviour(Function<E, Integer> decider, ExtendedBehaviour<? super E>... behaviours) {
        super(behaviours);
        this.decider = decider;
    }

    public DecisionBehaviour<E> shouldInterrupt(boolean shouldInterrupt) {
        this.shouldInterrupt = shouldInterrupt;
        return this;
    }

    @Override
    protected @Nullable ExtendedBehaviour<? super E> pickBehaviour(ServerLevel level, E entity, long gameTime, SBLShufflingList<ExtendedBehaviour<? super E>> behaviours) {
        int nextIndex = decider.apply(entity);
        if (nextIndex >= 0 && nextIndex < this.behaviours.size())
            return this.behaviours.get(nextIndex);
        return null;
    }

    @Override
    protected void tick(ServerLevel level, E owner, long gameTime) {
        if (shouldInterrupt) {
            ExtendedBehaviour<? super E> nextBehaviour = pickBehaviour(level, owner, gameTime, this.behaviours);
            if (nextBehaviour != runningBehaviour) {
                // exit early
                if (runningBehaviour != null)
                    runningBehaviour.doStop(level, owner, gameTime);

                // and begin the next behavior, maybe
                runningBehaviour = nextBehaviour;
                if (runningBehaviour == null || !runningBehaviour.tryStart(level, owner, gameTime)) {
                    doStop(level, owner, gameTime);
                    return;
                }
            }
        }
        super.tick(level, owner, gameTime);
    }

    @Override
    protected void start(ServerLevel level, E entity, long gameTime) {
        super.start(level, entity, gameTime);
        if (runningBehaviour != null) runningBehaviour.tryStart(level, entity, gameTime);
    }
}