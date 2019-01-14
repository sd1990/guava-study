package org.songdan.guava.limiter;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.*;

/**
 * Guava限流器
 *
 * @author: Songdan
 * @create: 2018-10-17 15:16
 **/
public class GuavaLimiter {

    private RateLimiter rateLimiter;

    private int limit;

    public GuavaLimiter(int limit) {
        this.limit = limit;
        //可以支持突发1s的流量
        rateLimiter = RateLimiter.create(limit);
    }

    public GuavaLimiter(int limit,long warmPeriod,TimeUnit timeUnit) {
        this.limit = limit;
        rateLimiter = RateLimiter.create(limit,warmPeriod, timeUnit);
    }

    public static void testThreshold(GuavaLimiter guavaLimiter) throws InterruptedException {
        for (int i = 0; i < guavaLimiter.limit; i++) {
            System.out.println(guavaLimiter.rateLimiter.acquire());
        }
    }

    public static void testWarmUp(GuavaLimiter guavaLimiter) throws InterruptedException {
        for (int i = 0; i < guavaLimiter.limit-1; i++) {
            System.out.println(guavaLimiter.rateLimiter.acquire());
        }
    }

    public static void testPreCost(GuavaLimiter guavaLimiter) throws InterruptedException {
        System.out.println(guavaLimiter.rateLimiter.acquire(guavaLimiter.limit*100));
        TimeUnit.SECONDS.sleep(1);
        System.out.println(guavaLimiter.rateLimiter.acquire());
    }

    public static void testBursty(GuavaLimiter guavaLimiter) throws InterruptedException {
        System.out.println(guavaLimiter.rateLimiter.acquire(guavaLimiter.limit));
        //unuse 1 second,store limit permit
        TimeUnit.SECONDS.sleep(2);
        // permit n*2 request
        System.out.println(guavaLimiter.rateLimiter.acquire(guavaLimiter.limit*2));
        TimeUnit.SECONDS.sleep(1);
        System.out.println(guavaLimiter.rateLimiter.acquire(guavaLimiter.limit));
    }

    public static void testConcurrent(GuavaLimiter guavaLimiter) {

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < guavaLimiter.limit * 2; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    double cost = guavaLimiter.rateLimiter.acquire();
                    System.out.println(System.currentTimeMillis()+">>cost:"+ cost);
                }
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {

        }
    }



    public static void main(String[] args) throws InterruptedException {
//        testThreshold(new GuavaLimiter(1));
//        testConcurrent(new GuavaLimiter(2));
//        testPreCost(new GuavaLimiter(1));
        testBursty(new GuavaLimiter(1));
//        testConcurrent(new GuavaLimiter(2,1,TimeUnit.SECONDS));
    }
}
