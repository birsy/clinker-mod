package birsy.clinker.core.util.codecs;

import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.function.Function;

// i fucking hate codecs
public class ExtraByteBufCodecs {
    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B p_330310_) {
                T1 t1 = codec1.decode(p_330310_);
                T2 t2 = codec2.decode(p_330310_);
                T3 t3 = codec3.decode(p_330310_);
                T4 t4 = codec4.decode(p_330310_);
                T5 t5 = codec5.decode(p_330310_);
                T6 t6 = codec6.decode(p_330310_);
                T7 t7 = codec7.decode(p_330310_);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B p_332052_, C p_331912_) {
                codec1.encode(p_332052_, getter1.apply(p_331912_));
                codec2.encode(p_332052_, getter2.apply(p_331912_));
                codec3.encode(p_332052_, getter3.apply(p_331912_));
                codec4.encode(p_332052_, getter4.apply(p_331912_));
                codec5.encode(p_332052_, getter5.apply(p_331912_));
                codec6.encode(p_332052_, getter6.apply(p_331912_));
                codec7.encode(p_332052_, getter7.apply(p_331912_));
            }
        };
    }

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
