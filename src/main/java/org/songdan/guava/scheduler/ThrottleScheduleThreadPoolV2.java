package org.songdan.guava.scheduler;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @author: Songdan
 * @create: 2019-01-13 12:06
 **/
public class ThrottleScheduleThreadPoolV2 extends ScheduledThreadPoolExecutor {

    private ListeningScheduledExecutorService scheduledExecutorService;
    private ScheduledThreadPoolExecutor delegate;

    private Executor executor = MoreExecutors.directExecutor();
    private ConcurrentHashMap<String, ListenableScheduledFuture> taskMap = new ConcurrentHashMap<>();

    public ThrottleScheduleThreadPoolV2(int corePoolSize) {
        super(corePoolSize);
        this.delegate = new ScheduledThreadPoolExecutor(corePoolSize);
        scheduledExecutorService = MoreExecutors.listeningDecorator(delegate);
    }

    public ThrottleScheduleThreadPoolV2(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
        this.delegate = new ScheduledThreadPoolExecutor(corePoolSize,threadFactory);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize, threadFactory));
    }

    public ThrottleScheduleThreadPoolV2(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
        this.delegate = new ScheduledThreadPoolExecutor(corePoolSize,handler);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize, handler));
    }

    public ThrottleScheduleThreadPoolV2(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
        this.delegate = new ScheduledThreadPoolExecutor(corePoolSize,threadFactory,handler);
        scheduledExecutorService = MoreExecutors.listeningDecorator(new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit timeUnit) {
        if (!ThrottleTask.class.isInstance(command)) {
            throw new IllegalArgumentException("task type error!!! must be instance of " + ThrottleTask.class);
        }
        ThrottleTask task = (ThrottleTask) command;
        try {
            return submitDelayTask(task, delay, timeUnit);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
        }
    }


    public ScheduledFuture submitDelayTask(ThrottleTask task, long delay, TimeUnit timeUnit) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        while (true) {
            ListenableScheduledFuture preFuture = taskMap.get(task.identify());
            long cost = timeUnit.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            if (shouldSubmit(preFuture, delay, timeUnit)) {
                Result result = innerSubmit(task, delay, timeUnit, preFuture);
                //为了防止并发情况下，delay时间短的任务把delay时间长的任务顶掉
                if (result.success) {
                    //说明task提交成功
                    return result.future;
                }
            } else {
                return preFuture;
            }
        }
    }

    private boolean shouldSubmit(ListenableScheduledFuture preFuture, long delay, TimeUnit timeUnit) {
        return preFuture == null || preFuture.getDelay(timeUnit) < delay;
    }

    private Result innerSubmit(ThrottleTask task, long delay, TimeUnit timeUnit, ListenableScheduledFuture preFuture) throws ExecutionException, InterruptedException {
        print("ready submit task:" + task);
        ListenableScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(task, delay, timeUnit);
        print("pre submit task:" + task);
        scheduledFuture.addListener(() -> {
            //从map中移除，防止内存泄漏
            taskMap.remove(task.identify(), scheduledFuture);
        }, executor);
        if (preFuture == null) {
            if (taskMap.putIfAbsent(task.identify(), scheduledFuture) == null) {
                return new Result(true, scheduledFuture);
            } else {
                scheduledFuture.cancel(true);
                return new Result(false, scheduledFuture);
            }
        } else {
            if (taskMap.replace(task.identify(), preFuture, scheduledFuture)) {
                preFuture.cancel(true);
                return new Result(true, scheduledFuture);
            } else {
                scheduledFuture.cancel(true);
                return new Result(false, scheduledFuture);
            }
        }
    }

    private void print(String content) {
        System.out.println(Thread.currentThread() + "[" + LocalDateTime.now() + "]" + content);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    class Result {
        boolean success;
        ListenableScheduledFuture future;

        public Result(boolean success, ListenableScheduledFuture future) {
            this.success = success;
            this.future = future;
        }
    }

    @Override
    public int prestartAllCoreThreads() {
        return delegate.prestartAllCoreThreads();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }


}
