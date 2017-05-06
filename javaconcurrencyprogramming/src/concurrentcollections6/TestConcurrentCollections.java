package concurrentcollections6;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by silence on 2017/4/1.
 */
public class TestConcurrentCollections {

    /**
     * ConcurrentHashMap在jdk1.7中是基于分段锁实现的
     */
    @Test
    public void testConcurrentHashMap() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
        map.put("Tom", 1);
        map.put("Mary", 2);
        System.out.println(map.get("Tom"));
    }
}
