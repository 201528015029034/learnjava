package atomicclasses7;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by silence on 2017/3/26.
 */
public class TestAtomicClasses {

    /**
     * 测试使用AtomicInteger
     * AtomicInteger：原子更新整形
     * 使用方式：
     * ①创建：AtomicInteger ai = new AtomicInteger(int initialValue) 参数initialValue代表初始值
     * ②使用：ai.getAndIncrement()
     * 主要API包含：
     *         get()
     *         getAndIncrement()
     *         getAndSet(int newValue)
     *         addAndGet(int delta)：原子增加delta
     *         compareAndSet(int expect, int update)
     *         lazySet(int newValue)：其他线程短时间内还是可以读到旧的值
     */
    @Test
    public void testAtomicInteger() {
        AtomicInteger ai = new AtomicInteger(1);
        System.out.println(ai.getAndIncrement());
        System.out.println(ai.get());
    }/* Output:
        1     //值已经加1，但函数返回原值
        2
    */

    /**
     * 测试使用AtomicIntegerArray
     * AtomicIntegerArray：原子更新整形数组里的元素
     * 使用方式：
     * ①创建：AtomicIntegerArray ai = new AtomicIntegerArray(int[] array) 参数array代表初始化数组
     * ②使用：ai.addAndGet(int i, int delta)
     * 主要API包含：
     *      get(int i)
     *      getAndSet(int i, int val)
     *      addAndGet(int i, int delta)
     *      compareAndSet(int i, int expect, int update)
     */
    @Test
    public void testAtomicIntegerArray() {
        int[] value = new int[]{1, 2};
        //AtomicIntegerArry会将value复制一份，封装
        AtomicIntegerArray ai = new AtomicIntegerArray(value);
        ai.addAndGet(0, 2);
        System.out.println(ai.get(0));
        System.out.println(value[0]);
    }/*
        3
        1
    */


    /**
     * 测试使用AtomicReference
     * AtomicReference：原子更新引用类型
     * 使用方式：
     * ①创建：对V类型的引用进行更新
     *         AtomicReference<V> ar = new AtomicReference<V>();
     * ②使用：ar.set(V v)
     *         ar.compareAndSet(V v1, V v2)
     * 主要API包含：
     *      get()：返回封装的对象
     *      set(V v)：设置封装的对象
     *      compareAndSet(V v1, V v2)
     *      getAndSet(V v1):
     */
    @Test
    public void testAtomicReference() {
        User user = new User("Tom");
        AtomicReference<User> ar = new AtomicReference<User>();
        ar.set(user);
        User updateUser = new User("Green");
        ar.compareAndSet(user, updateUser);
        System.out.println(ar.get().getName());
        System.out.println(user.getName());
    }/*
        Green
        Tom
    */
    static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 测试使用AtomicIntegerFieldUpdater
     * AtomicIntegerFieldUpdater：原子更新字段类型
     * 使用方式：
     * ①创建：对V类的e属性字段进行更新，这里要注意V类的e属性字段必要使用public volatile修饰符，还有
     *         因为是Integer字段的更新器，这里的e属性必须是Integer类型
     *         AtomicIntegerFieldUpdater<V> ai =
     *                    AtomicIntegerFieldUpdater.newUpdater(V.class, int e);
     * ②使用：ai.getAndIncrement(V v)
     * 主要API如下：
     *      get(V v)：获得v对象的更新属性字段值
     *      getAndIncrement(V v)：对v对象的更新字段属性加1
     *      getAndDecrement(V v)：对v对象的更新字段属性减1
     *      getAndSet(V v, int e)
     *      compareAndSet(V v, int expect, int update)
     *
     */
    @Test
    public void testAtomicIntegerFieldUpdater() {
        AtomicIntegerFieldUpdater<Student> ai =
                AtomicIntegerFieldUpdater.newUpdater(Student.class, "age");
        Student student = new Student("Tom", 10);
        System.out.println(ai.getAndIncrement(student));
        System.out.println(ai.get(student));
    }/* Output:
        10 //虽然字段值已经增加，但函数会返回原值
        11
    */

    static class Student {
        private String name;
        //更新类的字段必须使用public volatile修饰
        public volatile int age;
        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
