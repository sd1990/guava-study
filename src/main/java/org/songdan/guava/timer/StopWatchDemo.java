package org.songdan.guava.timer;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * @author: Songdan
 * @create: 2018-08-16 14:46
 **/
public class StopWatchDemo {

    public static void main(String[] args) throws InterruptedException {
        PipeLineStopWatch timer = PipeLineStopWatch.createStarted();
        TimeUnit.SECONDS.sleep(1);
        System.out.println(timer.elapsed(TimeUnit.MILLISECONDS));
        TimeUnit.SECONDS.sleep(1);
        System.out.println(timer.elapsed(TimeUnit.MILLISECONDS));
        TimeUnit.SECONDS.sleep(1);
        System.out.println(timer.elapsed(TimeUnit.MILLISECONDS));
        TimeUnit.SECONDS.sleep(1);
        System.out.println(timer.elapsed(TimeUnit.MILLISECONDS));
        System.out.println(timer.stop());
    }



}
