package tech.nilsson.interaction_schemes;

import tech.nilsson.thinkgear.events.BlinkEvent;
import tech.nilsson.thinkgear.events.ESenseEvent;
import tech.nilsson.utils.SelectionManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class HighBlinkingInteractionScheme extends InteractionScheme {
    private static final int BLINK_TIMER_DELAY = 2000;

    public HighBlinkingInteractionScheme(
            SelectionManager selectionManager,
            int weakBlinkThreshold,
            int strongBlinkThreshold
    ) {
        super(selectionManager, weakBlinkThreshold, strongBlinkThreshold);
    }

    @Override
    public void onReceived(ESenseEvent eSenseEvent) {
        // Ignore
    }

    @Override
    public void onReceived(BlinkEvent blinkEvent) {
        if (isVoluntaryStrongBlink(blinkEvent)) {
            // perform action
            System.out.println("Got strong blink");
        } else if (isVoluntaryWeakBlink(blinkEvent)) {
            System.out.println("Got weak blink");
            incrementCurrentAmountOfBlinks();
            TimerTask task = new TimerTask() {
                public void run() {
                    if (getCurrentAmountOfBlinks() == 1) {
                        getSelectionManager().selectNextNode();
                    } else if (getCurrentAmountOfBlinks() > 1) {
                        getSelectionManager().selectPreviousNode();
                    }
                    resetCurrentAmountOfBlinks();
                }
            };
            Timer timer = new Timer("timer");
            timer.schedule(task, BLINK_TIMER_DELAY);
        }
    }

    private boolean isVoluntaryWeakBlink(BlinkEvent blinkEvent) {
        return  blinkEvent.getBlinkStrength() >= getWeakBlinkThreshold() &&
                blinkEvent.getBlinkStrength() < getStrongBlinkThreshold();
    }

    private boolean isVoluntaryStrongBlink(BlinkEvent blinkEvent) {
        return blinkEvent.getBlinkStrength() >= getStrongBlinkThreshold();
    }

    @Override
    public void stop() {

    }
}
