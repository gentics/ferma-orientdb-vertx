package com.gentics.ferma.ext.orientdb.vertx;

import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;
import org.junit.Before;

import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.ext.orientdb.model.Person;

import io.vertx.core.Vertx;

public class AbstractOrientDBVertexTest {

	protected OrientGraphFactory graphFactory;
	protected OrientDBTxVertexFactory graph;

	@Before
	public void setupDB() {
		graphFactory = new OrientGraphFactory("memory:tinkerpop").setupPool(4, 10);
		graph = new OrientDBTxVertexFactory(graphFactory, Vertx.vertx(), "com.syncleus.ferma.ext.orientdb.model");
	}

	/**
	 * Update the person name and add 10 more friends. Update the names of all friends.
	 * 
	 * @param graph
	 * @param person
	 */
	public void manipulatePerson(FramedGraph graph, Person person) {
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
	public void addFriend(FramedGraph graph, Person person) {
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
	public Person addPersonWithFriends(FramedGraph graph, String name) {
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
