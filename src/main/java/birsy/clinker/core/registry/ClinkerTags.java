package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ClinkerTags {
    public static final TagKey<Block> WORKSTATION = BlockTags.create(new ResourceLocation(Clinker.MOD_ID, "valid_workstation_blocks"));

}
