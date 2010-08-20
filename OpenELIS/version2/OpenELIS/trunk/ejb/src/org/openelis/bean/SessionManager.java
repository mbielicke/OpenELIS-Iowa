package org.openelis.bean;


import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;

import org.jboss.ejb3.annotation.Service;

@Service(objectName="jboss:custom=SessionManager")
public class SessionManager implements SessionManagerInt {
	
	HashMap<String,HashMap<String,Object>> sessions = new HashMap<String,HashMap<String,Object>>();
	
    @Resource
    private SessionContext ctx;

	private HashMap<String,Object> createSession() {
		HashMap<String,Object> sess = new HashMap<String,Object>();
		sessions.put(ctx.getCallerPrincipal().getName(), sess);
		return sess;
	}

	public void destroySession() {
		sessions.remove(ctx.getCallerPrincipal().getName());
	}

	private HashMap<String,Object> getSession() {
		HashMap<String,Object> session = sessions.get(ctx.getCallerPrincipal().getName());
		if(session == null)
			return createSession();
		return session;
	}

	public Object getAttribute(String key) {
		return getSession().get(key);
	}

	public void removeAttribute(String key) {
		getSession().remove(key);
	}

	public void setAttribute(String key, Object value) {
		getSession().put(key, value);
	}

}
