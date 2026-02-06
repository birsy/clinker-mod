package birsy.clinker.client;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

//@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerMusic {
    public static final Music OTHERSHORE_SURFACE = Musics.createGameMusic(ClinkerSounds.MUSIC_OTHERSHORE_SURFACE);
//    private static final RandomSource musicRandom = RandomSource.create();
//    private static int aquiferExitTimer = 0;

    // todo: figure out how the SelectMusicEvent works
    // it seems super broken?
//    @SubscribeEvent
//    public static void selectClinkerMusic(SelectMusicEvent event) {
//        LocalPlayer player = Minecraft.getInstance().player;
//
//        if (player == null) {
//            aquiferExitTimer = -1;
//            return;
//        }
//        if (player.level().dimension() != ClinkerWorld.OTHERSHORE) {
//            aquiferExitTimer = -1;
//            return;
//        }
//
//        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
//
//        // aquifer automatically turns off all music
//        aquiferExitTimer--;
//        if (event.getPlayingMusic() != null) {
//            if (biome.is(ClinkerBiomes.AQUIFER) || aquiferExitTimer > 0) {
//                if (aquiferExitTimer <= 0) aquiferExitTimer = musicRandom.nextIntBetweenInclusive(
//                        event.getMusic() == null ? OTHERSHORE_SURFACE.getMinDelay() : event.getMusic().getMinDelay(),
//                        event.getMusic() == null ? OTHERSHORE_SURFACE.getMaxDelay() : event.getMusic().getMaxDelay());
//                event.overrideMusic(null);
//            }
//        }
//    }
}
