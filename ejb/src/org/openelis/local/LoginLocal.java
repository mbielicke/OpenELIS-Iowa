package org.openelis.local;

import org.openelis.gwt.common.SecurityUtil;
import org.openelis.security.domain.SystemUserDO;

import javax.ejb.Local;

@Local
public interface LoginLocal {

    public Integer getSystemUserId();
    
    public SecurityUtil getSecurityUtil();
    
    public SystemUserDO getSystemUserDO();
}
