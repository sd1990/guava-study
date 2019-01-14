package org.songdan.guava.scheduler;

import com.google.common.util.concurrent.*;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @author: Songdan
 * @create: 2019-01-13 12:06
 **/
public class ThrottleScheduleThreadPool extends ScheduledThreadPoolExecutor {

    private ListeningScheduledExecutorService scheduledExecutorService;

    private Executor executor = MoreExecutors.directExecutor();
    private ConcurrentHashMap<IdentifyTask, ScheduledFuture> taskMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<IdentifyTask, Future<ScheduledFuture>> scheduleMap = new ConcurrentHashMap<>();


    public ThrottleScheduleThreadPool(int corePoolSize) {
        super(corePoolSize);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize));
    }

    public ThrottleScheduleThreadPool(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize, threadFactory));
    }

    public ThrottleScheduleThreadPool(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize, handler));
    }

    public ThrottleScheduleThreadPool(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit timeUnit) {
        if (!IdentifyTask.class.isInstance(command)) {
            throw new IllegalArgumentException("任务类型错误");
        }
        IdentifyTask task = (IdentifyTask) command;
        try {
            return submitDelayTask(task, delay, timeUnit);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public ScheduledFuture submitDelayTask(IdentifyTask task, long delay, TimeUnit timeUnit) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        while (true) {
            ScheduledFuture preFuture = taskMap.get(task);
            if (preFuture == null) {
                Result result = innerSubmit(task, delay, timeUnit);
                //为了防止并发情况下，delay时间短的任务把delay时间长的任务顶掉
                if (result.success) {
                    //说明task提交成功
                    return result.future;
                }
            } else if (preFuture.getDelay(timeUnit) < (delay-timeUnit.convert(System.currentTimeMillis()-start,TimeUnit.MILLISECONDS))) {
//                System.out.println(LocalDateTime.now()+" task:" + task + "replace pre future:"+preFuture);
                if (taskMap.remove(task, preFuture)) {
                    preFuture.cancel(false);
                    Result result = innerSubmit(task, delay, timeUnit);
                    //为了防止并发情况下，delay时间短的任务把delay时间长的任务顶掉
                    if (result.success) {
                        //说明task提交成功
                        return result.future;
                    }
                }
            } else {
                return preFuture;
            }
        }
    }

    private Result innerSubmit(IdentifyTask task, long delay, TimeUnit timeUnit) throws ExecutionException, InterruptedException {
        ListenableFutureTask<ScheduledFuture> futureTask = ListenableFutureTask.create(() -> {
            ScheduledFuture future = taskMap.get(task);
            if (future == null) {
                ListenableScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(task, delay, timeUnit);
                taskMap.put(task, scheduledFuture);
                scheduledFuture.addListener(() -> {
                    //从map中移除，防止内存泄漏
                    taskMap.remove(task, scheduledFuture);
                }, executor);
                future = scheduledFuture;
            } else {
                System.out.println(LocalDateTime.now()+": task ["+task+"] concurrent occur!!!");
            }
            return future;
        });
        futureTask.addListener(() -> {
            //从map中移除，防止内存泄漏
            //并发问题由此引出，移除速度过快，导致putIfAbsent没有起到太大的效果,从taskMap.remove 到 新提交任务这一步
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {

            }
            scheduleMap.remove(task, futureTask);
        }, executor);
        boolean success = false;
//        System.out.println(LocalDateTime.now()+"---- put schedule work" + task);
        Future<ScheduledFuture> future = scheduleMap.putIfAbsent(task, futureTask);
        if (future == null) {
//            System.out.println(LocalDateTime.now()+"---- put schedule work" + task+" success!!!");
            future = futureTask;
            futureTask.run();
            success = true;
        }
        return new Result(success, future.get());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        scheduledExecutorService.shutdown();
    }

    interface IdentifyTask extends Runnable {
        int getId();
    }

    class Pair {
        IdentifyTask task;

        ListenableScheduledFuture listenableScheduledFuture;

        public Pair(IdentifyTask task, ListenableScheduledFuture listenableScheduledFuture) {
            this.task = task;
            this.listenableScheduledFuture = listenableScheduledFuture;
        }
    }

    class Result {
        boolean success;
        ScheduledFuture future;

        public Result(boolean success, ScheduledFuture future) {
            this.success = success;
            this.future = future;
        }
    }


}
