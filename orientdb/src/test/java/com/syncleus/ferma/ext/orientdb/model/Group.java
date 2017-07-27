package com.syncleus.ferma.ext.orientdb.model;

import java.util.List;

import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

@GraphElement
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
