package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

import java.util.ArrayList;
import java.util.List;

public record NoiseFieldModifier(Synthesizer synthesizer, Combiner combiner) {
    public static Synthesizer merged(Synthesizer original, List<NoiseFieldModifier> modifiers) {
        if (modifiers.isEmpty()) return original;

        int minY = original.minY, maxY = original.maxY;
        ArrayList<Synthesizer> synthesizers = new ArrayList<>(modifiers.size() + 1);
        synthesizers.add(original);

        for (NoiseFieldModifier modifier : modifiers) {
            synthesizers.add(modifier.synthesizer);
            minY = Math.min(minY, modifier.synthesizer.minY);
            maxY = Math.max(maxY, modifier.synthesizer.maxY);
        }

        return Synthesizer.builder(original.profilerId + "_merged")
                .withDefaultValue(original.lowerSliceDefaultValue, original.lowerSliceDefaultValue, original.defaultValueSliceY)
                .withRange(minY, maxY)
                .withDependencies(synthesizers.toArray(new Synthesizer[0]))
                .build(
                        original.resolution,
                        ctx -> {
                            float value = ctx.dependentValue(0);
                            // todo: we can probably do more with the range.
                            //       since we don't need to combine outside of the modifier's synth range...
                            for (int i = 0; i < modifiers.size(); i++)
                                value = modifiers.get(i).combiner.combine(value, ctx.dependentValue(1));
                            return value;
                        }
                );
    }

    public interface Combiner {
        float combine(float original, float computed);
    }
}
