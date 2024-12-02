package birsy.clinker.common.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;


import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ClientboundPushPacket(double x, double y, double z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundPushPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("mymod", "my_data"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, MyData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MyData::name,
            ByteBufCodecs.VAR_INT,
            MyData::age,
            MyData::new
    );

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);

        buffer.writeNbt(tag);
    }

    @Override
    public ResourceLocation id() {
        return null;
    }
}

public class ClientboundPushPacket extends ClientboundPacket {
    private final double x, y, z;

    public ClientboundPushPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ClientboundPushPacket(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        this.x = tag.getDouble("x");
        this.y = tag.getDouble("y");
        this.z = tag.getDouble("z");
    }

    @Override
    public void write(FriendlyByteBuf buffer) {

    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft.getInstance().player.push(x, y, z);
    }
}
