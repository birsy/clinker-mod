package birsy.clinker.client.render.zosimus;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

abstract class ZosimusBodyPart {
    final ZosimusBodyPart parent;
    float x, y, z,
          xRot, yRot, zRot; // in degrees
    float pX, pY, pZ,
          pXRot, pYRot, pZRot;
    protected final List<ZosimusBodyPart> children = new ArrayList<>();

    protected final Vector3f scratch = new Vector3f();

    public ZosimusBodyPart(float x, float y, float z) {
        this(null, x, y, z);
    }

    public ZosimusBodyPart(@Nullable ZosimusBodyPart parent, float x, float y, float z) {
        this.parent = parent;
        if (parent != null)
            this.parent.children.add(this);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected final void tick() {
        update();
        for (ZosimusBodyPart child : this.children) child.tick();
    }

    protected void update() {
        pX = x; pY = y; pZ = z;
        pXRot = xRot; pYRot = yRot; pZRot = zRot;
    }

    protected final void render(VertexConsumer bufferbuilder, float partialTick,
                                AccumulatedTransform transform) {
        float rX = Mth.lerp(partialTick, pX, x),
              rY = Mth.lerp(partialTick, pY, y),
              rZ = Mth.lerp(partialTick, pZ, z);

        scratch.set(rX, rY, rZ);
        transform.transform(scratch, scratch);

        // snapshot
        float savedX = transform.x, savedY = transform.y, savedZ = transform.z;
        float savedXRot = transform.xRot, savedYRot = transform.yRot, savedZRot = transform.zRot;

        transform.x = scratch.x; transform.y = scratch.y; transform.z = scratch.z;
        transform.xRot += Mth.rotLerp(partialTick, pXRot, xRot);
        transform.yRot += Mth.rotLerp(partialTick, pYRot, yRot);
        transform.zRot += Mth.rotLerp(partialTick, pZRot, zRot);

        this.draw(bufferbuilder, partialTick, transform);
        for (ZosimusBodyPart child : this.children) {
            child.render(bufferbuilder, partialTick, transform);
        }

        // restore
        transform.x = savedX; transform.y = savedY; transform.z = savedZ;
        transform.xRot = savedXRot; transform.yRot = savedYRot; transform.zRot = savedZRot;
    }

    protected void draw(VertexConsumer consumer, float partialTick, AccumulatedTransform transform) {}

    protected static void drawTube(VertexConsumer consumer, Matrix4f matrix,
                                   float x1, float y1, float width1, float u1, float v1, int color1,
                                   float x2, float y2, float width2, float u2, float v2, int color2) {
        float neckX = (x1 - x2), neckY = (y1 - y2);
        float length = Mth.sqrt(neckX*neckX + neckY*neckY);
        float widthOffsetX = (-neckY / length) * 0.5f,
                widthOffsetY = ( neckX / length) * 0.5f;
        consumer.addVertex(matrix, x1 - widthOffsetX * width1, y1 - widthOffsetY * width1, 0)
                .setUv(u1, v1)
                .setColor(color1);
        consumer.addVertex(matrix, x1 + widthOffsetX * width1, y1 + widthOffsetY * width1, 0)
                .setUv(u2, v1)
                .setColor(color1);
        consumer.addVertex(matrix, x2 + widthOffsetX * width2, y2 + widthOffsetY * width2, 0)
                .setUv(u2, v2)
                .setColor(color2);
        consumer.addVertex(matrix, x2 - widthOffsetX * width2, y2 - widthOffsetY * width2, 0)
                .setUv(u1, v2)
                .setColor(color2);
    }

    protected static class AccumulatedTransform {
        protected float x = 0, y = 0, z = 0;
        protected float xRot = 0, yRot = 0, zRot = 0;

        protected void transform(Vector3fc in, Vector3f out) {
            rotate(in, out);
            offset(out, out);
        }

        protected void offset(Vector3fc in, Vector3f out) {
            in.add(x, y, z, out);
        }

        protected void rotate(Vector3fc in, Vector3f out) {
            float iX = in.x(), iY = in.y(), iZ = in.z();
            float nX, nY, nZ;

            // "rotate" everything...
            float xRotRad = xRot * Mth.DEG_TO_RAD;
            float cosX = Mth.cos(xRotRad), sinX = Mth.sin(xRotRad);
            nY = iY * cosX - iZ * sinX;
            nZ = iY * sinX + iZ * cosX;
            iY = nY; iZ = nZ;

            float yRotRad = yRot * Mth.DEG_TO_RAD;
            float cosY = Mth.cos(yRotRad), sinY = Mth.sin(yRotRad);
            nX =  iX * cosY + iZ * sinY;
            nZ = -iX * sinY + iZ * cosY;
            iX = nX; iZ = nZ;

            float zRotRad = zRot * Mth.DEG_TO_RAD;
            float cosZ = Mth.cos(zRotRad), sinZ = Mth.sin(zRotRad);
            nX = iX * cosZ - iY * sinZ;
            nY = iX * sinZ + iY * cosZ;
            iX = nX; iY = nY;

            out.set(iX, iY, iZ);
        }
    }
}
