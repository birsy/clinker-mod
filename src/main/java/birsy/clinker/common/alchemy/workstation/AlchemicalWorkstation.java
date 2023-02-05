package birsy.clinker.common.alchemy.workstation;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.ColliderBody;
import birsy.clinker.core.util.rigidbody.PhysicsEnvironment;
import birsy.clinker.core.util.rigidbody.colliders.MeshCollisionShape;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlchemicalWorkstation extends PhysicsEnvironment implements INBTSerializable<CompoundTag> {
    public Level level;
    private List<PhysicalItem> items;
    public Set<BlockPos> containedBlocks;
    public boolean filledCollisions;
    public AlchemicalWorkstation(Level level, BlockPos initialPos) {
        this(level);
        filledCollisions = false;
        this.addItem(new ItemStack(Blocks.GLOWSTONE), new Vec3(initialPos));

    }
    public AlchemicalWorkstation(Level level) {
        super();
        this.level = level;
        this.containedBlocks = new HashSet<>();
        this.items = new ArrayList<>();
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
                            bodies.add(new ColliderBody(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Vector3f.XP.rotationDegrees(0),
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
