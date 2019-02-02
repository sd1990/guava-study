package org.songdan.guava.scheduler;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;

public class ThrottleScheduleThreadPoolTestV2 {

    private int count = 0;

    class Task implements ThrottleTask {

        private int taskId;

        private int version;

        private long delay;

        public Task(int taskId, int version, int delay) {
            this.taskId = taskId;
            this.version = version;
            this.delay = delay;
        }

        @Override
        public void run() {
            synchronized (ThrottleScheduleThreadPoolTestV2.this) {
                count++;
                print("task " + taskId + "[" + version + "]" + "delay: " + delay + " run !!!");
            }

        }

        private void print(String content) {
            System.out.println(Thread.currentThread() + "[" + LocalDateTime.now() + "]" + content);
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

        @Override
        public String identify() {
            return String.valueOf(taskId);
        }
    }

    @Test
    public void submit() throws ExecutionException, InterruptedException {
        ThrottleScheduleThreadPoolV2 scheduleThreadPool = new ThrottleScheduleThreadPoolV2(5);
        scheduleThreadPool.prestartAllCoreThreads();
        Task task1 = new Task(1, 1, 50);
        scheduleThreadPool.schedule(task1, 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        Task task2 = new Task(1, 2, 50);
        scheduleThreadPool.schedule(task2, 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        scheduleThreadPool.schedule(new Task(1, 3, 50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        scheduleThreadPool.schedule(new Task(1, 4, 50), 50, TimeUnit.MILLISECONDS);
        Thread.sleep(10);
        scheduleThreadPool.schedule(new Task(1, 5, 50), 50, TimeUnit.MILLISECONDS);
        scheduleThreadPool.shutdown();
        Thread.sleep(1000);
        while (!scheduleThreadPool.isTerminated()) {
        }
    }

    @Test
    public void submitConcurrent() throws ExecutionException, InterruptedException {
        ThrottleScheduleThreadPoolV2 scheduleThreadPool = new ThrottleScheduleThreadPoolV2(2);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 600; i++) {
            int finalI = i;
            executorService.submit(() -> {
//                int delay = ThreadLocalRandom.current().nextInt(10, 50);
                int delay = 50;
                scheduleThreadPool.schedule(new Task(1, finalI, delay), delay, TimeUnit.MILLISECONDS);
            });
//            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 5));
        }
        Thread.sleep(1000);
        scheduleThreadPool.shutdown();
        executorService.shutdown();
        Assert.assertEquals(1, count);
        while (!scheduleThreadPool.isTerminated()) {
            System.out.println();
        }
    }

    @Test
    public void submitCost() {
        long start = System.currentTimeMillis();
        print("hello world");
        System.out.println(System.currentTimeMillis()-start);
//        print("commit ["+9999+"][" + 199999 + "]costs:" + (System.currentTimeMillis() - start));
        for (int j = 0; j < 20; j++) {
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16));
            executorService.prestartAllCoreThreads();
            for (int i = 0; i < 5; i++) {
                start = System.currentTimeMillis();
                executorService.submit(() -> {
                });
                print((System.currentTimeMillis() - start));
            }
            executorService.shutdownNow();
        }
    }

    @Test
    public void submitCostV3() {
        long start = System.currentTimeMillis();
        /*for (int j = 0; j < 20; j++) {
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16));
            executorService.prestartAllCoreThreads();
            for (int i = 0; i < 5; i++) {
                start = System.currentTimeMillis();
                print("commit fuck costs:" + (System.currentTimeMillis() - start));
                executorService.submit(() -> {

                });
                print("commit ["+j+"][" + i + "]costs:" + (System.currentTimeMillis() - start));
            }
            executorService.shutdownNow();
        }*/
        for (int j = 0; j < 20; j++) {
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16));
            executorService.prestartAllCoreThreads();
            for (int i = 0; i < 5; i++) {
                start = System.currentTimeMillis();
//                print(System.currentTimeMillis() - start);
//                print("commit fuck costs:" + (System.currentTimeMillis() - start));
                executorService.submit(() -> {

                });
//                print("commit ["+j+"][" + i + "]costs:" + (System.currentTimeMillis() - start));
                print((System.currentTimeMillis() - start));
            }
            executorService.shutdownNow();
        }
    }

    @Test
    public void submitCostV2() {
        for (int j = 0; j < 20; j++) {
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16));
//        executorService.prestartAllCoreThreads();
            for (int i = 0; i < 5; i++) {
                long start = System.currentTimeMillis();
                executorService.submit(() -> {
                });
                print("commit ["+j+"][" + i + "]costs:" + (System.currentTimeMillis() - start));
            }
            executorService.shutdownNow();
        }
    }

    @Test
    public void submitCostSchedule() {
        ScheduledThreadPoolExecutor executorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
        executorService.prestartAllCoreThreads();
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            executorService.submit(() -> {
            });
            print("commit [" + i + "]costs:" + (System.currentTimeMillis() - start));
        }
        executorService.shutdownNow();
    }

    private void print(Object content) {
//        System.out.println(Thread.currentThread() + "[" + LocalDateTime.now() + "]" + content);
//        System.out.println("[" + LocalDateTime.now() + "]" + content);
//        System.out.println("[" + LocalDateTime.now() + "]" + content);
//        System.out.println("[" + new Date() + "]" + content);
//        System.out.println(Thread.currentThread() + content);
        System.out.println(content);
    }

    private void print(long content) {
//        System.out.println(Thread.currentThread() + "[" + LocalDateTime.now() + "]" + content);
//        System.out.println("[" + LocalDateTime.now() + "]" + content);
//        System.out.println("[" + LocalDateTime.now() + "]" + content);
//        System.out.println("[" + LocalDateTime.now() + "]" + content);
//        System.out.println(Thread.currentThread() + content);
        System.out.println(content);
    }
}