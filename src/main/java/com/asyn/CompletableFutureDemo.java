package com.asyn;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * @Auther: lktbz
 * @Date: 2019/7/27 12:30
 * @Description: jdk 1.8 异步编程
 *public static CompletableFuture<Void> runAsync(Runnable runnable)
 * public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
 * 前面两个是没有返回值的异步
 * public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
 * public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
 */
public class CompletableFutureDemo {
    /**
     *
     */
    @Test
    public  void test01(){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //jdk1.8，通过调用给定的Supplier，异步完成executor中的task
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("task started!");
            try {
                //模拟耗时操作
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "worker task is finished!";
        }, executor);
        //采用lambada的实现方式
        future.thenAccept(e -> {
            System.out.printf("%s ok", e);
            executor.shutdown();
        });
        System.out.println("main thread is finished！");
    }
    /**
     * runAsync与supplyAsync
     * 创建不可返回的异步线程
     */
    @Test
    public void test02() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        //创建异步线程
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("end....");
        });
        System.out.println("是否有返回值："+runAsync.get());

    }
    /**
     * 创建有返回指
     */
    @Test
    public void test03() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        //创建异步线程
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("end....");
            return "hhh";
        });
        System.out.println("是否有返回值："+future.get());

    }

    /**
     *  whenComplete(BiConsumer<? super T,? super Throwable> action)
     * public CompletableFuture<T> whenCompleteAsync
     * public CompletableFuture<T> exceptionally
     *
     * whenComplete 和 whenCompleteAsync 的区别：
     * whenComplete：是执行当前任务的线程执行继续执行 whenComplete 的任务。
     * whenCompleteAsync：是执行把 whenCompleteAsync 这个任务继续提交给线程池来进行执行
     *
     */
    @Test
    public  void test04() throws InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (new Random().nextInt() % 2 >= 0) {
                int i = 12 / 0;
            }
            System.out.println("run ...end");
        });
        future.whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void aVoid, Throwable throwable) {
                System.out.println("执行完成");
            }
        });
        future.exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                System.out.println("执行失败");
                return null;
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }
    /**
     * thenApply 方法
     * 当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化
     */
    @Test
    public void test05(){
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                long i = new Random().nextInt(100);
                System.out.println("线程处理的结果为" + i);
                return i;
            }
        }).thenApply(new Function<Long, Object>() {
            @Override
            public Object apply(Long aLong) {
                System.out.println("along的值为："+aLong);
                long res = aLong * 5;
                System.out.println("依赖上一步的结果。。。" + res);
                return res;
            }
        });
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * handle 方法
     * handle 是执行任务完成时对结果的处理。
     * handle 方法和 thenApply 方法处理方式基本一样。
     * 不同的是 handle 是在任务完成后再执行，还可以处理异常的任务。
     * thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法。
     */
    @Test
    public void test06() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> handle = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = 10 / 0;
                return new Random().nextInt(10);
            }
        }).handle(new BiFunction<Integer, Throwable, Object>() {
            @Override
            public Object apply(Integer integer, Throwable throwable) {
                int result = -1;
                if (throwable == null) {
                    return result = integer * 3;
                } else {

                    System.out.println(throwable.getMessage());
                }
                return result;
            }
        });
        Object o = handle.get();
        System.out.println(o);
    }
    /**
     * thenAccept 消费处理结果
     * 接收任务的处理结果，并消费处理，无返回结果。
     */
    @Test
    public void test07() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return new Random().nextInt(10);
            }
        }).thenAccept(integer -> {
            System.out.println(integer);
        });
        future.get();
    }
    /**
     * thenRun 方法
     * 跟 thenAccept 方法不一样的是，不关心任务的处理结果。
     * 只要上面的任务执行完成，就开始执行 thenAccept 。
     */
    @Test
    public void test08() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return new Random().nextInt(10);
            }
        }).thenRun(() -> {
            System.out.println("thenRun ...");
        });
        future.get();
    }
    /**
     * thenCombine 合并任务
     * thenCombine 会把 两个 CompletionStage 的任务都执行完成后，
     * 把两个任务的结果一块交给 thenCombine 来处理。
     */

    @Test
    public void test09() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "hello";
            }
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "hello";
            }
        });
        CompletableFuture<String> result = future1.thenCombine(future2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String t, String u) {
                return t+" "+u;
            }
        });
        System.out.println(result.get());
    }
    /**
     * thenAcceptBoth
     * 当两个CompletionStage都执行完成后，
     * 把结果一块交给thenAcceptBoth来进行消耗
     */
    @Test
    public void test10() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1="+t);
                return t;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2="+t);
                return t;
            }
        });
        f1.thenAcceptBoth(f2, new BiConsumer<Integer, Integer>() {
            @Override
            public void accept(Integer t, Integer u) {
                System.out.println("f1="+t+";f2="+u+";");
            }
        });
    }
    /**
     * applyToEither 方法
     * 两个CompletionStage，谁执行返回的结果快，
     * 我就用那个CompletionStage的结果进行下一步的转化操作。
     */
    @Test
    public void test11() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        });
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        });

        CompletableFuture<Integer> result = f1.applyToEither(f2, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                System.out.println(t);
                return t * 2;
            }
        });

        System.out.println(result.get());
    }
    /**
     * acceptEither 方法
     * 两个CompletionStage，谁执行返回的结果快，
     * 我就用那个CompletionStage的结果进行下一步的消耗操作
     */
    @Test
    public void test12() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1="+t);
                return t;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2="+t);
                return t;
            }
        });
        f1.acceptEither(f2, new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                System.out.println(t);
            }
        });
    }
    /**
     * runAfterEither 方法
     * 两个CompletionStage，任何一个完成了都会执行下一步的操作（Runnable）
     */
    @Test
    public  void test13(){
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1="+t);
                return t;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2="+t);
                return t;
            }
        });
        f1.runAfterEither(f2, new Runnable() {

            @Override
            public void run() {
                System.out.println("上面有一个已经完成了。");
            }
        });
    }
    /**
     *runAfterBoth
     * 两个CompletionStage，都完成了计算才会执行下一步的操作（Runnable）
     */
    public  void test14(){

            CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
                @Override
                public Integer get() {
                    int t = new Random().nextInt(3);
                    try {
                        TimeUnit.SECONDS.sleep(t);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("f1="+t);
                    return t;
                }
            });

            CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
                @Override
                public Integer get() {
                    int t = new Random().nextInt(3);
                    try {
                        TimeUnit.SECONDS.sleep(t);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("f2="+t);
                    return t;
                }
            });
            f1.runAfterBoth(f2, new Runnable() {

                @Override
                public void run() {
                    System.out.println("上面两个任务都执行完成了。");
                }
            });
    }
    /**
     * thenCompose 方法
     * thenCompose 方法允许你对两个 CompletionStage 进行流水线操作，
     * 第一个操作完成时，将其结果作为参数传递给第二个操作
     */
    @Test
    public void test15() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                System.out.println("t1="+t);
                return t;
            }
        }).thenCompose(new Function<Integer, CompletionStage<Integer>>() {
            @Override
            public CompletionStage<Integer> apply(Integer param) {
                return CompletableFuture.supplyAsync(new Supplier<Integer>() {
                    @Override
                    public Integer get() {
                        int t = param *2;
                        System.out.println("t2="+t);
                        return t;
                    }
                });
            }

        });
        System.out.println("thenCompose result : "+f.get());
    }
}
