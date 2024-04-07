package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerGameEvents {
    public static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(BuiltInRegistries.GAME_EVENT, Clinker.MOD_ID);

    public static final Supplier<GameEvent> GNOME_RESUPPLY_CALLOUT = GAME_EVENTS.register("gnome_resupply_callout", () -> new GameEvent(24));
}
