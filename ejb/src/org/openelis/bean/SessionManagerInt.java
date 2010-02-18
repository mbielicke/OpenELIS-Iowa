package org.openelis.bean;

import javax.ejb.Local;

@Local
public interface SessionManagerInt {
	
	public void destroySession();
	
	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);
	 
	public void removeAttribute(String key);
}
