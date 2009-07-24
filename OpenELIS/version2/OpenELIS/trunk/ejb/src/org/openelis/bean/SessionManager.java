package org.openelis.bean;


import java.util.HashMap;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;

import org.jboss.annotation.ejb.Service;

@Service
@Local(SessionManagerInt.class)
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
		if(sessions.containsKey(ctx.getCallerPrincipal().getName())){
			return sessions.get(ctx.getCallerPrincipal().getName());
		}
		return createSession();
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
