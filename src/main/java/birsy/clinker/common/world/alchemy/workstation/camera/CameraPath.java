package birsy.clinker.common.world.alchemy.workstation.camera;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaterniond;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CameraPath {
    protected final Workstation workstation;
    public final List<CameraPathNode> cameraPathNodes;
    public final List<CameraPathLine> cameraPathLines;

    public CameraPath(Workstation workstation) {
        this.workstation = workstation;
        this.cameraPathNodes = new ArrayList<>();
        this.cameraPathLines = new ArrayList<>();
    }

    public void generateCameraPath() {
        this.cameraPathNodes.clear();
        this.cameraPathLines.clear();

        Set<BlockSide> openSides = new HashSet<>();
        this.generateSides(openSides);
        Map<BlockSide, CameraPathNode> filledSides = new HashMap<>();

        int maxIterations = openSides.size();
        for (int i = 0; i < maxIterations; i++) {
            createPath((BlockSide) openSides.toArray()[0], openSides, filledSides);

            if (openSides.isEmpty()) {
                //Clinker.LOGGER.info("Full camera path generation completed, took " + i + " iterations.");
                break;
            }
        }
    }

    private void generateSides(Set<BlockSide> sides) {
        for (BlockPos containedBlock : workstation.containedBlocks) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                boolean isAir = workstation.level.getBlockState(containedBlock.relative(direction)).isAir() && workstation.level.getBlockState(containedBlock.relative(direction).above()).isAir();
                boolean isOtherWorkstation = (workstation.containedBlocks.containsBlock(containedBlock.relative(direction)) || workstation.containedBlocks.containsBlock(containedBlock.relative(direction).above()));
                BlockPos floorPos = containedBlock.relative(direction).below();
                boolean canWalk = workstation.level.getBlockState(floorPos).isFaceSturdy(workstation.level, floorPos, Direction.UP, SupportType.RIGID);
                if (isAir && !isOtherWorkstation && canWalk) {
                    BlockSide side = new BlockSide(containedBlock, direction);
                    sides.add(side);
                }
            }
        }
    }

    private void createPath(BlockSide startSide, Set<BlockSide> sides, Map<BlockSide, CameraPathNode> map) {
        Direction direction = startSide.dir();
        Direction pathDirection = startSide.dir().getClockWise();
        BlockPos.MutableBlockPos blockPos = startSide.pos().mutable();
        BlockPos.MutableBlockPos pathPos = startSide.pos().mutable().move(startSide.dir());

        //insert node
        CameraPathNode previousNode = insertNode(null, blockPos, pathPos);
        map.put(startSide, previousNode);
        sides.remove(startSide);

        int safetyLimit = 128;
        for (int i = 0; i < safetyLimit; i++) {
            blockPos.move(pathDirection);
            pathPos.move(pathDirection);

            BlockPos floorPos = pathPos.relative(direction).below();
            if (this.workstation.containedBlocks.containsBlock(pathPos)) {
                //we've hit an inside corner
                //move the pathPos back
                pathPos.move(pathDirection.getOpposite());
                //turn
                pathDirection = pathDirection.getCounterClockWise();
                direction = direction.getCounterClockWise();
                //move the blockpos forward
                blockPos.move(pathDirection);
            } else if (!this.workstation.level.getBlockState(pathPos).isAir() || !this.workstation.level.getBlockState(pathPos.above()).isAir()) {
                // we've hit a solid block. the path ends here.
               // Clinker.LOGGER.info("Partial camera path completed, took " + i + " movements.");
                return;
            } else if (!this.workstation.containedBlocks.containsBlock(blockPos)) {
                if (!this.workstation.level.getBlockState(blockPos).isAir()) {
                    // we've hit a solid block. the path ends here.
                    //Clinker.LOGGER.info("Partial camera path completed, took " + i + " movements.");
                    return;
                }

                //we've hit an outside corner or end of a line
                //move the blockpos back.
                blockPos.move(pathDirection.getOpposite());
                //turn
                pathDirection = pathDirection.getClockWise();
                direction = direction.getClockWise();

                //insert corner node
                previousNode = insertNode(previousNode, blockPos, pathPos);

                pathPos.move(pathDirection);
            }
            BlockSide side = new BlockSide(blockPos, direction);

            //if we already have a node for this side, we can hook it up and be done.
            if (map.containsKey(side)) {
                CameraPathNode node = map.get(side);
                previousNode.setLeftNeighbor(node);
                cameraPathLines.add(new CameraPathLine(node, previousNode));
                //Clinker.LOGGER.info("Partial camera path completed, took " + i + " movements.");
                return;
            } else {
                previousNode = insertNode(previousNode, blockPos, pathPos);
                map.put(side, previousNode);
                sides.remove(side);
            }
        }

        Clinker.LOGGER.info("Partial camera path exceeded 128 generation limit.");
    }

    private CameraPathNode insertNode(@Nullable CameraPathNode parent, BlockPos blockPos, BlockPos pathPos) {
        Vec3 camPos = new Vec3(pathPos.getX() + 0.5, pathPos.getY(), pathPos.getZ() + 0.5);
        Vec3 lookDir = new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5).subtract(camPos).normalize();
        CameraPathNode node = new CameraPathNode(camPos, lookDir);
        cameraPathNodes.add(node);

        if (parent != null) {
            node.setRightNeighbor(parent);
            cameraPathLines.add(new CameraPathLine(node, parent));
        }

        return node;
    }


    private record BlockSide(BlockPos pos, Direction dir) {}

    public static class CameraPathNode {
        public Vec3 position;
        public Vec3 direction;
        public float offset, height;
        public CameraPathNode[] neighbors = new CameraPathNode[2];

        protected CameraPathNode(Vec3 position, float offset, float height, Vec3 orientation) {
            this.position = position;
            this.offset = offset;
            this.height = height;
            this.direction = orientation;
        }

        protected CameraPathNode(Vec3 position, Vec3 orientation) {
            this(position, 1.8F, 0, orientation);
        }

        public CameraPathNode getRightNeighbor() {
            return this.neighbors[1];
        }

        protected void setRightNeighbor(CameraPathNode node) {
            this.neighbors[1] = node;
            // our left neighbor's right neighbor is us
            node.neighbors[0] = this;
        }

        public CameraPathNode getLeftNeighbor() {
            return this.neighbors[0];
        }

        protected void setLeftNeighbor(CameraPathNode node) {
            this.neighbors[0] = node;
            // our right neighbor's left neighbor is us
            node.neighbors[1] = this;
        }
    }

    public record CameraPathLine(CameraPathNode node1, CameraPathNode node2) {
        public Vec3 getCenter() {
            return node1.position.add(node2.position).scale(0.5);
        }
    }
}
