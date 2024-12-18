package birsy.clinker.client.entity.mogul;


import foundry.veil.api.client.necromancer.render.Skin;
import foundry.veil.api.client.necromancer.render.mesh.CubeMesh;

public class MogulSkin extends Skin<MogulSkeleton> {
    public MogulSkin() {
        super();
        int texWidth = 256;
        int texHeight = 256;
        CubeMesh mesh0 = new CubeMesh(texWidth, texHeight);

        CubeMesh mesh1 = new CubeMesh(texWidth, texHeight);
        mesh1.addCube(36F, 6F, 36F, -18F, 20F, -19.5F, 0F, 0F, 0F, 112F, 0F, false);
        mesh1.addCube(36F, 20F, 42F, -18F, 0F, -19.5F, 0F, 0F, 0F, 100F, 42F, false);

        CubeMesh mesh2 = new CubeMesh(texWidth, texHeight);
        mesh2.addCube(36F, 25F, 2F, -18F, -24F, 0F, -0.1F, 0F, 0F, 104F, 131F, false);

        CubeMesh mesh3 = new CubeMesh(texWidth, texHeight);
        mesh3.addCube(36F, 25F, 2F, -18F, -24F, -2F, -0.1F, 0F, 0F, 180F, 131F, false);

        CubeMesh mesh4 = new CubeMesh(texWidth, texHeight);
        mesh4.addCube(42F, 25F, 2F, -21F, -24F, 0F, -0.1F, 0F, 0F, 168F, 104F, false);

        CubeMesh mesh5 = new CubeMesh(texWidth, texHeight);
        mesh5.addCube(42F, 25F, 2F, -21F, -24F, 0F, -0.1F, 0F, 0F, 80F, 104F, false);

        CubeMesh mesh6 = new CubeMesh(texWidth, texHeight);
        mesh6.addCube(5F, 13F, 6F, -3F, -13.5F, -3F, 0F, 0F, 0F, 0F, 113F, false);
        mesh6.addCube(4F, 19F, 4F, -2.5F, -32.5F, -1.5F, 0F, 0F, 0F, 0F, 132F, false);
        mesh6.addCube(4F, 10.5F, 4F, -2.5F, -24.5F, -1.5F, 0.4F, 0.4F, 0.4F, 16F, 133F, false);

        CubeMesh mesh8 = new CubeMesh(texWidth, texHeight);
        mesh8.addCube(5F, 24F, 6F, -2.5F, -24F, -3F, 0F, 0F, 0F, 32F, 113F, false);
        mesh8.addCube(5F, 9.5F, 6F, -2.5F, -24F, -3F, 0.25F, 0.25F, 0.25F, 54F, 128F, false);

        CubeMesh mesh9 = new CubeMesh(texWidth, texHeight);
        mesh9.addCube(5F, 24F, 6F, -2.5F, -24F, -3F, 0F, 0F, 0F, 32F, 113F, true);
        mesh9.addCube(5F, 9.5F, 6F, -2.5F, -24F, -3F, 0.25F, 0.25F, 0.25F, 54F, 128F, true);

        CubeMesh mesh10 = new CubeMesh(texWidth, texHeight);
        mesh10.addCube(5F, 13F, 6F, -2.5F, -13.5F, -3F, 0F, 0F, 0F, 0F, 113F, true);
        mesh10.addCube(4F, 10.5F, 4F, -2F, -24.5F, -1.5F, 0.4F, 0.4F, 0.4F, 16F, 133F, true);
        mesh10.addCube(4F, 19F, 4F, -2F, -32.5F, -1.5F, 0F, 0F, 0F, 0F, 132F, true);

        CubeMesh mesh11 = new CubeMesh(texWidth, texHeight);
        mesh11.addCube(12F, 9F, 14F, -6F, -3.25F, -10F, 0F, 0F, 0F, 64F, 0F, false);

        CubeMesh mesh12 = new CubeMesh(texWidth, texHeight);
        mesh12.addCube(18F, 16F, 14F, -9F, -5F, -10.5F, 0F, 0F, 0F, 0F, 0F, false);

        CubeMesh mesh13 = new CubeMesh(texWidth, texHeight);
        mesh13.addCube(18F, 4F, 4F, -9F, -11.666666666666664F, -3.5F, 0F, 0F, 0F, 2F, 48F, false);
        mesh13.addCube(22F, 16F, 4F, -11F, -7.666666666666664F, -3.5F, 0F, 0F, 0F, 0F, 32F, false);
        mesh13.addCube(18F, 2F, 4F, -9F, 8.333333333333336F, -3.5F, 0F, 0F, 0F, 2F, 30F, false);

        CubeMesh mesh14 = new CubeMesh(texWidth, texHeight);
        mesh14.addCube(6F, 6F, 6F, -3F, -6F, 0F, 0F, 0F, 0F, 52F, 30F, false);

        CubeMesh mesh15 = new CubeMesh(texWidth, texHeight);
        mesh15.addCube(20F, 8F, 18F, -10F, -2.25F, -10F, 0F, 0F, 0F, 0F, 63F, false);
        mesh15.addCube(14F, 3F, 2F, -7F, 5.75F, -9F, 0F, 0F, 0F, 0F, 56F, false);

        CubeMesh mesh16 = new CubeMesh(texWidth, texHeight);
        mesh16.addCube(16F, 6F, 12F, -8F, -0.25F, 0F, 0F, 0F, 0F, 58F, 63F, false);
        mesh16.addCube(10F, 5F, 12F, -5F, 5.75F, 0F, 0F, 0F, 0F, 58F, 46F, false);

        CubeMesh mesh17 = new CubeMesh(texWidth, texHeight);
        mesh17.addCube(5F, 9F, 0F, -2.5F, 0F, -7.105427357601002e-15F, 0F, 0F, 0F, 0F, 63F, false);

        CubeMesh mesh18 = new CubeMesh(texWidth, texHeight);
        mesh18.addCube(20F, 10F, 2F, -10F, -10F, -1F, 0F, 0F, 0F, 0F, 89F, false);

        CubeMesh mesh19 = new CubeMesh(texWidth, texHeight);
        mesh19.addCube(15F, 10F, 2F, -7.5F, -10F, -1F, 0F, 0F, 0F, 34F, 101F, false);

        CubeMesh mesh20 = new CubeMesh(texWidth, texHeight);
        mesh20.addCube(15F, 10F, 2F, -7.5F, -10F, -1F, 0F, 0F, 0F, 0F, 101F, false);

        this.addMesh("MogulRoot", mesh0);
        this.addMesh("MogulBody", mesh1);
        this.addMesh("MogulFrontRobe", mesh2);
        this.addMesh("MogulBackRobe", mesh3);
        this.addMesh("MogulLeftRobe", mesh4);
        this.addMesh("MogulRightRobe", mesh5);
        this.addMesh("MogulRightArm", mesh6);
        this.addMesh("MogulRightLeg", mesh8);
        this.addMesh("MogulLeftLeg", mesh9);
        this.addMesh("MogulLeftArm", mesh10);
        this.addMesh("MogulNeck", mesh11);
        this.addMesh("MogulHead", mesh12);
        this.addMesh("MogulFace", mesh13);
        this.addMesh("MogulNose", mesh14);
        this.addMesh("MogulHelmetBase", mesh15);
        this.addMesh("MogulHelmetUpper", mesh16);
        this.addMesh("MogulHelmetOrnament", mesh17);
        this.addMesh("MogulBackHelmetFlap", mesh18);
        this.addMesh("MogulLeftHelmetFlap", mesh19);
        this.addMesh("MogulRightHelmetFlap", mesh20);
    }
}
