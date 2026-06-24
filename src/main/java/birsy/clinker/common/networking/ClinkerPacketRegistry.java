package birsy.clinker.common.networking;

import birsy.clinker.common.networking.packet.*;
import birsy.clinker.common.networking.packet.debug.ClientboundBrainDebugPacket;
import birsy.clinker.common.networking.packet.debug.ClientboundPathfindingDebugPacket;
import birsy.clinker.common.networking.packet.debug.ClientboundRiverDebugPacket;
import birsy.clinker.common.networking.packet.debug.ClientboundSquadDebugPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherChangedPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherInitPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherSyncPacket;
import birsy.clinker.common.networking.packet.weather.ServerboundOthershoreWeatherInitPacket;
import birsy.clinker.core.Clinker;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerPacketRegistry {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);

        // client bound
        registrar.playToClient(ClientboundMobLocomotionSyncPacket.TYPE,
                ClientboundMobLocomotionSyncPacket.STREAM_CODEC,
                ClientboundMobLocomotionSyncPacket::handle);

        registrar.playToClient(ClientboundOrdnanceExplosionPacket.TYPE,
                ClientboundOrdnanceExplosionPacket.STREAM_CODEC,
                ClientboundOrdnanceExplosionPacket::handle);

        registrar.playToClient(ClientboundSaltpetreLeachPacket.TYPE,
                ClientboundSaltpetreLeachPacket.STREAM_CODEC,
                ClientboundSaltpetreLeachPacket::handle);

        registrar.playToClient(ClientboundOthershoreWeatherInitPacket.TYPE,
                ClientboundOthershoreWeatherInitPacket.STREAM_CODEC,
                ClientboundOthershoreWeatherInitPacket::handle);
        registrar.playToClient(ClientboundOthershoreWeatherChangedPacket.TYPE,
                ClientboundOthershoreWeatherChangedPacket.STREAM_CODEC,
                ClientboundOthershoreWeatherChangedPacket::handle);
        registrar.playToClient(ClientboundOthershoreWeatherSyncPacket.TYPE,
                ClientboundOthershoreWeatherSyncPacket.STREAM_CODEC,
                ClientboundOthershoreWeatherSyncPacket::handle);

        // server bound
        registrar.playToServer(ServerboundOthershoreWeatherInitPacket.TYPE,
                ServerboundOthershoreWeatherInitPacket.STREAM_CODEC,
                ServerboundOthershoreWeatherInitPacket::handle);

        //debug packets
        registrar.playToClient(ClientboundPathfindingDebugPacket.TYPE,
                ClientboundPathfindingDebugPacket.STREAM_CODEC,
                ClientboundPathfindingDebugPacket::handle);
        registrar.playToClient(ClientboundBrainDebugPacket.TYPE,
                ClientboundBrainDebugPacket.STREAM_CODEC,
                ClientboundBrainDebugPacket::handle);
        registrar.playToClient(ClientboundSquadDebugPacket.TYPE,
                ClientboundSquadDebugPacket.STREAM_CODEC,
                ClientboundSquadDebugPacket::handle);
        registrar.playToClient(ClientboundRiverDebugPacket.TYPE,
                ClientboundRiverDebugPacket.STREAM_CODEC,
                ClientboundRiverDebugPacket::handle);

        Clinker.LOGGER.info("REGISTERED CLINKER PACKETS!");
    }
}
