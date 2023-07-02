package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.rigidbody.ColliderBody;
import birsy.clinker.core.util.rigidbody.VerletPhysicsEnvironment;
import birsy.clinker.core.util.rigidbody.colliders.MeshCollisionShape;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class AlchemicalWorkstation extends VerletPhysicsEnvironment implements INBTSerializable<CompoundTag> {
    public Level level;
    private List<PhysicalItem> items;
    public Set<BlockPos> containedBlocks;
    public boolean filledCollisions;
    public final UUID id;

    public AlchemicalWorkstation(Level level, BlockPos initialPos) {
        this(level);
        filledCollisions = false;
    }
    public AlchemicalWorkstation(Level level) {
        this(UUID.randomUUID());
        this.level = level;
        this.containedBlocks = new HashSet<>();
        this.items = new ArrayList<>();
    }

    public AlchemicalWorkstation(UUID id) {
        super();
        this.id = id;
    }

    public void fillCollisions(Level level, BlockPos initialPos) {
        BlockPos.MutableBlockPos blockPos = initialPos.mutable();
        int radius = 2;
        for (int x = -radius; x < radius + 1; x++) {
            for (int y = -1; y < 3; y++) {
                for (int z = -radius; z < radius + 1; z++) {
                    blockPos.set(initialPos);
                    blockPos.move(x, y, z);
                    this.containedBlocks.add(blockPos);
                    BlockState state = level.getBlockState(blockPos);
                    if (!state.isAir()) {
                        VoxelShape shape = state.getCollisionShape(level, blockPos);
                        for (AABB aabb : shape.toAabbs()) {
                            bodies.add(new ColliderBody(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), new Quaterniond(Vector3f.XP.rotationDegrees(0)),
                                    MeshCollisionShape.Box(new Vec3(aabb.minX, aabb.minY, aabb.minZ), new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ))));
                        }
                    }
                }
            }
        }
        //Clinker.LOGGER.info(this.containedBlocks);
        filledCollisions = true;
    }

    public void addItem(ItemStack stack, Vec3 position) {
        PhysicalItem PE = new PhysicalItem(this, stack, position);
        bodies.add(PE);
        items.add(PE);
    }

    public List<PhysicalItem> getItems() {
        return items;
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {}
}
