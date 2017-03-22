package threadpool9;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

/**
 * Created by silence on 2017/3/14.
 */
public class TestThreadPoolExecutor {

    /**
     * 测试ThreadPoolExectuor，及CountDownLatch的使用
     * 这里需要注意ThreadPoolExecutor各个参数的含义：
     * new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds, runnableTaskQueue, handler)
     * corePoolSize:核心线程的数量，当线程池的线程的数量小于corePoolSize时，每来一个任务就会新建一个线程运行任务，
     *              直到线程数量达到corePoolSize。也可以通过线程池的prestartAllCoreThreads()预先创建并启动全部线程。
     * maximumPoolSize: 线程池支持的最大线程的数量，注意这里不是限制任务的数量，而是限制工作者线程Worker的数量。
     *                  如果任务队列满了，并且已创建的线程数小于maximumPoolSize,那线程池就会新建工作者线程，那也
     *                  就是说这个参数对于无界的任务队列没有效果。
     * keepAliveTime: 线程池的工作线程空闲后，保证存活的时间。
     * runnableTaskQueue: 任务队列，用于保存等待执行的任务的阻塞队列（blockingQueue）,常用的有：
     *                    ArrayBlockingQueue: 基于数组，有界，FIFO
     *                    LinkedBlockingQueue: 基于链表，吞吐量高于ArrayBlockingQueue，FixedThreadPool用的这个，FIFO
     *                    SynchronousQueue: 不存储元素，每个插入操作要阻塞到有其他线程调用移除操作，吞吐量高于
     *                                      LinkedBlockingQueue，CachedThreadPool用的这个
     *                    PriorityBlockingQueue: 优先级，无界
     * handler: RejectedExecutionHandler(饱和策略)：当任务队列和线程池都满了，饱和状态，用于处理新提交任务的策略，有：
     *          AbortPolicy: 直接抛出异常，默认策略
     *          CallerRunsPolicy: 只用调用者所在线程来运行任务
     *          DiscardOldestPolicy: 丢弃队列里最近的一个任务，并执行当前任务
     *          DiscardPolicy: 不处理，不丢弃
     */
    @Test
    public void testThreadPoolExecutor() throws InterruptedException {
        CountDownLatch c = new CountDownLatch(2);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                5, 10, 60,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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

    @Test
    public void testFuture() {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                5, 10, 60,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        Future<Object> result = threadPool.submit(new Callable<Object>(

        ) {
            @Override
            public Object call() throws Exception {
                return new Integer(1);
            }
        });
        try {
            Object obj = result.get();
            System.out.println(((Integer)obj).intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

}
