package birsy.clinker.common.world.entity.ai.behaviors;

import birsy.clinker.core.Clinker;
import net.minecraft.world.entity.Mob;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class StateMachineBehavior<E extends Mob> extends ExtendedBehaviour<E> {
    private Function<E, State<E>> initialStateConstructor;
    private StateMachine<E> stateMachine;

    public StateMachineBehavior<E> initialState(Function<E, State<E>> initialStateConstructor) {
        this.initialStateConstructor = initialStateConstructor;
        return this;
    }

    @Override
    protected void start(E entity) {
        Clinker.LOGGER.info("initializing state machine...");
        this.stateMachine = new StateMachine<>(entity, initialStateConstructor.apply(entity));
    }

    @Override
    protected void tick(E entity) {
        super.tick(entity);
        this.stateMachine.tick();
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return this.stateMachine.currentState != null;
    }

    @Override
    protected void stop(E entity) {
        if (this.stateMachine.currentState != null) this.stateMachine.end();
        this.stateMachine = null;
        Clinker.LOGGER.info("stopped state machine");
    }

    public static class StateMachine<E extends Mob> {
        private final E entity;
        @Nullable private State<E> currentState;

        public StateMachine(E entity, State<E> initialState) {
            this.entity = entity;
            this.currentState = initialState;
            Clinker.LOGGER.info("initialized state machine with initial state {}", initialState.getClass().getSimpleName());
        }

        protected void tick() {
            if (currentState != null)
                currentState.tick(this, this.entity);
        }

        public void transition(State<E> newState) {
            if (currentState != null)
                currentState.onExit(this, this.entity);
            if (newState != null) {
                Clinker.LOGGER.info("transitioned to state {}", newState.getClass().getSimpleName());
            } else {
                Clinker.LOGGER.info("transitioned to null state");
            }

            this.currentState = newState;
        }

        public void end() {
            if (currentState != null) currentState.onExit(this, this.entity);
            this.currentState = null;
        }
    }

    public interface State<E extends Mob> {
        default void tick(StateMachine<E> stateMachine, E entity) {}
        default void onExit(StateMachine<E> stateMachine, E entity) {}
    }
}
