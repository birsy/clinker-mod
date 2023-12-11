package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.level.interactable.InteractableAttachment;
import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.NetworkEvent;

public class ClientboundInteractableUnloadChunkPacket extends ClientboundPacket {
    final ChunkPos pos;

    public ClientboundInteractableUnloadChunkPacket(ChunkPos pos) {
        this.pos = pos;
    }
    public ClientboundInteractableUnloadChunkPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readChunkPos();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeChunkPos(pos);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        ClientInteractableManager manager = (ClientInteractableManager) InteractableAttachment.getInteractableManagerForLevel(level);
        manager.unloadChunk(level.getChunk(pos.x, pos.z));
    }
}
