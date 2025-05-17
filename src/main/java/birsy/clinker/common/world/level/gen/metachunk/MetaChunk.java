package birsy.clinker.common.world.level.gen.metachunk;

import birsy.clinker.common.networking.packet.debug.ClientboundMetaChunkFinishGenDebugPacket;
import birsy.clinker.common.world.level.gen.metachunk.feature.MetaChunkFeature;
import birsy.clinker.common.world.level.gen.metachunk.feature.TestMCFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetaChunk {
    public static final int MAX_SIZE = 512;
    public static final int MIN_SIZE = 0;

    @Nullable
    final MetaChunk parent;
    public final int minimumX, minimumZ;
    public final int size;
    public final int depth;
    volatile MetaChunk[] children;

    private final AtomicBoolean generated = new AtomicBoolean(false);

    public List<MetaChunkFeature> features;

    public MetaChunk(int minimumX, int minimumZ) {
        this(null, minimumX, minimumZ, MAX_SIZE);
    }

    protected MetaChunk(MetaChunk parent, int minimumX, int minimumZ, int size) {
        this.parent = parent;
        this.minimumX = minimumX;
        this.minimumZ = minimumZ;
        this.size = size;
        this.depth = this.parent == null ? 0 : this.parent.depth + 1;
        this.features = new ArrayList<>();
    }

    public void createChildrenIfNeeded() {
        if (this.children != null || this.isLeaf()) return;
        synchronized (this) {
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
    }

    public void subdivideToContain(ChunkPos pos) {
        if (this.isLeaf()) return;
        this.createChildrenIfNeeded();
        this.getChildContainingPos(pos).subdivideToContain(pos);
    }

    public MetaChunk getChildContainingPos(ChunkPos pos) {
        return this.getChildContainingPos(pos.x, pos.z);
    }

    public MetaChunk getChildContainingPos(int chunkX, int chunkZ) {
        if (this.isLeaf() || this.children == null) return this;
        int index = 0;
        if (chunkX > this.minimumX + size/2) index |= 0b10;
        if (chunkZ > this.minimumZ + size/2) index |= 0b01;
        return this.children[index];
    }

    public boolean isLeaf() {
        return this.size <= MIN_SIZE;
    }

    public void generate(RandomSource randomSource) {
        //Clinker.LOGGER.info("Generated MetaChunk at ({}, {}), sized {}, on thread {}", this.minimumX, this.minimumZ, this.size, Thread.currentThread().threadId());
        if (this.size == 16) {
            TestMCFeature feature = new TestMCFeature(
                    this.minimumX * 16 + randomSource.nextIntBetweenInclusive(TestMCFeature.RADIUS, this.size * 16 - TestMCFeature.RADIUS),
                    this.minimumZ * 16 + randomSource.nextIntBetweenInclusive(TestMCFeature.RADIUS, this.size * 16 - TestMCFeature.RADIUS)
            );
            feature.plan(this, randomSource);
            this.features.add(feature);
        }
        if (this.parent != null) {
            for (MetaChunkFeature feature : this.parent.features) {
                // propagate features from parent
                if (feature.containedInRange(this.minimumX, this.minimumZ, this.minimumX + this.size, this.minimumZ + this.size))
                    this.features.add(feature);
            }
        }
    }

    public boolean markGenerated() {
        return generated.compareAndSet(false, true);
    }

    public boolean isGenerated() {
        return generated.get();
    }
}
