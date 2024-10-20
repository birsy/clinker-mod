package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.OldOrdnanceEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundOrdnanceExplosionPacket extends ClientboundPacket {
    private final Vec3 position;

    public ClientboundOrdnanceExplosionPacket(Vec3 position) {
        this.position = position;
    }

    public ClientboundOrdnanceExplosionPacket(FriendlyByteBuf buffer) {
        this.position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }


    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(position.x());
        buffer.writeDouble(position.y());
        buffer.writeDouble(position.z());
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        OldOrdnanceEntity.createOrdnanceExplosion(position, level, null, null);
    }
}
