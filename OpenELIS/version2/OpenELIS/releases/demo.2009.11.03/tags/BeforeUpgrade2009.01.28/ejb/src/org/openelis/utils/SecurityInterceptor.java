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
package org.openelis.utils;

import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.SecuritySection.SectionFlags;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.JBossCachingManager;

public class SecurityInterceptor {
        
    /*
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
    */
    
    public static void applySecurity(String user, String module, ModuleFlags flag) throws Exception {
        SecurityUtil security = (SecurityUtil)JBossCachingManager.getElement("openelis","security", user+"util"); 
        try {
            if(!security.has(module, flag)){
                throw new Exception("You do not have suffiecient permissions");
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void applySecurity(String user, String module, SectionFlags flag) throws Exception {
        SecurityUtil security = (SecurityUtil)JBossCachingManager.getElement("openelis","security", user+"util"); 
        try {
            if(!security.has(module, flag)){
                throw new Exception("You do not have sufficeient permissions");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void applySecurity(String user, SecurityModuleElement[] elems, boolean hasAll) throws Exception{
        SecurityUtil security = (SecurityUtil)JBossCachingManager.getElement("openelis","security", user+"util"); 
        try {
            for(int i = 0; i < elems.length; i++) {
                if(!security.has(elems[i].name, elems[i].flag) && hasAll){
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
    
    public static void applySecurity(String user, SecuritySectionElement[] elems, boolean hasAll) throws Exception{
        SecurityUtil security = (SecurityUtil)JBossCachingManager.getElement("openelis","security", user+"util"); 
        try {
            for(int i = 0; i < elems.length; i++) {
                if(!security.has(elems[i].name, elems[i].flag) && hasAll){
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
