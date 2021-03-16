package tech.nilsson.interaction_schemes;

import tech.nilsson.thinkgear.events.BlinkEvent;
import tech.nilsson.thinkgear.events.ESenseEvent;
import tech.nilsson.utils.SelectionManager;

import java.util.concurrent.atomic.AtomicInteger;

public class LowBlinkingInteractionScheme extends InteractionScheme {
    public static final int NAVIGATION_THRESHOLD = 20;

    private final AtomicInteger currentAttention;
    private boolean running;

    public LowBlinkingInteractionScheme(SelectionManager selectionManager, int weakBlinkThreshold, int strongBlinkThreshold) {
        super(selectionManager, weakBlinkThreshold, strongBlinkThreshold);
        currentAttention = new AtomicInteger(0);
        running = true;

        // start tråd
        Thread navigationThread = new Thread(() -> {
            while (running) {
                if (currentAttention.get() >= NAVIGATION_THRESHOLD) {
                    getSelectionManager().selectNextNode();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Killing thread.");
        });
        navigationThread.start();
    }

    @Override
    public void onReceived(ESenseEvent eSenseEvent) {
        int attention = eSenseEvent.geteSense().getAttention();
        currentAttention.set(attention);
    }

    @Override
    public void onReceived(BlinkEvent blinkEvent) {

    }

    @Override
    public void stop() {
        running = false;
    }
}
