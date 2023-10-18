package birsy.clinker.common.world.level;

import net.minecraft.util.RandomSource;

public class BookOfStarNames {
    public static final String[] NAMES = {"Andromeda", "Antlia", "Apus", "Aquarius", "Aquila", "Ara", "Aries", "Auriga", "Barnard's Star", "Boutes", "Caelum", "Camelopardalis", "Cancer", "Canes Venatici", "Canis Major", "Canis Minor", "Capricornus", "Carina", "Cassiopeia", "Centaurus", "Cepheus", "Cetus", "Chamaeleonus", "Circinus", "Columba", "Coma Berenices", "Corona Australis", "Corona Borealis", "Corvus", "Crater", "Crux", "Cygnus", "Delphinus", "Dorado", "Draco", "Equuleus", "Eridanus", "Fornax", "Gemini", "Grus", "Hercules", "Horologium", "Hydra", "Hydrus", "Indus", "Lacerta", "Leo", "Leo Minor", "Lepus", "Libra", "Lupus", "Lynx", "Lyra", "Mensa", "Microscopium", "Monoceros", "Musca", "Norma", "Octans", "Ophiuchus", "Orion", "Pavo", "Pegasus", "Perseus", "Phoenix", "Pictor", "Pisces", "Piscis Austrinus", "Puppis", "Pyxis", "Reticulum", "Sagitta", "Sagittarius", "Scorpius", "Sculptor", "Scutum", "Serpens", "Sextans", "Taurus", "Telescopium", "Triangulum", "Triangulum Australis", "Tucana", "Ursa Major", "Ursa Minor", "Vela", "Virgo", "Volans", "Vulpecula", "Wormwood"};

    public static String getRandomName(RandomSource source) {
        return NAMES[source.nextInt(NAMES.length)];
    }

}
