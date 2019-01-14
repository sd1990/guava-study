package org.songdan.guava.timer;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * 流水线式计算每段耗时
 *
 * @author: Songdan
 * @create: 2018-08-16 14:55
 **/
public class PipeLineStopWatch {

    public static PipeLineStopWatch createStarted() {
        return wrap(Stopwatch.createStarted());
    }

    private static PipeLineStopWatch wrap(Stopwatch stopwatch) {
        return new PipeLineStopWatch(Stopwatch.createStarted());
    }

    private Stopwatch stopwatch;

    private long startTime;

    private PipeLineStopWatch(Stopwatch stopwatch) {
        this.stopwatch = stopwatch;
        this.startTime = System.currentTimeMillis();
    }

    public long elapsed(TimeUnit timeUnit) {
        long elapsed = stopwatch.elapsed(timeUnit);
        reset();
        return elapsed;
    }

    public void reset() {
        stopwatch.reset();
        stopwatch.start();
    }

    public long stop() {
        stopwatch.stop();
        return System.currentTimeMillis() - startTime;
    }

}
