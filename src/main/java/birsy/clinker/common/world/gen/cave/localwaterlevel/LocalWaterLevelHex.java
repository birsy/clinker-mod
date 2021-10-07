package birsy.clinker.common.world.gen.cave.localwaterlevel;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Hex;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocalWaterLevelHex extends Hex {
    private final Random cellRand;
    private final long worldSeed;
    /* Index 0 = Water Height
     * Index 1 = Aquifer Depth */
    public final List<int[]> aquifers;
    private final int aquiferAmount;

    private static final int aquiferLayers = 10;
    public static final int hexSize = 30;
    private static final int borderSize = 6;


    public LocalWaterLevelHex (int q, int r, long worldSeed) {
        super(q, r, hexSize);
        this.worldSeed = worldSeed;
        this.cellRand = new Random(this.hashCode() * this.worldSeed);

        this.aquiferAmount = (int) (MathUtils.bias(cellRand.nextFloat(), 0.1F) * 6);
        this.aquifers = new ArrayList<>();
        this.initAquifers();

    }

    public LocalWaterLevelHex (Hex hex, long worldSeed) {
        this(hex.q(), hex.r(), worldSeed);
    }

    private void initAquifers() {
        List<Float> disallowedHeights = new ArrayList<>();
        for (int i = 0; i < aquiferAmount; i++) {
            //the Math.floor() makes aquifers fall along the same height more frequently.
            float aquiferHeight = (float) (Math.floor(MathUtils.bias(cellRand.nextFloat(), 0.5F) * aquiferLayers) * (255.0F / aquiferLayers));
            while (disallowedHeights.contains(aquiferHeight)) {
                aquiferHeight = (float) (Math.floor(MathUtils.bias(cellRand.nextFloat(), 0.5F) * aquiferLayers) * (255.0F / aquiferLayers));
            }
            disallowedHeights.add(aquiferHeight);

            float aquiferDepth = (MathUtils.bias(cellRand.nextFloat(), 0.5F) * 40.0F) + 8;

            aquifers.add(new int[]{(int) aquiferHeight, (int) aquiferDepth});
        }

        //If an aquifer intersects another aquifer, combine them into one.
        for (int i = 0; i < aquifers.size() - 1; i++) {
            if (getAquiferBottomY(i) < aquifers.get(i + 1)[0]) {
                aquifers.set(i, new int[]{aquifers.get(i)[0], aquifers.get(i + 1)[1]});
                aquifers.remove(i + 1);
            }
        }

        if (aquifers.size() != 0) {
            for (int[] aquifer : aquifers) {
                Clinker.LOGGER.info("Aquifer with water level of " + aquifer[0] + " and depth of " + aquifer[1] + " placed at " + this.toString());
            }
        } else {
            Clinker.LOGGER.info("No aquifers placed at " + this.toString());
        }
    }

    public int getAquiferBottomY(int index) {
        return aquifers.get(index)[0] - aquifers.get(index)[1];
    }

    public boolean isWithinAquifer(int y) {
        for (int[] aquifer : aquifers) {
            if (y < aquifer[0] && y > aquifer[0] - aquifer[1]) {
                return true;
            }
        }
        
        return false;
    }

    public boolean isWithinAquiferWater(int y) {
        for (int[] aquifer : aquifers) {
            if (y < aquifer[0] && y > aquifer[0] - (aquifer[1] - borderSize)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns -1 if not in an aquifer. If it is, it returns the index of the aquifer.
     */
    public int isWithinAquiferAtIndex(int y) {
        for (int i = 0; i < aquifers.size(); i++) {
            int[] aquifer = aquifers.get(i);
            if (y < aquifer[0] && y > aquifer[0] - aquifer[1]) {
                return i;
            }
        }

        return -1;
    }

    public static void buildAquiferAtPos(BlockPos placementPos, BlockPos samplePos, LocalWaterLevelHex sampleHex, IChunk chunkIn, BlockState wallBlock, BlockState fluidBlock, long worldSeed) {
        if (!chunkIn.getBlockState(placementPos).isSolid()) {
            int aquiferIndex = sampleHex.isWithinAquiferAtIndex(samplePos.getY());
            if (aquiferIndex != -1) {
                if (samplePos.getY() < sampleHex.getAquiferBottomY(aquiferIndex) + borderSize) {
                    chunkIn.setBlockState(placementPos, wallBlock, false);
                    return;
                } else {
                    double blockQ = Hex.blockToHexQ(samplePos.getX(), hexSize);
                    double blockR = Hex.blockToHexR(samplePos.getX(), samplePos.getZ(), hexSize);
                    LocalWaterLevelHex adjacentHex = sampleHex.adjacent(sampleHex.q(), sampleHex.r(), blockQ, blockR, worldSeed);

                    if (!adjacentHex.isWithinAquifer(samplePos.getY())) {
                        float radius = (float) Hex.radius(sampleHex.q(), sampleHex.r(), blockQ, blockR) * hexSize;
                        if (radius > hexSize - borderSize) {
                            chunkIn.setBlockState(placementPos, wallBlock, false);
                            return;
                        }
                    }
                }

                chunkIn.setBlockState(placementPos, fluidBlock, true);
            }
        }
    }

    /**
     * Calculates the nearest adjacent hex to an axial coordinate {@code (fq, fr)}, residing in a hex of axial coordinates {@code (q, r)}.
     */
    public static LocalWaterLevelHex adjacent(int q, int r, double fq, double fr, long worldSeed) {
        final double s = axialToCube(q, r);
        final double fs = axialToCube(fq, fr);

        final double dq = q - fq;
        final double dr = r - fr;
        final double ds = s - fs;

        final double qr = Math.abs(dq - dr);
        final double rs = Math.abs(dr - ds);
        final double sq = Math.abs(ds - dq);

        if (qr >= rs && qr >= sq)
        {
            return dr > dq ? new LocalWaterLevelHex(q + 1, r - 1, worldSeed) : new LocalWaterLevelHex(q - 1, r + 1, worldSeed);
        }
        else if (rs >= sq)
        {
            return dr > ds ? new LocalWaterLevelHex(q, r - 1, worldSeed) : new LocalWaterLevelHex(q, r + 1, worldSeed);
        }
        else
        {
            return ds > dq ? new LocalWaterLevelHex(q + 1, r, worldSeed) : new LocalWaterLevelHex(q - 1, r, worldSeed);
        }
    }
}
