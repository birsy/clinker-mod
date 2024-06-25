package birsy.clinker.client.oracle;

public abstract class DialogueEvent {
    protected boolean begun = false;
    protected int progress = 0;
    protected final int lengthInTicks;

    public DialogueEvent(int lengthInTicks) {
        this.lengthInTicks = lengthInTicks;
    }

    public void tick() {
        this.progress++;
    }

    public boolean hasBegun() {
        return this.begun;
    }

    public boolean hasFinished() {
        return this.progress >= this.lengthInTicks;
    }

    public void begin() {
        this.begun = true;
    }

    public void interrupt() {}

    public void continueFromInterruption() {}

    public void finish() {}
}
