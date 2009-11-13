/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.openelis.gwt.common.SecurityUtil;
import org.openelis.local.LoginLocal;
import org.openelis.persistence.JBossCachingManager;
import org.openelis.remote.LoginRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SecurityLocal;
import org.openelis.security.local.SystemUserUtilLocal;

@EJBs({
    @EJB(name="ejb/Security",beanInterface=SecurityLocal.class),
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class)
})
@Stateless
public class LoginBean implements LoginRemote, LoginLocal {

    private SecurityLocal security;
    private SystemUserUtilLocal sysUser;
    
    @EJB SessionManagerInt session;
    
    @Resource
    private SessionContext ctx;
    
    @PostConstruct
    private void init(){
        if(!JBossCachingManager.isAlive("openelis","security"))
            JBossCachingManager.init("openelis",LoginBean.class.getClassLoader().getResourceAsStream("META-INF/ehcache.xml"));
        security =  (SecurityLocal)ctx.lookup("ejb/Security");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
        
    }
        
    public SecurityUtil login() {
        SecurityUtil securityUtil = security.getSecurity("openelis");
        SystemUserDO sysUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        securityUtil.setSystemUserId(sysUserDO.getId());
        securityUtil.setSystemUserName(sysUserDO.getLoginName());
        securityUtil.setFirstName(sysUserDO.getFirstName());
        securityUtil.setLastName(sysUserDO.getLastName());
        securityUtil.setInitials(sysUserDO.getInitials());
        session.setAttribute("security",securityUtil);
        session.setAttribute("userdo",sysUserDO);
        JBossCachingManager.putElement("openelis","security", ctx.getCallerPrincipal().getName()+"util",securityUtil);
        JBossCachingManager.putElement("openelis","security", ctx.getCallerPrincipal().getName()+"userdo", sysUserDO);
       
        return securityUtil;
    }

    public void logout() {
       
    }

    public SecurityUtil getSecurityUtil() {
        SecurityUtil util = (SecurityUtil)JBossCachingManager.getElement("openelis","security", ctx.getCallerPrincipal().getName()+"util");
        if(util == null){
            return login();
        }
        return util;
    }

    public SystemUserDO getSystemUserDO() {
        SystemUserDO userDO = (SystemUserDO)JBossCachingManager.getElement("openelis","security", ctx.getCallerPrincipal().getName()+"userdo");
        if(userDO == null){
            login();
            userDO = (SystemUserDO)JBossCachingManager.getElement("openelis","security", ctx.getCallerPrincipal().getName()+"userdo");
        }
        return userDO;
    }

    public Integer getSystemUserId() {
        SecurityUtil util = (SecurityUtil)JBossCachingManager.getElement("openelis","security", ctx.getCallerPrincipal().getName()+"util");
        if(util == null)
            util = login();
        return util.getSystemUserId();
    }
 
    
}
