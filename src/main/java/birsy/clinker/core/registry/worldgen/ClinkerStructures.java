package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.structure.TownRuinsStructure;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerStructures {
    public static class Types {
        public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Clinker.MOD_ID);

        public static Supplier<StructureType<TownRuinsStructure>> TOWN_RUINS =
                STRUCTURE_TYPES.register("town_ruins", () -> () -> TownRuinsStructure.CODEC);
    }

    public static class Pieces {
        public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PIECE, Clinker.MOD_ID);

        public static Supplier<StructurePieceType> TOWN_RUINS_CROSS =
                STRUCTURE_PIECES.register("town_ruins_cross", () -> TownRuinsStructure.CrossBuilding::new);
    }
}
