package com.asyn;

import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Auther: lktbz
 * @Date: 2019/7/27 14:55
 * @Description: guava 异步编程demo 例子
 */
public class GuavaAsynDemo {
     private static ListeningExecutorService service = MoreExecutors.newDirectExecutorService();
    /**
     * 不带返回值方式
     */
    @Test
    public  void test(){
        //构建guava 自己的线程池
//        ListeningExecutorService service = MoreExecutors.newDirectExecutorService();
        //这个包装jdk 自带的线程池
        ListeningScheduledExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(2));
        ListenableFuture<?> hhh = service.submit(() -> {
            System.out.println("hhh");
        });
        hhh.addListener(()->{
            System.out.println("监听到动静。。。");
        },service);
    }
    /**
     * 带返回值方式,失败方式
     */
    @Test
    public void test02(){
        ListenableFuture<String> submit = service.submit(() -> {
            System.out.println(Thread.currentThread().getName());

            System.out.println("有返回值测试。。。。。。。");

            throw  new Exception("运行失败");
        });
        Futures.addCallback(submit, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String s) {
                System.out.println("异步运行成功。。。"+Thread.currentThread().getName());
                System.out.println("get result-->"+s);
            }
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
                System.out.println("运行失败。。。。");
                throwable.printStackTrace();
            }
        },service);
    }
    /**
     * 带返回值方式,成功方式
     */
    @Test
    public void test03(){
        ListenableFuture<String> submit = service.submit(() -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println("有返回值测试。。。。。。。");
            return "hhhs";
        });
        Futures.addCallback(submit, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String s) {
                System.out.println("异步运行成功。。。"+Thread.currentThread().getName());
                System.out.println("get result-->"+s);
            }
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
                System.out.println("运行失败。。。。");
                throwable.printStackTrace();
            }
        },service);
    }
    /**ListenableFuture
     *
     *这个方法用来把多个ListenableFuture组合成一个。
     * 当其中一个Future失败或者取消的时候，将会进入失败或者取消。
     */
    @Test
    public void test04(){
        ListenableFuture<String> future1 = service.submit(() -> "Hello");
        ListenableFuture<Integer> future2 = service.submit(() -> 2);
        ListenableFuture<List<Object>> future = Futures.allAsList(future1, future2);
        Futures.addCallback(future, new FutureCallback<List<Object>>() {
            @Override
            public void onSuccess(@Nullable List<Object> objects) {
                System.out.println(objects);
            }
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }
        },service);
    }
    /**
     * 转换返回值(同步)

    @Test
    public  void test05() throws ExecutionException, InterruptedException {
        ListenableFuture<String> submit = service.submit(() -> "hello");
        ListenableFuture<Integer> transform = Futures.transform(submit, String::length, service);
        System.out.println(transform.get());
    }     */
    /**
     * 转换返回值(异步)

    @Test
    public  void test06() throws ExecutionException, InterruptedException {
        ListenableFuture<String> submit = service.submit(() -> "hello");
        ListenableFuture<Integer> transform = Futures.transformAsync(submit,
                input->Futures.immediateFuture(input.length()),service);
        System.out.println(transform.get());
    }     */
    /**
     * Futures.successfulAsList
     * 和allAsList相似，唯一差别是对于失败或取消的Future返回值用null代替。不会进入失败或者取消流程。
     * immediateFuture和immediateCancelledFuture
     * 这个两个类主要就是包装同步结果返回一个Future的。
     * 其实内部结果已经确定了。
     * 这两个的isDone的返回值不同。
     * immediateFuture是True而immediateCancelledFuture是false
     */
    /**
     * 感觉这是个异步执行，同步获取的方法，只是用起来很方便。
     * 如果我们在一个线程中需要等待另外一个线程的异步任务。
     * 那么我们就可以去设置一个SettableFuture
     * 但是在进行get获取的时候，是同步阻塞的。
     */
    @Test
    public void test07() throws ExecutionException, InterruptedException {
        SettableFuture<Object> future = SettableFuture.create();
        service.submit(()->{
           future.set("hello");
        });
        System.out.println("是否完成"+future.isDone());
        System.out.println(future.get());
    }

/** JdkFutureAdapters
 * 一个适配器的类，把JDK的Future转化成ListenableFuture
 */
@Test
 public void test08() throws ExecutionException, InterruptedException {
     ExecutorService executorService = Executors.newCachedThreadPool();
     Future<String> stringFuture = executorService.submit(() -> "hello,world");
     ListenableFuture<String> listenableFuture = JdkFutureAdapters.listenInPoolThread(stringFuture);
    String s = listenableFuture.get();
    System.out.println(s);
}

}
