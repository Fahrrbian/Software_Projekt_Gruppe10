/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.timer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.TimerTask;
import com.vaadin.flow.component.html.Span;

public class Timer extends HorizontalLayout {

    private final Span timeLabel = new Span();
    private final long duration;
    private final Runnable onTimeUp;

    private long endTime;
    private final java.util.Timer timer = new java.util.Timer(true);
    private final UI ui;

    public Timer(long duration, Runnable onTimeUp) {
        this.duration = duration;
        this.onTimeUp = onTimeUp;
        this.ui = UI.getCurrent();

        add(new Span("Verbleibende Zeit: "), timeLabel);

        start();
    }

    private void start() {
        endTime = System.currentTimeMillis() + duration;
        updateLabel();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ui.access(() -> {
                    long remaining = endTime - System.currentTimeMillis();
                    if (remaining <= 0) {
                        timeLabel.setText("00:00");
                        timer.cancel();
                        onTimeUp.run();
                    } else {
                        updateLabel();
                    }
                });
            }
        }, 0, 1000);
    }

    private void updateLabel() {
        long remaining = endTime - System.currentTimeMillis();
        long minutes = (remaining / 1000) / 60;
        long seconds = (remaining / 1000) % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

}