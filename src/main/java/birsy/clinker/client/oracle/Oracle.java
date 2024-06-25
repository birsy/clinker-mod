package birsy.clinker.client.oracle;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Oracle {
    private boolean interrupted = false;
    protected DialogueEvent interruptingDialogue;
    protected final List<DialogueEvent> dialogueQueue = new ArrayList<>();

    public void tickDialogue() {
        DialogueEvent currentDialogue = this.getCurrentlyTickingDialogueEvent();

        if (currentDialogue == null) return;

        if (!currentDialogue.hasBegun()) {
            currentDialogue.begin();
        }

        currentDialogue.tick();

        if (currentDialogue.hasFinished()) {
            this.progressDialogue();
        }
    }

    protected void progressDialogue() {
        DialogueEvent currentDialogue = this.getCurrentlyTickingDialogueEvent();
        currentDialogue.finish();

        if (this.interrupted) {
            // if we've been interrupted, and we're supposed to continue what we've been saying, then continue what we were saying.
            this.interrupted = false;
            DialogueEvent newCurrentDialogue = this.getCurrentlyTickingDialogueEvent();
            if (newCurrentDialogue != null) newCurrentDialogue.continueFromInterruption();
        } else {
            this.dialogueQueue.remove(0);
        }
    }

    public void queueDialogueEvent(DialogueEvent event) {
        this.dialogueQueue.add(event);
    }

    public void interrupt(DialogueEvent interruption, boolean continuePreviousSentenceAfter ) {
        DialogueEvent currentDialogue = this.getCurrentlyTickingDialogueEvent();
        if (currentDialogue != null) currentDialogue.interrupt();

        if (continuePreviousSentenceAfter) {
            this.interrupted = true;

            this.interruptingDialogue = interruption;
            this.interruptingDialogue.begin();
        } else {
            this.dialogueQueue.clear();
            this.queueDialogueEvent(interruption);
        }
    }

    @Nullable
    public DialogueEvent getCurrentlyTickingDialogueEvent() {
        if (this.interrupted) {
            return this.interruptingDialogue;
        }
        if (!this.dialogueQueue.isEmpty()) {
            return this.dialogueQueue.get(0);
        }

        return null;
    }
}
