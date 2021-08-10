package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import SurfaceBuilderBaseConfiguration;

public class ClinkerSurfaceBuilderConfigs {
	
	public static final SurfaceBuilderBaseConfiguration ASH_CONFIG = new SurfaceBuilderBaseConfiguration(ClinkerBlocks.ASH.get().defaultBlockState(), ClinkerBlocks.PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.SALT_BLOCK.get().defaultBlockState());
	public static final SurfaceBuilderBaseConfiguration ROOTED_ASH_CONFIG = new SurfaceBuilderBaseConfiguration(ClinkerBlocks.ROOTED_ASH.get().defaultBlockState(), ClinkerBlocks.PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.SALT_BLOCK.get().defaultBlockState());
	
	public static final SurfaceBuilderBaseConfiguration ASH_STEPPES_CONFIG = new SurfaceBuilderBaseConfiguration(ClinkerBlocks.PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.ROCKY_PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.SALT_BLOCK.get().defaultBlockState());
	public static final SurfaceBuilderBaseConfiguration ASH_STEPPES_ROOTED_CONFIG = new SurfaceBuilderBaseConfiguration(ClinkerBlocks.ROOTED_PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.ROCKY_PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.SALT_BLOCK.get().defaultBlockState());
	public static final SurfaceBuilderBaseConfiguration ASH_STEPPES_ROCKY_CONFIG = new SurfaceBuilderBaseConfiguration(ClinkerBlocks.ROCKY_PACKED_ASH.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), ClinkerBlocks.SALT_BLOCK.get().defaultBlockState());
}
