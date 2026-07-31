package birsy.clinker.common.item.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum CrossbowState implements StringRepresentable {
    STANDBY("standby"),
    LOADING("loading"),
    LOADED("loaded"),
    FIRING("firing");
    private final String name;

    public static final IntFunction<CrossbowState> BY_ID = ByIdMap.continuous(CrossbowState::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<CrossbowState> CODEC = StringRepresentable.fromValues(CrossbowState::values);
    public static final StreamCodec<ByteBuf, CrossbowState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CrossbowState::ordinal);

    CrossbowState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
