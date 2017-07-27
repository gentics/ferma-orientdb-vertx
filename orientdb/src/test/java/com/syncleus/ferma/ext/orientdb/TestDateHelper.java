package com.syncleus.ferma.ext.orientdb;

import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.ext.orientdb.model.Person;

public interface TestDateHelper {

	/**
	 * Update the person name and add 10 more friends. Update the names of all friends.
	 * 
	 * @param graph
	 * @param person
	 */
	default void manipulatePerson(FramedGraph graph, Person person) {
		person.setName("Changed " + System.currentTimeMillis());
		for (int i = 0; i < 10; i++) {
			Person friend = graph.addFramedVertex(Person.class);
			friend.setName("Friend " + i);
			person.addFriend(friend);
		}
		for (Person friend : person.getFriends()) {
			friend.setName("Changed Name " + System.currentTimeMillis());
		}
	}

	/**
	 * Add a friend to the provided person
	 * 
	 * @param graph
	 * @param person
	 */
	default void addFriend(FramedGraph graph, Person person) {
		Person friend = graph.addFramedVertex(Person.class);
		friend.setName("NewFriend");
		person.addFriend(friend);
	}

	/**
	 * Create a single person which has 10 friends (11 Vertices will be created)
	 * 
	 * @param graph
	 * @param name
	 * @return Created person
	 */
	default Person addPersonWithFriends(FramedGraph graph, String name) {
		Person p = graph.addFramedVertex(Person.class);
		p.setName(name);

		for (int i = 0; i < 10; i++) {
			Person friend = graph.addFramedVertex(Person.class);
			friend.setName("Friend " + i);
			p.addFriend(friend);
		}
		return p;
	}
}
