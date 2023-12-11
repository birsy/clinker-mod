package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.world.level.interactable.manager.InteractableManager;
import net.minecraft.world.level.Level;

/**
 * Handles identifying and processing interactions with interactables.
 */
public class InteractionHandler {
    final protected InteractableManager manager;
    final protected Level level;

    public InteractionHandler(InteractableManager manager) {
        this.manager = manager;
        this.level = manager.level;
    }

    public void tick() {

    }
}
