package tech.nilsson.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class Interval implements Runnable {
    private final Callback callback;
    private final int delay;
    private final AtomicBoolean running;

    public Interval(Callback callback, int delay) {
        this.callback = callback;
        this.delay = delay;
        running = new AtomicBoolean(false);
    }

    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        do {
            try {
                callback.callback();
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(running.get());
    }

    public interface Callback {
        void callback();
    }
}
