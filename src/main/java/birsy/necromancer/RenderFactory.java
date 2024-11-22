package birsy.necromancer;

import birsy.necromancer.animation.Animator;

public interface RenderFactory<P extends SkeletonParent> {
    default void setup(P parent) {}
    Skeleton<P> createSkeleton(P parent);
    Animator<P, Skeleton<P>> createAnimator(P parent, Skeleton<P> skeleton);
}
