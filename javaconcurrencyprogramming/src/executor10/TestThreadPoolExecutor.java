package executor10;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by silence on 2017/3/14.
 */
public class TestThreadPoolExecutor {

    @Test
    public void testExecutors() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.execute(new Runnable(){

            @Override
            public void run() {
                for(int i=0; i<5; i++) {
                    System.out.println(i);
                }
            }
        });
        System.out.println("main() end");
    }
}
