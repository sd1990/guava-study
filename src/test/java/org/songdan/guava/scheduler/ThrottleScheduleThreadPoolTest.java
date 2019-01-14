package org.songdan.guava.scheduler;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class ThrottleScheduleThreadPoolTest {

    class Task implements ThrottleScheduleThreadPool.IdentifyTask {

        private int taskId;

        private int version;

        private long delay;

        public Task(int taskId, int version, int delay) {
            this.taskId = taskId;
            this.version = version;
            this.delay = delay;
        }

        @Override
        public int getId() {
            return taskId;
        }

        @Override
        public void run() {
            System.out.println(LocalDateTime.now() +"-->task " + taskId + "[" + version + "]" + "delay: "+delay+" run !!!");

        }

        @Override
        public String toString() {
            return "Task{" +
                    "taskId=" + taskId +
                    ", version=" + version +
                    ", delay=" + delay +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Task task = (Task) o;
            return taskId == task.taskId;
        }

        @Override
        public int hashCode() {

            return Objects.hash(taskId);
        }
    }

    @Test
    public void submit() throws ExecutionException, InterruptedException {
        ThrottleScheduleThreadPool scheduleThreadPool = new ThrottleScheduleThreadPool(2);
        scheduleThreadPool.schedule(new Task(1, 1,50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(40);
        scheduleThreadPool.schedule(new Task(1, 2,50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        scheduleThreadPool.schedule(new Task(1, 3,50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        scheduleThreadPool.schedule(new Task(1, 4,50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        scheduleThreadPool.schedule(new Task(1, 5,50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
        scheduleThreadPool.shutdown();
        while (!scheduleThreadPool.isTerminated()) {
        }
    }

    @Test
    public void submitConcurrent() throws ExecutionException, InterruptedException {
        ThrottleScheduleThreadPool scheduleThreadPool = new ThrottleScheduleThreadPool(2);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> {
//                int delay = ThreadLocalRandom.current().nextInt(10, 50);
                int delay = finalI+1;
                scheduleThreadPool.schedule(new Task(1, finalI,delay), delay, TimeUnit.MILLISECONDS);
            });
//            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 5));
        }
        Thread.sleep(1000);
        scheduleThreadPool.shutdown();
        executorService.shutdown();
        while (!scheduleThreadPool.isTerminated()) {
        }
    }
}