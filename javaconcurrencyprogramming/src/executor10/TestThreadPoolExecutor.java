package executor10;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by silence on 2017/3/14.
 */
public class TestThreadPoolExecutor {

    static List<Runnable> tasks = new ArrayList<Runnable>();

    static CountDownLatch c = new CountDownLatch(5);
    //初始化任务集
    static {
        for(int i=0; i<5; i++) {
            final int taskId = i;
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    for(int j=0; j<5; j++) {
                        System.out.println("Task " + taskId);
                        try {
                            Thread.currentThread().sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(System.currentTimeMillis());
//                    c.countDown();
                }
            });
        }
    }

    /**
     * 测试FixedThreadPool
     * FixedThreadPool重复使用固定的工作者线程，由参数nThreads指定，Executors.newFixedThreadPool(nThreads)。
     * 底层源码实现：
     * public static ExecutorService newFixedThreadPool(int nThreads) {
     *      return new ThreadPoolExecutor(nThreads, nThreads,
     *      0L, TimeUnit.MILLISECONDS,
     *      new LinkedBlockingQueue<Runnable>());
     *  }
     *  结合上一章的知识可以发现，参数对应为：
     *  corePoolSize : nThreads
     *  maximumPoolSize : nThreads 上述两个参数控制FixedThreadPool重复使用固定的工作者线程
     *  keepAliveTime : 0 工作者线程空闲后立即被收集
     *  runnableTaskQueue ： LinkedBlockingQueue 使用无界队列LinkedBlockingQueue作为任务队列，队列的容量为
     *                       Integer.MAX_VALUE。这样导致maximumPoolSize是一个无效参数，同时运行中的
     *                       FixedThreadPool不会拒绝任务。
     */
    @Test
    public void testFixedThreadPool() throws Exception{
        //nThreads指定工作者线程数量为5
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(Runnable task : tasks) {
            executor.execute(task);
        }
        c.await();
        System.out.println("main() end");
    }

    /**
     * 测试使用CachedThreadPool
     * CachedThreadPool会根据需要创建新的工作者线程
     * 底层源码实现：
     * public static ExecutorService newCachedThreadPool() {
     *      return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
     *      60L, TimeUnit.SECONDS,
     *      new SynchronousQueue<Runnable>());
     * }
     * 结合上一章的知识可以发现，参数对应为：
     *  corePoolSize : 0
     *  maximumPoolSize : Integer.MAX_VALUE 就是来一个任务创建一个工作者线程，如果没有工作者线程空闲的话
     *  keepAliveTime : 0 工作者线程空闲后60s被收集
     *  runnableTaskQueue ： SynchronousQueue 使用没有容量的SynchronousQueue作为任务队列，同时maximumPool
     *                       又是无界的，这样意味着，如果主线程提交任务的速度高于maximumPool中线程处理任务
     *                       的速度时，CachedThreadPool会不断的创建新线程。同时因为SynchronousQueue是一个
     *                       没有容量的阻塞队列，每个插入操作（SynchronousQueue.offer(Runnable task)）必须
     *                       等待另一个工作者的对应移除操作(SynchronousQueue.poll())，反之亦然。
     *
     */
    @Test
    public void testCachedThreadPool() throws Exception{
        ExecutorService executor = Executors.newCachedThreadPool();
        for (Runnable task : tasks) {
            executor.execute(task);
        }
        c.await();
        System.out.println("main() end");
    }

    /**
     * 测试使用SingleThreadExecutor
     * SingleThreadExecutor使用单个工作者线程
     * 底层源码实现：
     * public static ExecutorService newSingleThreadExecutor() {
     *      return new FinalizableDelegatedExecutorService
     *      (new ThreadPoolExecutor(1, 1,
     *      0L, TimeUnit.MILLISECONDS,
     *      new LinkedBlockingQueue<Runnable>()));
     * }
     * 参数对应：
     *  corePoolSize : 1
     *  maximumPoolSize : 1 只能有1个工作者线程
     *  keepAliveTime : 0 工作者线程空闲后60s被收集
     *  runnableTaskQueue ： LinkedBlockingQueue 使用无界队列LinkedBlockingQueue作为任务队列。
     */
    @Test
    public void testSingleThreadExecutor() throws Exception{
        ExecutorService executor = Executors.newSingleThreadExecutor();
        for (Runnable task : tasks) {
            executor.execute(task);
        }
        c.await();
        System.out.println("main() end");
    }/*
    因为只有一个工作者线程，所以是顺序打印
    */

    /**
     * 测试使用ScheduledThreadPoolExecutor
     * Executors.newScheduledThreadPool()的底层实现，返回一个ScheduledThreadPoolExecutor.
     * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
     *      return new ScheduledThreadPoolExecutor(corePoolSize);
     * }
     * ScheduledThreadPoolExecutor继承自ThreadPoolExecutor，主要用来在给定的延时之后运行任务，或者定期执行任务，
     * 只有线程池调用了shutdown()或shutdownNow()关闭线程池时才会停止执行任务
     * public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService {}
     * ScheduledThreadPoolExecutor的构造函数：
     * public ScheduledThreadPoolExecutor(int corePoolSize) {
     *      super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
     *      new DelayedWorkQueue());
     * }
     * 从上面ThreadPoolExecutor的构造函数的参数，可以看出ScheduledThreadPoolExecutor使用无界队列DelayQueue
     * 作为任务队列。
     * ScheduledThreadPoolExecutor的执行主要分为两大部分：
     *      1. 当调用scheduleAtFixedRate()方法或scheduleWithFixedDelay(Runnable ···)方法时，会将任务
     *         封装成一个实现了RunnableScheduledFuture接口的ScheduledFutureTask，并将它加入到DelayQueue
     *      2. 线程池中的工作者线程从DelayQueue中获取ScheduledFutureTask，然后执行任务。
     * scheduleAtFixedRate()方法的参数含义：
     *      intialDelay: 第一次执行任务前的延时
     *      period: 两次执行任务之间的间隔时间
     */
    @Test
    public void testScheduledThreadPoolExecutor() throws Exception{
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
            executor.scheduleAtFixedRate(tasks.get(0), 1000, 1000, TimeUnit.MILLISECONDS);

        Thread.currentThread().sleep(10000);
        executor.shutdown();
        System.out.println("main() end");
    }

    /**
     * 测试使用FutureTask和Callable
     * Future接口和FutureTask代表异步计算的结果，底层实现如下：
     * public class FutureTask<V> implements RunnableFuture<V> {}
     * public interface RunnableFuture<V> extends Runnable, Future<V> {}
     * 因为实现了Runnable接口，所以FutureTask可以交给Executor执行，也可以直接futureTask.run()运行。下面的
     * 例子就是直接futureTask.run()运行。当然，交给Executor执行也是要futureTask.run()
     * 此外，ExecutorService.submit()方法返回的就是FutureTask.
     * FutureTask的主要API有：
     *      1.run()
     *      2.get()
     *      3.cancel()
     * 根据futureTask.run()是否运行可以将FutureTask分为3种状态：未启动、已启动、已完成
     * 当FutureTask处于未启动或已启动状态时，执行FutureTask.get()会导致调用线程阻塞；处于已完成状态时，会
     * 立即返回结果。
     */
    @Test
    public void testFutureTask() {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Thread.currentThread().sleep(5000);
                        return "task finished.";
                    }
                }
        );
        task.run();
        try {
            System.out.println(task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }/*
    5s后打印 task finished
    */

    /**
     * 测试使用FutureTask和Runnable
     * 使用Runnable去初始化FutureTask时，必须提供一个结果变量，这个在任务运行结束后返回
     * 也可以使用Executors.callable(Runnable)去将一个Runnable封装成Callable，结果为null
     */
    @Test
    public void testFutureTaskWithRunnalbe(){
        Runnable runTask = new Runnable() {
            @Override
            public void run() {
                System.out.println("runTask begin");
                try {
                    Thread.currentThread().sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        String result = "runTask finish";
        FutureTask<String> task = new FutureTask<String>(runTask, result);
//        FutureTask task1 = new FutureTask(Executors.callable(runTask));

        task.run();
        try {
            System.out.println(task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
