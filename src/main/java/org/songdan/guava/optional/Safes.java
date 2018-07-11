package org.songdan.guava.optional;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @description: 安全操作类
 * @author: Songdan05
 * @create: 2018-07-11 15:30
 **/
public class Safes {

    public static <T> Optional<T> first(Collection<T> collection) {
        if (assertCollectionEmpty(collection)) {
            return Optional.absent();
        }
        return Optional.fromNullable(collection.iterator().next());
    }

    private static <T> boolean assertCollectionEmpty(Collection<T> collection) {
        return collection == null || collection.size() == 0;
    }

    public static <T> List<T> ofList(Collection<T> collection) {
        if (assertCollectionEmpty(collection)) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(collection);
    }

    public static <T> Set<T> ofSet(Collection<T> collection) {
        if (assertCollectionEmpty(collection)) {
            return Sets.newHashSet();
        }
        return Sets.newHashSet(collection);
    }

    public static void main(String[] args) {
        System.out.println(first(Lists.newArrayList(1)).or(new Supplier<Integer>() {
            @Override
            public Integer get() {
                throw new IllegalArgumentException("集合为空");
            }
        }));
        List<Integer> list = Lists.newArrayList();
        list.add(null);
        System.out.println(first(list).or(-1));
        first(null).or(new Supplier<Integer>() {
            @Override
            public Integer get() {
                throw new IllegalArgumentException("集合为空");
            }
        });
    }

}
