package org.openelis.utils;

import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.SecuritySection.SectionFlags;
import org.openelis.security.local.SecurityLocal;
import javax.naming.InitialContext;

public class SecurityInterceptor {
    
    private static SecurityLocal security; 
    
    static {
        try {
            InitialContext ctx = new InitialContext();
            security = (SecurityLocal)ctx.lookup("SecurityBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void applySecurity(String module, ModuleFlags flag) throws Exception {
        try {
            if(!security.has("openelis",module, flag)){
                throw new Exception("You do not have suffiecient permissions");
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void applySecurity(String module, SectionFlags flag) throws Exception {
        try {
            if(!security.has("openelis", module, flag)){
                throw new Exception("You do not have sufficeient permissions");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void applySecurity(SecurityModuleElement[] elems, boolean hasAll) throws Exception{
        try {
            for(int i = 0; i < elems.length; i++) {
                if(!security.has("openelis", elems[i].name, elems[i].flag) && hasAll){
                    throw new Exception("You do not have sufficient permissions");
                }else if(!hasAll){
                    return;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void applySecurity(SecuritySectionElement[] elems, boolean hasAll) throws Exception{
        try {
            for(int i = 0; i < elems.length; i++) {
                if(!security.has("openelis", elems[i].name, elems[i].flag) && hasAll){
                    throw new Exception("You do not have sufficient permissions");
                }else if(!hasAll){
                    return;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
