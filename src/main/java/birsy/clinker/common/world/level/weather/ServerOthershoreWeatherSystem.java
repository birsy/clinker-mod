package birsy.clinker.common.world.level.weather;

import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherChangedPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherInitPacket;
import birsy.clinker.common.networking.packet.weather.ClientboundOthershoreWeatherSyncPacket;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerOthershoreWeatherTypes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ServerOthershoreWeatherSystem extends SavedData {
    public final ServerLevel level;
    public final OthershoreWeatherSystem system;
    int syncPacketTimer = 0;

    public ServerOthershoreWeatherSystem(ServerLevel level) {
        this.level = level;
        this.system = new OthershoreWeatherSystem(level);
    }

    @Nullable
    public static ServerOthershoreWeatherSystem getServerSystem(ServerLevel level) {
        if (OthershoreWeatherSystem.hasOthershoreWeather(level)) {
            return level.getDataStorage().computeIfAbsent(
                    new Factory<>(
                            () -> {
                                ServerOthershoreWeatherSystem serverSystem = new ServerOthershoreWeatherSystem(level);
                                // set the initial weather
                                serverSystem.system.setWeather(
                                        ClinkerOthershoreWeatherTypes.NORMAL.get().factory().apply(serverSystem.system)
                                );
                                return serverSystem;
                            },
                            (data, registry) -> ServerOthershoreWeatherSystem.load(level, data, registry),
                            null
                    ), "othershore_weather_system"
            );
        }
        return null;
    }

    @Nullable
    public static OthershoreWeatherSystem get(ServerLevel level) {
        ServerOthershoreWeatherSystem serverSystem = getServerSystem(level);
        if (serverSystem == null) return null;
        return serverSystem.system;
    }

    @SubscribeEvent
    public static void tickOthershoreWeather(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level && OthershoreWeatherSystem.hasOthershoreWeather(level)) {
            ServerOthershoreWeatherSystem serverSystem = getServerSystem(level);
            if (serverSystem == null) return;

            serverSystem.system.tick();

            // distribute changed packets
            if (serverSystem.system.changedWeatherThisTick) {
                serverSystem.distributeChangedPackets();
                serverSystem.syncPacketTimer = 0;
            }

            // and sync packets!
            if (serverSystem.system.currentWeather.hasSyncPacket()) {
                serverSystem.syncPacketTimer++;
                if (serverSystem.syncPacketTimer >= 5 && !serverSystem.system.changedWeatherThisTick) {
                    serverSystem.distributeSyncPackets();
                    serverSystem.syncPacketTimer = 0;
                    serverSystem.setDirty();
                }
            }
        }
    }

    private void distributeSyncPackets() {
        ByteBuf buffer = Unpooled.buffer();
        RegistryFriendlyByteBuf friendlyBuffer = new RegistryFriendlyByteBuf(buffer, level.registryAccess(), ConnectionType.NEOFORGE);
        try {
            system.currentWeather.uploadToSyncPacket(friendlyBuffer);
            PacketDistributor.sendToPlayersInDimension(level, new ClientboundOthershoreWeatherSyncPacket(system.currentWeather.type(), friendlyBuffer));
        } finally {
            friendlyBuffer.release();
        }
        friendlyBuffer.release();
    }
    public void distributeChangedPackets() {
        PacketDistributor.sendToPlayersInDimension(level, new ClientboundOthershoreWeatherChangedPacket(system.currentWeather));
    }
    public void distributeInitPacket(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new ClientboundOthershoreWeatherInitPacket(system.currentWeather, system.weatherTickCount));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        system.serialize(tag, registries);
        return tag;
    }

    static ServerOthershoreWeatherSystem load(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        ServerOthershoreWeatherSystem system = new ServerOthershoreWeatherSystem(level);
        system.system.deserialize(tag, registries);
        return system;
    }
}
