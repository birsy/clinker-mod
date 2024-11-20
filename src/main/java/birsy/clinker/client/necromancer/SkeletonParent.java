package birsy.clinker.client.necromancer;

import birsy.clinker.client.necromancer.animation.Animator;

public interface SkeletonParent<T extends SkeletonParent<?>> {
    void setSkeleton(Skeleton skeleton);
    void setAnimator(Animator<T, Skeleton<T>> animator);

    Skeleton<T> getSkeleton();
    Animator<T, Skeleton<T>> getAnimator();
}
