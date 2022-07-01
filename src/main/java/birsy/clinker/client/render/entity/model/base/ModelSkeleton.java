package birsy.clinker.client.render.entity.model.base;

import java.util.ArrayList;
import java.util.List;

/*
 * Manages animation of models.
 * TODO: basic keyframed animation. can use linear interpolation for just about everything, as the ComplexModelParts automatically calculate some basic follow-through. just need something that can go through poses!
 */
public class ModelSkeleton {
    List<ComplexModelPart> parts;
    
    public ModelSkeleton() {
        this.parts = new ArrayList<>();
    }
    
    public void addPart(ComplexModelPart part) {
        this.parts.add(part);
    }
    
    public void resetPose() {
        for (ComplexModelPart part : this.parts) {
            part.resetPose();
        }
    }
}
