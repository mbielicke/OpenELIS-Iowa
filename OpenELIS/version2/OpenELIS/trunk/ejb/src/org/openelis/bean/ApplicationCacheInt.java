package org.openelis.bean;

import javax.ejb.Local;

@Local
public interface ApplicationCacheInt {

    public Object getAttribute(String appId, Object key);

    public void setAttribute(String appId, Object key, Object value);

    public void removeAttribute(String appId, Object key);

    public void removeApplication(String appId);

}
