package birsy.clinker.client.entity.mogul;

import birsy.clinker.client.necromancer.render.mesh.StaticMesh;

public class MogulWarhook {
    public static final StaticMesh WARHOOK = new StaticMesh(32, 32);
    static {
        WARHOOK.addCube(1F, 4F, 2F, 0F, 16.5F, -6F, 0F, 0F, 0F, 6F, 23F, false);
        WARHOOK.addCube(2F, 14F, 3F, -0.5F, -9.5F, -1.5F, -0.2F, -0.2F, -0.2F, 6F, 0F, false);
        WARHOOK.addCube(1F, 29F, 2F, 0F, -17.5F, -1F, 0F, 0F, 0F, 0F, 0F, false);
        WARHOOK.addCube(1F, 1F, 4F, 0F, -18.5F, -2F, 0F, 0F, 0F, 16F, 23F, false);
        WARHOOK.addCube(1F, 1F, 4F, 0F, -21.5F, -2F, 0F, 0F, 0F, 22F, 24F, false);
        WARHOOK.addCube(1F, 2F, 1F, 0F, -20.5F, -2F, 0F, 0F, 0F, 16F, 4F, false);
        WARHOOK.addCube(1F, 2F, 1F, 0F, -20.5F, 1F, 0F, 0F, 0F, 25F, 4F, false);
        WARHOOK.addCube(2F, 1F, 6F, -0.5F, -15.5F, -3F, 0F, 0F, 0F, 6F, 23F, false);
        WARHOOK.addCube(1F, 2F, 7F, 0F, 9.5F, 1F, 0F, 0F, 0F, 16F, 0F, false);
        WARHOOK.addCube(1F, 9F, 2F, 0F, 11.5F, 6F, 0F, 0F, 0F, 18F, 9F, false);
        WARHOOK.addCube(1F, 2F, 2F, 0F, 18.5F, 4F, 0F, 0F, 0F, 16F, 0F, false);
        WARHOOK.addCube(1F, 2F, 2F, 0F, 18.5F, -4F, 0F, 0F, 0F, 25F, 0F, false);
        WARHOOK.addCube(1F, 2F, 10F, 0F, 20.5F, -4F, 0F, 0F, 0F, 6F, 11F, false);
    }
}
