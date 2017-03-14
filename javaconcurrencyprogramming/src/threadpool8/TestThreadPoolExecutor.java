package threadpool8;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by silence on 2017/3/14.
 */
public class TestThreadPoolExecutor {

    /**
     * 测试ThreadPoolExectuor，及CountDownLatch的使用
     * 这里需要注意ThreadPoolExecutor各个参数的含义：
     * corePoolSize:核心线程的数量，当线程池的线程的数量小于corePoolSize时，每来一个任务就会新建一个线程运行任务，
     *              直到线程数量达到corePoolSize。也可以通过线程池的prestartAllCoreThreads()预先创建并启动全部线程。
     * maximumPoolSize: 线程池支持的最大线程的数量，注意这里不是限制任务的数量，而是限制工作者线程Worker的数量。
     *                  如果任务队列满了，并且已创建的线程数小于maximumPoolSize,那线程池就会新建工作者线程，那也
     *                  就是说这个参数对于无界的任务队列没有效果。
     */
    @Test
    public void testThreadPoolExecutor() throws InterruptedException {
        CountDownLatch c = new CountDownLatch(2);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                5, 10, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<10000; i++) {
                    System.out.println(i);
                }
                c.countDown();
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for(int i=5; i<10; i++) {
                    System.out.println(i);
                }
                c.countDown();
            }
        });
        c.await();
        System.out.println("finished");
    }


}
