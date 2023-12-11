package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.world.level.interactable.manager.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

import java.util.HashMap;

/**
 * Hooks an InteractableManager to a particular level.
 */
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractableAttachment {
    private static HashMap<Level, InteractableManager> levelToInteractableManager = new HashMap<>();
    public static HashMap<ResourceKey<Level>, ServerLevel> dimensionToServerLevel = new HashMap<>();

    public static InteractableManager getInteractableManagerForLevel(Level level) {
        if (!levelToInteractableManager.containsKey(level)) throw new RuntimeException("Interactable Manager does not exist for Level " + level.dimension().location() + " !");
        return levelToInteractableManager.get(level);
    }

    public static void attachManagerToLevel(Level level, InteractableManager manager) {
        levelToInteractableManager.put(level, manager);
        if (level instanceof ServerLevel slvl) {
            dimensionToServerLevel.put(slvl.dimension(), slvl);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            levelToInteractableManager.get(event.level).tick();
        }
    }
}
