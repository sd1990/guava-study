package org.songdan.guava.other;

/**
 * @author: Songdan
 * @create: 2018-08-23 19:28
 **/
public class Test {

    public static void main(String[] args) {
        int size = 2;
        double a = (size * 1.0) / Runtime.getRuntime().availableProcessors();
        System.out.println(a);
        System.out.println(Math.ceil(a));
        System.out.println(Double.valueOf(Math.ceil(a)).intValue());
    }

}
