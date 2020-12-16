package birsy.clinker.client.render.model;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BirsyBaseModel<T extends Entity> extends EntityModel<T>
{
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
	public void bob(BirsyModelRenderer box, float speed, float degree, boolean bounce, float limbSwingAmount, float limbSwingSpeed, boolean inverse) {
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
	 * Randomly rotates a part based on the entity's ID.
	 * 
	 * @param box         the box to rotate
	 * @param entityIn    the entity
	 * @param minimum     the minimum of the variation.
	 * @param maximum     the maximum of the variation.
	 * @param axis        the axis of the variation.
	 */
	public void rotVar(BirsyModelRenderer box, T entityIn, float minimum, float range, Axis axis)
	{	
        switch(axis) {
    	case X:
    		box.rotateAngleX =+ randomSine(entityIn.getEntityId(), minimum, range);
    		break;
    	case Y:
    		box.rotateAngleY =+ randomSine(entityIn.getEntityId(), minimum, range);
    		break;
    	case Z:
    		box.rotateAngleZ =+ randomSine(entityIn.getEntityId(), minimum, range);
    		break;
    	default: 
    		box.rotateAngleX =+ randomSine(entityIn.getEntityId(), minimum, range);
    		throw new RuntimeException(entityIn + " had no axis assigned for thier " + box + "'s rotation variation. Defaulting to X!");
        }
	}
	
	/**
	 * Randomly moves a part based on the entity's ID.
	 * 
	 * @param box         the box to rotate
	 * @param entityIn    the entity
	 * @param minimum     the minimum of the variation.
	 * @param maximum     the maximum of the variation.
	 * @param axis        the axis of the variation.
	 */
	public void locVar(BirsyModelRenderer box, T entityIn, float minimum, float range, Axis axis)
	{	
        switch(axis) {
    	case X:
    		box.rotationPointX =+ randomSine(entityIn.getEntityId(), minimum, range);
    		break;
    	case Y:
    		box.rotationPointY =+ randomSine(entityIn.getEntityId(), minimum, range);
    		break;
    	case Z:
    		box.rotationPointZ =+ randomSine(entityIn.getEntityId(), minimum, range);
    		break;
    	default: 
    		box.rotationPointX =+ randomSine(entityIn.getEntityId(), minimum, range);
    		throw new RuntimeException(entityIn + " had no axis assigned for thier " + box + "'s location variation. Defaulting to X!");
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
	public void look(BirsyModelRenderer box, float netHeadYaw, float headPitch, float yawDivisor, float pitchDivisor) {
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
	public void swing(BirsyModelRenderer box, float speed, float degree, boolean invert, float offset, float weight, float swing, float limbSwingAmount, Axis axis) {
        switch(axis) {
        	case X:
        		box.rotateAngleX =+ this.calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        		break;
        	case Y:
        		box.rotateAngleY =+ this.calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        		break;
        	case Z:
        		box.rotateAngleZ =+ this.calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        		break;
        	default: 
        		box.rotateAngleX =+ this.calculateRotation(speed, degree, invert, offset, weight, swing, limbSwingAmount);
        		throw new RuntimeException(box + "had no axis assigned for thier swing. Defaulting to X!");
        }
    }
	
	public void swingLimbs(BirsyModelRenderer left, BirsyModelRenderer right, float speed, float degree, float offset, float weight, float swing, float limbSwingAmount)
	{
		swing(left, speed, degree, true, offset, weight, swing, limbSwingAmount, Axis.X);
		swing(right, speed, degree, false, offset, weight, swing, limbSwingAmount, Axis.X);
	}

	public void inverseKinematicsBase(BirsyModelRenderer Joint0, BirsyModelRenderer Joint1, Vector3d Target)
	{
		float length0 = (float) Joint0.defaultModelRendererPosition.distanceTo(Joint1.defaultModelRendererPosition);
		float length1 = (float) Joint1.defaultModelRendererPosition.distanceTo(Target);
		
		float jointAngle0;
	    float jointAngle1;
	 
	    float length2 = (float) Joint0.defaultModelRendererPosition.distanceTo(Target);
	 
	    Vector3d diff = Target.subtract(Joint0.defaultModelRendererPosition);
	    float atan = (float) Math.atan2(diff.y, diff.x);

	    if (length0 + length1 <= length2)
	    {
	        jointAngle0 = atan;
	        jointAngle1 = 0f;
	    }
	    else
	    {
	        float cosAngle0 = ((length2 * length2) + (length0 * length0) - (length1 * length1)) / (2 * length2 * length0);
	        float angle0 = (float) Math.acos(cosAngle0);
	 
	        float cosAngle1 = ((length1 * length1) + (length0 * length0) - (length2 * length2)) / (2 * length1 * length0);
	        float angle1 = (float) Math.acos(cosAngle1);
	 
	        // So they work in Unity reference frame
	        jointAngle0 = atan - angle0;
	        jointAngle1 = 180f - angle1;
	    }
	    
	    Joint0.rotateAngleX = jointAngle0;
	    Joint1.rotateAngleX = jointAngle1;
	}
	
	public Vector3d sideSideSide (float lengthA, float lengthB, float lengthC)
	{
		float cosA = ((lengthB*lengthB) + (lengthC*lengthC) - (lengthA*lengthA)) / (2 * lengthB * lengthC);
		float cosB = ((lengthC*lengthC) + (lengthA*lengthA) - (lengthB*lengthB)) / (2 * lengthC * lengthA);
		float cosC = ((lengthA*lengthA) + (lengthB*lengthB) - (lengthC*lengthC)) / (2 * lengthA * lengthB);
		
		float angleA = (float) Math.asin(cosA);
		float angleB = (float) Math.asin(cosB);
		float angleC = (float) Math.asin(cosC);
		
		Vector3d angles = new Vector3d(angleA, angleB, angleC);
		
		return angles;
	}
	
	public void resetParts(BirsyModelRenderer... boxes) {
		for (BirsyModelRenderer modelRenderer : boxes) {
			modelRenderer.rotateAngleX = modelRenderer.defaultRotateAngleX;
			modelRenderer.rotateAngleY = modelRenderer.defaultRotateAngleY;
			modelRenderer.rotateAngleZ = modelRenderer.defaultRotateAngleZ;

			modelRenderer.rotationPointX = modelRenderer.defaultRotationPointX;
			modelRenderer.rotationPointY = modelRenderer.defaultRotationPointY;
			modelRenderer.rotationPointZ = modelRenderer.defaultRotationPointZ;
        }
	}
	
	public float nextFloatSeed(float seed, float minimum, float maximum) {
		return minimum >= maximum ? minimum : seed * (maximum - minimum) + minimum;
	}
	
	public float randomSine(float seed, float range, float minimum) {
		return (float) Math.max(MathHelper.sin(seed) * range, minimum);
	}
	
    public void setRotateAngle(BirsyModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
        
        modelRenderer.defaultRotateAngleX = x;
        modelRenderer.defaultRotateAngleY = y;
        modelRenderer.defaultRotateAngleZ = z;
    }
    
    private float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
        float rotation = (MathHelper.cos(f * (speed) + offset) * (degree) * f1) + (weight * f1);
        return invert ? -rotation : rotation;
    }
    
    public enum Axis {
    	X,
    	Y,
    	Z;
    }
}
