package birsy.clinker.common.world.level.gen;

public interface NoiseFieldWithOffset extends NoiseField {
    void fillFromLocal(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, NoiseField.NoiseFillFunction fillFunction, Object... params);

    float getValueLocal(int x, int y, int z);

    void setPosOffset(int xPosOffset, int yPosOffset, int zPosOffset);

    int offsetX();
    int offsetY();
    int offsetZ();

    default double localToWorldX(int localX) { return localX + offsetX(); }
    default double localToWorldY(int localY) { return localY + offsetY(); }
    default double localToWorldZ(int localZ) { return localZ + offsetZ(); }

    default double worldToLocalX(double worldX) { return worldX - offsetX(); }
    default double worldToLocalY(double worldY) { return worldY - offsetY(); }
    default double worldToLocalZ(double worldZ) { return worldZ - offsetZ(); }
}
