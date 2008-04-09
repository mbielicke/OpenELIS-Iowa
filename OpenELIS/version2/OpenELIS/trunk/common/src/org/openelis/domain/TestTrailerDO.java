package org.openelis.domain;

import java.io.Serializable;

public class TestTrailerDO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected String name;
	protected String description;
	protected String text;
	
	public TestTrailerDO(){
		
	}

	public TestTrailerDO(Integer id, String name, String description, String text){
		this.id = id;
		this.name = name;
		this.description = description;
		this.text = text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
