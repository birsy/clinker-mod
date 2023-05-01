package birsy.clinker.common.world.level.interactable;

import java.util.List;

public interface InteractableParent {
    /**
     * Gets all interactables that this object has created.
     * @return A list of all interactables that this object has created.
     */
    List<Interactable> getChildInteractables();

    /**
     * Clears interactables that this object has created.
     */
    void clearChildren();

    /**
     * Called when the object is destroyed. Marks all child interactables to be cleared next tick.
     */
    default void remove() {
        List<Interactable> children = this.getChildInteractables();
        for (Interactable child : children) {
            child.markForRemoval();
        }

        this.clearChildren();
    }

    default void addChild(Interactable interactable) {
        interactable.setParent(this);
        this.getChildInteractables().add(interactable);
    }
}
