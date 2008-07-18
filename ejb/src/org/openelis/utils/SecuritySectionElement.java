package org.openelis.utils;

import org.openelis.gwt.common.SecuritySection.SectionFlags;

public class SecuritySectionElement {
    
    public String name;
    public SectionFlags flag;
    
    public SecuritySectionElement(String name, SectionFlags flag){
        this.name = name;
        this.flag = flag;
    }

}
