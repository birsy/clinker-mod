package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Clinker.MOD_ID);
    
    //Block Sounds
    public static final Supplier<SoundEvent> BLOCK_STOVE_LOOP = SOUNDS.register("block.stove.loop",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("block.stove.loop")));

    //Entity Sounds
    public static final Supplier<SoundEvent> ENTITY_GNOME_AMBIENT = SOUNDS.register("entity.gnome.ambient",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.gnome.ambient")));
    
    public static final Supplier<SoundEvent> ENTITY_GNOME_HURT = SOUNDS.register("entity.gnome.hurt",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.gnome.hurt")));
    
    public static final Supplier<SoundEvent> ENTITY_GNOME_CHAT = SOUNDS.register("entity.gnome.chat",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.gnome.chat")));
    
    public static final Supplier<SoundEvent> ENTITY_GNOME_DEATH = SOUNDS.register("entity.gnome.death",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.gnome.death")));


    public static final Supplier<SoundEvent> ORDNANCE_BOUNCE = SOUNDS.register("entity.ordnance.bounce",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.ordnance.bounce")));

    public static final Supplier<SoundEvent> ORDNANCE_EXPLODE = SOUNDS.register("entity.ordnance.explode",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.ordnance.explode")));

    public static final Supplier<SoundEvent> ORDNANCE_SPARKLE_LOOP = SOUNDS.register("entity.ordnance.sparkle",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("entity.ordnance.sparkle")));

    //Ambient Sounds
    public static final Supplier<SoundEvent> AMBIENT_ASH_PLAINS_ADDITIONS = SOUNDS.register("ambient.ash_plains.additions",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("ambient.ash_plains.additions")));

    public static final Supplier<SoundEvent> AMBIENT_ASH_PLAINS_LOOP = SOUNDS.register("ambient.ash_plains.loop",
            () -> SoundEvent.createVariableRangeEvent(Clinker.resource("ambient.ash_plains.loop")));




}
