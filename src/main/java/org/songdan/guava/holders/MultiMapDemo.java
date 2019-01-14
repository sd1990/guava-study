package org.songdan.guava.holders;

import java.util.Collections;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.songdan.guava.model.Person;

/**
 * MultiMap可用于替代类似于计算word出现次数这样的场景
 *
 * @author song dan
 * @since 10 十二月 2017
 */
public class MultiMapDemo {

	/**
	 * size 实际上获取的是所有entry的数量
	 */
	public static void size(){
		Multimap<String,String> multiMap= HashMultimap.create();
		multiMap.put("a", "1");
		multiMap.put("a", "2");
		multiMap.put("a", "3");
		multiMap.put("b", "4");
		multiMap.put("b", "4");
		System.out.println(multiMap.get("a"));
		System.out.println(multiMap.size() == 4);
		Multimap<String,String> another= ArrayListMultimap.create();
		another.put("a", "1");
		another.put("a", "2");
		another.put("a", "3");
		another.put("b", "4");
		another.put("b", "4");
		System.out.println(another.size() == 5);
	}

	public static void add() {
		Multimap<String,String> multiMap= HashMultimap.create();
		multiMap.put("a", "1");
		System.out.println(multiMap.get("a"));
		System.out.println(multiMap.get("b"));
	}

	public static void min() {
		Multimap<String,String> multiMap= HashMultimap.create();
		multiMap.put("a", "1");
		multiMap.put("a", "2");
		multiMap.put("a", "3");
		multiMap.put("b", "4");
		multiMap.put("b", "4");
		System.out.println(Collections.min(multiMap.values()));
	}

	private static Multimap<String, String> getHashMultimap() {
		Multimap<String,String> multiMap= HashMultimap.create();
		multiMap.put("a", "1");
		multiMap.put("a", "2");
		multiMap.put("a", "3");
		multiMap.put("b", "4");
		return multiMap;
	}

	private static void generate() {
		Person first = new Person();
		first.setName("zhangsan");
		first.setAge("23");
		Person like = new Person();
		like.setName("zhangsan");
		like.setAge("25");

//		ImmutableListMultimap<String, Person> multimap = Multimaps.index(Lists.newArrayList(first, like), person -> {
//			return person.getName();
//		});
//		System.out.println(multimap.get("zhangsan"));
	}

	public static void main(String[] args) {
//		size();
//		add();
//		min();
		generate();
	}

}
