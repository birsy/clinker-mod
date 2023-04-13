package birsy.clinker.common.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ServerboundPacket {
    public ServerboundPacket() {}
    public ServerboundPacket(FriendlyByteBuf buffer) {}

    public abstract void toBytes(FriendlyByteBuf buffer);

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> this.run(context));
        context.setPacketHandled(true);
        return true;
    }

    public abstract void run(NetworkEvent.Context context);
}
