package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.common.world.entity.OrdnanceEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.NetworkEvent;

public class ClientboundOrdnanceExplosionPacket extends ClientboundPacket {
    private final Vec3 position;

    public ClientboundOrdnanceExplosionPacket(Vec3 position) {
        this.position = position;
    }

    public ClientboundOrdnanceExplosionPacket(FriendlyByteBuf buffer) {
        this.position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeDouble(position.x());
        buffer.writeDouble(position.y());
        buffer.writeDouble(position.z());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        OrdnanceEntity.createOrdnanceExplosion(position, level, null, null);
    }
}
