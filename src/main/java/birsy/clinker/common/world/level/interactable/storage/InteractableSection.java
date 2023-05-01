package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class InteractableSection {
    private final InteractableLookup lookup = new InteractableLookup();

    public InteractableSection() {}

    public boolean isEmpty() {
        return this.lookup.isEmpty();
    }

    public void forInteractablesInBounds(AABB pBounds, Consumer<Interactable> pConsumer) {
        for(Interactable t : this.lookup.getAllInteractables()) {
            if (t.shape.getBounds().intersects(pBounds)) {
                pConsumer.accept(t);
            }
        }
    }

    public Stream<Interactable> getInteractables() {
        return this.lookup.stream();
    }

    public void add(Interactable interactable) {
        this.lookup.add(interactable);
    }

    public void remove(Interactable interactable) {
        this.lookup.remove(interactable);
    }

    public void clear() {
        this.lookup.clear();
    }
}
