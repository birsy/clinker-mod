package birsy.clinker.core.util.codecs;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

// i fucking hate codecs
public class ExtraByteBufCodecs {
    public static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS = BlockPos.STREAM_CODEC;
    public static final StreamCodec<ByteBuf, UUID> UUID = new StreamCodec<>() {
        public UUID decode(ByteBuf buffer) {
            return new UUID(buffer.readLong(), buffer.readLong());
        }
        public void encode(ByteBuf buffer, UUID uuid) {
            buffer.writeLong(uuid.getMostSignificantBits());
            buffer.writeLong(uuid.getLeastSignificantBits());
        }
    };

    public static final StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<>() {
        public Vec3 decode(ByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
        public void encode(ByteBuf buffer, Vec3 pos) {
            buffer.writeDouble(pos.x()); buffer.writeDouble(pos.y()); buffer.writeDouble(pos.z());
        }
    };

    public static final StreamCodec<ByteBuf, Path> PATH = new StreamCodec<>() {
        public Path decode(ByteBuf buffer) {
            return Path.createFromStream(new FriendlyByteBuf(buffer));
        }
        public void encode(ByteBuf buffer, Path path) {
            path.writeToStream(new FriendlyByteBuf(buffer));
        }
    };

    public static <T> StreamCodec<ByteBuf, T[]> array(StreamCodec<ByteBuf, T> codec) {
        return new StreamCodec<>() {
            public T[] decode(ByteBuf buffer) {
                int count = buffer.readInt();
                T[] array = (T[]) new Object[count]; // Don't Care!
                for (int i = 0; i < count; i++) array[i] = codec.decode(buffer);
                return array;
            }

            public void encode(ByteBuf buffer, T[] array) {
                buffer.writeInt(array.length);
                for (int i = 0; i < array.length; i++) codec.encode(buffer, array[i]);
            }
        };
    }
}
