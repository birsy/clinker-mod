package birsy.clinker.common.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;


import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundPushPacket extends ClientboundPacket{
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
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);

        buffer.writeNbt(tag);
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft.getInstance().player.push(x, y, z);
    }
}
