package birsy.clinker.common.world.level.interactable.manager;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.interactable.*;
import birsy.clinker.common.world.level.interactable.Interactable;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Collection;

public class ServerInteractableManager extends InteractableManager {
    public ServerInteractableManager(ServerLevel level) {
        super(level);
    }

    @Override
    public void addInteractable(Interactable interactable) {
        this.storage.addInteractable(interactable);

        // tell all the clients to add this interactable, unless it shouldn't sync.
        if (!interactable.shouldSync) return;
        ChunkPos pos = interactable.getSectionPos().chunk();
        ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunk(pos.x, pos.z), new ClientboundInteractableAddPacket(interactable));
    }

    @Override
    public void removeInteractable(Interactable interactable) {
        interactable.removed = true;
        this.storage.removeInteractable(interactable);

        // tell all the clients to remove this interactable, unless it shouldn't sync.
        if (!interactable.shouldSync) return;
        ChunkPos pos = interactable.getSectionPos().chunk();
        ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunk(pos.x, pos.z), new ClientboundInteractableRemovePacket(interactable));
    }

    @Override
    public void loadChunk(LevelChunk chunk) {
        // interactables aren't serialized, so i don't have to load anything from the chunk on the serverside.
    }

    @Override
    public void tick() {
        this.interactionHandler.tick();
        storage.reorganize();
        for (Interactable interactable : storage.getInteractables()) {
            interactable.tick();
            if (!interactable.markedDirty) return;
            if (!interactable.shouldSync) return;
            ChunkPos pos = interactable.getSectionPos().chunk();
            ClinkerPacketHandler.sendToClientsTrackingChunk(this.level.getChunk(pos.x, pos.z), new ClientboundInteractableUpdatePacket(interactable));
            interactable.markedDirty = false;
        }
    }

    @Override
    public void unloadChunk(LevelChunk chunk) {
        for (int y = chunk.getMinSection(); y < chunk.getMaxSection(); y++) {
            SectionPos pos = SectionPos.of(chunk.getPos(), y);
            this.storage.unloadSection(pos);
        }
        // tell all clients who have that chunk loaded to unload that chunk...
        ClinkerPacketHandler.sendToClientsTrackingChunk(chunk, new ClientboundInteractableUnloadChunkPacket(chunk.getPos()));
    }

    public void loadChunkToClient(ServerPlayer client, ChunkPos chunkPos) {
        LevelChunk chunk = this.level.getChunk(chunkPos.x, chunkPos.z);
        Collection<Interactable> interactablesInChunk = new ArrayList<>();
        for (int y = chunk.getMinSection(); y < chunk.getMaxSection(); y++) {
            SectionPos pos = SectionPos.of(chunk.getPos(), y);
            for (Interactable interactable : this.storage.getInteractablesInSection(pos)) {
                if (!interactable.shouldSync) continue;
                interactablesInChunk.add(interactable);
            }
        }
        ClinkerPacketHandler.sendToClient(client, new ClientboundInteractableLoadChunkPacket(chunkPos, interactablesInChunk, true));
    }
}
