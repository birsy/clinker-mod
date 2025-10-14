package birsy.clinker.client.entity;

import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.Skeleton;

public class SingleBoneSkeleton extends Skeleton {
    public final Bone root;

    public SingleBoneSkeleton() {
        super();
        this.root = new Bone("Root");
        this.addBone(this.root);
        this.buildRoots();
    }
}
