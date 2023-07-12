package birsy.clinker.common.world.physics.rigidbody.gjkepa;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a Simplex or Polytope for use in GJK & EPA, in the Minkowski Difference space.
 * All points contain a reference to the original points they were formed from.
 */
public class Simplex extends ArrayList<Support> {

    /**
     * Constructs a simplex given a vararg of points.
     * @param points A vararg of points
     */
    public Simplex(Support... points) {
        super(Arrays.asList(points));
    }

}