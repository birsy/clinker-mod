package birsy.clinker.client.model.base.constraint;

import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaternionf;
import birsy.clinker.core.util.VectorUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InverseKinematicsConstraint implements Constraint {
    List<InterpolatedBone> bones;
    public List<Vector3f> points;
    List<Float> segmentLengths;
    Vector3f endPlacement;
    public Vector3f target;
    public Vector3f poleTarget;
    private PoseStack stack;
    private PolePlane polePlane;

    private final float minimumAcceptableDistance;

    public InverseKinematicsConstraint(InterpolatedBone chainEnd, int depth, float endX, float endY, float endZ, float minimumAcceptableDistance) {
        this.minimumAcceptableDistance = minimumAcceptableDistance;
        this.bones = new ArrayList<>(depth + 1);
        this.points = new ArrayList<>(depth + 1);
        this.segmentLengths = new ArrayList<>(depth + 1);
        this.endPlacement = new Vector3f(endX, endY, endZ);
        this.target = new Vector3f();
        this.poleTarget = new Vector3f();
        this.stack = new PoseStack();
        this.polePlane = new PolePlane(new Vector3f(), new Vector3f(1, 0, 0), new Vector3f(0, 0, 1));
        InterpolatedBone currentBone = chainEnd;
        for (int i = 0; i < depth + 1; i++) {
            bones.add(i, chainEnd);
        }

        bones.set(depth, currentBone);
        depth--;
        while (depth > 0) {
            if (currentBone.parent == null) throw new IllegalArgumentException("Inverse Kinematics Constraint depth exceeds maximum!");
            bones.add(depth, currentBone.parent);
            currentBone = currentBone.parent;
            depth--;
        }

        for (int i = 0; i < bones.size() + 1; i++) {
            points.add(new Vector3f());
        }
        this.updatePointLocations();

        for (int i = 0; i < points.size() - 1; i++) {
            segmentLengths.add(VectorUtils.distance(points.get(i + 1), points.get(i)));
        }
    }

    @Override
    public void initialize() {
        this.updatePoleTarget();
    }

    @Override
    public void apply() {
        this.updatePoleTarget();
        this.updatePointLocations();
        // thanks sebastian lague
        Vector3f origin = Vector3f.ZERO.copy();

        Vector3f planeNormal = this.target.copy();
        planeNormal.cross(this.poleTarget);
        planeNormal.normalize();

        // do it twice to do a full reverse pass
        for (int iteration = 0; iteration < 16; iteration ++) {
            boolean startingFromTarget = iteration % 2 == 0;
            // reverse arrays to alternate between forward and backward passes
            Collections.reverse(points);
            Collections.reverse(segmentLengths);


            Vector3f currentTarget = (startingFromTarget) ? target : origin;
            points.get(0).set(currentTarget.x(), currentTarget.y(), currentTarget.z());

            // constrain lengths
            for (int i = 1; i < points.size(); i++) {
                Vector3f dir = points.get(i).copy();
                dir.sub(points.get(i-1));
                dir.normalize();
                dir.mul(this.segmentLengths.get(i-1));

                points.get(i).set(points.get(i-1).x() + dir.x(), points.get(i-1).y() + dir.y(), points.get(i-1).z() + dir.z());

                //project onto pole plane
                Vector3f v = points.get(i).copy();
                v.sub(this.poleTarget);

                float dist = v.dot(planeNormal);
                points.get(i).set(points.get(i).x() - dist * planeNormal.x(), points.get(i).y() - dist * planeNormal.y(), points.get(i).z() - dist * planeNormal.z());
            }
        }

        //this.updateBoneTransforms();
    }

    private List<Vector3f> updatePointLocations() {
        this.points.clear();
        this.segmentLengths.clear();
        int boneCount = 5;
        this.points.add(new Vector3f(0, 0 * -16, 0));
        this.segmentLengths.add(16.0F);
        this.points.add(new Vector3f(16, 1 * -16, 0));
        this.segmentLengths.add(16.0F);
        this.points.add(new Vector3f(24, 2 * -16, 0));
        this.segmentLengths.add(16.0F);
        this.points.add(new Vector3f(16, 3 * -16, 0));
        this.segmentLengths.add(16.0F);
        this.points.add(new Vector3f(0, 4 * -16, 0));


//        stack.pushPose();
//        bones.get(0).getModelSpaceTransformMatrix(stack, 1);
//        for (int i = 0; i < bones.size(); i++) {
//            InterpolatedBone bone = bones.get(i);
//            if (i != 0) bone.transform(stack, 1);
//            VectorUtils.mul(stack.last().pose(), points.get(i));
//            if (i == bones.size() - 1) VectorUtils.mul(stack.last().pose(), points.get(i + 1));
//        }
//
//        stack.popPose();
        return points;
    }

    private PolePlane updatePoleTarget() {
        Vector3f start = points.get(0);
        Vector3f end = this.poleTarget;

        // get the center
        this.polePlane.location.set((start.x() + end.x()) * 0.5F, (start.y() + end.y()) * 0.5F, (start.z() + end.z()) * 0.5F);

        // pointing towards pole target
        this.polePlane.poleDirection.set(this.polePlane.location.x(), this.polePlane.location.y(), this.polePlane.location.z());
        this.polePlane.poleDirection.sub(this.poleTarget);

        // cross with limb direction vector to make plane normal
        this.polePlane.normal.set(start.x() - end.x(), start.y() - end.y(), start.z() - end.z());
        this.polePlane.normal.cross(this.polePlane.poleDirection);

        return this.polePlane;
    }

    // fuck me
    private void updateBoneTransforms() {
        stack.pushPose();

        bones.get(0).getModelSpaceTransformMatrix(stack, 1);

        Quaternionf modelSpaceOrientation = new Quaternionf();
        Quaternionf inverseModelSpaceRotation = new Quaternionf();
        Vector3f modelSpaceLocation;

        for (int i = 0; i < points.size() - 1; i++) {
            Vector3f pY = points.get(i).copy();
            pY.sub(points.get(i + 1));
            Vector3f pZ = polePlane.poleDirection.copy();
            Vector3f pX = pY.copy();
            pX.cross(pZ);

            modelSpaceOrientation.set(0, 0, 0, 1);
            modelSpaceOrientation.rotateTo(Vector3f.YP, pY);
            Vector3f initialXP = modelSpaceOrientation.transform(Vector3f.XP.copy());
            modelSpaceOrientation.rotateTo(initialXP, pX);
            modelSpaceLocation = points.get(i);

            InterpolatedBone bone = bones.get(i);
            if (i != 0) bone.transform(stack, 1);
            Matrix4f inverseModelSpaceMatrix = stack.last().pose().copy();
            inverseModelSpaceMatrix.invert();
            inverseModelSpaceRotation.setFromUnnormalized(inverseModelSpaceMatrix);
            modelSpaceOrientation.mul(inverseModelSpaceRotation);
            VectorUtils.mul(inverseModelSpaceMatrix, modelSpaceLocation);
        }

        stack.popPose();
    }

    @Override
    public boolean isSatisfied() {
        // terminate if close enough to target
        float dstToTarget = VectorUtils.distance(points.get(points.size() - 1), target);
        if (dstToTarget <= minimumAcceptableDistance) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isIterative() {
        return true;
    }

    private record PolePlane(Vector3f location, Vector3f normal, Vector3f poleDirection) {}
}
