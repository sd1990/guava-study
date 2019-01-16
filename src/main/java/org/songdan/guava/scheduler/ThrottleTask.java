package org.songdan.guava.scheduler;

/**
 * 具备标示的task
 */
public interface ThrottleTask extends Runnable {
    /**
     * 任务的识别，相同的identify task视为同类task
     * @return
     */
    String identify();
}
