package org.openelis.bean;

import org.openelis.gwt.common.SecurityUtil;
import org.openelis.local.LoginLocal;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.LoginRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SecurityLocal;
import org.openelis.security.local.SystemUserUtilLocal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@EJBs({
    @EJB(name="ejb/Security",beanInterface=SecurityLocal.class),
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class)
})
@Stateless
public class LoginBean implements LoginRemote, LoginLocal {

    private SecurityLocal security;
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    @PostConstruct
    private void init(){
        if(!CachingManager.isAlive("security"))
            CachingManager.init(LoginBean.class.getClassLoader().getResourceAsStream("META-INF/ehcache.xml"));
        security =  (SecurityLocal)ctx.lookup("ejb/Security");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }
    
    public SecurityUtil login() {
        SecurityUtil securityUtil = security.getSecurity("openelis");
        SystemUserDO sysUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        securityUtil.setSystemUserId(sysUserDO.getId());
        CachingManager.putElement("security", ctx.getCallerPrincipal().getName()+"util",securityUtil);
        CachingManager.putElement("securtiy", ctx.getCallerPrincipal().getName()+"userdo", sysUserDO);
        return securityUtil;
    }

    public void logout() {
       
    }

    public SecurityUtil getSecurityUtil() {
        SecurityUtil util = (SecurityUtil)CachingManager.getElement("security", ctx.getCallerPrincipal().getName()+"util");
        if(util == null){
            return login();
        }
        return util;
    }

    public SystemUserDO getSystemUserDO() {
        SystemUserDO userDO = (SystemUserDO)CachingManager.getElement("security", ctx.getCallerPrincipal().getName()+"userdo");
        if(userDO == null){
            login();
            userDO = (SystemUserDO)CachingManager.getElement("security", ctx.getCallerPrincipal().getName()+"userdo");
        }
        return userDO;
    }

    public Integer getSystemUserId() {
        SecurityUtil util = (SecurityUtil)CachingManager.getElement("security", ctx.getCallerPrincipal().getName()+"util");
        if(util == null)
            util = login();
        return util.getSystemUserId();
    }
 
    
}
