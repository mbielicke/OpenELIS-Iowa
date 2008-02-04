package org.openelis.domain;

import java.io.Serializable;

public class StandardNoteDO  implements Serializable{

	private static final long serialVersionUID = -1135423604704945867L;
	
	Integer id;
	String name;
	String description;
	Integer type;
	String text;
	
	public StandardNoteDO(){
		
	}

	public StandardNoteDO(Integer id, String name, String description, Integer type, String text){
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

}
