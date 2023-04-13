package birsy.clinker.common.world.level.chunk;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

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
        diffuse(1.0F);
        advect(1.0F);
    }

    public void diffuse(float delta) {
        for (int x = 0; x < cellArray.length; x++) {
            for (int y = 0; y < cellArray[0].length; y++) {
                for (int z = 0; z < cellArray[0][0].length; z++) {
                    FluidCell cell = getLocalCell(x, y, z);
                    FluidCell[] neighboringCells = {getLocalCell(x + 1, y, z), getLocalCell(x - 1, y, z), getLocalCell(x, y + 1, z), getLocalCell(x, y - 1, z), getLocalCell(x, y, z + 1), getLocalCell(x, y, z - 1)};

                    //get current fluid attributes
                    float cDensity = cell.density.get();
                    float[] cSurroundingDensities = Util.make(new float[6], (array) -> {
                        for (int i = 0; i < array.length; i++) {
                            array[i] = neighboringCells[i].density.get();
                        }
                    });
                    float averageSurroundingDensity = 0;
                    for (float neighborDensity : cSurroundingDensities) {
                        averageSurroundingDensity += neighborDensity;
                    }
                    averageSurroundingDensity /= neighboringCells.length;


                    float nDensity = cDensity;
                    float[] nSurroundingDensities = cSurroundingDensities;

                    float nAverageSurroundingDensity = 0;
                    for (float neighborDensity : nSurroundingDensities) {
                        nAverageSurroundingDensity += neighborDensity;
                    }
                    nAverageSurroundingDensity /= neighboringCells.length;

                    for (int iteration = 0; iteration < 5; iteration++) {
                        for (int i = 0; i < cSurroundingDensities.length; i++) {
                            nSurroundingDensities[i] = 6 * ((nDensity * (1 + delta) - cDensity) / delta) - (nAverageSurroundingDensity * neighboringCells.length - nSurroundingDensities[i]);
                        }
                        nDensity = (cDensity + delta * (nAverageSurroundingDensity)) / (1 + delta);
                    }

                    cell.density.setAttributes(nDensity);
                    /*List<FluidProperty<?>> properties = cell.properties;
                    for (int pI = 0; pI < properties.size(); pI++) {
                    int finalPI = pI;

                    FluidProperty<?> property = properties.get(pI);
                        float[] propertyAttributes = property.getAttributes();
                        for (int vI = 0; vI < property.amountOfAttributes; vI++) {
                            int finalVI = vI;

                            float cAttribute = propertyAttributes[vI];
                            float[] cSurroundingAttributes = Util.make(new float[6], (array) -> { for (int i = 0; i < array.length; i++) { array[i] = neighboringCells[i].properties.get(finalPI).getAttributes()[finalVI]; } });
                            float averageSurroundingAttribute = 0; for (float neighborDensity : cSurroundingAttributes) { averageSurroundingAttribute += neighborDensity; } averageSurroundingAttribute /= neighboringCells.length;

                            float nAttribute = cAttribute;
                            float[] nSurroundingAttributes = cSurroundingAttributes;
                            for (int iteration = 0; iteration < 5; iteration++) {
                                float nAverageSurroundingAttribute = 0; for (float neighborDensity : nSurroundingAttributes) { nAverageSurroundingAttribute += neighborDensity; } nAverageSurroundingAttribute /= neighboringCells.length;

                                for (int i = 0; i < cSurroundingAttributes.length; i++) {
                                    nSurroundingAttributes[i] = 6 * ((nAttribute * (1 + delta) - cAttribute) / delta) - (nAverageSurroundingAttribute * neighboringCells.length - nSurroundingAttributes[i]);
                                }
                                nAttribute = (cAttribute + delta * (nAverageSurroundingAttribute)) / (1 + delta);
                            }

                            propertyAttributes[vI] = nAttribute;
                        }

                        property.setAttributes(propertyAttributes);
                    }*/
                }
            }
        }
    }


    public void advect(float delta) {
        for (int x = 0; x < cellArray.length; x++) {
            for (int y = 0; y < cellArray[0].length; y++) {
                for (int z = 0; z < cellArray[0][0].length; z++) {
                    FluidCell cell = getLocalCell(x, y, z);
                    Vec3 position = new Vec3(x, y, z);
                    Vec3 fO = position.subtract(cell.velocity.scale(delta));

                    Vec3i fOMin = new Vec3i(fO.x(), fO.y(), fO.z());
                    Vec3i fOMax = fOMin.offset(1, 1, 1);
                    Vec3 fOfrac = new Vec3(Mth.frac(fO.x()), Mth.frac(fO.y()), Mth.frac(fO.z()));

                    float density000 = getLocalCell(fOMin.getX(), fOMin.getY(), fOMin.getZ()).density.get();
                    float density001 = getLocalCell(fOMin.getX(), fOMin.getY(), fOMax.getZ()).density.get();
                    float density010 = getLocalCell(fOMin.getX(), fOMax.getY(), fOMin.getZ()).density.get();
                    float density011 = getLocalCell(fOMin.getX(), fOMax.getY(), fOMax.getZ()).density.get();
                    float density100 = getLocalCell(fOMax.getX(), fOMin.getY(), fOMin.getZ()).density.get();
                    float density101 = getLocalCell(fOMax.getX(), fOMin.getY(), fOMax.getZ()).density.get();
                    float density110 = getLocalCell(fOMax.getX(), fOMax.getY(), fOMin.getZ()).density.get();
                    float density111 = getLocalCell(fOMax.getX(), fOMax.getY(), fOMax.getZ()).density.get();;

                    float density00 = (float) Mth.lerp(fOfrac.x(), density000, density100);
                    float density01 = (float) Mth.lerp(fOfrac.x(), density001, density101);
                    float density10 = (float) Mth.lerp(fOfrac.x(), density010, density110);
                    float density11 = (float) Mth.lerp(fOfrac.x(), density011, density111);

                    float density0 = (float) Mth.lerp(fOfrac.z(), density00, density01);
                    float density1 = (float) Mth.lerp(fOfrac.z(), density10, density11);

                    cell.density.setAttributes((float) Mth.lerp(fOfrac.y(), density0, density1));
                }
            }
        }
    }


    public class FluidCell {
        private Collection<Fluid> fluids = ForgeRegistries.FLUIDS.getValues();
        //TODO: turn velocity into a FluidProperty
        public Vec3 velocity;
        FluidProperty<Float> density = new FluidProperty<>(1, (num) -> new float[]{num}, (array) -> array[0]);
        List<FluidProperty<?>> properties;

        public FluidCell(List<FluidProperty<?>> properties) {
            this.properties = properties;
        }
    }

    //TODO: make fluid properties fluid-field-wide rather than per-cell.
    // this was a really dumb decision.
    public static class FluidProperty<P> {
        final int amountOfAttributes;
        final GetAttributeFunction<P> getAttributes;
        final SetAttributeFunction<P> setAttributes;
        private P property;

        public FluidProperty(int amountOfAttributes, GetAttributeFunction<P> getAttributes, SetAttributeFunction<P> setAttributes) {
            this.amountOfAttributes = amountOfAttributes;
            this.getAttributes = getAttributes;
            this.setAttributes = setAttributes;
        }

        public P get() {
            return property;
        }

        public float[] getAttributes() {
            return getAttributes.run(property);
        }

        public void setAttributes(float... attributes) {
            this.property = setAttributes.run(attributes);
        }

        interface GetAttributeFunction<P> {
            float[] run(P attributes);
        }

        interface SetAttributeFunction<P> {
            P run(float[] attribute);
        }
    }
}
