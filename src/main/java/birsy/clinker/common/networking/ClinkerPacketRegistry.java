package birsy.clinker.common.networking;

import birsy.clinker.common.networking.packet.*;
import birsy.clinker.common.networking.packet.debug.ClientboundBrainDebugPacket;
import birsy.clinker.common.networking.packet.debug.ClientboundPathfindingDebugPacket;
import birsy.clinker.common.networking.packet.debug.GnomadSquadDebugPacket;
import birsy.clinker.common.networking.packet.debug.GnomadSquadRemovalDebugPacket;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntityInitPacket;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntitySegmentAddPacket;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntitySyncPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationChangeBlockPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationLoadPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationMergePacket;
import birsy.clinker.common.networking.packet.workstation.ServerboundWorkstationLoadRequestPacket;
import birsy.clinker.core.Clinker;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClinkerPacketRegistry {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);

        // client bound
        registrar.playToClient(ClientboundPushPacket.TYPE, ClientboundPushPacket.STREAM_CODEC, ClientboundPushPacket::handle);

        registrar.playToClient(ClientboundGroundLocomotorSyncPacket.TYPE, ClientboundGroundLocomotorSyncPacket.STREAM_CODEC, ClientboundGroundLocomotorSyncPacket::handle);

        registrar.playToClient(ClientboundRopeEntityInitPacket.TYPE,
                ClientboundRopeEntityInitPacket.STREAM_CODEC,
                ClientboundRopeEntityInitPacket::handle);
        registrar.playToClient(ClientboundRopeEntitySegmentAddPacket.TYPE,
                ClientboundRopeEntitySegmentAddPacket.STREAM_CODEC,
                ClientboundRopeEntitySegmentAddPacket::handle);
        registrar.playToClient(ClientboundRopeEntitySyncPacket.TYPE,
                ClientboundRopeEntitySyncPacket.STREAM_CODEC,
                ClientboundRopeEntitySyncPacket::handle);

        registrar.playToClient(ClientboundWorkstationChangeBlockPacket.TYPE,
                ClientboundWorkstationChangeBlockPacket.STREAM_CODEC,
                ClientboundWorkstationChangeBlockPacket::handle);
        registrar.playToClient(ClientboundWorkstationLoadPacket.TYPE,
                ClientboundWorkstationLoadPacket.STREAM_CODEC,
                ClientboundWorkstationLoadPacket::handle);
        registrar.playToClient(ClientboundWorkstationMergePacket.TYPE,
                ClientboundWorkstationMergePacket.STREAM_CODEC,
                ClientboundWorkstationMergePacket::handle);

        registrar.playToClient(ClientboundOrdnanceExplosionPacket.TYPE,
                ClientboundOrdnanceExplosionPacket.STREAM_CODEC,
                ClientboundOrdnanceExplosionPacket::handle);

        registrar.playToClient(ClientboundMoldGrowthPacket.TYPE,
                ClientboundMoldGrowthPacket.STREAM_CODEC,
                ClientboundMoldGrowthPacket::handle);

        registrar.playToClient(ClientboundInverseKinematicsStepPacket.TYPE,
                ClientboundInverseKinematicsStepPacket.STREAM_CODEC,
                ClientboundInverseKinematicsStepPacket::handle);

        // server bound
        registrar.playToServer(ServerboundWorkstationLoadRequestPacket.TYPE,
                ServerboundWorkstationLoadRequestPacket.STREAM_CODEC,
                ServerboundWorkstationLoadRequestPacket::handle);

        //debug packets
        registrar.playToClient(GnomadSquadDebugPacket.TYPE,
                GnomadSquadDebugPacket.STREAM_CODEC,
                GnomadSquadDebugPacket::handle);
        registrar.playToClient(GnomadSquadRemovalDebugPacket.TYPE,
                GnomadSquadRemovalDebugPacket.STREAM_CODEC,
                GnomadSquadRemovalDebugPacket::handle);

        registrar.playToClient(ClientboundPathfindingDebugPacket.TYPE,
                ClientboundPathfindingDebugPacket.STREAM_CODEC,
                ClientboundPathfindingDebugPacket::handle);
        registrar.playToClient(ClientboundBrainDebugPacket.TYPE,
                ClientboundBrainDebugPacket.STREAM_CODEC,
                ClientboundBrainDebugPacket::handle);

        Clinker.LOGGER.info("REGISTERED CLINKER PACKETS!");
    }
}
