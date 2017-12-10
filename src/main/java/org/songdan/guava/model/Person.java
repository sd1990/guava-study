package org.songdan.guava.model;

/**
 * TODO completion javadoc.
 *
 * @author song dan
 * @since 10 十二月 2017
 */
public class Person {

	private String name;

	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (name != null ? !name.equals(person.name) : person.name != null) return false;
		return age != null ? age.equals(person.age) : person.age == null;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (age != null ? age.hashCode() : 0);
		return result;
	}


	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", age='" + age + '\'' +
				'}';
	}
}
