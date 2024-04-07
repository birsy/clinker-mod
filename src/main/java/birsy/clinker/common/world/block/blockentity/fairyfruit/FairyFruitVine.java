package birsy.clinker.common.world.block.blockentity.fairyfruit;

import birsy.clinker.core.util.CollisionUtils;
import birsy.clinker.core.util.JomlConversions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FairyFruitVine {
    public Vec3 origin;
    final Level level;
    public List<FairyFruitVineSegment> segments = new ArrayList<>();

    int fruitGrowth = 0;

    public FairyFruitVine(Level level, Vec3 origin) {
        this.origin = origin;
        this.level = level;
    }

    public void tick() {
        for (FairyFruitVineSegment segment : this.segments) {
            segment.tick();
        }

        for (int iteration = 0; iteration < 16; iteration++) {
            FairyFruitVineSegment previousSegment = null;
            for (int i = 0; i < this.segments.size(); i++) {
                FairyFruitVineSegment segment = this.segments.get(i);

                if (i == 0) {
                    Vec3 pos1 = origin;
                    Vec3 pos2 = segment.position;

                    Vec3 center = pos1.add(pos2).scale(0.5);
                    Vec3 direction = pos1.subtract(pos2).normalize();

                    segment.position = center.subtract(direction.scale(segment.length * 0.5));
                } else {
                    Vec3 pos1 = previousSegment.position;
                    Vec3 pos2 = segment.position;

                    Vec3 center = pos1.add(pos2).scale(0.5);
                    Vec3 direction = pos1.subtract(pos2).normalize();

                    previousSegment.position = center.add(direction.scale(segment.length * 0.5));
                    segment.position = center.subtract(direction.scale(segment.length * 0.5));
                }

                previousSegment = segment;
            }
        }

    }

    public Vec3 getEndPosition() {
        if (segments.isEmpty()) return origin;
        return this.getTipSegment().position;
    }

    public void breakSegment(int index) {
        this.fruitGrowth = 0;
        for (int i = segments.size() - 1; i >= index; i--) {
            this.segments.get(index).destroy();
        }
        this.getTipSegment().setLength(0.5F);
    }

    @Nullable
    public FairyFruitVineSegment getTipSegment() {
        if (this.segments.isEmpty()) return null;
        return segments.get(segments.size() - 1);
    }

    public void grow() {
        if (this.getTipSegment() != null) this.getTipSegment().setLength(1.0F);
        FairyFruitVineSegment segment = new FairyFruitVineSegment(this, 0.125F, 0.5F);
    }

    public static class FairyFruitVineSegment {
        final FairyFruitVine vine;
        private float length;
        final float radius;
        Vec3 position, previousPosition;

        public FairyFruitVineSegment(FairyFruitVine vine, float radius, float initialLength) {
            this.vine = vine;
            this.radius = radius;
            this.length = initialLength;

            Vec3 endingPosition = vine.getEndPosition();
            this.position = endingPosition.subtract(0, this.length, 0);
            this.previousPosition = this.position;

            vine.segments.add(this);
        }

        public void tick() {
            Vec3 velocity = this.position.subtract(this.previousPosition).add(0, -0.08, 0).scale(0.99);
            this.previousPosition = this.position;
            this.position = this.position.add(velocity);
            collideWithLevel();
            collideWithEntities();
        }

        private void collideWithLevel() {
            Level level = this.vine.level;
            for (VoxelShape shape : level.getBlockCollisions(null, new AABB(this.position, this.position).inflate(this.radius + 0.1))) {
                List<AABB> aabbs = shape.toAabbs();

                for (AABB aabb : aabbs) {
                    Optional<CollisionUtils.CollisionManifold> manifold = CollisionUtils.sphereAABBCollision(JomlConversions.toJOML(this.position), this.radius, aabb);
                    if (manifold.isPresent()) {
                        this.position = JomlConversions.toMojang(manifold.get().adjustment());
                    }
                }
            }
        }

        private void collideWithEntities() {
            Level level = this.vine.level;
            for (Entity entity : level.getEntities(null, new AABB(this.position, this.position).inflate(this.radius + 0.1))) {
                Optional<CollisionUtils.CollisionManifold> manifold = CollisionUtils.sphereAABBCollision(JomlConversions.toJOML(this.position), this.radius, entity.getBoundingBox());
                if (manifold.isPresent()) {
                    this.position = JomlConversions.toMojang(manifold.get().adjustment());
                }
            }
        }

        public void setLength(float length) {
            this.length = length;
        }

        public Vec3 getPosition(float partialTick) {
            return this.previousPosition.lerp(this.position, partialTick);
        }

        protected void destroy() {
            this.vine.segments.remove(this);
        }
    }
}
