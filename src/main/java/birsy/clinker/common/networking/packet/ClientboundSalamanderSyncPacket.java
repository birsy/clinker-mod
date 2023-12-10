package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientboundSalamanderSyncPacket extends ClientboundPacket {
    private UUID entityUUID;
    private int entityID;
    private List<Vec3> positions;
    private List<Vec3> pPositions;

    public ClientboundSalamanderSyncPacket(NewSalamanderEntity entity) {
        this.entityUUID = entity.getUUID();
        this.entityID = entity.getId();
        this.positions = new ArrayList<>();
        this.pPositions = new ArrayList<>();

        for (NewSalamanderEntity.SalamanderJoint joint : entity.joints) {
            this.positions.add(joint.position);
            this.pPositions.add(joint.physicsPrevPosition);
        }
    }

    public ClientboundSalamanderSyncPacket(FriendlyByteBuf buffer) {
        long[] bits = buffer.readLongArray();
        this.entityUUID = new UUID(bits[0], bits[1]);
        this.entityID = buffer.readInt();

        this.positions = new ArrayList<>();
        this.pPositions = new ArrayList<>();
        CompoundTag tag = buffer.readNbt();
        for (int i = 0; i < tag.size(); i++) {
            CompoundTag jointTag = tag.getCompound("" + i);
            this.positions.add(i, new Vec3(jointTag.getDouble("x"), jointTag.getDouble("y"), jointTag.getDouble("z")));
            this.pPositions.add(i, new Vec3(jointTag.getDouble("pX"), jointTag.getDouble("pY"), jointTag.getDouble("pZ")));
        }
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        long[] bits = {entityUUID.getMostSignificantBits(), entityUUID.getLeastSignificantBits()};
        buffer.writeLongArray(bits);
        buffer.writeInt(entityID);

        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < this.positions.size(); i++) {
            CompoundTag jointTag = new CompoundTag();
            Vec3 position = this.positions.get(i);
            jointTag.putDouble("x", position.x());
            jointTag.putDouble("y", position.y());
            jointTag.putDouble("z", position.z());

            Vec3 pPosition = this.pPositions.get(i);
            jointTag.putDouble("pX", pPosition.x());
            jointTag.putDouble("pY", pPosition.y());
            jointTag.putDouble("pZ", pPosition.z());

            tag.put("" + i, jointTag);
        }

        buffer.writeNbt(tag);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Entity entity = level.getEntity(this.entityID);
        if (entity == null) { Clinker.LOGGER.warn("No matching Salamander entity found to sync!"); return; }
        NewSalamanderEntity salamander = (NewSalamanderEntity) entity;
        for (int i = 0; i < salamander.joints.size(); i++) {
            NewSalamanderEntity.SalamanderJoint joint = salamander.joints.get(i);
            joint.physicsPrevPosition = this.pPositions.get(i);
            joint.position = this.positions.get(i);
            joint.physicsNextPosition = this.positions.get(i);
        }
    }
}
