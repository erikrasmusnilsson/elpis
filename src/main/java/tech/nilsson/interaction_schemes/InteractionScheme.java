package tech.nilsson.interaction_schemes;

import tech.nilsson.thinkgear.ThinkGearConnection;
import tech.nilsson.utils.SelectionManager;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class InteractionScheme implements ThinkGearConnection.Listener {
    private final SelectionManager selectionManager;
    private AtomicInteger currentAmountOfBlinks;
    private int weakBlinkThreshold;
    private int strongBlinkThreshold;

    public InteractionScheme(
            SelectionManager selectionManager,
            int weakBlinkThreshold,
            int strongBlinkThreshold
    ) {
        this.selectionManager = selectionManager;
        this.weakBlinkThreshold = weakBlinkThreshold;
        this.strongBlinkThreshold = strongBlinkThreshold;
        currentAmountOfBlinks = new AtomicInteger(0);
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public int getWeakBlinkThreshold() {
        return weakBlinkThreshold;
    }

    public void setWeakBlinkThreshold(int weakBlinkThreshold) {
        this.weakBlinkThreshold = weakBlinkThreshold;
    }

    public int getStrongBlinkThreshold() {
        return strongBlinkThreshold;
    }

    public void setStrongBlinkThreshold(int strongBlinkThreshold) {
        this.strongBlinkThreshold = strongBlinkThreshold;
    }

    public int incrementCurrentAmountOfBlinks() {
        return currentAmountOfBlinks.incrementAndGet();
    }

    public int getCurrentAmountOfBlinks() {
        return currentAmountOfBlinks.get();
    }

    public void resetCurrentAmountOfBlinks() {
        currentAmountOfBlinks.set(0);
    }

    public abstract void stop();
}
