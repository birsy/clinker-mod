package birsy.clinker.common.world.level.gen.system.biome;

public abstract class NoiseComputerBiomeSource {//extends BiomeSource {
//    public abstract ClimateZone getClimateZone(int quartPosX, int quartPosY, int quartPosZ,
//                                               NoiseFieldCache noiseExecutor, RandomState randomState);
//
//    public Holder<Biome> getNoiseBiome(int quartPosX, int quartPosY, int quartPosZ,
//                                       NoiseFieldCache noiseExecutor, RandomState randomState) {
//        ClimateZone climateZone = getClimateZone(quartPosX, quartPosY, quartPosZ, noiseExecutor, randomState);
//        return climateZone.getBiome(quartPosX, quartPosY, quartPosZ, noiseExecutor, randomState);
//    }
//
//    public Set<Holder<Biome>> getBiomesWithin(int x1, int y1, int z1,
//                                              int x2, int y2, int z2,
//                                              NoiseFieldCache executor, RandomState randomState) {
//        int minX = QuartPos.fromBlock(Math.min(x1, x2)),
//            minY = QuartPos.fromBlock(Math.min(y1, y2)),
//            minZ = QuartPos.fromBlock(Math.min(z1, z2));
//        int maxX = QuartPos.fromBlock(Math.max(x1, x2)) + 1,
//            maxY = QuartPos.fromBlock(Math.max(y1, y2)) + 1,
//            maxZ = QuartPos.fromBlock(Math.max(z1, z2)) + 1;
//        Set<Holder<Biome>> set = Sets.newHashSet();
//        for (int x = minX; x <= maxX; x++) {
//            for (int y = minY; y <= maxY; y++) {
//                for (int z = minZ; z <= maxZ; z++) {
//                    set.add(this.getNoiseBiome(x, y, z, executor, randomState));
//                }
//            }
//        }
//        return set;
//    }
//
//    @Override
//    public Holder<Biome> getNoiseBiome(int quartPosX, int quartPosY, int quartPosZ, Climate.Sampler sampler) {
//        return this.getNoiseBiome(quartPosX, quartPosY, quartPosZ, createNoiseComputerExecutor(), getRandomState());
//    }
//
//    @Override
//    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
//        return this.getBiomesWithin(
//                x - radius, y - radius, z - radius,
//                x + radius, y + radius, z + radius,
//                createNoiseComputerExecutor(), getRandomState()
//        );
//    }
//
//    protected static RandomState getRandomState() {
//        return ServerLifecycleHooks.getCurrentServer()
//                .getLevel(ClinkerWorld.OTHERSHORE)
//                .getChunkSource().chunkMap.randomState();
//    }
//
//    protected static NoiseFieldCache createNoiseComputerExecutor() {
//        return new UncachedNoiseComputerExecutor(
//                ((SeededNoiseHolderHolder)(Object) getRandomState()).clinker$noiseHolder()
//        );
//    }
}
