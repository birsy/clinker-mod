package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class ClinkerSurfaceBuilderConfigs {
	
	public static final SurfaceBuilderConfig ASH_CONFIG = new SurfaceBuilderConfig(ClinkerBlocks.ASH.get().getDefaultState(), ClinkerBlocks.PACKED_ASH.get().getDefaultState(), ClinkerBlocks.SALT_BLOCK.get().getDefaultState());
	public static final SurfaceBuilderConfig ROOTED_ASH_CONFIG = new SurfaceBuilderConfig(ClinkerBlocks.ROOTED_ASH.get().getDefaultState(), ClinkerBlocks.PACKED_ASH.get().getDefaultState(), ClinkerBlocks.SALT_BLOCK.get().getDefaultState());
	
	public static final SurfaceBuilderConfig ASH_STEPPES_CONFIG = new SurfaceBuilderConfig(ClinkerBlocks.PACKED_ASH.get().getDefaultState(), ClinkerBlocks.ROCKY_PACKED_ASH.get().getDefaultState(), ClinkerBlocks.SALT_BLOCK.get().getDefaultState());
	public static final SurfaceBuilderConfig ASH_STEPPES_ROOTED_CONFIG = new SurfaceBuilderConfig(ClinkerBlocks.ROOTED_PACKED_ASH.get().getDefaultState(), ClinkerBlocks.ROCKY_PACKED_ASH.get().getDefaultState(), ClinkerBlocks.SALT_BLOCK.get().getDefaultState());
	public static final SurfaceBuilderConfig ASH_STEPPES_ROCKY_CONFIG = new SurfaceBuilderConfig(ClinkerBlocks.ROCKY_PACKED_ASH.get().getDefaultState(), ClinkerBlocks.BRIMSTONE.get().getDefaultState(), ClinkerBlocks.SALT_BLOCK.get().getDefaultState());
}
