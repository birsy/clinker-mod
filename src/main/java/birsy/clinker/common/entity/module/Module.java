package birsy.clinker.common.entity.module;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.SequencedMap;

public class Module<E extends Entity & ModuleHolder<E>> {
    public final E self;
    protected Module(E self) { this.self = self; }

    public void save(CompoundTag tag) {}
    public void load(CompoundTag tag) {}
    public void defineSyncedData(SynchedEntityData.Builder builder) {}

    public void tick() {}
    public boolean canAddPassenger(Entity passenger) { return true; }
    public boolean positionRider(Entity passenger, Entity.MoveFunction moveFunction) { return false; }
    public boolean isPushable() { return true; }

    public static class Set<E extends Entity & ModuleHolder<E>> {
        private final SequencedMap<Class<? extends Module>, Module<E>> byType = new LinkedHashMap<>();

        public <M extends Module<E>> M add(M module) {
            byType.put(module.getClass(), module);
            return module;
        }
        public <M extends Module<E>> Optional<M> get(Class<M> clazz) {
            return Optional.ofNullable(clazz.cast(byType.get(clazz)));
        }

        public void save(CompoundTag tag) {
            CompoundTag moduleData = new CompoundTag(byType.size());
            byType.forEach((clazz, module) -> {
                CompoundTag moduleTag = new CompoundTag();
                module.save(moduleTag);
                if (!moduleTag.isEmpty())
                    moduleData.put(clazz.getSimpleName(), moduleTag);
            });
            tag.put("ModuleData", moduleData);
        }
        public void load(CompoundTag tag) {
            CompoundTag moduleData = tag.getCompound("ModuleData");
            byType.forEach((clazz, module) -> {
                String name = clazz.getSimpleName();
                if (!moduleData.contains(name)) return;
                CompoundTag moduleTag = moduleData.getCompound(name);
                module.load(moduleTag);
            });
        }
        public void defineSyncedData(SynchedEntityData.Builder builder) {
            this.byType.forEach((clazz, module) -> module.defineSyncedData(builder));
        }
        public void tick() { byType.values().forEach(Module::tick); }
        public boolean canAddPassenger(Entity passenger) {
            for (Module module : this.byType.values())
                if (!module.canAddPassenger(passenger)) return false;
            return true;
        }
        public boolean positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
            for (Module module : this.byType.values())
                if (module.positionRider(passenger, moveFunction)) return true;
            return false;
        }
        public boolean isPushable() {
            for (Module module : this.byType.values())
                if (!module.isPushable()) return false;
            return true;
        }
    }
}
