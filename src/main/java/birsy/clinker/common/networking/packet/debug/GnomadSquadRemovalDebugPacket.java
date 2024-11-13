package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquadTask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GnomadSquadRemovalDebugPacket extends ClientboundPacket {
    public final UUID squadID;

    public GnomadSquadRemovalDebugPacket(GnomadSquad squad) {
        this.squadID = squad.id;
    }

    public GnomadSquadRemovalDebugPacket(FriendlyByteBuf buffer) {
        this.squadID = buffer.readUUID();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.squadID);
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClinkerDebugRenderers.gnomadSquadDebugRenderer.removeSquad(this.squadID);
    }
}
