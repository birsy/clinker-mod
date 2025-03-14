package birsy.clinker.common.world.level.gen.metachunk;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nullable;
import java.util.List;

public class MetaChunk {
    public static final int MAX_SIZE = 512;
    public static final int MIN_SIZE = 2;

    @Nullable
    final MetaChunk parent;
    final int minimumX, minimumZ;
    final int size;

    MetaChunk[] children;

    boolean generated = false;

    public MetaChunk(int minimumX, int minimumZ) {
        this(null, minimumX, minimumZ, MAX_SIZE);
    }

    protected MetaChunk(MetaChunk parent, int minimumX, int minimumZ, int size) {
        this.parent = parent;
        this.minimumX = minimumX;
        this.minimumZ = minimumZ;
        this.size = size;
    }

    public void createChildrenIfNeeded() {
        if (this.children != null) return;
        if (this.size == MIN_SIZE) return;
        this.children = new MetaChunk[4];
        int childSize = this.size / 2;
        // northwest
        this.children[0b00] = new MetaChunk(this, this.minimumX, this.minimumZ, childSize);
        // northeast
        this.children[0b01] = new MetaChunk(this, this.minimumX, this.minimumZ + childSize, childSize);
        // southwest
        this.children[0b10] = new MetaChunk(this, this.minimumX + childSize, this.minimumZ, childSize);
        // southeast
        this.children[0b11] = new MetaChunk(this, this.minimumX + childSize, this.minimumZ + childSize, childSize);
    }

    public void subdivideToContain(ChunkPos pos) {
        if (!this.hasChildren()) return;
        this.createChildrenIfNeeded();
        this.getChildContainingPos(pos).subdivideToContain(pos);
    }

    public MetaChunk getChildContainingPos(ChunkPos pos) {
        if (!this.hasChildren()) return null;
        int index = 0;
        if (pos.x > this.minimumX + size/2) index |= 0b10;
        if (pos.z > this.minimumZ + size/2) index |= 0b01;
        return this.children[index];
    }

    public boolean hasChildren() {
        return this.size != MIN_SIZE;
    }

    public void scheduleGeneration(ChunkPos pos) {
        if (!this.generated) {
            // todo: generate metafeatures
            Clinker.LOGGER.info("generating metafeatures for metachunk at ({}, {}) sized {}", this.minimumX, this.minimumZ, this.size);
            this.createChildren();
        }
        if (this.hasChildren()) this.getChildContainingPos(pos).scheduleGeneration(pos);
    }
}
