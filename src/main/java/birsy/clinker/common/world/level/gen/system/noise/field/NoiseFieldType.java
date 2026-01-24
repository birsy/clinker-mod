package birsy.clinker.common.world.level.gen.system.noise.field;

public interface NoiseFieldType<T extends NoiseField> {
    T create(int chunkHeight, int paddingBlocks);
}
