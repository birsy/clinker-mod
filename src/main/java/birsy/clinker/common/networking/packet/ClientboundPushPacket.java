package birsy.clinker.common.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkEvent;

public class ClientboundPushPacket extends ClientboundPacket{
    private Vec3 amount;

    public ClientboundPushPacket(Vec3 amount) {
        this.amount = amount;
    }

    public ClientboundPushPacket(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        this.amount = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", amount.x());
        tag.putDouble("y", amount.y());
        tag.putDouble("z", amount.z());

        buffer.writeNbt(tag);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft.getInstance().player.move(MoverType.PLAYER, amount);
    }
}
