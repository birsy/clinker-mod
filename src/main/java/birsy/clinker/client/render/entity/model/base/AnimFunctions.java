package birsy.clinker.client.render.entity.model.base;

import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

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
     */
    public static void bob(DynamicModelPart box, float speed, float degree, boolean bounce, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F)) * limbSwingSpeed * degree);
        }

        box.y += bob;
    }

    public static void bob(Bone box, float speed, float degree, boolean bounce, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F)) * limbSwingSpeed * degree);
        }

        box.y += bob;
    }

    public static void bob(DynamicModelPart box, float speed, float degree, boolean bounce, float offset, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed + offset) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F) + offset) * limbSwingSpeed * degree);
        }

        box.y += bob;
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
    public static void look(DynamicModelPart box, float netHeadYaw, float headPitch, float yawDivisor, float pitchDivisor) {
        box.yRot += (netHeadYaw * ((float)Math.PI / 180F))/yawDivisor;
        box.xRot += (headPitch * ((float)Math.PI / 180F))/pitchDivisor;
    }

    public static void look(Bone box, float netHeadYaw, float headPitch, float yawDivisor, float pitchDivisor) {
        box.rotate( (netHeadYaw * (Mth.PI / 180F)) / yawDivisor, Direction.Axis.Y);
        box.rotate( (headPitch * (Mth.PI / 180F)) / pitchDivisor, Direction.Axis.X);
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
    public static float swing(DynamicModelPart box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, RotAxis axis) {
        float rotation = calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        if (box != null) {
            switch (axis) {
                case X:
                    box.xRot += rotation;
                    break;
                case Y:
                    box.yRot += rotation;
                    break;
                case Z:
                    box.zRot += rotation;
                    break;
                default:
                    box.xRot += rotation;
                    throw new RuntimeException(box + "had no axis assigned for their swing. Defaulting to X!");
            }

        }
        return rotation;
    }

    public static float swing(Bone bone, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, Direction.Axis axis) {
        float rotation = calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        bone.rotate(rotation, axis);
        return rotation;
    }

    public static float clampedSwing(Bone bone, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, boolean clampNegative, Direction.Axis axis) {
        float rotation = calculateRotationClamped(speed, degree, invert, offset, weight, swing, limbSwingAmount, clampNegative);
        bone.rotate(rotation, axis);
        return rotation;
    }

    public static void swingLimbs(Bone left, Bone right, float speed, float degree, float offset, float weight, float swing, float limbSwingAmount) {
        swing(left, speed, degree, true, offset, weight, swing, limbSwingAmount, Direction.Axis.X);
        swing(right, speed, degree, false, offset, weight, swing, limbSwingAmount, Direction.Axis.X);
    }


    public static float clampedSwing(DynamicModelPart box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, boolean clampNegative, RotAxis axis) {
        float rotation = calculateRotationClamped(speed, degree, invert, offset, weight, swing, limbSwingAmount, clampNegative);
        //;
        if (box != null) {
            switch (axis) {
                case X:
                    box.xRot += rotation;
                    break;
                case Y:
                    box.yRot += rotation;
                    break;
                case Z:
                    box.zRot += rotation;
                    break;
                default:
                    box.xRot += rotation;
                    throw new RuntimeException(box + "had no axis assigned for their swing. Defaulting to X!");
            }

        }
        return rotation;
    }

    public static void swingLimbs(DynamicModelPart left, DynamicModelPart right, float speed, float degree, float offset, float weight, float swing, float limbSwingAmount)
    {
        swing(left, speed, degree, true, offset, weight, swing, limbSwingAmount, RotAxis.X);
        swing(right, speed, degree, false, offset, weight, swing, limbSwingAmount, RotAxis.X);
    }

    public static void setScale(DynamicModelPart part, float scale) {
        part.setScale(scale, scale, scale);
    }

    public static void cancelRotation(DynamicModelPart part, boolean x, boolean y, boolean z) {
        cancelRotation(part, x, y, z, Integer.MAX_VALUE);
    }

    public static void cancelRotation(DynamicModelPart part, boolean x, boolean y, boolean z, int depth) {
        cancelRotationIterative(part, part, x, y, z, depth);
    }

    public static void cancelRotationIterative(DynamicModelPart originalPart, DynamicModelPart part, boolean x, boolean y, boolean z, int depth) {
        if (originalPart != part) {
            originalPart.xRot -= x ? part.xRot : 0.0F;
            originalPart.yRot -= y ? part.yRot : 0.0F;
            originalPart.zRot -= z ? part.zRot : 0.0F;
        }

        DynamicModelPart parent = part.parent;
        if (parent != null && depth > 0) {
            cancelRotationIterative(originalPart, parent, x, y, z, depth - 1);
        }
    }

    public static void getGlobalTransForm(DynamicModelPart part, PoseStack matrixStack) {
        DynamicModelPart parent = part.parent;
        if (parent != null) {
            getGlobalTransForm(parent, matrixStack);
        }
        part.translateAndRotateAndScale(matrixStack);
    }
    
    /**
     * Bobs a part up and down.
     *
     * @param box               the box to bob
     * @param speed             the speed of the bobbing
     * @param degree            the amount to bob
     * @param bounce            gives the bob a subtle bounce
     * @param limbSwingAmount   the amount the legs move
     * @param limbSwingSpeed    the speed the legs move
     */
    public static void bob(ModelPart box, float speed, float degree, boolean bounce, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F)) * limbSwingSpeed * degree);
        }

        box.y += bob;
    }

    public static void bob(ModelPart box, float speed, float degree, boolean bounce, float offset, float limbSwingAmount, float limbSwingSpeed) {
        float bob = (float) (Math.sin(limbSwingAmount * speed + offset) * limbSwingSpeed * degree - limbSwingSpeed * degree);
        if (bounce) {
            bob = (float) -Math.abs(Math.sin(limbSwingAmount * (speed * 0.5F) + offset) * limbSwingSpeed * degree);
        }

        box.y += bob;
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
    public static void look(ModelPart box, float netHeadYaw, float headPitch, float yawDivisor, float pitchDivisor) {
        box.yRot += (netHeadYaw * ((float)Math.PI / 180F))/yawDivisor;
        box.xRot += (headPitch * ((float)Math.PI / 180F))/pitchDivisor;
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
    public static float swing(ModelPart box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, RotAxis axis) {
        float rotation = calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        if (box != null) {
            switch (axis) {
                case X:
                    box.xRot += rotation;
                    break;
                case Y:
                    box.yRot += rotation;
                    break;
                case Z:
                    box.zRot += rotation;
                    break;
                default:
                    box.xRot += rotation;
                    throw new RuntimeException(box + "had no axis assigned for their swing. Defaulting to X!");
            }

        }
        return rotation;
    }

    public static float clampedSwing(ModelPart box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, boolean clampNegative, RotAxis axis) {
        float rotation = calculateRotationClamped(speed, degree, invert, offset, weight, swing, limbSwingAmount, clampNegative);
        //;
        if (box != null) {
            switch (axis) {
                case X:
                    box.xRot += rotation;
                    break;
                case Y:
                    box.yRot += rotation;
                    break;
                case Z:
                    box.zRot += rotation;
                    break;
                default:
                    box.xRot += rotation;
                    throw new RuntimeException(box + "had no axis assigned for their swing. Defaulting to X!");
            }

        }
        return rotation;
    }

    public static void swingLimbs(ModelPart left, ModelPart right, float speed, float degree, float offset, float weight, float swing, float limbSwingAmount)
    {
        swing(left, speed, degree, true, offset, weight, swing, limbSwingAmount, RotAxis.X);
        swing(right, speed, degree, false, offset, weight, swing, limbSwingAmount, RotAxis.X);
    }

    private static float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
        float rotation = (Mth.cos(f * (speed) + offset) * (degree) * f1) + (weight * f1);
        return invert ? -rotation : rotation;
    }


    private static float calculateRotationClamped(float speed, float degree, boolean invert, float offset, float weight, float f, float f1, boolean clampNegative) {
        float wave = clampNegative ? MathUtils.smoothMax(Mth.sin(f * (speed) + offset), 0, 0.2F) : MathUtils.smoothMin(Mth.sin(f * (speed) + offset), 0, 0.2F);
        float rotation = (wave * degree * f1) + (weight * f1);
        return invert ? -rotation : rotation;
    }

    public enum RotAxis {
        X,
        Y,
        Z
    }

    public static void setOffset(ModelPart part, float xOffset, float yOffset, float zOffset) {
        part.setPos(xOffset, yOffset, zOffset);
    }

    public static void setOffsetAndRotation(ModelPart part, float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation) {
        part.setPos(xOffset, yOffset, zOffset);
        part.setRotation(xRotation, yRotation, zRotation);
    }

    public static void setScale(CappinModelPart part, float scale) {
        part.setScale(scale, scale, scale);
    }


    public static void resetScale(CappinModelPart part) {
        part.setScale(1.0F, 1.0F, 1.0F);
    }


    //Prevents mobs animations from appearing synced on world load. Makes creatures look slightly more natural.
    public static void desyncAnimations(Entity entity, float ageInTicks) {
        ageInTicks += entity.getId();
    }

    public static void cancelRotation(CappinModelPart part, boolean x, boolean y, boolean z) {
        cancelRotation(part, x, y, z, Integer.MAX_VALUE);
    }

    public static void cancelRotation(CappinModelPart part, boolean x, boolean y, boolean z, int depth) {
        cancelRotationIterative(part, part, x, y, z, depth);
    }

    public static void cancelRotationIterative(CappinModelPart originalPart, CappinModelPart part, boolean x, boolean y, boolean z, int depth) {
        if (originalPart != part) {
            originalPart.xRot -= x ? part.xRot : 0.0F;
            originalPart.yRot -= y ? part.yRot : 0.0F;
            originalPart.zRot -= z ? part.zRot : 0.0F;
        }

        CappinModelPart parent = part.parent;
        if (parent != null && depth > 0) {
            cancelRotationIterative(originalPart, parent, x, y, z, depth - 1);
        }
    }

    public static void getGlobalTransForm(CappinModelPart part, PoseStack matrixStack) {
        CappinModelPart parent = part.parent;
        if (parent != null) {
            getGlobalTransForm(parent, matrixStack);
        }
        part.translateAndRotate(matrixStack);
    }

    public static void getGlobalTransFormI(CappinModelPart part, PoseStack matrixStack) {
        CappinModelPart parent = part.parent;
        if (parent != null) {
            getGlobalTransFormI(parent, matrixStack);
        }

        matrixStack.translate(part.x / 16.0F, part.y / 16.0F, part.z / 16.0F);

        if (part.zRot != 0.0F) {
            matrixStack.mulPose(Axis.ZP.rotation(part.zRot));
        }

        if (part.yRot != 0.0F) {
            matrixStack.mulPose(Axis.YP.rotation(part.yRot));
        }

        if (part.xRot != 0.0F) {
            matrixStack.mulPose(Axis.XP.rotation(-part.xRot));
        }

        matrixStack.scale(part.xScale, part.yScale, part.zScale);
    }

        /**
         * Finds the solution an IK system utilizing basic a FABRIK solver.
         * @param points
         * @param target
         * @param maxIterations
         * @param minAcceptableDst
         * @return A location array (index 0), and a rotation array (index 1)
         */
    public static Vec3[][] solveIK (Vec3[] points, Vec3 target, int maxIterations, double minAcceptableDst) {
        //If the max iterations isn't even, this corrects it to be so. Makes sure I don't get a backwards array!
        maxIterations = maxIterations % 2 == 0 ? maxIterations : maxIterations + 1;
        Vec3 origin = points[0];

        double[] segmentLengths = new double[points.length - 1];
        for (int i = 0; i < segmentLengths.length; i++) {
            segmentLengths[i] = (points[i + 1].subtract(points[i])).length();
        }

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            boolean startingFromTarget = iteration % 2 == 0;

            //Reverses arrays to alternate between forward and backward passes.
            points = (Vec3[]) MathUtils.reverseArray(points);
            segmentLengths = (double[]) MathUtils.reverseArray(segmentLengths);
            points[0] = startingFromTarget ? target : origin;

            //Constrains lengths
            for (int i = 1; i < points.length; i++) {
                Vec3 dir = (points[i].subtract(points[i - 1])).normalize();
                points[i] = points[i-1].add(dir).multiply(segmentLengths[i - 1], segmentLengths[i - 1], segmentLengths[i - 1]);
            }

            double dstToTarget = (points[points.length - 1].subtract(target)).length();
            if (!startingFromTarget && dstToTarget <= minAcceptableDst) {
                break;
            }
        }


        Vec3[][] returnArray = new Vec3[points.length][2];
        for (int i = 0; i < points.length; i++) {
            returnArray[i][0] = points[i];
            returnArray[i][1] = i != points.length ? points[i].subtract(points[i + 1]).normalize() : points[i].subtract(target).normalize();
        }

        return returnArray;
    }

    public static Vec3 getWorldPos(Entity entity, CappinModelPart part, float partialTick) {
        return Vec3.ZERO;

    }
}
