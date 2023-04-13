package birsy.clinker.common.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ClientboundPacket {
    public ClientboundPacket() {}
    public ClientboundPacket(FriendlyByteBuf buffer) {}

    public abstract void toBytes(FriendlyByteBuf buffer);

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> run(context));
        context.setPacketHandled(true);
        return true;
    }

    public abstract void run(NetworkEvent.Context context);
}
