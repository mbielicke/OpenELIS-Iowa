package org.openelis.utils;

import org.openelis.gwt.common.SecurityModule.ModuleFlags;

public class SecurityModuleElement {
    
    public String name;
    public ModuleFlags flag;
    
    public SecurityModuleElement(String name, ModuleFlags flag){
        this.name = name;
        this.flag = flag;
    }
    
}
