package birsy.clinker.common.world.level.gen.content.worldfeatures;

public class UndergroundRiverWorldFeature { //extends WorldFeature {
//    private RiverSegment[] segments;
//    private RiverBoundingBox boundingBoxHierarchy;
//    private NoiseComputer
//            riverDistanceComputer,
//            riverSurfaceHeightComputer,
//            riverCeilingHeightComputer,
//            riverRadiusComputer,
//            riverFlowComputer,
//            riverNoiseComputer;
//    private double totalFlow;
//    private double[] funkyRiverRadiusOffsets;
//
//    public UndergroundRiverWorldFeature(int depth, int separationRadius) {
//        super(depth, separationRadius);
//    }
//
//    @Override
//    public int getCenterX() { return (segments[0].x(0) + segments[segments.length - 1].x(1)) / 2; }
//    @Override
//    public int getCenterZ() { return (segments[0].z(0) + segments[segments.length - 1].z(1)) / 2; }
//    @Override
//    public boolean within(int minX, int minZ, int maxX, int maxZ) {
//        return this.boundingBoxHierarchy.intersectsRecursive(minX, Integer.MIN_VALUE, minZ, maxX, Integer.MAX_VALUE, maxZ);
//    }
//
//    @Override
//    public boolean plan(LevelAccessor level, MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
//
//        BlockPos.MutableBlockPos start = BlockPos.ZERO.mutable(), end = BlockPos.ZERO.mutable();
//        double distance = 0;
//        for (int i = 0; i < 32; i++) {
//            start.set(
//                    randomSource.nextInt(metaChunk.minX(), metaChunk.maxX()), 35,
//                    randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ())
//            );
//            end.set(
//                    randomSource.nextInt(metaChunk.minX(), metaChunk.maxX()), 10,
//                    randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ())
//            );
//            distance = Mth.length(start.getX() - end.getX(), start.getZ() - end.getZ());
//            if (distance >= 50) break;
//        }
//
//        this.funkyRiverRadiusOffsets = new double[32];
//        int i = 0;
//        double inset = 0;
//        while (i < this.funkyRiverRadiusOffsets.length) {
//            int count = randomSource.nextIntBetweenInclusive(2, 6);
//            double insetGoal = Mth.lerp(randomSource.nextDouble(), -1, 3);
//            for (int j = 0; j < count && i < this.funkyRiverRadiusOffsets.length; j++) {
//                inset = Mth.approach((float) inset, (float) insetGoal, 1.0F);
//                this.funkyRiverRadiusOffsets[i] = inset;
//                i++;
//            }
//        }
//
//        List<BlockPos> riverShape = generateRiverCurve(
//                start, end, 64, metaChunk, randomSource, context
//        );
//        List<RiverNode> riverNodes = new ArrayList<>(64);
//        double riverLength = resampleCurve(riverShape, 10, context, riverNodes);
//        this.totalFlow = randomSource.triangle(riverLength, riverLength * 0.25) / 4.5;
//
//        this.segments = new RiverSegment[riverNodes.size() - 1];
//        for (int j = 0; j < riverNodes.size() - 1; j++) {
//            this.segments[j] = new RiverSegment(riverNodes.get(j), riverNodes.get(j + 1));
//        }
//
//        this.boundingBoxHierarchy = RiverBoundingBox.fromSegments(this.segments, 0, this.segments.length);
//        this.boundingBoxHierarchy.expand(20, 20, 20);
//
//        int riverId = randomSource.nextInt();
//        this.riverDistanceComputer = new NoiseComputer(
//                "river_distance_" + riverId,
//                CacheType.INTERPOLATED_2D_FINE,
//                (x, y, z, noiseContext) -> {
//                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//                    return coordinates.signedHorizontalDistance();
//                }
//        );
//        this.riverSurfaceHeightComputer = new NoiseComputer(
//                "river_surface_height_" + riverId,
//                CacheType.INTERPOLATED_2D_FINE,
//                (x, y, z, noiseContext) -> {
//                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//                    return coordinates.riverHeight();
//                }
//        );
//        this.riverCeilingHeightComputer = new NoiseComputer(
//                "river_ceiling_height_" + riverId,
//                CacheType.INTERPOLATED_2D_FINE,
//                (x, y, z, noiseContext) -> {
//                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//                    return coordinates.ceilingHeight();
//                }
//        );
//        this.riverRadiusComputer = new NoiseComputer(
//                "river_radius_" + riverId,
//                CacheType.INTERPOLATED_2D_FINE,
//                (x, y, z, noiseContext) -> {
//                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//                    return coordinates.riverRadius();
//                }
//        );
//        this.riverFlowComputer = new NoiseComputer(
//                "river_flow_" + riverId,
//                CacheType.INTERPOLATED_2D_FINE,
//                (x, y, z, noiseContext) -> {
//                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//                    return coordinates.progress() * totalFlow;
//                }
//        );
//
//        int randomOffset = randomSource.nextInt(500);
//        this.riverNoiseComputer = new NoiseComputer(
//                "river_noise_" + riverId,
//                CacheType.INTERPOLATED_2D_FINE,
//                (x, y, z, noiseContext) -> {
//                    RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//                    double distanceX = coordinates.signedHorizontalDistance(),
//                           distanceY = coordinates.progress() * riverLength;
//                    noiseContext.noiseHolder().registerNoise("river_bed");
//                    return noiseContext.noiseHolder().sample("river_bed", distanceX / 12.0 + randomOffset, 0, distanceY / 28.0);
//                }
//        );
//
////        Clinker.LOGGER.info("River generated starting at {} {} {} and ending at {} {} {}",
////                riverShape.getFirst().getX(), riverShape.getFirst().getY(), riverShape.getFirst().getZ(),
////                riverShape.getLast().getX(), riverShape.getLast().getY(), riverShape.getLast().getZ());
//        return true;
//    }
//
//    private static List<BlockPos> generateRiverCurve(BlockPos startPos, BlockPos endPos, int count, MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
//        int riverX = endPos.getX() - startPos.getX(),
//            riverZ = endPos.getZ() - startPos.getZ();
//        int perpRiverX = riverZ,
//            perpRiverZ = -riverX;
//        double riverLength = Mth.length(riverX, riverZ);
//
//        int frequency = (int) Math.round(riverLength / 100.0);
//        List<BlockPos> nodePositions = new ArrayList<>(count);
//
//        nodePositions.add(startPos);
//        int y = startPos.getY();
//        for (int i = 1; i < count - 1; i++) {
//            float factor = i / (count - 1.0F);
//            float midFactor = Mth.clampedMap(factor, 0, 0.5F, 0, 1) * Mth.clampedMap(factor, 0.5F, 1, 1, 0);
//            midFactor = (float) Mth.smoothstep(midFactor);
//
//            int baseX = Mth.lerpDiscrete(factor, startPos.getX(), endPos.getX()),
//                baseZ = Mth.lerpDiscrete(factor, startPos.getZ(), endPos.getZ());
//            float riverWiggliness = (float)context.noiseComputerExecutor().compute(baseX, 0, baseZ, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
//            riverWiggliness = riverWiggliness * 0.5F + 0.5F;
//            riverWiggliness *= midFactor;
//
//            float localX = factor - Mth.sin(frequency * factor * Mth.TWO_PI) * riverWiggliness * 0.3F * 0.2F,
//                  localZ = Mth.sin(frequency * factor * Mth.PI) * riverWiggliness * 0.3F;
//            double xOffset = riverX * localX + perpRiverX * localZ,
//                   zOffset = riverZ * localX + perpRiverZ * localZ;
//
//            // some logic for computing the y...
//            // honestly could not really tell you how this worked.
//            int startY = startPos.getY(),
//                endY = endPos.getY();
//            int straightLineY = Mth.lerpDiscrete(factor, startY, endY);
//            int difference = y - straightLineY;
//            if (Math.abs(difference) > 4 && difference > randomSource.nextInt(24))
//                y = straightLineY;
//
//            BlockPos nodePos = new BlockPos(startPos.getX() + (int) xOffset, y, startPos.getZ() + (int) zOffset);
//            nodePositions.add(nodePos);
//        }
//        nodePositions.add(endPos);
//        return nodePositions;
//    }
//
//    // returns length
//    private static double resampleCurve(List<BlockPos> nodePositions, double step, NoiseComputerContext context, List<RiverNode> result) {
//        if (nodePositions.size() < 2) return 0;
//
//        // compute length
//        int n = nodePositions.size();
//        double[] lengths = new double[n];
//        lengths[0] = 0;
//
//        for (int i = 1; i < n; i++)
//            lengths[i] = lengths[i - 1] + Math.sqrt(nodePositions.get(i - 1).distSqr(nodePositions.get(i)));
//        double totalLength = lengths[n - 1];
//
//        // resample curve so its evenly spaced
//        int ceilingHeight = nodePositions.getFirst().getY();
//        for (double targetLength = 0; targetLength <= totalLength; targetLength += step) {
//            int x, y, z;
//            int closestIndex = Arrays.binarySearch(lengths, targetLength);
//            if (closestIndex < 0) closestIndex = -closestIndex - 1;
//
//            if (closestIndex <= 0) {
//                x = nodePositions.getFirst().getX(); y = nodePositions.getFirst().getY(); z = nodePositions.getFirst().getZ();
//            } else if (closestIndex >= n) {
//                x = nodePositions.getLast().getX(); y = nodePositions.getLast().getY(); z = nodePositions.getLast().getZ();
//            } else {
//                BlockPos a = nodePositions.get(closestIndex - 1);
//                BlockPos b = nodePositions.get(closestIndex);
//
//                double segStart = lengths[closestIndex - 1];
//                double segEnd = lengths[closestIndex];
//                float f = (float) ((targetLength - segStart) / (segEnd - segStart));
//
//                x = Mth.lerpDiscrete(f, a.getX(), b.getX());
//                y = f < 0.5 ? a.getY() : b.getY();
//                z = Mth.lerpDiscrete(f, a.getZ(), b.getZ());
//            }
//
//            double riverRadius = OthershoreNoiseComputers.BASE_NOISE_2D[8].compute(x, y, z, context);
//            riverRadius = Mth.map(riverRadius, -1, 1, 0, 1);
//
//            ceilingHeight = MathUtils.approach(ceilingHeight, y, (int)(step / 8.0));
//            double progress = targetLength / totalLength;
//            riverRadius *= Mth.clampedMap(progress, 0, 0.2, 0.3, 1);
//            riverRadius *= Mth.clampedMap(progress, 0.8, 1, 1, 0.5);
//            riverRadius = Mth.lerp(riverRadius, 3, 20);
//
//            result.add(new RiverNode(progress, x, z, y, ceilingHeight,  riverRadius));
//        }
//
//        BlockPos last = nodePositions.getLast();
//        result.add(new RiverNode(1.0, last.getX(), last.getZ(), last.getY(), ceilingHeight, result.getLast().riverRadius()));
//
//        return totalLength;
//    }
//
//    @Override
//    public double modifyCaveTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
//        double riverDistance = context.noiseComputerExecutor().compute(x, y, z, this.riverDistanceComputer);
//        double waterHeight = context.noiseComputerExecutor().compute(x, y, z, this.riverSurfaceHeightComputer),
//               ceilingHeight = context.noiseComputerExecutor().compute(x, y, z, this.riverCeilingHeightComputer);
//
//        // out of range
//        if (waterHeight <= -9999 || ceilingHeight <= -9999 || Math.abs(riverDistance) >= 9999)
//            return currentNoiseValue;
//        // flow represents the cross-sectional area of the river
//        double flow = context.noiseComputerExecutor().compute(x, y, z, this.riverFlowComputer);
//        double baseRiverRadius = context.noiseComputerExecutor().compute(x, y, z, this.riverRadiusComputer);
//        double riverRadius = baseRiverRadius;
//        double riverDepth = flow / riverRadius;
//        riverDepth = Math.clamp(riverDepth, 2.0, 30.0);
//
//        if (riverRadius <= 0)
//            return currentNoiseValue;
//        if (Double.isNaN(riverDistance) || Double.isInfinite(riverDistance) ||
//            Double.isNaN(waterHeight) || Double.isInfinite(waterHeight) ||
//            Double.isNaN(ceilingHeight) || Double.isInfinite(ceilingHeight) ||
//            Double.isNaN(flow) || Double.isInfinite(flow) ||
//            Double.isNaN(riverRadius) || Double.isInfinite(riverRadius) ||
//            Double.isNaN(riverDepth) || Double.isInfinite(riverDepth)) {
//            Clinker.LOGGER.error("ERROR VAL: {}, {}, {}, {}, {}, {}", riverDistance, waterHeight, ceilingHeight, flow, riverRadius, riverDepth);
//        }
//
//        double yDist = 0;
//        double ceilingRadius = Math.max((riverDepth + riverRadius) * 0.5, riverDepth * 1.5);
//        if (y < waterHeight) {
//            yDist = (y - waterHeight) / riverDepth;
//        } else if (y > ceilingHeight) {
//            yDist = (y - ceilingHeight) / ceilingRadius;
//        }
//        if (yDist > 1.1) return currentNoiseValue;
//
//        double yDifference = y - waterHeight;
//        if (y > waterHeight) {
//            double shorelineDepth = Mth.clampedMap(riverDepth, 3, 10, 1.8, 1.0);
//            int shoreHeight = 3;
//            double riverRadiusMultiplier = 1;
//            if (yDifference < shoreHeight) {
//                riverRadiusMultiplier = Mth.clampedMap(yDifference, -1, shoreHeight, 0, 1);
//                riverRadiusMultiplier = Mth.smoothstep(riverRadiusMultiplier);
//                riverRadiusMultiplier = Mth.lerp(riverRadiusMultiplier, 1, shorelineDepth);
//            } else {
//                riverRadiusMultiplier = Mth.clampedMap(yDifference, shoreHeight, ceilingRadius, 0, 1);
//                riverRadiusMultiplier = Mth.smoothstep(riverRadiusMultiplier);
//                riverRadiusMultiplier = Mth.lerp(riverRadiusMultiplier, shorelineDepth, 1);
//            }
//            riverRadius *= riverRadiusMultiplier;
//        }
//        riverRadius += this.funkyRiverRadiusOffsets[Math.floorMod(y, this.funkyRiverRadiusOffsets.length-1)];
//        double noiseAddition = context.noiseComputerExecutor().compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE[5]) * 8;
//        noiseAddition = Mth.clampedMap(yDifference, 0, 5, 0, noiseAddition);
//        riverRadius += noiseAddition;
//
//        double xzDist = riverDistance / riverRadius;
//        double riverCutout = (1 - Math.sqrt(xzDist * xzDist + yDist * yDist)) * riverRadius;
//
//        double riverFloorNoise = context.noiseComputerExecutor().compute(x, y, z, this.riverNoiseComputer);
//        riverFloorNoise = 1 - Math.abs(riverFloorNoise);
//        riverFloorNoise = Math.sqrt(riverFloorNoise);
//        riverFloorNoise += Mth.map(y - waterHeight, -8, -2, -1.0, 1.0);
//        riverFloorNoise += riverDepth / 8.0;
//
//        riverCutout = Math.min(riverCutout, riverFloorNoise);
//
//        double speleothem = context.noiseComputerExecutor().compute(x, y, z, OthershoreNoiseComputers.SPELEOTHEMS);
//        speleothem = speleothem + Math.abs(1 - yDist) * 4 + Mth.clampedMap(yDifference, 2, ceilingRadius, 4, -5);
//        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);
//        riverCutout += speleothem;
//
//        double waterContainer = Math.clamp(yDifference, -10, 10);
//        waterContainer = Math.max(waterContainer, Mth.map(Math.abs(riverDistance), baseRiverRadius * 1.8, baseRiverRadius * 1.8 + 20, 0, 10));
//        waterContainer = Math.max(waterContainer, (waterHeight - riverDepth) - y);
//        return -MathUtils.smoothMinExpo(-MathUtils.smoothMinExpo(currentNoiseValue, waterContainer, 5), -riverCutout, 2);
//    }
//
//    @Override
//    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseComputerContext context) {
//        RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//
//        double flow = context.noiseComputerExecutor().compute(x, y, z, this.riverFlowComputer);
//        double riverRadius = context.noiseComputerExecutor().compute(x, y, z, this.riverRadiusComputer);
//        double riverDepth = flow / riverRadius;
//        riverDepth = Math.max(riverDepth, 10);
//        if (riverRadius <= 0)
//            return currentFluidLevel;
//
//        int yDiff = y - (int)coordinates.riverHeight();
//        if (yDiff > riverRadius * 3 || yDiff < -riverDepth * 2) return currentFluidLevel;
//        if (Math.abs(coordinates.signedHorizontalDistance) > riverRadius + 10) return currentFluidLevel;
//        return new FluidLevel((int) coordinates.riverHeight, Blocks.WATER.defaultBlockState());
//    }
//
//    @Override
//    public double modifyWaterfallPresence(int x, int y, int z, double currentValue, NoiseComputerContext context) {
//        RiverSpaceCoordinates coordinates = this.getRiverSpaceCoordinates(x, y, z, true);
//        double radius = context.noiseComputerExecutor().compute(x, y, z, this.riverRadiusComputer);
//        int yDiff = y - (int) coordinates.riverHeight();
//        if (yDiff > radius * 3 || yDiff < -radius * 2 || Math.abs(coordinates.signedHorizontalDistance) > radius) return currentValue;
//        return Math.max(Mth.clampedMap(Math.abs(coordinates.signedHorizontalDistance), 0, radius, 2.5, 0.5), currentValue);
//    }
//
//    public RiverSpaceCoordinates getRiverSpaceCoordinates(int x, int y, int z, boolean ignoreY) {
//        MutableRiverSpaceCoordinates best = new MutableRiverSpaceCoordinates(10000, -10000, -10000, 0, 0);
//        sampleDistanceFromRiverRecursive(this.boundingBoxHierarchy, x, y, z, ignoreY, best);
//        return best.toImmutable();
//    }
//
//    private void sampleDistanceFromRiverRecursive(RiverBoundingBox box, int x, int y, int z, boolean ignoreY, MutableRiverSpaceCoordinates best) {
//        if (ignoreY) {
//            if (!box.containsHorizontal(x, z)) return; // don't take y into consideration
//        } else {
//            if (box.contains(x, y, z)) return;
//        }
//
//        if (box.riverSegmentIndex >= 0) {
//            RiverSegment segment = this.segments[box.riverSegmentIndex];
//
//            int horizontalDistance = (int) lineSegmentDistance(
//                    x, z,
//                    segment.x(0), segment.z(0),
//                    segment.x(1), segment.z(1)
//            );
//
//            if (Math.abs(horizontalDistance) < Math.abs(best.signedHorizontalDistance)) {
//                best.signedHorizontalDistance = horizontalDistance;
//
//                float gradient = (x - segment.x(1)) * (segment.x(0) - segment.x(1)) +
//                                 (z - segment.z(1)) * (segment.z(0) - segment.z(1));
//                gradient /= (segment.x(0) - segment.x(1)) * (segment.x(0) - segment.x(1)) +
//                            (segment.z(0) - segment.z(1)) * (segment.z(0) - segment.z(1));
//                gradient = 1 - gradient;
//                if (Float.isNaN(gradient)) gradient = 0;
//                best.surfaceHeight = segment.waterHeight(0);
//                best.ceilingHeight = segment.ceilingHeight(gradient);
//                best.riverRadius = segment.riverRadius(gradient);
//                best.progress = segment.progress(gradient);
//            }
//        } else {
//            sampleDistanceFromRiverRecursive(box.childA, x, y, z, ignoreY, best);
//            sampleDistanceFromRiverRecursive(box.childB, x, y, z, ignoreY, best);
//        }
//    }
//
//    private static double lineSegmentDistance(int px, int py, int ax, int ay, int bx, int by) {
//        double bax = bx - ax, bay = by - ay;
//        double rx = px - ax, ry = py - ay;
//        double h = Mth.clamp((rx * bax + ry * bay) / (bax * bax + bay * bay), 0, 1);
//        double dx = rx - bax * h, dy = ry - bay * h;
//        // compute the dot product with a perpendicular line vector to compute the "sign" of the distance
//        double dot = rx * bay + ry * -bax;
//        return Math.sqrt(dx * dx + dy * dy) * (dot < 0 ? -1 : 1);
//    }
//
//    private record RiverSpaceCoordinates(int signedHorizontalDistance, double riverHeight, double ceilingHeight, double riverRadius, double progress) {}
//    private static class MutableRiverSpaceCoordinates {
//        int signedHorizontalDistance;
//        double surfaceHeight, ceilingHeight, riverRadius, progress;
//        MutableRiverSpaceCoordinates(int horiz, double surfaceHeight, double ceilingHeight, double riverRadius, double progress) {
//            this.signedHorizontalDistance = horiz;
//            this.surfaceHeight = surfaceHeight;
//            this.ceilingHeight = ceilingHeight;
//            this.riverRadius = riverRadius;
//            this.progress = progress;
//        }
//        RiverSpaceCoordinates toImmutable() {
//            return new RiverSpaceCoordinates(signedHorizontalDistance, surfaceHeight, ceilingHeight, riverRadius, progress);
//        }
//    }
//
//    private record RiverNode(double progress, int x, int z, int waterHeight, int ceilingSurfaceHeight, double riverRadius) {}
//    private record RiverSegment(RiverNode start, RiverNode end) {
//        double progress(float delta) {
//            return Mth.clampedLerp(start.progress(), end.progress(), delta);
//        }
//        int x(float delta) {
//            return Mth.lerpDiscrete(Mth.clamp(delta, 0, 1), start.x(), end.x());
//        }
//        int z(float delta) {
//            return Mth.lerpDiscrete(Mth.clamp(delta, 0, 1), start.z(), end.z());
//        }
//        double waterHeight(float delta) {
//            return Mth.clampedLerp(start.waterHeight(), end.waterHeight(), delta);
//        }
//        double ceilingHeight(float delta) {
//            return Mth.clampedLerp(start.ceilingSurfaceHeight(), end.ceilingSurfaceHeight(), delta);
//        }
//        double riverRadius(float delta) {
//            return Mth.clampedLerp(start.riverRadius(), end.riverRadius(), delta);
//        }
//    }
//
//    private static class RiverBoundingBox {
//        int riverSegmentIndex; // -1 for non-leaf nodes
//        int x1, y1, z1, x2, y2, z2;
//        RiverBoundingBox childA, childB;
//
//        RiverBoundingBox(int riverSegmentIndex, int x1, int y1, int z1, int x2, int y2, int z2) {
//            this.riverSegmentIndex = riverSegmentIndex;
//            this.x1 = x1; this.y1 = y1; this.z1 = z1;
//            this.x2 = x2; this.y2 = y2; this.z2 = z2;
//        }
//
//        RiverBoundingBox expand(int x, int y, int z) {
//            this.x1 -= x; this.y1 -= y; this.z1 -= z;
//            this.x2 += x; this.y2 += y; this.z2 += z;
//            if (this.childA != null) this.childA.expand(x, y, z);
//            if (this.childB != null) this.childB.expand(x, y, z);
//            return this;
//        }
//
//        boolean contains(int x, int y, int z) {
//            return x >= x1 && x <= x2 &&
//                    y >= y1 && y <= y2 &&
//                    z >= z1 && z <= z2;
//        }
//
//        boolean containsHorizontal(int x, int z) {
//            return x >= x1 && x <= x2 &&
//                    z >= z1 && z <= z2;
//        }
//
//        boolean intersects(int x1, int y1, int z1, int x2, int y2, int z2) {
//            return this.x1 < x2 && this.x2 > x1 && this.y1 < y2 && this.y2 > y1 && this.z1 < z2 && this.z2 > z1;
//        }
//
//        boolean intersectsRecursive(int x1, int y1, int z1, int x2, int y2, int z2) {
//            if (this.intersects(x1, y1, z1, x2, y2, z2)) {
//                if (this.childA != null && this.childA.intersectsRecursive(x1, y1, z1, x2, y2, z2)) {
//                    return true;
//                } else if (this.childB != null && this.childB.intersectsRecursive(x1, y1, z1, x2, y2, z2)) {
//                    return true;
//                }
//                return true;
//            }
//            return false;
//        }
//
//        static RiverBoundingBox fromSegments(RiverSegment[] segments, int start, int end) {
//            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
//            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
//
//            for (int i = start; i < end; i++) {
//                RiverSegment seg = segments[i];
//
//                int minWaterHeight = Math.min(Mth.floor(seg.waterHeight(0)), Mth.floor(seg.waterHeight(1))),
//                    minCeilingHeight = Math.min(Mth.floor(seg.ceilingHeight(0)), Mth.floor(seg.ceilingHeight(1)));
//                int maxWaterHeight = Math.max(Mth.ceil(seg.waterHeight(0)), Mth.ceil(seg.waterHeight(1))),
//                    maxCeilingHeight = Math.max(Mth.ceil(seg.ceilingHeight(0)), Mth.ceil(seg.ceilingHeight(1)));
//
//                int startRadius = (int) Math.ceil(seg.riverRadius(0)),
//                    endRadius = (int) Math.ceil(seg.riverRadius(1));
//                int maxRadius = Math.max(startRadius, endRadius);
//
//                minX = Math.min(minX, Math.min(seg.x(0), seg.x(1))) - maxRadius;
//                minY = Math.min(minY, Math.min(minCeilingHeight, minWaterHeight));
//                minZ = Math.min(minZ, Math.min(seg.z(0), seg.z(1))) - maxRadius;
//
//                maxX = Math.max(maxX, Math.max(seg.x(0), seg.x(1))) + maxRadius;
//                maxY = Math.max(maxY, Math.max(maxCeilingHeight, maxWaterHeight));
//                maxZ = Math.max(maxZ, Math.max(seg.z(0), seg.z(1))) + maxRadius;
//            }
//
//            RiverBoundingBox box = new RiverBoundingBox(-1, minX, minY, minZ, maxX, maxY, maxZ);
//
//            if (end - start == 1) {
//                // leaf node
//                box.riverSegmentIndex = start;
//            } else {
//                int mid = (start + end) / 2;
//                box.childA = fromSegments(segments, start, mid);
//                box.childB = fromSegments(segments, mid, end);
//            }
//
//            return box;
//        }
//    }
}
