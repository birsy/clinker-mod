package birsy.clinker.common.item.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum CrossbowAttachment implements StringRepresentable {
    REPEATER("repeater"),
    SPRAY("spray"),
    LONG_RANGE("long_range");
    private final String name;

    public static final IntFunction<CrossbowAttachment> BY_ID = ByIdMap.continuous(CrossbowAttachment::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<CrossbowAttachment> CODEC = StringRepresentable.fromValues(CrossbowAttachment::values);
    public static final StreamCodec<ByteBuf, CrossbowAttachment> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CrossbowAttachment::ordinal);

    CrossbowAttachment(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
