package birsy.clinker.common.world.level.gen.system.sampling.field;

public interface InterpolatingFieldFiller {
    double compute(int x, int y, int z);
    void setSlice(int cellY);
    void advanceX(); void advanceY(); void advanceZ();
}
