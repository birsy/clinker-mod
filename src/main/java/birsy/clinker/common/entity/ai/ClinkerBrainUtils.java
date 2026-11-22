package birsy.clinker.common.entity.ai;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.util.BrainUtils;

public class ClinkerBrainUtils {
    public static <M> void serializeMemory(LivingEntity entity, CompoundTag tag, MemoryModuleType<M> type) {
        serializeMemory(entity.getBrain(), tag, type);
    }
    public static <M> void serializeMemory(Brain<?> brain, CompoundTag tag, MemoryModuleType<M> type) {
        if (type.getCodec().isEmpty()) return;
        M memory = BrainUtils.getMemory(brain, type);
        ResourceLocation key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKeyOrNull(type);
        if (key == null) return;
        if (memory != null) {
            long timeUntilExpire = brain.getTimeUntilExpiry(type);
            if (timeUntilExpire == 0L) timeUntilExpire = Long.MAX_VALUE;
            type.getCodec().get().encodeStart(NbtOps.INSTANCE, ExpirableValue.of(memory, timeUntilExpire))
                    .ifSuccess(memTag -> tag.put(key.toString(), memTag));
        }
    }

    public static <M> void deserializeMemory(LivingEntity entity, CompoundTag tag, MemoryModuleType<M> type) {
        deserializeMemory(entity.getBrain(), tag, type);
    }
    public static <M> void deserializeMemory(Brain<?> brain, CompoundTag tag, MemoryModuleType<M> type) {
        if (type.getCodec().isEmpty()) return;
        ResourceLocation key = BuiltInRegistries.MEMORY_MODULE_TYPE.getKeyOrNull(type);
        if (key == null) return;
        String memoryName = key.toString();
        if (tag.contains(memoryName)) {
            type.getCodec().get().decode(NbtOps.INSTANCE, tag.get(memoryName))
                    .ifSuccess(pair -> {
                        ExpirableValue<M> memory = pair.getFirst();
                        if (memory.canExpire()) BrainUtils.setForgettableMemory(brain, type, memory.getValue(), (int) memory.getTimeToLive());
                        else BrainUtils.setMemory(brain, type, memory.getValue());
                    });
        }
    }
}
