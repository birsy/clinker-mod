package birsy.clinker.common.networking;

import birsy.clinker.common.networking.packet.*;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ClinkerPacketHandler {
    public static SimpleChannel NETWORK;

    private static final String PROTOCOL_VERSION = "1";
    private static int packetId = 0;
    private static int createId() {
        packetId = packetId + 1;
        return packetId;
    }

    public static void register() {
        NETWORK = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Clinker.MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        registerPackets();
    }

    public static void registerPackets() {
        NETWORK.registerMessage(createId(), ClientboundInteractableAddPacket.class, ClientboundInteractableAddPacket::toBytes, ClientboundInteractableAddPacket::new, ClientboundInteractableAddPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundInteractableRemovePacket.class, ClientboundInteractableRemovePacket::toBytes, ClientboundInteractableRemovePacket::new, ClientboundInteractableRemovePacket::handle);
        NETWORK.registerMessage(createId(), ClientboundInteractableSyncPacket.class, ClientboundInteractableSyncPacket::toBytes, ClientboundInteractableSyncPacket::new, ClientboundInteractableSyncPacket::handle);
        NETWORK.registerMessage(createId(), ServerboundInteractableInteractionPacket.class, ServerboundInteractableInteractionPacket::toBytes, ServerboundInteractableInteractionPacket::new, ServerboundInteractableInteractionPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundSalamanderSyncPacket.class, ClientboundSalamanderSyncPacket::toBytes, ClientboundSalamanderSyncPacket::new, ClientboundSalamanderSyncPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundPushPacket.class, ClientboundPushPacket::toBytes, ClientboundPushPacket::new, ClientboundPushPacket::handle);


        Clinker.LOGGER.info("REGISTERED PACKETS!!!");
        /*NETWORK.messageBuilder(ClientboundInteractableAddPacket.class, createId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundInteractableAddPacket::new)
                .encoder(ClientboundInteractableAddPacket::toBytes)
                .consumerMainThread(ClientboundInteractableAddPacket::handle)
                .add();

        NETWORK.messageBuilder(ClientboundInteractableRemovePacket.class, createId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundInteractableRemovePacket::new)
                .encoder(ClientboundInteractableRemovePacket::toBytes)
                .consumerMainThread(ClientboundInteractableRemovePacket::handle)
                .add();

        NETWORK.messageBuilder(ClientboundInteractableSyncPacket.class, createId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundInteractableSyncPacket::new)
                .encoder(ClientboundInteractableSyncPacket::toBytes)
                .consumerMainThread(ClientboundInteractableSyncPacket::handle)
                .add();

        NETWORK.messageBuilder(ServerboundInteractableInteractionPacket.class, createId(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundInteractableInteractionPacket::new)
                .encoder(ServerboundInteractableInteractionPacket::toBytes)
                .consumerMainThread(ServerboundInteractableInteractionPacket::handle)
                .add();*/
    }

    public static <M> void sendToServer(M packet) {
        NETWORK.sendToServer(packet);
    }
    public static <M> void sendToClient(ServerPlayer client, M packet) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> client), packet);
    }
    public static <M> void sendToAllClients(M packet) {
        NETWORK.send(PacketDistributor.ALL.noArg(), packet);
    }
    public static <M> void sendToClientsInChunk(LevelChunk chunk, M packet) {
        NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }
}
