package de.jotschi.ferma.model;

import java.util.List;

import de.jotschi.ferma.orientdb.AbstractInterceptingVertexFrame;

public class Job extends AbstractInterceptingVertexFrame {

	public List<? extends Person> getEmployee() {
		return out("HAS_EMPLOYEE").toListExplicit(Person.class);
	}

	public void addEmployee(Person person) {
		addFramedEdge("HAS_EMPLOYEE", person);
	}
}
