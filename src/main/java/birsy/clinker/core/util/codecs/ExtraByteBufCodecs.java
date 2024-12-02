package birsy.clinker.core.util.codecs;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

// i fucking hate codecs
public class ExtraByteBufCodecs {
    public static <T> StreamCodec<ByteBuf, T[]> array(StreamCodec<ByteBuf, T> codec) {
        return new StreamCodec<>() {
            public T[] decode(ByteBuf buffer) {
                int count = buffer.readInt();
                T[] array = new T[count];
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
