package birsy.clinker.core.util.profiling;

import java.util.concurrent.atomic.LongAdder;

public class RunningAverageTracker {
    private final LongAdder totalSum = new LongAdder();
    private final LongAdder totalCount = new LongAdder();

    public void recordTime(long nanoSeconds) {
        totalSum.add(nanoSeconds);
        totalCount.increment();
    }

    public double getAverage() {
        long count = totalCount.sum();
        if (count == 0) return 0.0;
        return totalSum.sum() / (double) count;
    }

    public void reset() {
        totalSum.reset();
        totalCount.reset();
    }
}
