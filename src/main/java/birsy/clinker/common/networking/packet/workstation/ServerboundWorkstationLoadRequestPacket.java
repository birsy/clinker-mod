package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.networking.packet.ServerboundPacket;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class ServerboundWorkstationLoadRequestPacket extends ServerboundPacket {
    private final UUID id;
    public ServerboundWorkstationLoadRequestPacket(UUID id, Level level) {
        this.id = id;
    }

    public ServerboundWorkstationLoadRequestPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(id);
    }

    @Override
    public void run(PlayPayloadContext context) {
        Entity sender = context.player().get();
        if (sender.level() instanceof ServerLevel level) {
            WorkstationManager manager = WorkstationManager.managerByLevel.get(level);
            manager.loadWorkstationToClient(id, (ServerPlayer) context.player().get());
        }
    }
}
