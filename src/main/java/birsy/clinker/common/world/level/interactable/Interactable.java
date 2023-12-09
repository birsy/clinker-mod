package birsy.clinker.common.world.level.interactable;


import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.UUID;

public abstract class Interactable {
    protected Vector3d position, previousPosition;
    protected Quaterniond orientation, previousOrientation;
    public final UUID id;

    public Interactable(Vector3d position, Quaterniond orientation) {
        this(UUID.randomUUID());
        this.position = position;
        this.previousPosition = new Vector3d(position);
        this.orientation = orientation;
        this.previousOrientation = new Quaterniond(orientation);
    }

    public Interactable(UUID id) {
        this.id = id;
    }
}
