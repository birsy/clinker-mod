package birsy.clinker.common.world.level.gen;

public interface NoiseFieldWithOffset extends NoiseField {
    void fillFromLocal(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, NoiseField.NoiseFillFunction fillFunction, Object... params);

    float getValueLocal(int x, int y, int z);

    void setPosOffset(int xPosOffset, int yPosOffset, int zPosOffset);
}
