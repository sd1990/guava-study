package org.songdan.guava.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Songdan
 * @create: 2018-10-29 16:54
 **/
public class RetryDemo {

    public static void main(String[] args) {
        retryOne();
        retryWithTimes();
        retryWithBackOff();
    }

    private static void retryOne() {
        Callable<Boolean> callable = () -> {
            return true; // do something useful here
        };

        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            retryer.call(callable);
        } catch (RetryException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void retryWithTimes() {
        AtomicInteger time = new AtomicInteger();
        Callable<Boolean> callable = () -> {
            System.out.println(time.incrementAndGet());
            return false; // do something useful here
        };

        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfResult(input -> !input)
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            retryer.call(callable);
        } catch (RetryException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void retryWithBackOff() {
        AtomicInteger time = new AtomicInteger();
        Callable<Boolean> callable = () -> {
            System.out.println(System.currentTimeMillis() + ":" + time.incrementAndGet());
            if (time.get() > 10) {
                return true;
            }
            return false; // do something useful here
        };
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(input -> !input)
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.exponentialWait(3, 5, TimeUnit.MINUTES))
                .withStopStrategy(StopStrategies.neverStop())
                .build();
        try {
            retryer.call(callable);
        } catch (RetryException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
