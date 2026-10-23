package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.profiling.ThreadSafeRunningAverageTracker;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// todo: better profiling
public final class SynthesizerProfiler {
    public static final SynthesizerProfiler INSTANCE = new SynthesizerProfiler();

    final ConcurrentMap<String, ThreadSafeRunningAverageTracker> totalTimeTrackers = new ConcurrentHashMap<>();
    final ConcurrentMap<String, ThreadSafeRunningAverageTracker> timePerSliceTrackers = new ConcurrentHashMap<>();

    private SynthesizerProfiler() {}

    void addTime(@Nullable String profilerId, long ns, int cellsFilled) {
        if (profilerId == null) return;
        if (cellsFilled == 0) return;
        long nsPerCell = ns / cellsFilled;
        totalTimeTrackers.computeIfAbsent(profilerId, key -> new ThreadSafeRunningAverageTracker()).recordTime(ns);
        timePerSliceTrackers.computeIfAbsent(profilerId, key -> new ThreadSafeRunningAverageTracker()).recordTime(nsPerCell);
    }

    public void log() {
        List<String> keys = totalTimeTrackers.keySet().stream()
                .sorted(Comparator.comparingDouble(key -> totalTimeTrackers.get(key).getAverage()).reversed())
                .toList();
        Clinker.LOGGER.info("SYNTHESIZER PROFILING RESULTS:");
        for (String id : keys) {
            double avgMs = totalTimeTrackers.get(id).getAverage() / 1_000_000.0;
            double avgPerCellNs = timePerSliceTrackers.get(id).getAverage();
            Clinker.LOGGER.info("{}: avg = {} ms, avg per cell = {} ns", id, avgMs, avgPerCellNs);
        }
        Clinker.LOGGER.info("-------------------------------");
    }
}
