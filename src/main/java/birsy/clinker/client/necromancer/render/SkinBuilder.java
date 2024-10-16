package birsy.clinker.client.necromancer.render;

import com.mojang.blaze3d.vertex.*;

public class SkinBuilder {
    private final float textureSizeX, textureSizeY;
    private final BufferBuilder bufferBuilder;

    public SkinBuilder(float textureSizeX, float textureSizeY) {
        this.textureSizeX = textureSizeX;
        this.textureSizeY = textureSizeY;
        this.bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, NecromancerVertexFormat.SKINNED_ENTITY);
    }

    public SkinBuilder cube(float offsetX, float offsetY, float offsetZ,
                            float sizeX, float sizeY, float sizeZ,
                            float u, float v,
                            int boneIndex) {
        quad(PlaneAxis.X_POSITIVE, false, offsetX + sizeX, offsetY, offsetZ,
                sizeZ, sizeY,
                u, v + sizeZ, boneIndex);
        quad(PlaneAxis.X_NEGATIVE, false, offsetX, offsetY, offsetZ,
                sizeZ, sizeY,
                u + sizeZ + sizeX, v + sizeZ, boneIndex);
        quad(PlaneAxis.Y_POSITIVE, false, offsetX, offsetY + sizeY, offsetZ,
                sizeX, sizeZ,
                u + sizeZ, v, boneIndex);
        quad(PlaneAxis.Y_NEGATIVE, false, offsetX, offsetY, offsetZ,
                sizeX, sizeZ,
                u + sizeX + sizeZ, v, boneIndex);
        // front face
        quad(PlaneAxis.Z_POSITIVE, false, offsetX, offsetY, offsetZ + sizeZ,
                sizeX, sizeY,
                u + sizeZ, v + sizeZ, boneIndex);
        quad(PlaneAxis.Z_NEGATIVE, false, offsetX, offsetY, offsetZ,
                sizeX, sizeY,
                u, v + sizeZ, boneIndex);


        return this;
    }

    public SkinBuilder quad(PlaneAxis axis, boolean doubleSided, float offsetX, float offsetY, float offsetZ, float width, float height, float u, float v, int boneIndex) {
        if (doubleSided) {
            this.quad(axis, false, offsetX, offsetY, offsetZ, width, height, u, v, boneIndex);
            this.quad(axis.opposite(), false, offsetX, offsetY, offsetZ, width, height, u, v, boneIndex);
            return this;
        }

        float u1 = u, v1 = v;
        float u2 = u + width, v2 = v + height;

        float nX = axis.normalX(), nY = axis.normalY(), nZ = axis.normalZ();
        float pX = axis.perpendicularX(), pY = axis.perpendicularY(), pZ = axis.perpendicularZ();
        float oX = axis.opposite().perpendicularX(), oY = axis.opposite().perpendicularY(), oZ = axis.opposite().perpendicularZ();

        // triangle 1
        vertex(offsetX + pX * width * 0 + oX * height * 0,
               offsetY + pY * width * 0 + oY * height * 0,
               offsetZ + pZ * width * 0 + oZ * height * 0,
               u1, v1, nX, nY, nZ, boneIndex);
        vertex(offsetX + pX * width * 0 + oX * height * 1,
               offsetY + pY * width * 0 + oY * height * 1,
               offsetZ + pZ * width * 0 + oZ * height * 1,
               u1, v2, nX, nY, nZ, boneIndex);
        vertex(offsetX + pX * width * 1 + oX * height * 1,
               offsetY + pY * width * 1 + oY * height * 1,
               offsetZ + pZ * width * 1 + oZ * height * 1,
               u2, v2, nX, nY, nZ, boneIndex);

        // triangle 2
        vertex(offsetX + pX * width * 1 + oX * height * 1,
                offsetY + pY * width * 1 + oY * height * 1,
                offsetZ + pZ * width * 1 + oZ * height * 1,
                u2, v2, nX, nY, nZ, boneIndex);
        vertex(offsetX + pX * width * 1 + oX * height * 0,
               offsetY + pY * width * 1 + oY * height * 0,
               offsetZ + pZ * width * 1 + oZ * height * 0,
               u2, v1, nX, nY, nZ, boneIndex);
        vertex(offsetX + pX * width * 0 + oX * height * 0,
                offsetY + pY * width * 0 + oY * height * 0,
                offsetZ + pZ * width * 0 + oZ * height * 0,
                u1, v1, nX, nY, nZ, boneIndex);

        return this;
    }

    public Skin build() {
        VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

        BufferBuilder.RenderedBuffer renderedBuffer = this.bufferBuilder.end();

        vbo.bind();
        vbo.upload(renderedBuffer);
        VertexBuffer.unbind();

        return new VertexBufferSkin(vbo);
    }

    private void vertex(float x, float y, float z, float u, float v, float nx, float ny, float nz, int boneIndex) {
        bufferBuilder
                .vertex(x, y, z)
                .color(0xFFFFFFFF)
                .uv(u / this.textureSizeX, v / this.textureSizeY)
                .normal(nx, ny, nz)
                .misc(NecromancerVertexFormat.ELEMENT_BONE_INDEX, boneIndex)
                .endVertex();
    }

    public enum PlaneAxis {
        X_POSITIVE(0), X_NEGATIVE(1),
        Y_POSITIVE(2), Y_NEGATIVE(3),
        Z_POSITIVE(4), Z_NEGATIVE(5);

        public final int ordinal;

        PlaneAxis(int o) {
            this.ordinal = o;
        }

        private static final PlaneAxis[] OPPOSITE = new PlaneAxis[] {
                X_NEGATIVE, X_POSITIVE,
                Y_NEGATIVE, Y_POSITIVE,
                Z_NEGATIVE, Z_NEGATIVE
        };
        public PlaneAxis opposite() {
            return OPPOSITE[this.ordinal];
        }

        private static final int[] NORMAL_DIRECTIONS = new int[] {
                1, 0, 0,   -1, 0, 0,
                0, 1, 0,   0, -1, 0,
                0, 0, 1,   0, 0, -1,
        };
        public int normalX() {return NORMAL_DIRECTIONS[this.ordinal * 3 + 0];}
        public int normalY() {return NORMAL_DIRECTIONS[this.ordinal * 3 + 1];}
        public int normalZ() {return NORMAL_DIRECTIONS[this.ordinal * 3 + 2];}

        private static final int[] PERPENDICULAR_DIRECTIONS = new int[] {
                0, 0, 1,   0, 1, 0,
                1, 0, 0,   0, 0, 1,
                0, 1, 0,   1, 0, 0,
        };
        public int perpendicularX() {return PERPENDICULAR_DIRECTIONS[this.ordinal * 3 + 0];}
        public int perpendicularY() {return PERPENDICULAR_DIRECTIONS[this.ordinal * 3 + 1];}
        public int perpendicularZ() {return PERPENDICULAR_DIRECTIONS[this.ordinal * 3 + 2];}
    }
}
