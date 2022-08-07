package birsy.clinker.client.render.entity.model.base;

import java.util.ArrayList;
import java.util.List;

/*
 * Manages animation of models.
 * TODO: basic keyframed animation. can use linear interpolation for just about everything, as the ComplexModelParts automatically calculate some basic follow-through. just need something that can go through poses!
 */
public class DynamicModel {
    List<DynamicModelPart> parts;
    public final float textureWidth;
    public final float textureHeight;
    public DynamicModel(float textureWidth, float textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.parts = new ArrayList<>();
    }
    
    public void addPart(DynamicModelPart part) {
        this.parts.add(part);
        part.textureWidth = this.textureWidth;
        part.textureHeight = this.textureHeight;
    }
    
    public void resetPose() {
        for (DynamicModelPart part : this.parts) {
            part.resetPose();
        }
    }

    public void setDynamics(float frequency, float damping, float response) {
        for (DynamicModelPart part : this.parts) {
            part.setDynamics(frequency, damping, response);
        }
    }
}
