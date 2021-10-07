
package birsy.clinker.core.util;

import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;

/*
 * Ripped from the HexLands mod! Code by AlcatrazEscape, I assume.
 * Will be used for generating aquifer coordinates, instead of the cell system vanilla uses. Should look more natural.
 */

/**
 * See https://www.redblobgames.com/grids/hexagons/
 */
public class Hex
{
    private static final double SQRT_3 = 1.7320508075688772;

    /**
     * Calculates the containing hex of block coordinates {@code (x, z)}.
     */
    public static Hex blockToHex(double x, double z, double size)
    {
        final double q = blockToHexQ(x, size);
        final double r = blockToHexR(x, z, size);
        return hexRound(q, r, size);
    }

    /**
     * Calculates axial coordinate {@code q} from block coordinate {@code x}.
     */
    public static double blockToHexQ(double x, double size)
    {
        return ((2d / 3d) * x) / size;
    }

    /**
     * Calculates axial coordinate {@code r} from block coordinates {@code (x, z)}.
     */
    public static double blockToHexR(double x, double z, double size)
    {
        return ((-1d / 3d) * x + (SQRT_3 / 3d) * z) / size;
    }

    /**
     * Calculates {@code s} from axial coordinates {@code (q, r)}
     */
    public static int axialToCube(int q, int r)
    {
        return -q - r;
    }

    /**
     * Calculates {@code s} from axial coordinates {@code (q, r)}
     */
    public static double axialToCube(double q, double r)
    {
        return -q - r;
    }

    /**
     * Calculates block coordinates {@code (x, z)} from the axial coordinates {@code (q, r)}.
     */
    public static BlockPos hexToBlock(int q, int r, double size)
    {
        final double x = hexToBlockX(q, size);
        final double z = hexToBlockZ(q, r, size);
        return new BlockPos(Math.round(x), 0, Math.round(z));
    }

    /**
     * Calculates block coordinate {@code x} from axial coordinate {@code q}
     */
    public static double hexToBlockX(double q, double size)
    {
        return size * ((3d / 2d) * q);
    }

    /**
     * Calculates block coordinate {@code z} from axial coordinates {@code (q, r)}
     */
    public static double hexToBlockZ(double q, double r, double size)
    {
        return size * ((SQRT_3 / 2d) * q + SQRT_3 * r);
    }

    /**
     * Rounds floating point axial coordinates {@code (q, r)} to the nearest integer valued hex.
     */
    public static Hex hexRound(double q, double r, double size)
    {
        double s = axialToCube(q, r);

        int rq = (int) Math.round(q);
        int rr = (int) Math.round(r);
        int rs = (int) Math.round(s);

        double dq = Math.abs(rq - q);
        double dr = Math.abs(rr - r);
        double ds = Math.abs(rs - s);

        if (dq > dr && dq > ds)
        {
            rq = axialToCube(rr, rs);
        }
        else if (dr > ds && dr > dq)
        {
            rr = axialToCube(rq, rs);
        }

        return new Hex(rq, rr, size);
    }

    /**
     * Calculates the nearest adjacent hex to an axial coordinate {@code (fq, fr)}, residing in a hex of axial coordinates {@code (q, r)}.
     */
    public static Hex adjacent(int q, int r, double fq, double fr, double size)
    {
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
            return dr > dq ? new Hex(q + 1, r - 1, size) : new Hex(q - 1, r + 1, size);
        }
        else if (rs >= sq)
        {
            return dr > ds ? new Hex(q, r - 1, size) : new Hex(q, r + 1, size);
        }
        else
        {
            return ds > dq ? new Hex(q + 1, r, size) : new Hex(q - 1, r, size);
        }
    }

    /**
     * Calculates the angular radius between an axial coordinate {@code (q, r)}, representing the hex center, and an axial coordinate {@code (fq, fr)} representing another point in the hex.
     *
     * @return A value in the range [0, 1].
     */
    public static double radius(int q, int r, double fq, double fr)
    {
        final double s = axialToCube(q, r);
        final double fs = axialToCube(fq, fr);

        final double dq = q - fq;
        final double dr = r - fr;
        final double ds = s - fs;

        final double qr = Math.abs(dq - dr);
        final double rs = Math.abs(dr - ds);
        final double sq = Math.abs(ds - dq);

        return (1d / 2d) * (qr + rs + sq);
    }

    private final int q, r; // Axial / Cube coordinates: q = x, r = z
    private final double size;

    public Hex(int q, int r, double size)
    {
        this.q = q;
        this.r = r;
        this.size = size;
    }

    /**
     * Calculates the center of this hex in block coordinates {@code (x, z)}.
     */
    public BlockPos center()
    {
        return hexToBlock(q, r, size);
    }

    /**
     * Calculates the axial radius from the center of this hex to the given block coordinate {@code (x, z)}.
     *
     * @return A value in the range [0, 1].
     */
    public double radius(double x, double z)
    {
        final double fq = blockToHexQ(x, size);
        final double fr = blockToHexR(x, z, size);
        return Hex.radius(q, r, fq, fr);
    }

    /**
     * Calculates the nearest adjacent hex to the given block coordinate {@code (x, z)} within this hex.
     */
    public Hex adjacent(double x, double z)
    {
        final double fq = blockToHexQ(x, size);
        final double fr = blockToHexR(x, z, size);
        return Hex.adjacent(q, r, fq, fr, size);
    }

    public int q()
    {
        return q;
    }

    public int r()
    {
        return r;
    }

    public double x()
    {
        return hexToBlockX(q, size);
    }

    public double z()
    {
        return hexToBlockZ(q, r, size);
    }

    @Override
    public int hashCode()
    {
        return q * 31 + r;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hex hex = (Hex) o;
        return q == hex.q && r == hex.r;
    }

    @Override
    public String toString()
    {
        return "Hex{" + "q=" + q + ", r=" + r + '}';
    }
}