package birsy.clinker.common.world.alchemy.workstation.storage;

import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;

import java.util.BitSet;

public class SectionBitSet extends BitSet {
    protected SectionBitSet() {
        super(4096);
    }

    public boolean get(Vec3i vec) {
        return this.get(vec.getX(), vec.getY(), vec.getZ());
    }

    public boolean get(int x, int y, int z) {
        return this.get(x + (y * SectionPos.SECTION_SIZE) + (z * SectionPos.SECTION_SIZE * SectionPos.SECTION_SIZE));
    }

    public void set(Vec3i vec, boolean block) {
        this.set(vec.getX(), vec.getY(), vec.getZ(), block);
    }

    public void set(int x, int y, int z, boolean block) {
        this.set(x + y * 16 + z * 256, block);
    }

    public static Vec3i toLocalCoords(BlockPos posIn) {
        return new Vec3i(MathUtils.wrap(posIn.getX(), SectionPos.SECTION_SIZE), MathUtils.wrap(posIn.getY(), SectionPos.SECTION_SIZE), MathUtils.wrap(posIn.getZ(), SectionPos.SECTION_SIZE));
    }


}
