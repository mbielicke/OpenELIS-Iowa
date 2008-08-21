package org.openelis.remote;

import org.openelis.gwt.common.SecurityUtil;

import javax.ejb.Remote;

@Remote
public interface LoginRemote {

    public SecurityUtil login();
    
    public void logout();
    
}
