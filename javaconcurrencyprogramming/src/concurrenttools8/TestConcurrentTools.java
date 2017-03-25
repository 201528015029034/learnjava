package concurrenttools8;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by silence on 2017/3/25.
 */
public class TestConcurrentTools {

    /**
     * 测试使用CountDownLatch
     * CountDownLatch允许一个或多个线程等待其他线程完成操作
     * 主要API有countDown()和await()
     * 使用方式如下:
     * ①创建：CountDownLatch c = new CountDownLatch(int count); 参数count代表计数器数量
     *         注意这里count一经初始化，就不能重新初始化或者修改，除了使用countDown()
     * ②countDown()：c.countDown()使计数器count减1
     * ③await()：c.await()下面的程序会被阻塞，直到计数器count为0，才能继续向下运行
     *            言外之意就是我们需要几个计数器，就初始化count为多少，否则，多申请了count，
     *            导致count不能减少到0的话，那c.await()将会一直阻塞
     *            还有，count初始化必须大于等于0，只不过等于0的话，c.await()就不会阻塞
     *            引申：c.await(long time,TimeUnit unit)，指定阻塞时间
     */
    @Test
    public void testCountDownLatch() {
        CountDownLatch c = new CountDownLatch(3);
        List<Runnable> tasks = new ArrayList<Runnable>();
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("I am task0, and I need to run 5 seconds");
                try {
                    Thread.currentThread().sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("I am task0, and I finish");
                c.countDown();
            }
        };
        tasks.add(task1);
        for(int i=0; i<2; i++) {
            final int temp = i+1;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    System.out.println("I am task" + temp + ", and I need to run 2 seconds.");
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("I am task" + temp + ", and I finish.");
                    c.countDown();
                }
            };
            tasks.add(task);
        }
        for(int i=0; i<tasks.size(); i++) {
            Thread thread = new Thread(tasks.get(i));
            thread.start();
        }
        try {
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("I am main thread, and I need to wait other tasks.");
    }

    /**
     * 测试使用CyclicBarrier
     * CyclicBarrier让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达
     * 屏障时，屏障才会开门，所有被屏障拦截的线程才会继续运行
     * 主要API有：
     *      await()
     *      reset():重置parties，区别于CountDownLatch
     *      getNumberWaiting():获得CyclicBarrier阻塞的线程数量
     *      isBroken():了解阻塞的线程是否被中断
     * 使用方式：
     * ①创建：CyclicBarrier c = new CyclicBarrier(int parties); 参数parties代表屏障拦截的线程数量
     * ②await()：线程调用c.await()告诉CyclicBarrier，我已经到达了屏障，然后该线程被阻塞
     *            所有到达屏障的线程被阻塞，第parties个线程达到屏障，所有阻塞的线程都继续运行
     *            言外之意，我们必须申请初始化合适的parties，否则，可能导致所有线程均阻塞
     * ③引申：CyclicBarrier c = new CyclicBarrier(int parties, Runnable barrierAction)
     *         这个构造函数用于在第parties个线程到达屏障时，优先执行barrierAction，然后再执行其他
     *         阻塞线程
     */
    @Test
    public void testCyclicBarrier() {
        CyclicBarrier c = new CyclicBarrier(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("I am task1, and I arrive at barrier.");
                try {
                    c.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("I am task1, and I go through barrier.");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("I am task2, and I arrive at barrier.");
                try {
                    Thread.currentThread().sleep(2000);
                    c.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("I am task2, and I go through barrier.");
            }
        }).start();

        System.out.println("I am main, and I arrive at barrier.");
        try {
            c.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("I am main, and I go through barrier.");
    } /* Output:
        I am task1, and I arrive at barrier.
        I am main, and I arrive at barrier.
        I am task2, and I arrive at barrier.
        I am task2, and I go through barrier.
        I am task1, and I go through barrier.
        I am main, and I go through barrier.
    */

    /**
     * 测试使用new CyclicBarrier(int parties, Runnable barrierAction)
     */
    @Test
    public void testCyclicBarrierWithBarrierAction() {
        Runnable barrierAction = new Runnable() {
            @Override
            public void run() {
                System.out.println("I am barrierAction");
            }
        };
        CyclicBarrier c = new CyclicBarrier(2, barrierAction);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("I am task1, and I arrive at barrier");
                try {
                    c.await();
//                    Thread.currentThread().sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("I am task1, and I go through barrier");
            }
        }).start();

        System.out.println("I am main, and I arrive at barrier");
        try {
            c.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("I am main, and I go through barrier");
    }/* Output:
        I am main, and I arrive at barrier
        I am task1, and I arrive at barrier
        I am barrierAction   //barrierAction先执行，task和main不确定
        I am main, and I go through barrier
        I am task1, and I go through barrier
    */

    /**
     * 测试使用Semaphore
     * Semaphore(信号量)是用来控制同时访问特定资源的线程数量，它通过协调各个线程，
     * 以保证合理的使用公共资源
     * 主要API有：acquire()
     *            release()
     *            availablePermits()：返回此信号量当前可用的许可证数
     *            getQueueLength()：返回正在等待获取许可证的线程数
     *            hasQueuedThreads()：是否有线程正在等待获取许可证
     *            reducePermits(int reduction)：减少reduction个许可证
     *            getQueuedThreads()：返回所有等待获取许可证的线程集合
     * 使用方式：
     * ①创建：Semaphore s = new Semaphore(int permits); 参数permits代表许可证的数量
     * ②获取许可证：s.acquire()，当前可用许可证数量减1
     * ③释放许可证：s.release()，当前可用许可证数量加1
     */
    @Test
    public void testSemaphore() {
        final int THREAD_COUNT = 30;
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        //只有10个线程同时saving data
        Semaphore s = new Semaphore(10);
        for(int i=0; i<THREAD_COUNT; i++) {
            final int temp = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        s.acquire();
                        System.out.println("i = " + temp + "I am saving data");
                        Thread.currentThread().sleep(5000);
                        s.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            Thread.currentThread().sleep(18000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        System.out.println(executor.isTerminated());
    }

    /**
     * 测试使用Exchanger
     * Exchanger(交换者)用于进行线程间的数据交换，它提供一个同步点，
     * 在这个同步点，两个线程可以交换彼此的数据.注意是两个线程
     * 主要API有：exchange(V v)
     * 使用方式：
     * ①创建：Exchanger<V> exch = new Exchanger<V>();
     * ②交换数据：exch.exchange(V v); 先到达exch.exchange的线程会
     *             一直等待第二个线程到达exch.exchange，然后交换数据
     *             引申：exch.exchange(V v, long timeout, TimeUnit unit)
     *             设置最大等待时间，避免一直等待
     */
    @Test
    public void testExchanger() {
        Exchanger<String> exch = new Exchanger<String>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String A = "银行流水A";
                try {
                    A = exch.exchange(A);
                    System.out.println("I am task1, and I am dealing with " + A);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String B = "银行流水B";
                try {
                    B = exch.exchange(B);
                    System.out.println("I am task2, and I am dealing with " + B);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdown();
    }

}
