package birsy.clinker.common.world.feature.structure;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

public class GnomeVillageStructure extends Structure<NoFeatureConfig>
{
	public GnomeVillageStructure(Codec<NoFeatureConfig> config) {
		super(config);
	}
	
	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
