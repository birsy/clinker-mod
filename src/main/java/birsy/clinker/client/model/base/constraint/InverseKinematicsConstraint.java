package birsy.clinker.client.model.base.constraint;

import birsy.clinker.client.model.base.InterpolatedBone;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaternionf;
import birsy.clinker.core.util.VectorUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InverseKinematicsConstraint implements Constraint {
    public List<InterpolatedBone> bones;
    public List<Vector3f> points;
    public boolean[] jointDirections;
    List<Float> segmentLengths;
    Vector3f endPlacement;
    public Vector3f target;
    public Vector3f poleTarget;
    InverseKinematicDirection forwardDirection;
    private final float minimumAcceptableDistance;

    public InverseKinematicsConstraint(InterpolatedBone chainEnd, int depth, float endX, float endY, float endZ, float minimumAcceptableDistance) {
        this(InverseKinematicDirection.NEGATIVE_Y, chainEnd, depth, endX, endY, endZ, minimumAcceptableDistance);
    }

    public InverseKinematicsConstraint(InverseKinematicDirection forwardDirection, InterpolatedBone chainEnd, int depth, float endX, float endY, float endZ, float minimumAcceptableDistance) {
        this.forwardDirection = forwardDirection;
        this.minimumAcceptableDistance = minimumAcceptableDistance;
        this.bones = new ArrayList<>(depth);
        this.points = new ArrayList<>(depth + 1);
        this.segmentLengths = new ArrayList<>(depth);
        this.endPlacement = new Vector3f(endX, endY, endZ);
        this.target = new Vector3f();
        this.poleTarget = new Vector3f();

        for (int i = chainEnd.parentChain.size() + 1 - depth; i < chainEnd.parentChain.size(); i++) {
            bones.add(chainEnd.parentChain.get(i));
        }
        bones.add(chainEnd);

        for (int i = 0; i < bones.size() + 1; i++) {
            points.add(new Vector3f());
        }
        for (int i = 0; i < points.size() - 1; i++) {
            segmentLengths.add(VectorUtils.distance(points.get(i + 1), points.get(i)));
        }
        this.updatePointLocations();

        jointDirections = new boolean[points.size()];
    }

    @Override
    public void initialize() {}

    @Override
    public void apply() {
        this.updatePointLocations();
        this.updateJointDirections();

        // thanks sebastian lague
        Vector3f origin = this.points.get(0).copy();

        Vector3f planeNormal = this.target.copy();
        planeNormal.sub(origin);
        Vector3f pn2 = this.poleTarget.copy();
        pn2.sub(origin);
        planeNormal.cross(pn2);
        planeNormal.normalize();

        for (int iteration = 0; iteration < 32; iteration ++) {
            boolean startingFromTarget = iteration % 2 == 0;
            // reverse arrays to alternate between forward and backward passes
            Collections.reverse(points);
            Collections.reverse(segmentLengths);

            Vector3f currentTarget = (startingFromTarget) ? target : origin;
            points.get(0).set(currentTarget.x(), currentTarget.y(), currentTarget.z());

            correctPointLengths();
            projectPointsOntoPlane(planeNormal);
            correctJointDirections();
        }

        this.updateBoneTransforms();
    }

    // primary step of the FaBRIK algorithm
    private void correctPointLengths() {
        for (int i = 1; i < points.size(); i++) {
            Vector3f point = points.get(i);
            Vector3f previousPoint = points.get(i - 1);

            Vector3f dir = point.copy();
            dir.sub(previousPoint);
            dir.normalize();
            dir.mul(this.segmentLengths.get(i - 1));

            point.set(previousPoint.x() + dir.x(), previousPoint.y() + dir.y(), previousPoint.z() + dir.z());
        }
    }

    // used to align to pole target
    private void projectPointsOntoPlane(Vector3f planeNormal) {
        for (int i = 0; i < points.size(); i++) {
            Vector3f v = points.get(i).copy();
            v.sub(this.poleTarget);
            float dist = v.dot(planeNormal);
            points.get(i).set(points.get(i).x() - dist * planeNormal.x(), points.get(i).y() - dist * planeNormal.y(), points.get(i).z() - dist * planeNormal.z());
        }
    }

    // makes sure that the joints don't suddenly flip-flop directions from the previous step.
    // this one took a while to work out.
    private void correctJointDirections() {
        for (int i = 1; i < points.size() - 1; i++) {
            Vector3f point = points.get(i);
            Vector3f prevPoint = points.get(i - 1);
            Vector3f nextPoint = points.get(i + 1);

            boolean direction = calculateJointDirection(point, prevPoint, nextPoint, this.poleTarget);

            if (direction != this.jointDirections[i]) {
                Vector3f projectedPoint = VectorUtils.projectPointOntoLine(point, prevPoint, nextPoint);
                point.set(Mth.lerp(2, point.x(), projectedPoint.x()), Mth.lerp(2, point.y(), projectedPoint.y()), Mth.lerp(2, point.z(), projectedPoint.z()));
            }
        }
    }

    // defines the joint's "direction," relative to its neighboring bones. uses this to flip incorrect directions in the previous function.
    private boolean[] updateJointDirections() {
        Vector3f polePoint = this.points.get(0).copy();
        polePoint.lerp(this.points.get(this.points.size() - 1), 0.5F);
        Vector3f poleDirection = Vector3f.ZERO.copy();
        switch (this.forwardDirection) {
            case NEGATIVE_X -> poleDirection.add( 0, 8, 0);
            case POSITIVE_X -> poleDirection.add( 0,-8, 0);
            case NEGATIVE_Y -> poleDirection.add( 0, 0, 8);
            case POSITIVE_Y -> poleDirection.add( 0, 0,-8);
            case NEGATIVE_Z -> poleDirection.add( 8, 0, 0);
            case POSITIVE_Z -> poleDirection.add(-8, 0, 0);
        }
        // hopefully correcting the weird rotation garbage
        if (this.bones.get(0).parent != null) {
            Quaternionf rotation = new Quaternionf();
            for (InterpolatedBone bone : this.bones.get(0).parent.parentChain) {
                rotation.mul(bone.rotation);
            }
            rotation.mul(this.bones.get(0).parent.rotation);

            poleDirection = rotation.transform(poleDirection);
        }
        polePoint.add(poleDirection);

        for (int i = 1; i < points.size() - 1; i++) {
            Vector3f point = points.get(i);
            Vector3f prevPoint = points.get(i - 1);
            Vector3f nextPoint = points.get(i + 1);

            jointDirections[i] = calculateJointDirection(point, prevPoint, nextPoint, polePoint);
        }

        return jointDirections;
    }

    // calculates the "direction" of a joint relative to it's neighbors, used in the joint direction correction step
    private boolean calculateJointDirection(Vector3f point, Vector3f prevPoint, Vector3f nextPoint, Vector3f polePoint) {
        Vector3f projectedPoint = VectorUtils.projectPointOntoLine(point, prevPoint, nextPoint);
        Vector3f projectedPole = VectorUtils.projectPointOntoLine(polePoint, prevPoint, nextPoint);
        Vector3f pointDirection = projectedPoint.copy();
        pointDirection.sub(point);
        Vector3f poleDirection = projectedPole.copy();
        poleDirection.sub(polePoint);

        float dot = pointDirection.dot(poleDirection);
        return dot > 0;
    }

    // updates IK point locations based off bone transforms.
    private List<Vector3f> updatePointLocations() {
        PoseStack stack = new PoseStack();
        for (int i = 0; i < this.points.size() - 1; i++) {
            stack.pushPose();
            InterpolatedBone bone = bones.get(i);
            bone.getModelSpaceTransformMatrix(stack, 1);

            Vector4f point4 = new Vector4f(0, 0, 0, 1);
            point4.transform(stack.last().pose());
            points.get(i).set(point4.x(), point4.y(), point4.z());
            stack.popPose();
        }

        stack.pushPose();
        InterpolatedBone bone = bones.get(bones.size() - 1);
        bone.getModelSpaceTransformMatrix(stack, 1);
        Vector4f point4 = new Vector4f(endPlacement.x(), endPlacement.y(), endPlacement.z(), 1);
        point4.transform(stack.last().pose());
        points.get(points.size() - 1).set(point4.x(), point4.y(), point4.z());

        stack.popPose();

        for (int i = 0; i < this.segmentLengths.size(); i++) {
            this.segmentLengths.set(i, VectorUtils.distance(points.get(i), points.get(i + 1)));
        }

        return points;
    }

    // updates bone transforms based off IK point locations
    private void updateBoneTransforms() {
        Vector3f perpendicular = points.get(points.size() - 1).copy();
        perpendicular.cross(poleTarget);
        perpendicular.normalize();
        perpendicular.mul(-1);

        Vector3f nextPoint;
        Vector3f point;

        for (int i = 0; i < bones.size(); i++) {
            InterpolatedBone bone = bones.get(i);

            point = points.get(i);
            nextPoint = this.points.get(i + 1);

            Vector3f forward = nextPoint.copy();
            forward.sub(point);
            forward.normalize();

            // todo: be smarter about this and take pole target into consideration. can't be fucked rn
            bone.setGlobalSpaceRotation(new Quaternionf().rotationTo(new Vector3f(0, -1, 0), forward));
        }
    }

    @Override
    // i don't actually use this.
    // todo: actually use this
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

    @Override
    public void renderDebugInfo(InterpolatedSkeleton skeleton, InterpolatedSkeletonParent parent, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.LINES);

        Vector3f x = this.points.get(this.points.size() - 1).copy();
        x.cross(this.poleTarget);
        x.normalize();
        x.mul(-1);

        Vector3f point;
        Vector3f pPoint;

        for (int i = 0; i < this.points.size(); i++) {
            float color1 = (float)i / (float)this.points.size();

            point = this.points.get(i);
            DebugRenderUtil.renderSphere(poseStack, vertexconsumer, 16, 0.2F, point.x(), point.y(), point.z(), color1, i == 0 ? 0.0F : color1, color1, 1.0F);

            if (i >= 1) {
                float color0 = (float)(i - 1) / (float)this.points.size();
                pPoint = this.points.get(i - 1);
                DebugRenderUtil.renderLine(poseStack, vertexconsumer, point.x(), point.y(), point.z(), pPoint.x(), pPoint.y(), pPoint.z(), color1, color1, color1, 1.0F, color0, color0, color0, 1.0F);
            }
        }

        point = this.target;
        DebugRenderUtil.renderSphere(poseStack, vertexconsumer, 16, 0.4F, point.x(), point.y(), point.z(), 1.0F, 0.0F, 0.0F, 1.0F);
        point = this.poleTarget;
        DebugRenderUtil.renderSphere(poseStack, vertexconsumer, 16, 0.4F, point.x(), point.y(), point.z(), 0.0F, 1.0F, 0.0F, 1.0F);
    }

    public enum InverseKinematicDirection {
        POSITIVE_X(Vector3f.XP), POSITIVE_Y(Vector3f.YP), POSITIVE_Z(Vector3f.ZP), NEGATIVE_X(Vector3f.XN), NEGATIVE_Y(Vector3f.YN), NEGATIVE_Z(Vector3f.ZN);

        Vector3f direction;
        InverseKinematicDirection(Vector3f direction) {
            this.direction = direction;
        }
    }
}
