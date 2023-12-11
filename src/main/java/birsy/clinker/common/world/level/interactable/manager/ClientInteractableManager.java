package birsy.clinker.common.world.level.interactable.manager;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.interactable.ServerboundInteractableLoadChunkRequestPacket;
import birsy.clinker.common.world.level.interactable.Interactable;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class ClientInteractableManager extends InteractableManager {
    private final LongSortedSet requestedChunks = new LongAVLTreeSet();

    public ClientInteractableManager(ClientLevel level) {
        super(level);
    }

    @Override
    public void addInteractable(Interactable interactable) {
        this.storage.addInteractable(interactable);
    }

    @Override
    public void removeInteractable(Interactable interactable) {
        interactable.removed = true;
        this.storage.removeInteractable(interactable);
    }

    @Override
    public void loadChunk(LevelChunk chunk) {
        // send a request to the server for the interactables within a chunk
        ClinkerPacketHandler.sendToServer(new ServerboundInteractableLoadChunkRequestPacket(chunk));
        requestedChunks.add(chunk.getPos().toLong());
    }

    public void loadChunkFromPacket(ChunkPos pos, Collection<Interactable> interactables) {
        if (!requestedChunks.contains(pos.toLong())) return;
        for (Interactable interactable : interactables) {
            this.addInteractable(interactable);
        }
        requestedChunks.remove(pos.toLong());
    }

    @Override
    public void unloadChunk(LevelChunk chunk) {
        for (int y = chunk.getMinSection(); y < chunk.getMaxSection(); y++) {
            SectionPos pos = SectionPos.of(chunk.getPos(), y);
            this.storage.unloadSection(pos);
        }
    }
}
