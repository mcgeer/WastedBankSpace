package com.wastedbankspace.util;

import java.util.Timer;
import java.util.TimerTask;

/*
  If data is updated delay processing until a delay of inputs of time delay ms is reached.
 */
public class DelayedRunnable {
    private Timer timer = new Timer();
    private final int delay;
    private final Runnable runnable;

    public DelayedRunnable(int delay, Runnable r)
    {
        this.delay = delay;
        this.runnable = r;
    }

    public void onDataUpdate() {
        // Cancel any previously scheduled task
        timer.cancel();
        timer = new Timer();

        // Schedule a new task after the delay
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
    }
}