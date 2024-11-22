package birsy.necromancer.animation;

import birsy.necromancer.Skeleton;
import birsy.necromancer.SkeletonParent;

public abstract class Animation<P extends SkeletonParent, T extends Skeleton<P>> {
    public boolean running(P parent, T skeleton, float mixFactor, float time) { return mixFactor > 0; }
    public void apply(P parent, T skeleton, float mixFactor, float time) {}
}
