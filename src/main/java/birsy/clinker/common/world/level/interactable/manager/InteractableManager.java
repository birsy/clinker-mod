package birsy.clinker.common.world.level.interactable.manager;

import birsy.clinker.common.world.level.interactable.ClientInteractionHandler;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractionHandler;
import birsy.clinker.common.world.level.interactable.storage.InteractableStorage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.UUID;

/**
 * Manages, syncs, etc, interactables within a single level.
 * Actions preformed on the serverside are automatically synced to the client, unless {@link Interactable.shouldSync} is false.
 */
public abstract class InteractableManager {
    protected InteractableStorage storage;
    protected InteractionHandler interactionHandler;
    protected final Level level;

    public InteractableManager(Level level) {
        this.storage = new InteractableStorage();
        this.interactionHandler = level instanceof ClientLevel ? new ClientInteractionHandler((ClientLevel) level) : new InteractionHandler(level);
        this.level = level;
    }

    /**
     * Adds an interactable to the level.
     * Automatically synced to the client, when done on the serverside.
     * @param interactable The interactable to add.
     */
    public abstract void addInteractable(Interactable interactable);

    /**
     * Removes an interactable from the scene. Automatically syncs to the client, when done on serverside.
     * @param interactable The interactable to remove.
     */
    public abstract void removeInteractable(Interactable interactable);

    public Interactable getInteractable(UUID id) {
        return this.storage.getInteractableFromUUID(id);
    }

    public abstract void loadChunk(LevelChunk chunk);

    public abstract void unloadChunk(LevelChunk chunk);

    public void tick() {
        this.interactionHandler.tick();
        storage.reorganize();
        for (Interactable interactable : storage.getInteractables()) {
            interactable.tick();
            interactable.markedDirty = false;
        }
    }
}
