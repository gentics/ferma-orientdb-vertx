package de.jotschi.ferma.model;

import java.util.List;

import de.jotschi.ferma.AbstractInterceptingVertexFrame;

public class Group extends AbstractInterceptingVertexFrame {

	public List<? extends Person> getMembers() {
		return out("HAS_MEMBER").has(Person.class).toListExplicit(Person.class);
	}

	public void addMember(Person person) {
		linkOut(person, "HAS_MEMBER");
	}

	public void setName(String name) {
		setProperty("name", name);
	}

	public String getName() {
		return getProperty("name");
	}

}
