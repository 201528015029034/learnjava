package apis;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;

/**
 * Created by test on 2017/3/13.
 */
public class TestAPI {


    //测试Character的isLetterOrDigit和toLowerCase
    @Test
    public void testCharacter() {
        //LeetCode Num 10 atoi  简化判断
        System.out.println(Character.isLetterOrDigit('1'));
        System.out.println(Character.toLowerCase('9'));
    }

    @Test
    public void testLinkedListStack() {
        LinkedList<Integer> stack = new LinkedList<Integer>();
        stack.push(1);
        stack.push(2);
        System.out.println(stack.pop());
    }
}
