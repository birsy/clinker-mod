package birsy.clinker.core.util.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.Function;

public class ExtraExtraCodecs {
    public static final Codec<Vector3f> FANCY_VECTOR3F = Codec.withAlternative(ExtraCodecs.VECTOR3F,
            Codec.FLOAT.xmap(Vector3f::new, Vector3f::x)
    );
    private record SRTMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        private final static Codec<SRTMatrix> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FANCY_VECTOR3F.optionalFieldOf("translation", new Vector3f(0, 0, 0)).forGetter(SRTMatrix::translation),
                FANCY_VECTOR3F.optionalFieldOf("rotation", new Vector3f(0, 0, 0)).forGetter(SRTMatrix::rotation),
                FANCY_VECTOR3F.optionalFieldOf("scale", new Vector3f(1)).forGetter(SRTMatrix::scale)
        ).apply(instance, SRTMatrix::new));
        Matrix4f toMatrix() {
            return new Matrix4f()
                    .translate(translation)
                    .rotateYXZ(rotation.y() * Mth.DEG_TO_RAD, rotation.x() * Mth.DEG_TO_RAD, rotation.z() * Mth.DEG_TO_RAD)
                    .scale(scale);
        }
    }
    public static final Codec<Matrix4f> FANCY_MATRIX4F = Codec.either(SRTMatrix.CODEC, ExtraCodecs.MATRIX4F).xmap(
            either -> either.map(SRTMatrix::toMatrix, Function.identity()), Either::right
    );
}
