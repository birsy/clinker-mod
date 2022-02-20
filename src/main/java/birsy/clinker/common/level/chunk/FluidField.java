package birsy.clinker.common.level.chunk;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FluidField {
    private final ChunkAccess chunk;
    private final FluidCell[][][] cellArray;

    public FluidField(ChunkAccess chunkIn) {
        this.chunk = chunkIn;
        this.cellArray = new FluidCell[16][this.chunk.getMaxBuildHeight()][16];
    }

    public FluidCell getLocalCell(int x, int y, int z) {
        return cellArray[x][y][z];
    }

    public FluidCell getCell(int x, int y, int z) {
        try {
            return getLocalCell(x - chunk.getPos().getMinBlockX(), y, z - chunk.getPos().getMinBlockZ());
        } catch (Exception e) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void update() {
        diffuse(0.5F);
    }

    public void diffuse(float amountOfChange) {
        for (int x = 0; x < cellArray.length; x++) {
            for (int y = 0; y < cellArray[0].length; y++) {
                for (int z = 0; z < cellArray[0][0].length; z++) {
                    FluidCell cell = getLocalCell(x, y, z);
                    FluidCell cellU = getLocalCell(x, y + 1, z);
                    FluidCell cellD = getLocalCell(x, y - 2, z);
                    FluidCell cellN = getLocalCell(x, y, z - 1);
                    FluidCell cellS = getLocalCell(x, y, z + 1);
                    FluidCell cellE = getLocalCell(x - 1, y, z);
                    FluidCell cellW = getLocalCell(x + 1, y, z);

                    float currentDensity = cell.density;
                    float average = (cell.density + cellU.density + cellD.density + cellN.density + cellS.density + cellE.density + cellW.density) / 7;
                    float nextDensity = (currentDensity + (amountOfChange * average)) / (1 + amountOfChange);
                }
            }
        }
    }

    public class FluidCell {
        private Collection<Fluid> fluids = ForgeRegistries.FLUIDS.getValues();
        public Vec3 velocity;
        float density;
        public List<Pair<Fluid, Float>> makeup = Util.make(new ArrayList<>(fluids.size()), (arrayList) -> {
            for (int i = 0; i < fluids.size(); i++) {
                arrayList.add(i, new Pair(fluids.toArray()[i], 0.0F));
            }
        });
    }
}
