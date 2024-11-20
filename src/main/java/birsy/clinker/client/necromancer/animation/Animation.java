package birsy.clinker.client.necromancer.animation;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;

public abstract class Animation<P extends SkeletonParent, T extends Skeleton<P>> {
    public boolean running(P parent, T skeleton, float mixFactor, float time) { return mixFactor > 0; }
    public void apply(P parent, T skeleton, float mixFactor, float time) {}
}
