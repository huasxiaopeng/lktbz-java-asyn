package com.asyn;

import java.util.concurrent.*;

/**
 * @Auther: lktbz
 * @Date: 2019/7/27 12:09
 * @Description:多弄几个demo 异步求和
 */
public class FutureTaskDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        Task task=new Task();
        FutureTask<Integer> integerFutureTask = new FutureTask<>(task);
        service.submit(integerFutureTask);
        service.shutdown();
        Thread.sleep(1000);
        System.out.println("主线程在执行任务。。"+Thread.currentThread().getName());
        try {
            System.out.println("获取到的结果为："+integerFutureTask.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static class Task implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("子线程正在运行。。。"+Thread.currentThread().getName());
            Thread.sleep(3000);
            int sum=0;
            for (int i=0;i<100;i++){
                sum+=i;
            }
            System.out.println("子线程结束运行。。。"+Thread.currentThread().getName());
            return  sum;
        }
    }
}
