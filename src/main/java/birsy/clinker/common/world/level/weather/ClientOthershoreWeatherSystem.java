package birsy.clinker.common.world.level.weather;

import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherChangedPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherInitPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherSyncPacket;
import birsy.clinker.common.networking.packet.weather.ServerboundOthershoreWeatherInitPacket;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClientOthershoreWeatherSystem {
    private static OthershoreWeatherSystem SYSTEM;

    @Nullable
    public static OthershoreWeatherSystem get() {
        return SYSTEM;
    }

    public static void updateFromInitPacket(ClientboundOthershoreWeatherInitPacket packet) {
        if (SYSTEM == null) return;
        // set weather directly, skipping weather.begin()
        SYSTEM.currentWeather = packet.weather();
    }
    public static void updateFromChangedPacket(ClientboundOthershoreWeatherChangedPacket packet) {
        if (SYSTEM == null) return;
        SYSTEM.setWeather(packet.weather());
    }
    public static void updateFromSyncPacket(ClientboundOthershoreWeatherSyncPacket packet) {
        if (SYSTEM == null) return;
        if (SYSTEM.currentWeather.type() != packet.weatherType()) return;

        ByteBuf buffer = Unpooled.wrappedBuffer(packet.data());
        RegistryFriendlyByteBuf friendlyBuffer = new RegistryFriendlyByteBuf(
                buffer,
                SYSTEM.level.registryAccess(),
                ConnectionType.NEOFORGE
        );
        try { SYSTEM.currentWeather.updateFromSyncPacket(friendlyBuffer); }
        finally { friendlyBuffer.release(); }
    }

    @SubscribeEvent
    static void onEnterLevel(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level && OthershoreWeatherSystem.hasOthershoreWeather(level)) {
            SYSTEM = new OthershoreWeatherSystem(level);
            if (Minecraft.getInstance().getConnection() != null)
                PacketDistributor.sendToServer(new ServerboundOthershoreWeatherInitPacket());
        }
    }

    @SubscribeEvent
    static void onLeaveLevel(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel) SYSTEM = null;
    }

    @SubscribeEvent
    public static void tickOthershoreWeather(LevelTickEvent.Pre event) {
        if (!event.getLevel().isClientSide()) return;

        OthershoreWeatherSystem system = get();
        if (system == null && OthershoreWeatherSystem.hasOthershoreWeather(event.getLevel())) {
            PacketDistributor.sendToServer(new ServerboundOthershoreWeatherInitPacket());
        }
        if (system == null) return;
        system.tick();
    }
}
