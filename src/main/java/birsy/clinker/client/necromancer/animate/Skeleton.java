package birsy.clinker.client.necromancer.animate;

import birsy.clinker.core.Clinker;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Skeleton {
    public static int MAX_BONES = 2;
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
        root.visit(this::addBone);
        return this;
    }

    public boolean addBone(Bone bone) {
        if (bones.size() >= MAX_BONES) {
            Clinker.LOGGER.error("Skeleton exceeded max bone count {} !", MAX_BONES);
            return false;
        }

        bones.add(bone);
        return true;
    }
}
