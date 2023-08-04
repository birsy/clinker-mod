package birsy.clinker.common.data;

import birsy.clinker.core.Clinker;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClinkerSavedChunkData {
    public static void OnChunkSave(ChunkDataEvent.Save event) {
    }

    public static void OnChunkLoad(ChunkDataEvent.Load event) {

    }
}
