/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.timer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.TimerTask;
import com.vaadin.flow.component.html.Span;
import java.time.Instant;

public class Timer extends HorizontalLayout {

    private final Span timeLabel = new Span();
    private final Instant startTime;
    private final long duration;
    private final Runnable onTimeUp;

    private final java.util.Timer timer = new java.util.Timer(true);
    private final UI ui;

    public Timer(Instant startTime, long duration, Runnable onTimeUp) {
        this.startTime = startTime;
        this.duration = duration;
        this.onTimeUp = onTimeUp;
        this.ui = UI.getCurrent();

        add(new Span("Verbleibende Zeit: "), timeLabel);

        start();
    }

    private void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ui.access(() -> {
                    long elapsed = Instant.now().toEpochMilli() - startTime.toEpochMilli();
                    long remaining = duration - elapsed;

                    if (remaining <= 0) {
                        timeLabel.setText("00:00");
                        timer.cancel();
                        onTimeUp.run();
                    } else {
                        updateLabel(remaining);
                    }
                });
            }
        }, 0, 1000);
    }

    private void updateLabel(long remaining) {
        long totalSeconds = remaining / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
}
