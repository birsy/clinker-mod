package birsy.clinker.common.networking;

import birsy.clinker.common.networking.packet.*;
import birsy.clinker.common.networking.packet.debug.ClientboundBrainDebugPacket;
import birsy.clinker.common.networking.packet.debug.ClientboundPathfindingDebugPacket;
import birsy.clinker.common.networking.packet.debug.GnomadSquadDebugPacket;
import birsy.clinker.common.networking.packet.debug.GnomadSquadRemovalDebugPacket;
import birsy.clinker.common.networking.packet.interactable.*;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntityInitPacket;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntitySegmentAddPacket;
import birsy.clinker.common.networking.packet.ropeentity.ClientboundRopeEntitySyncPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationChangeBlockPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationLoadPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationMergePacket;
import birsy.clinker.common.networking.packet.workstation.ServerboundWorkstationLoadRequestPacket;
import birsy.clinker.core.Clinker;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClinkerPacketHandler {
    // todo: make this a thing for each packet and not this shitty awful thing.
    public static ResourceLocation createId(Class clazz) {
        return Clinker.resource(clazz.getSimpleName().toLowerCase());
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Clinker.MOD_ID);

        registrar.playToClient(ClientboundPushPacket.TYPE, ClientboundPushPacket.STREAM_CODEC, ClientboundPushPacket::handle);

        registrar.playToClient(ClientboundRopeEntityInitPacket.TYPE, ClientboundRopeEntityInitPacket.STREAM_CODEC, ClientboundRopeEntityInitPacket::handle);
        registrar.playToClient(ClientboundRopeEntitySegmentAddPacket.TYPE, ClientboundRopeEntitySegmentAddPacket.STREAM_CODEC, ClientboundRopeEntitySegmentAddPacket::handle);
        registrar.playToClient(ClientboundRopeEntitySyncPacket.TYPE, ClientboundRopeEntitySyncPacket.STREAM_CODEC, ClientboundRopeEntitySyncPacket::handle);

        //debug packets
        registrar.play(createId(ClientboundPathfindingDebugPacket.class), ClientboundPathfindingDebugPacket::new, ClientboundPathfindingDebugPacket::handle);


        registrar.play(createId(ClientboundWorkstationChangeBlockPacket.class), ClientboundWorkstationChangeBlockPacket::new, ClientboundWorkstationChangeBlockPacket::handle);
        registrar.play(createId(ClientboundWorkstationLoadPacket.class), ClientboundWorkstationLoadPacket::new, ClientboundWorkstationLoadPacket::handle);
        registrar.play(createId(ClientboundWorkstationMergePacket.class), ClientboundWorkstationMergePacket::new, ClientboundWorkstationMergePacket::handle);
        registrar.play(createId(ServerboundWorkstationLoadRequestPacket.class), ServerboundWorkstationLoadRequestPacket::new, ServerboundWorkstationLoadRequestPacket::handle);

        registrar.play(createId(ClientboundOrdnanceExplosionPacket.class), ClientboundOrdnanceExplosionPacket::new, ClientboundOrdnanceExplosionPacket::handle);

        registrar.play(createId(ClientboundMoldGrowthPacket.class), ClientboundMoldGrowthPacket::new, ClientboundMoldGrowthPacket::handle);

        registrar.play(createId(GnomadSquadDebugPacket.class), GnomadSquadDebugPacket::new, GnomadSquadDebugPacket::handle);
        registrar.play(createId(GnomadSquadRemovalDebugPacket.class), GnomadSquadRemovalDebugPacket::new, GnomadSquadRemovalDebugPacket::handle);

        registrar.play(createId(ClientboundInverseKinematicsStepPacket.class), ClientboundInverseKinematicsStepPacket::new, ClientboundInverseKinematicsStepPacket::handle);

        registrar.play(createId(ClientboundRopeEntityInitPacket.class), ClientboundRopeEntityInitPacket::new, ClientboundRopeEntityInitPacket::handle);
        registrar.play(createId(ClientboundRopeEntitySegmentAddPacket.class), ClientboundRopeEntitySegmentAddPacket::new, ClientboundRopeEntitySegmentAddPacket::handle);
        registrar.play(createId(ClientboundRopeEntitySyncPacket.class), ClientboundRopeEntitySyncPacket::new, ClientboundRopeEntitySyncPacket::handle);

        registrar.play(createId(ClientboundBrainDebugPacket.class), ClientboundBrainDebugPacket::new, ClientboundBrainDebugPacket::handle);

        Clinker.LOGGER.info("REGISTERED CLINKER PACKETS!");
    }


    public static <M extends CustomPacketPayload> void sendToServer(M packet) {
        PacketDistributor.SERVER.noArg().send(packet);
    }
    public static <M extends CustomPacketPayload> void sendToClient(ServerPlayer client, M packet) {
        PacketDistributor.PLAYER.with(client).send(packet);
    }
    public static <M extends CustomPacketPayload> void sendToAllClients(M packet) {
        PacketDistributor.ALL.noArg().send(packet);
    }
    public static <M extends CustomPacketPayload> void sendToClientsTrackingChunk(LevelChunk chunk, M packet) {
        PacketDistributor.TRACKING_CHUNK.with(chunk).send(packet);
    }
    public static <M extends CustomPacketPayload> void sendToClientsTrackingEntity(Entity entity, M packet) {
        PacketDistributor.TRACKING_ENTITY.with(entity).send(packet);
    }

}
