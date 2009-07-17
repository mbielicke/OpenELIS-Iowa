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
package org.openelis.managerOld;

import java.util.List;

import javax.naming.InitialContext;

import org.openelis.local.SampleProjectLocal;
import org.openelis.managerOld.SampleProjectsManager;
import org.openelis.managerOld.SampleProjectsManagerIOInt;

public class SampleProjectsManagerIOEJB implements SampleProjectsManagerIOInt {

public List fetch(Integer sampleId) {
        
        SampleProjectLocal local = getSampleProjectLocal();

        return local.getProjectsBySampleId(sampleId);
        
    }

    public void update(SampleProjectsManager sampleProjects) {
        
        SampleProjectLocal local = getSampleProjectLocal();

        local.update(sampleProjects);
        
    }
    
    private SampleProjectLocal getSampleProjectLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleProjectLocal)ctx.lookup("openelis/SampleProjectBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
