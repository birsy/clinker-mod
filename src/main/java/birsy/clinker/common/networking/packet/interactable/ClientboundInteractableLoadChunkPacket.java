package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableLevelAttachment;
import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.Collection;

public class ClientboundInteractableLoadChunkPacket extends ClientboundPacket {
    final ChunkPos pos;
    Collection<Interactable> interactables;
    final boolean requestedFromClient;
    public ClientboundInteractableLoadChunkPacket(ChunkPos chunkPos, Collection<Interactable> interactablesInChunk, boolean requestedFromClient) {
        this.pos = chunkPos;
        this.interactables = interactablesInChunk;
        this.requestedFromClient = requestedFromClient;
    }

    public ClientboundInteractableLoadChunkPacket(FriendlyByteBuf buffer) {
        this.pos = new ChunkPos(buffer.readLong());
        this.requestedFromClient = buffer.readBoolean();
        this.interactables = new ArrayList<>();
        CompoundTag tag = buffer.readNbt();
        for (int i = 0; i < tag.size(); i++) {
            CompoundTag interactableTag = tag.getCompound("" + i);
            this.interactables.add(Interactable.deserializeNBT(interactableTag));
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(pos.toLong());
        buffer.writeBoolean(requestedFromClient);
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for (Interactable interactable : this.interactables) {
            tag.put("" + (i++), interactable.serializeNBT());
        }
        buffer.writeNbt(tag);
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        ClientInteractableManager manager = (ClientInteractableManager) InteractableLevelAttachment.getInteractableManagerForLevel(level);
        if (requestedFromClient) {
            manager.loadChunkFromPacket(this.pos, this.interactables);
        } else {
            for (Interactable interactable : interactables) {
                manager.addInteractable(interactable);
            }
        }
    }
}
