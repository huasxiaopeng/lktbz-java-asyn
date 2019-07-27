package com.asyn;

import java.util.concurrent.*;

/**
 * @Auther: lktbz
 * @Date: 2019/7/27 11:10
 * @Description:  Callable一般跟线程池一起用
 */
public class RunnableDemo {

//    public String call() throws Exception {
//        return "这就是一个简单的返回";
//    }
public static void main(String[] args) throws ExecutionException, InterruptedException {
    /**
     * cancel(boolean mayInterruptIfRunning)：试图取消执行的任务，参数为true时直接中断正在执行的任务，
     * 否则直到当前任务执行完成，成功取消后返回true，否则返回false
     * isCancel()：判断任务是否在正常执行完前被取消的，如果是则返回true
     * isDone()：判断任务是否已完成
     * get()：等待计算结果的返回，如果计算被取消了则抛出异常
     * get(long timeout,TimeUtil unit)：设定计算结果的返回时间，如果在规定时间内没有返回计算结果则抛出TimeOutException
     */
    //线程池写法
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Future<java.lang.String> submit = executorService.submit(new Callable<String>() {
        @Override
        public String call() throws Exception {
            System.out.println(" 任务开始执行。。");
                TimeUnit.SECONDS.sleep(5);
                return "任务完成。。。";
        }
    });
    System.out.println( "任务是否准备好。"+submit.isDone());
    String s = submit.get();
    System.out.println("获取到的返回值为"+s);
    executorService.shutdown();
    System.out.println("main 线程执行完毕。。。。");

    testw();

}
    /**
     * FutureTask的写法
     */
    public static  void testw() throws ExecutionException, InterruptedException {
        FutureTask<String>futureTask=new FutureTask<String>(()->{
            System.out.println("FutureTask 正在执行");
            TimeUnit.SECONDS.sleep(2);
            return "线程执行完毕";

        });
        Thread t =new Thread(futureTask);
        t.start();
        System.out.println( "线程是否准备好"+futureTask.isDone());
        String s = futureTask.get();
        System.out.println("线程得到的值为："+s);
        System.out.println("testw 线程执行完毕");
    }
}