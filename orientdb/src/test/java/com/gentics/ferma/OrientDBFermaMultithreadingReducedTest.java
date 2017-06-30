package com.gentics.ferma;

import static com.gentics.ferma.util.TestUtils.runAndWait;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.gentics.ferma.model.Person;
import com.syncleus.ferma.tx.Tx;

public class OrientDBFermaMultithreadingReducedTest extends AbstractOrientDBTest {

	private Person p;

	@Before
	public void setup() {
		setupData();
	}

	private void setupData() {
		try (Tx tx = graph.tx()) {
			String name = "SomeName";
			p = addPersonWithFriends(tx.getGraph(), name);
			// tx.getGraph().commit();
			tx.success();
			runAndWait(() -> {
				try (Tx tx2 = graph.tx()) {
					readPerson(p);
					manipulatePerson(tx2.getGraph(), p);
				}
			});
		}

		runAndWait(() -> {
			try (Tx tx2 = graph.tx()) {
				readPerson(p);
				manipulatePerson(tx2.getGraph(), p);
			}
		});

	}

	@Test
	public void testMultithreading() {

		// fg.commit();
		runAndWait(() -> {
			Person reloaded;
			try (Tx tx = graph.tx()) {
				manipulatePerson(tx.getGraph(), p);
				String name = "newName";
				p.setName(name);
				reloaded = tx.getGraph().v().has(Person.class).has("name", name).nextOrDefaultExplicit(Person.class, null);
				System.out.println(reloaded.getName());
				assertNotNull(reloaded);
				manipulatePerson(tx.getGraph(), reloaded);
				tx.success();
			}
			runAndWait(() -> {
				try (Tx tx2 = graph.tx()) {
					readPerson(reloaded);
				}
			});
		});
	}

	private void readPerson(Person person) {
		person.getName();
		for (Person p : person.getFriends()) {
			p.getName();
			for (Person p2 : person.getFriends()) {
				p2.getName();
				for (Person p3 : p2.getFriends()) {
					p3.getName();
				}
			}
		}
	}

}
