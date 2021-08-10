package birsy.clinker.common.world.feature.structure;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;

public class GnomeVillageStructure extends StructureFeature<NoneFeatureConfiguration>
{
	public GnomeVillageStructure(Codec<NoneFeatureConfiguration> config) {
		super(config);
	}
	
	@Override
	public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
		// TODO Auto-generated method stub
		return null;
	}
}
