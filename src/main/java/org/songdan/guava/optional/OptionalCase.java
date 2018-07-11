package org.songdan.guava.optional;

import com.google.common.base.Optional;

/**
 * @description: Optional case
 * @author: Songdan05
 * @create: 2018-07-11 14:12
 **/
public class OptionalCase {

    public static void main(String[] args) {
        System.out.println(stringCase(null));
        System.out.println(stringCase(""));
        System.out.println(stringCase("hello world"));
    }

    private static String stringCase(String str) {
        return Optional.fromNullable(str).or("");
    }

}
