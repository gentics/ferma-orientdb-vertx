package com.gentics.ferma.model;

import java.util.List;

import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

@GraphElement
public class Person extends AbstractInterceptingVertexFrame {

	public List<? extends Person> getFriends() {
		return out("HAS_FRIEND").has(Person.class).toListExplicit(Person.class);
	}

	public void addFriend(Person person) {
		linkOut(person, "HAS_FRIEND");
	}

	public void setName(String name) {
		setProperty("name", name);
	}

	public String getName() {
		return getProperty("name");
	}

}
