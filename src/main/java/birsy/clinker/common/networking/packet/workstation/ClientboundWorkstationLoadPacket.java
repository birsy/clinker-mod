package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record ClientboundWorkstationLoadPacket(CompoundTag workstationSerialized, UUID uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundWorkstationLoadPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/workstation/load"));
    public static final StreamCodec<ByteBuf, ClientboundWorkstationLoadPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            ClientboundWorkstationLoadPacket::workstationSerialized,
            ExtraByteBufCodecs.UUID,
            ClientboundWorkstationLoadPacket::uuid,
            ClientboundWorkstationLoadPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        WorkstationManager clientManager = WorkstationManager.clientWorkstationManager;
        Workstation workstation = Workstation.deserialize(this.workstationSerialized, clientManager.level);
        clientManager.workstationStorage.remove(uuid);
        clientManager.workstationStorage.put(uuid, workstation);
    }
}
