package birsy.clinker.common.alchemy.workstation;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlchemicalWorkstation implements INBTSerializable<CompoundTag> {
    public Level level;
    private Set<BlockPos> containedPositions;
    private final List<PhyiscalItem> items;

    public AlchemicalWorkstation(Level level, BlockPos initialPos) {
        this(level);
        this.containedPositions.add(initialPos);
    }
    public AlchemicalWorkstation(Level level) {
        this.level = level;
        this.items = new ArrayList<>();
        this.containedPositions = new HashSet<>();
    }

    public void tick(float deltaTime) {
        for (PhyiscalItem item : items) {
            item.tick(deltaTime);
        }
    }

    public void addItem(ItemStack stack, Vec3 position) {
        items.add(new PhyiscalItem(this, stack, position));
    }

    public List<PhyiscalItem> getItems() {
        return items;
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
