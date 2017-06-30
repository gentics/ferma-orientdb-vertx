package com.gentics.ferma.model;

import java.util.List;

import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.ext.AbstractInterceptingVertexFrame;

@GraphElement
public class Job extends AbstractInterceptingVertexFrame implements IJob {

	public List<? extends Person> getEmployee() {
		return out("HAS_EMPLOYEE").toListExplicit(Person.class);
	}

	public void addEmployee(Person person) {
		addFramedEdge("HAS_EMPLOYEE", person);
	}

	@Override
	public void setName(String name) {
		setProperty("name", name);
	}

	@Override
	public String getName() {
		return getProperty("name");
	}

}
