package birsy.clinker.client.necromancer.animation;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import birsy.clinker.client.necromancer.constraint.Constraint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Animator<P extends SkeletonParent, T extends Skeleton<P>> {
    final P parent;
    final T skeleton;
    final List<ConstraintEntry> constraints;
    final List<AnimationEntry<P, T>> animations;

    protected Animator(P parent, T skeleton) {
        this.parent = parent;
        this.skeleton = skeleton;
        this.animations = new ArrayList<>();
        this.constraints = new ArrayList<>();
    }

    public void addConstraint(Constraint constraint, int priority) {
        constraints.add(new ConstraintEntry(constraint, priority));
        constraints.sort(Comparator.comparingInt(entry -> entry.priority));
    }

    public AnimationEntry<P, T> addAnimation(Animation<P, T> animation, int priority) {
        AnimationEntry<P, T> entry = new AnimationEntry<>(animation, priority);
        this.animations.add(entry);
        animations.sort(Comparator.comparingInt(animEntry -> animEntry.priority));
        return entry;
    }

    public void tick() {
        this.skeleton.tick();
        this.animations.forEach(animation -> animation.apply(this.parent, this.skeleton));
        this.animate();
        this.constraints.forEach(constraintEntry -> constraintEntry.constraint.apply());
        this.animatePostConstraints();
    }
    public void animate() {}
    public void animatePostConstraints() {}

    record ConstraintEntry(Constraint constraint, int priority) {}

    public static class AnimationEntry<P extends SkeletonParent, T extends Skeleton<P>> {
        final Animation<P, T> animation;
        final int priority;
        float mixFactor;
        float time;

        private AnimationEntry(Animation<P, T> animation, int priority) {
            this.animation = animation;
            this.priority = priority;
        }

        public void setMixFactor(float mixFactor) {
            this.mixFactor = mixFactor;
        }
        public void setTime(float time) {
            this.time = time;
        }
        public void progressTime(float deltaTime) {
            this.setTime(this.time + deltaTime);
        }
        protected void apply(P parent, T skeleton) {
            if (!this.animation.running(parent, skeleton, mixFactor, time)) return;
            this.animation.apply(parent, skeleton, mixFactor, time);
        }
    }
}
