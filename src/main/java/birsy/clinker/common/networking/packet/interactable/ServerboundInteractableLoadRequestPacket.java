package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ServerboundPacket;
import birsy.clinker.common.world.level.interactable.InteractableLevelAttachment;
import birsy.clinker.common.world.level.interactable.manager.ServerInteractableManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class ServerboundInteractableLoadRequestPacket extends ServerboundPacket {
    final UUID id;
    final ResourceKey<Level> dimension;

    public ServerboundInteractableLoadRequestPacket(UUID id, ResourceKey<Level> dimension) {
        this.id = id;
        this.dimension = dimension;
    }
    public ServerboundInteractableLoadRequestPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buffer.readUtf(), buffer.readUtf()));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeUtf(dimension.location().getNamespace());
        buffer.writeUtf(dimension.location().getPath());
    }

    @Override
    public void run(PlayPayloadContext context) {
        ServerLevel level = InteractableLevelAttachment.dimensionToServerLevel.get(dimension);
        ServerInteractableManager manager = (ServerInteractableManager) InteractableLevelAttachment.getInteractableManagerForLevel(level);
        if (manager.getInteractable(id) == null) return;

    }
}
