package birsy.clinker.common.world.alchemy.workstation.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class WorkstationBlocks implements Iterable<BlockPos> {
    public final Long2ObjectMap<SectionBitSet> bitsetBySection;
    public final Set<BlockPos> blocks;

    private Vec3i center;
    private boolean isCenterCalculated = false;
    private int maxRadius = 0;
    private boolean isRadiusCalculated = false;

    public WorkstationBlocks() {
        this.bitsetBySection = new Long2ObjectOpenHashMap<>();
        this.blocks = new HashSet<>();
    }

    public void insertBlock(BlockPos pos) {
        long sectionPos = SectionPos.of(pos).asLong();
        SectionBitSet set = bitsetBySection.get(sectionPos);

        if (set == null) {
            set = new SectionBitSet();
            bitsetBySection.put(sectionPos, set);
        }

        Vec3i localPos = SectionBitSet.toLocalCoords(pos);
        set.set(localPos, true);
        blocks.add(pos);
        this.isRadiusCalculated = false;
    }

    public void removeBlock(BlockPos pos) {
        long sectionPos = SectionPos.of(pos).asLong();
        SectionBitSet set = bitsetBySection.get(sectionPos);

        if (set != null) {
            Vec3i localPos = SectionBitSet.toLocalCoords(pos);
            set.set(localPos, false);

            if (set.isEmpty()) {
                bitsetBySection.remove(sectionPos);
            }
        }
        
        blocks.remove(pos);
        this.isRadiusCalculated = false;
    }

    public boolean containsSection(SectionPos pos) {
        return bitsetBySection.containsKey(pos.asLong());
    }

    public boolean containsBlock(BlockPos pos) {
        long sectionPos = SectionPos.of(pos).asLong();
        SectionBitSet set = bitsetBySection.get(sectionPos);

        if (set != null) {
            Vec3i localPos = SectionBitSet.toLocalCoords(pos);
            return set.get(localPos);
        }

        return false;
    }

    public SectionBitSet[] getAllInChunk(ChunkAccess chunk) {
        SectionBitSet[] array = new SectionBitSet[chunk.getMaxSection() - chunk.getMinSection()];

        for (int index = 0; index < chunk.getMaxSection() - chunk.getMinSection(); index++) {
            int y = index + chunk.getMinSection();
            long sectionPos = SectionPos.asLong(chunk.getPos().x, y, chunk.getPos().z);
            array[index] = this.bitsetBySection.get(sectionPos);
        }

        return array;
    }

    public Vec3i getCenter() {
        if (!this.isCenterCalculated) {
            Vec3 center = Vec3.ZERO;
            for (long sectionID : this.bitsetBySection.keySet()) {
                center.add(SectionPos.x(sectionID), SectionPos.y(sectionID), SectionPos.z(sectionID));
            }
            if (this.bitsetBySection.size() > 0) center.scale(1 / this.bitsetBySection.size());
            this.isCenterCalculated = true;

            this.center = new Vec3i((int) (center.x() * 16), (int) (center.y() * 16), (int) (center.z() * 16));
        }

        return this.center;
    }

    // Returns the maximum bounds of the workstation. Used for some sanity checks.
    public int getRoughMaxRadius() {
        if (!this.isRadiusCalculated) {
            Vec3i c = this.getCenter();
            Vec3 center = new Vec3(c.getX(), c.getY(), c.getZ());

            long furthestSection = 0;
            double maxDistance = 0;
            for (long sectionID : this.bitsetBySection.keySet()) {
                double dist = center.distanceToSqr(SectionPos.x(sectionID) * 16, SectionPos.y(sectionID) * 16, SectionPos.z(sectionID) * 16);
                if (dist > maxDistance) {
                    furthestSection = sectionID;
                    maxDistance = dist;
                }
            }

            this.maxRadius = (int) Math.ceil(center.distanceTo(new Vec3(SectionPos.x(furthestSection) * 16, SectionPos.y(furthestSection) * 16, SectionPos.z(furthestSection) * 16))) + 8;
            this.isRadiusCalculated = true;
        }

        return this.maxRadius;
    }

    @NotNull
    @Override
    public Iterator<BlockPos> iterator() {
        return this.blocks.iterator();
    }

    @Override
    public void forEach(Consumer<? super BlockPos> action) {
        this.blocks.forEach(action);
    }

    @Override
    public Spliterator<BlockPos> spliterator() {
        return this.blocks.spliterator();
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for (Long2ObjectMap.Entry<SectionBitSet> entry : this.bitsetBySection.long2ObjectEntrySet()) {
            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putLong("Position", entry.getLongKey());
            sectionTag.putLongArray("Bits", entry.getValue().toLongArray());
            tag.put("Section " + i++, sectionTag);
        }
        return tag;
    }

    public static WorkstationBlocks deserialize(CompoundTag tag) {
        WorkstationBlocks blocks = new WorkstationBlocks();
        for (int i = 0; i < tag.size(); i++) {
            CompoundTag sectionTag = tag.getCompound("Section " + i);
            SectionBitSet set = (SectionBitSet) BitSet.valueOf(sectionTag.getLongArray("Bits"));
            long sectionID = sectionTag.getLong("Position");
            blocks.bitsetBySection.put(sectionID, set);

            SectionPos section = SectionPos.of(sectionID);
            //much as it pains me it's more efficient just to have this
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (set.get(x, y, z)) {
                            blocks.blocks.add(new BlockPos(x + section.minBlockX(), y + section.minBlockY(), z + section.minBlockZ()));
                        }
                    }
                }
            }

        }
        return blocks;
    }
}
