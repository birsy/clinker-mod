package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ServerboundPacket;
import birsy.clinker.common.world.level.interactable.InteractableAttachment;
import birsy.clinker.common.world.level.interactable.manager.ServerInteractableManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.NetworkEvent;

public class ServerboundInteractableLoadChunkRequestPacket extends ServerboundPacket {
    final ChunkPos pos;
    final ResourceKey<Level> dimension;

    public ServerboundInteractableLoadChunkRequestPacket(LevelChunk chunk) {
        this.pos = chunk.getPos();
        this.dimension = chunk.getLevel().dimension();
    }
    public ServerboundInteractableLoadChunkRequestPacket(FriendlyByteBuf buffer) {
        this.pos = new ChunkPos(buffer.readLong());
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buffer.readUtf(), buffer.readUtf()));
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(pos.toLong());
        buffer.writeUtf(dimension.location().getNamespace());
        buffer.writeUtf(dimension.location().getPath());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        ServerLevel level = InteractableAttachment.dimensionToServerLevel.get(dimension);
        ServerInteractableManager manager = (ServerInteractableManager) InteractableAttachment.getInteractableManagerForLevel(level);
        manager.loadChunkToClient(context.getSender(), pos);
    }
}
