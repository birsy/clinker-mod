package birsy.clinker.common.world.level.gen.content.feature;

public class WaterlineFernFeature { //extends Feature<NoneFeatureConfiguration> {
//    public WaterlineFernFeature(Codec<NoneFeatureConfiguration> codec) {
//        super(codec);
//    }
//
//    @Override
//    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
//        WorldGenLevel lvl = ctx.level();
//        BlockPos orig = ctx.origin();
//        BlockPos.MutableBlockPos tgt = new BlockPos.MutableBlockPos();
//        BlockPos.MutableBlockPos tgtbelow = new BlockPos.MutableBlockPos();
//
//        for (int i = 0; i < 16; ++i) {
//            for (int j = 0; j < 16; ++j) {
//                int k = orig.getX() + i;
//                int l = orig.getZ() + j;
//                int i1 = lvl.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l);
//                tgt.set(k, i1, l);
//                tgtbelow.set(tgt).move(Direction.DOWN, 1);
//                Holder<Biome> biome = lvl.getBiome(tgt);
//                if(!biome.getRegisteredName().contains("clinker:brine_snakes"))
//                    continue;
//                int e = edge(lvl, tgtbelow);
//
//                if (e > 0) {
//                    setFern(lvl, tgt, e);
//                }
//
//
//            }
//        }
//
//        return true;
//    }
//
//    private static final ArrayList<Integer> waow = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
//
//    public static boolean setFern(WorldGenLevel lvl, BlockPos tgt, int fill) {
//        BlockState state = ClinkerBlocks.WATER_FERN.get().defaultBlockState();
//        for (BooleanProperty cornerProperty : WaterFernBlock.CORNER_PROPERTIES) state = state.setValue(cornerProperty, false);
//
//        Collections.shuffle(waow);
//        for(int i = 0; i < fill; i++) {
//            state = state.setValue(WaterFernBlock.CORNER_PROPERTIES[waow.get(i)], true);
//        }
//        lvl.setBlock(tgt, state, 2);
//        return fill > 0;
//    }
//
//    public static int edge(LevelReader level, BlockPos water) {
//        if (water.getY() >= level.getMinBuildHeight() && water.getY() < level.getMaxBuildHeight()) {
//            BlockState blockstate = level.getBlockState(water);
//            FluidState fluidstate = level.getFluidState(water);
//            if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof LiquidBlock) {
//
//
//                boolean flag = !level.isWaterAt(water.west()) || !level.isWaterAt(water.east()) || !level.isWaterAt(water.north()) || !level.isWaterAt(water.south());
//                if (flag) {
//                    return 4;
//                }
//                int h = level.getHeight(Heightmap.Types.OCEAN_FLOOR, water.getX(), water.getZ());
//                int ytgt = Math.max(0, water.getY()-h);
//
//                return Math.max(0, 4-ytgt);
//            }
//        }
//
//        return 0;
//    }

}
