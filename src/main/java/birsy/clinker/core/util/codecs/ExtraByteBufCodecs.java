package birsy.clinker.core.util.codecs;

import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.Colorc;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.function.Function;

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
        return new StreamCodec<>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }
            @Override
            public void encode(B buffer, C obj) {
                codec1.encode(buffer, getter1.apply(obj));
                codec2.encode(buffer, getter2.apply(obj));
                codec3.encode(buffer, getter3.apply(obj));
                codec4.encode(buffer, getter4.apply(obj));
                codec5.encode(buffer, getter5.apply(obj));
                codec6.encode(buffer, getter6.apply(obj));
                codec7.encode(buffer, getter7.apply(obj));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
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
            final StreamCodec<? super B, T8> codec8,
            final Function<C, T8> getter8,
            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public C decode(B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }
            @Override
            public void encode(B buffer, C obj) {
                codec1.encode(buffer, getter1.apply(obj));
                codec2.encode(buffer, getter2.apply(obj));
                codec3.encode(buffer, getter3.apply(obj));
                codec4.encode(buffer, getter4.apply(obj));
                codec5.encode(buffer, getter5.apply(obj));
                codec6.encode(buffer, getter6.apply(obj));
                codec7.encode(buffer, getter7.apply(obj));
                codec8.encode(buffer, getter8.apply(obj));
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

    public static final StreamCodec<ByteBuf, Colorc> COLOR = StreamCodec.of((buf, col) -> buf.writeInt(col.argb()), (buf) -> new Color(buf.readInt(), true));

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
