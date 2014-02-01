package de.wagentim.http;

import javax.persistence.Entity;

@Entity
public class MyHeader {

	private String name = null;
	private String value = null;
	
	
	public MyHeader(String name, String value) {

		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
