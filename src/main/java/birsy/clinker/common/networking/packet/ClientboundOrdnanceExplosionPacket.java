package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.projectile.OrdnanceEffects;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundOrdnanceExplosionPacket extends ClientboundPacket {
    private final Vec3 position;
    private final OrdnanceEffects effects;

    public ClientboundOrdnanceExplosionPacket(Vec3 position, OrdnanceEffects effects) {
        this.position = position;
        this.effects = effects;
    }

    public ClientboundOrdnanceExplosionPacket(FriendlyByteBuf buffer) {
        this.position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.effects = OrdnanceEffects.deserialize(buffer.readNbt());
    }


    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(position.x());
        buffer.writeDouble(position.y());
        buffer.writeDouble(position.z());
        buffer.writeNbt(this.effects.serialize(new CompoundTag()));
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        OrdnanceEntity.createOrdnanceExplosion(position, level, null, null, this.effects);
    }
}
