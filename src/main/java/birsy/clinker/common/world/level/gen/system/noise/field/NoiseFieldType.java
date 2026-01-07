package birsy.clinker.common.world.level.gen.system.noise.field;

public interface NoiseFieldType {
    NoiseField create(int chunkHeight, int paddingBlocks);
}
