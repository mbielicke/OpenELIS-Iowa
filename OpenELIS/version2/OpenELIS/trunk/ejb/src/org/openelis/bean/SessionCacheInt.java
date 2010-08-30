package org.openelis.bean;

import javax.ejb.Local;

@Local
public interface SessionCacheInt {

    public void setAttribute(String key, Object value);

    public Object getAttribute(String key);

    public void removeAttribute(String key);

    public void destroySession();
}
