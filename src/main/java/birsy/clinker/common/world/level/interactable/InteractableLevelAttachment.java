package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import birsy.clinker.common.world.level.interactable.manager.InteractableManager;
import birsy.clinker.common.world.level.interactable.manager.ServerInteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;

/**
 * Hooks an InteractableManager to a particular level.
 */
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractableLevelAttachment {
    private static HashMap<Level, InteractableManager> levelToInteractableManager = new HashMap<>();
    public static HashMap<ResourceKey<Level>, ServerLevel> dimensionToServerLevel = new HashMap<>();

    public static InteractableManager getInteractableManagerForLevel(Level level) {
//        if (!levelToInteractableManager.containsKey(level)) throw new RuntimeException("Interactable Manager does not exist for Level " + level.dimension().location() + " !");
//        return levelToInteractableManager.get(level);
        return null;
    }

    public static void attachManagerToLevel(Level level, InteractableManager manager) {
//        levelToInteractableManager.put(level, manager);
//        if (level instanceof ServerLevel slvl) {
//            dimensionToServerLevel.put(slvl.dimension(), slvl);
//        }
    }

    @SubscribeEvent
    public static void load(LevelEvent.Load event) {
//        if (event.getLevel() instanceof ClientLevel clientLevel) {
//            attachManagerToLevel(clientLevel, new ClientInteractableManager(clientLevel));
//        }
//        if (event.getLevel() instanceof ServerLevel serverLevel) {
//            attachManagerToLevel(serverLevel, new ServerInteractableManager(serverLevel));
//        }
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
//        if (levelToInteractableManager.containsKey(event.getLevel())) {
//            levelToInteractableManager.get(event.getLevel()).clear();
//            levelToInteractableManager.remove(event.getLevel());
//        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//            //if (levelToInteractableManager.containsKey(event.level)) levelToInteractableManager.get(event.level).tick();
//        }
    }

    @SubscribeEvent
    public static void chunkUnload(ChunkEvent.Unload event) {
//        if (event.getLevel() instanceof Level level) {
//            if (!levelToInteractableManager.containsKey(event.getLevel())) return;
//            getInteractableManagerForLevel(level).unloadChunk(level.getChunk(event.getChunk().getPos().x, event.getChunk().getPos().z));
//        }
    }

    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event) {
//        if (event.getLevel() instanceof Level level) {
//            if (!levelToInteractableManager.containsKey(event.getLevel())) return;
//            getInteractableManagerForLevel(level).loadChunk(level.getChunk(event.getChunk().getPos().x, event.getChunk().getPos().z));
//        }
    }
}
