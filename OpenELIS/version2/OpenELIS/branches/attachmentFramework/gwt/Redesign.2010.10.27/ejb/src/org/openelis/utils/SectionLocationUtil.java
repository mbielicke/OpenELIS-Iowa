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

import javax.naming.InitialContext;

import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.local.SectionLocal;

public class SectionLocationUtil {
    public static String getLocation (Integer sectionId) {
        String name;        
        SectionViewDO data;
        
        if (sectionId == null)
            return "";
                
        try {
            data = sectionLocal().fetchById(sectionId);
            
            name = DataBaseUtil.trim(data.getName());
            if (name == null)
                return name;
            
            if (name.endsWith("-ank")) 
                name = "Ankeny";
            else if (name.endsWith("-ic")) 
                name = "Iowa City";
            else if (name.endsWith("-lk")) 
                name = "Lakeside";
                        
            return name;
        } catch (Exception e) {            
            e.printStackTrace();    
            return null;
        }   
    }
    
    private static SectionLocal sectionLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SectionLocal)ctx.lookup("openelis/SectionBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }           
    }
}
