package birsy.clinker.client.render.util;

import net.minecraft.util.math.MathHelper;

public class AnimFunctions {
    /**
     * Bobs a part up and down.
     *
     * @param box               the box to bob
     * @param speed             the speed of the bobbing
     * @param degree            the amount to bob
     * @param bounce            gives the bob a subtle bounce
     * @param limbSwingAmount   the amount the legs move
     * @param limbSwingSpeed    the speed the legs move
     * @param inverse           used to correct an error with some calculations.
     */
    public static void bob(BirsyModelRenderer box, float speed, float degree, boolean bounce, float limbSwingAmount, float limbSwingSpeed, boolean inverse) {
        float bob = (float) (Math.sin(limbSwingAmount * speed) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs((Math.sin(limbSwingAmount * (speed * 0.5F)) * limbSwingSpeed * degree));
        }
        if (inverse) {
            box.rotationPointY =+ box.defaultRotationPointY + bob;
        } else {
            box.rotationPointY =+ bob;
        }
    }

    /**
     * Rotates the given boxes to face a target.
     *
     * @param box             the box to face the target
     * @param netHeadYaw      the yaw of the target
     * @param headPitch       the pitch of the target
     * @param yawDivisor      the amount to divide the yaw by. good to make it the amount of parts.
     * @param pitchDivisor    the amount to divide the pitch by. good to make it the amount of parts.
     */
    public static void look(BirsyModelRenderer box, float netHeadYaw, float headPitch, float yawDivisor, float pitchDivisor) {
        box.rotateAngleY =+ (netHeadYaw * ((float)Math.PI / 180F))/yawDivisor;
        box.rotateAngleX =+ (headPitch * ((float)Math.PI / 180F))/pitchDivisor;
    }


    /**
     * Swings boxes. Good for walk cycles or wind effects.
     *
     * @param box              the box to swing
     * @param speed            the speed to swing this at
     * @param degree           the amount to rotate this by
     * @param invert           the animation's invertedness
     * @param offset           the offset of the swing
     * @param weight           the strength of the swing
     * @param swing            the swing rotation
     * @param limbSwingAmount  the swing amount
     * @param axis             the axis to rotate on
     */
    public static float swing(BirsyModelRenderer box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, AnimFunctions.Axis axis) {
        if (box != null) {
            switch (axis) {
                case X:
                    box.rotateAngleX =+ calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
                    break;
                case Y:
                    box.rotateAngleY =+ calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
                    break;
                case Z:
                    box.rotateAngleZ =+ calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
                    break;
                default:
                    box.rotateAngleX =+ calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
                    throw new RuntimeException(box + "had no axis assigned for their swing. Defaulting to X!");
            }

        }
        return calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
    }

    public static void swingLimbs(BirsyModelRenderer left, BirsyModelRenderer right, float speed, float degree, float offset, float weight, float swing, float limbSwingAmount)
    {
        swing(left, speed, degree, true, offset, weight, swing, limbSwingAmount, AnimFunctions.Axis.X);
        swing(right, speed, degree, false, offset, weight, swing, limbSwingAmount, AnimFunctions.Axis.X);
    }

    public static void applyJointRotation(BirsyModelRenderer joint, int depth, boolean xAxis, boolean yAxis, boolean zAxis) {
        applyJointRotation(joint, joint, depth, xAxis, yAxis, zAxis);
    }

    private static void applyJointRotation(BirsyModelRenderer baseJoint, BirsyModelRenderer searchJoint, int depth, boolean xAxis, boolean yAxis, boolean zAxis) {
        if (depth == -1) {
            BirsyModelRenderer parent = searchJoint.getParent();
            if (parent != null) {
                baseJoint.scaleX *= xAxis ? 1.0F / parent.scaleX : 1.0F;
                baseJoint.scaleY *= yAxis ? 1.0F / parent.scaleY : 1.0F;
                baseJoint.scaleZ *= zAxis ? 1.0F / parent.scaleZ : 1.0F;

                baseJoint.rotateAngleX -= xAxis ? parent.rotateAngleX : 0.0F;
                baseJoint.rotateAngleY -= yAxis ? parent.rotateAngleY : 0.0F;
                baseJoint.rotateAngleZ -= zAxis ? parent.rotateAngleZ : 0.0F;

                applyJointRotation(baseJoint, parent, depth, xAxis, yAxis, zAxis);
            }
        } else {
            if (depth > 0) {
                BirsyModelRenderer parent = searchJoint.getParent();
                if (parent != null) {
                    baseJoint.scaleX *= 1.0F / parent.scaleX;
                    baseJoint.scaleY *= 1.0F / parent.scaleY;
                    baseJoint.scaleZ *= 1.0F / parent.scaleZ;

                    baseJoint.rotateAngleX -= parent.rotateAngleX;
                    baseJoint.rotateAngleY -= parent.rotateAngleY;
                    baseJoint.rotateAngleZ -= parent.rotateAngleZ;

                    depth--;
                    applyJointRotation(baseJoint, parent, depth, xAxis, yAxis, zAxis);
                }
            }
        }
    }

    public static void resetParts(BirsyModelRenderer... boxes) {
        for (BirsyModelRenderer modelRenderer : boxes) {
            modelRenderer.scaleX = modelRenderer.defaultScaleX;
            modelRenderer.scaleY = modelRenderer.defaultScaleY;
            modelRenderer.scaleZ = modelRenderer.defaultScaleZ;

            modelRenderer.rotateAngleX = modelRenderer.defaultRotateAngleX;
            modelRenderer.rotateAngleY = modelRenderer.defaultRotateAngleY;
            modelRenderer.rotateAngleZ = modelRenderer.defaultRotateAngleZ;

            modelRenderer.rotationPointX = modelRenderer.defaultRotationPointX;
            modelRenderer.rotationPointY = modelRenderer.defaultRotationPointY;
            modelRenderer.rotationPointZ = modelRenderer.defaultRotationPointZ;
        }
    }

    private static float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
        float rotation = (MathHelper.cos(f * (speed) + offset) * (degree) * f1) + (weight * f1);
        return invert ? -rotation : rotation;
    }

    public enum Axis {
        X,
        Y,
        Z
    }
}
