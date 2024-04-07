package birsy.clinker.common.world.level.interactable.manager;

import birsy.clinker.common.world.level.interactable.ClientInteractionHandler;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractionHandler;
import birsy.clinker.common.world.level.interactable.storage.InteractableStorage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Manages, syncs, etc, interactables within a single level.
 * Actions preformed on the serverside are automatically synced to the client, unless {@link Interactable#shouldSync} is false.
 */
public abstract class InteractableManager {
    public InteractableStorage storage;
    public InteractionHandler interactionHandler;
    public final Level level;

    public InteractableManager(Level level) {
        this.storage = new InteractableStorage();
        this.interactionHandler = new InteractionHandler(this);
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

    public Collection<Interactable> getInteractablesInBounds(AABB boundingBox) {
        int minX = SectionPos.posToSectionCoord(boundingBox.minX - 16.0D);
        int minY = SectionPos.posToSectionCoord(boundingBox.minY - 16.0D);
        int minZ = SectionPos.posToSectionCoord(boundingBox.minZ - 16.0D);
        int maxX = SectionPos.posToSectionCoord(boundingBox.maxX + 16.0D);
        int maxY = SectionPos.posToSectionCoord(boundingBox.maxY + 16.0D);
        int maxZ = SectionPos.posToSectionCoord(boundingBox.maxZ + 16.0D);

        Collection<Interactable> list = new ArrayList<>();
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    long sectionId = SectionPos.asLong(x, y, z);
                    SectionPos pos = SectionPos.of(sectionId);
                    if (storage.sectionContainsInteractables(pos)) {
                        list.addAll(storage.getInteractablesInSection(pos).stream().filter(interactable -> boundingBox.intersects(interactable.getBounds())).toList());
                    }
                }
            }
        }
        return list;
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

    public void clear() {
        this.storage.clear();
    }
}
