package birsy.clinker.common.networking;

import birsy.clinker.common.networking.packet.*;
import birsy.clinker.common.networking.packet.interactable.*;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationChangeBlockPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationLoadPacket;
import birsy.clinker.common.networking.packet.workstation.ClientboundWorkstationMergePacket;
import birsy.clinker.common.networking.packet.workstation.ServerboundWorkstationLoadRequestPacket;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;


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
        NETWORK.registerMessage(createId(), ClientboundInteractableTranslationSyncPacket.class, ClientboundInteractableTranslationSyncPacket::toBytes, ClientboundInteractableTranslationSyncPacket::new, ClientboundInteractableTranslationSyncPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundInteractableShapeSyncPacket.class, ClientboundInteractableShapeSyncPacket::toBytes, ClientboundInteractableShapeSyncPacket::new, ClientboundInteractableShapeSyncPacket::handle);
        NETWORK.registerMessage(createId(), ServerboundInteractableInteractionPacket.class, ServerboundInteractableInteractionPacket::toBytes, ServerboundInteractableInteractionPacket::new, ServerboundInteractableInteractionPacket::handle);

        NETWORK.registerMessage(createId(), ClientboundSalamanderSyncPacket.class, ClientboundSalamanderSyncPacket::toBytes, ClientboundSalamanderSyncPacket::new, ClientboundSalamanderSyncPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundPushPacket.class, ClientboundPushPacket::toBytes, ClientboundPushPacket::new, ClientboundPushPacket::handle);

        NETWORK.registerMessage(createId(), ClientboundFairyFruitSyncPacket.class, ClientboundFairyFruitSyncPacket::toBytes, ClientboundFairyFruitSyncPacket::new, ClientboundFairyFruitSyncPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundFairyFruitGrowPacket.class, ClientboundFairyFruitGrowPacket::toBytes, buffer -> new ClientboundFairyFruitGrowPacket(buffer), ClientboundFairyFruitGrowPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundFairyFruitBreakPacket.class, ClientboundFairyFruitBreakPacket::toBytes, ClientboundFairyFruitBreakPacket::new, ClientboundFairyFruitBreakPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundFairyFruitRemovalPacket.class, ClientboundFairyFruitRemovalPacket::toBytes, ClientboundFairyFruitRemovalPacket::new, ClientboundFairyFruitRemovalPacket::handle);

        NETWORK.registerMessage(createId(), ClientboundWorkstationChangeBlockPacket.class, ClientboundWorkstationChangeBlockPacket::toBytes, ClientboundWorkstationChangeBlockPacket::new, ClientboundWorkstationChangeBlockPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundWorkstationLoadPacket.class, ClientboundWorkstationLoadPacket::toBytes, ClientboundWorkstationLoadPacket::new, ClientboundWorkstationLoadPacket::handle);
        NETWORK.registerMessage(createId(), ClientboundWorkstationMergePacket.class, ClientboundWorkstationMergePacket::toBytes, ClientboundWorkstationMergePacket::new, ClientboundWorkstationMergePacket::handle);
        NETWORK.registerMessage(createId(), ServerboundWorkstationLoadRequestPacket.class, ServerboundWorkstationLoadRequestPacket::toBytes, ServerboundWorkstationLoadRequestPacket::new, ServerboundWorkstationLoadRequestPacket::handle);

        NETWORK.registerMessage(createId(), ClientboundOrdnanceExplosionPacket.class, ClientboundOrdnanceExplosionPacket::toBytes, ClientboundOrdnanceExplosionPacket::new, ClientboundOrdnanceExplosionPacket::handle);

        Clinker.LOGGER.info("REGISTERED PACKETS!!!");
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
