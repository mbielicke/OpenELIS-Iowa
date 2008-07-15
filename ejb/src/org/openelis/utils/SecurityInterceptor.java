package org.openelis.utils;

import org.openelis.bean.OrganizationBean;
import org.openelis.security.local.SecurityLocal;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class SecurityInterceptor {
    
    @EJB
    SecurityLocal security;
    
    @AroundInvoke
    public Object applySecurity(InvocationContext invocation) throws Exception {
        SecurityElement se = ((OrganizationBean)invocation.getTarget()).securityMap.get(invocation.getMethod().getName());
        if(security.getSecurity("openelis").has(se.module, se.flag)){
            return invocation.proceed();
        }else{
            throw new Exception("You do not have sufficient permissions");
        }
     
    }

}
