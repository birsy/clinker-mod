package birsy.clinker.client.necromancer.animate;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Skeleton {
    List<Bone> roots = new ArrayList<>();
    List<Bone> bones = new ArrayList<>();

    public Collection<Bone> roots() {
        return Collections.unmodifiableCollection(this.roots);
    }

    public List<Bone> boneByIndex() {
        return Collections.unmodifiableList(this.bones);
    }

    public Skeleton addRoot(Bone root) {
        this.roots.add(root);
        this.bones.add(root);
        root.visit(bones::add);
        return this;
    }
}
