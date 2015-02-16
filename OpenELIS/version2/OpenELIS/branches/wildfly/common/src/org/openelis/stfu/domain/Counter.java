package org.openelis.stfu.domain;

import org.openelis.domain.DataObject;

public class Counter extends DataObject {

	private static final long serialVersionUID = 1L;

	public int less;
	public int greater;
	public int equals;
	
	public Counter() {
		
	}

	public int getLess() {
		return less;
	}

	public void setLess(int less) {
		this.less = less;
	}

	public int getGreater() {
		return greater;
	}

	public void setGreater(int greater) {
		this.greater = greater;
	}

	public int getEquals() {
		return equals;
	}

	public void setEquals(int equals) {
		this.equals = equals;
	}
	

}
