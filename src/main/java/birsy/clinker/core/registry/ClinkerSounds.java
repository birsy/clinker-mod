package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registration.SoundHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClinkerSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Clinker.MOD_ID);
    public static final List<SoundHolder> SOUND_HOLDERS = new ArrayList<>();

    public static final SoundHolder BLOCK_STOVE_LOOP = SoundHolder.builder(SOUNDS, "block.stove.heat_loop")
            .noSubtitle()
            .stream().build(SOUND_HOLDERS);

    public static final SoundHolder GNOME_IDLE = SoundHolder.builder(SOUNDS, "entity.gnome.idle")
            .variants(6).build(SOUND_HOLDERS);
    public static final SoundHolder GNOME_HURT = SoundHolder.builder(SOUNDS, "entity.gnome.hurt")
            .variants(7).build(SOUND_HOLDERS);
    public static final SoundHolder GNOME_CHAT = SoundHolder.builder(SOUNDS, "entity.gnome.chat")
            .variants(3).build(SOUND_HOLDERS);
    public static final SoundHolder GNOME_DEATH = SoundHolder.builder(SOUNDS, "entity.gnome.death")
            .variants(4).build(SOUND_HOLDERS);

    public static final SoundHolder ORDNANCE_BOUNCE = SoundHolder.builder(SOUNDS, "entity.ordnance.bounce")
            .build(SOUND_HOLDERS);
    public static final SoundHolder ORDNANCE_BOUNCE_STICKY = SoundHolder.builder(SOUNDS, "entity.ordnance.bounce_sticky")
            .build(SOUND_HOLDERS);
    public static final SoundHolder ORDNANCE_BOUNCE_BOUNCY = SoundHolder.builder(SOUNDS, "entity.ordnance.bounce_bouncy")
            .build(SOUND_HOLDERS);
    public static final SoundHolder ORDNANCE_BOUNCE_THORNY = SoundHolder.builder(SOUNDS, "entity.ordnance.bounce_thorny")
            .build(SOUND_HOLDERS);
    public static final SoundHolder ORDNANCE_EXPLODE = SoundHolder.builder(SOUNDS, "entity.ordnance.explode")
            .build(SOUND_HOLDERS);
    public static final SoundHolder ORDNANCE_FUSE_LOOP = SoundHolder.builder(SOUNDS, "entity.ordnance.fuse_loop")
            .noSubtitle()
            .build(SOUND_HOLDERS);

    public static final SoundHolder AMBIENT_ASH_PLAINS_ADDITIONS = SoundHolder.builder(SOUNDS, "ambient.ash_plains.additions")
            .noSubtitle().stream().volume(0.6f)
            .variants("ambient/othershore/ash_plains/bubbling1", "ambient/othershore/ash_plains/bubbling2")
            .build(SOUND_HOLDERS);
    public static final SoundHolder AMBIENT_ASH_PLAINS_LOOP = SoundHolder.builder(SOUNDS, "ambient.ash_plains.loop")
            .noSubtitle().stream().volume(0.6f)
            .variants("ambient/othershore/ash_plains/chimney1")
            .build(SOUND_HOLDERS);

    public static final SoundHolder MUSIC_DISC_CODA = SoundHolder.builder(SOUNDS, "music_disc.coda")
            .noSubtitle().stream().variants("records/coda")
            .build(SOUND_HOLDERS);
    public static final SoundHolder MUSIC_OTHERSHORE_SURFACE = SoundHolder.builder(SOUNDS, "music.othershore.surface")
            .noSubtitle().stream().volume(0.5f)
            .variants(
                    "music/game/burckhardt",
                    "music/game/foot_of_worm",
                    "music/game/memoir",
                    "music/game/tableland",
                    "music/game/withered_history",
                    "music/game/zosimus"
            ).build(SOUND_HOLDERS);
    public static final SoundHolder MUSIC_OTHERSHORE_SUBTERRANEAN = SoundHolder.builder(SOUNDS, "music.othershore.subterranean")
            .noSubtitle().stream()
            .variant("music/game/subterranean/hive_bound", v -> v.volume(0.3f))
            .variant("music/game/subterranean/sardine", v -> v.volume(0.5f))
            .variant("music/game/subterranean/unplumbed", v -> v.volume(0.8f))
            .build(SOUND_HOLDERS);
    public static final SoundHolder MUSIC_OTHERSHORE_AQUIFER = SoundHolder.builder(SOUNDS, "music.othershore.aquifer")
            .noSubtitle()
            .variants("music/game/silence")
            .build(SOUND_HOLDERS);
}
