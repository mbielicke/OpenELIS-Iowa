package org.openelis.domain;

import org.openelis.gwt.common.RPC;

public class AnalysisVO implements RPC {

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected String test;
	protected String method;
	protected Integer status;
	
	public AnalysisVO() {
		
	}
	
	public AnalysisVO(Integer id, String test, String method, Integer status) {
		this.id = id;
		this.test = test;
		this.method = method;
		this.status = status;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	
	

}
