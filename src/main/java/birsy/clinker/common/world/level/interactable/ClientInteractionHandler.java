package birsy.clinker.common.world.level.interactable;

import net.minecraft.client.multiplayer.ClientLevel;

import java.util.Optional;

public class ClientInteractionHandler extends InteractionHandler {
    public static Optional<Interactable> seenInteractable = Optional.empty();

    public ClientInteractionHandler(ClientLevel level) {
        super(level);
    }

    @Override
    public void tick() {
        super.tick();
    }

    /**
     * Called every frame.
     */
    public void update() {

    }
}
