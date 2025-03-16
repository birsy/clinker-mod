package birsy.clinker.common.world.level.gen.metachunk;

import birsy.clinker.common.networking.packet.debug.ClientboundMetaChunkDebugPacket;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MetaChunk {
    public static final int MAX_SIZE = 512;
    public static final int MIN_SIZE = 2;

    @Nullable
    final MetaChunk parent;
    public final int minimumX, minimumZ;
    public final int size;
    public final int depth;
    MetaChunk[] children;

    boolean queuedForGeneration = false;
    boolean generated = false;

    public MetaChunk(int minimumX, int minimumZ) {
        this(null, minimumX, minimumZ, MAX_SIZE);
    }

    protected MetaChunk(MetaChunk parent, int minimumX, int minimumZ, int size) {
        this.parent = parent;
        this.minimumX = minimumX;
        this.minimumZ = minimumZ;
        this.size = size;
        this.depth = this.parent == null ? 0 : this.parent.depth + 1;
    }

    public void createChildrenIfNeeded() {
        if (this.children != null) return;
        if (this.isLeaf()) return;
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
        if (this.isLeaf()) return;
        this.createChildrenIfNeeded();
        this.getChildContainingPos(pos).subdivideToContain(pos);
    }

    public MetaChunk getChildContainingPos(ChunkPos pos) {
        if (this.isLeaf()) return null;
        int index = 0;
        if (pos.x > this.minimumX + size/2) index |= 0b10;
        if (pos.z > this.minimumZ + size/2) index |= 0b01;
        return this.children[index];
    }

    public boolean isLeaf() {
        return this.size <= MIN_SIZE;
    }

    public void waitForParentGeneration() {
        if (this.parent == null) return;
        //while (true) if (this.parent.generated) return;
    }

    public void generate() {
        Clinker.LOGGER.info("GENERATED METACHUNK AT ({}, {}) SIZED {}", this.minimumX, this.minimumZ, this.size);
        this.generated = true;
        PacketDistributor.sendToAllPlayers(new ClientboundMetaChunkDebugPacket(this));
    }

}
