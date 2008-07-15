package org.openelis.utils;

import org.openelis.gwt.common.SecurityModule.ModuleFlags;

public class SecurityElement {
    
    public String module;
    public ModuleFlags flag;
    
    public SecurityElement(String module, ModuleFlags flag){
        this.module = module;
        this.flag = flag;
    }

}
