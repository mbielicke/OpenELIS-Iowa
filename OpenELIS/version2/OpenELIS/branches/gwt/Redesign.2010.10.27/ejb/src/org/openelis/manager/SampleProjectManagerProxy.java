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
package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SampleProjectLocal;

public class SampleProjectManagerProxy {
    public SampleProjectManager fetchBySampleId(Integer sampleId) throws Exception {
        ArrayList<SampleProjectViewDO> projects;
        SampleProjectManager spm;
        
        projects = local().fetchBySampleId(sampleId);
        
        spm = SampleProjectManager.getInstance();
        spm.setProjects(projects);
        spm.setSampleId(sampleId);
        
        return spm;
    }
    
    public SampleProjectManager add(SampleProjectManager man) throws Exception {
        SampleProjectViewDO projectDO;
        SampleProjectLocal l;
        
        l = local();
        for(int i=0; i<man.count(); i++){
            projectDO = man.getProjectAt(i);
            projectDO.setSampleId(man.getSampleId());
            
            l.add(projectDO);
        }
        
        return man;
    }
    
    public SampleProjectManager update(SampleProjectManager man) throws Exception {
        SampleProjectViewDO projectDO;
        SampleProjectLocal l;
        
        l = local();
        for(int j=0; j<man.deleteCount(); j++){
            l.delete(man.getDeletedAt(j));
        }
        
        for(int i=0; i<man.count(); i++){
            projectDO = man.getProjectAt(i);
            
            if(projectDO.getId() == null){
                projectDO.setSampleId(man.getSampleId());
                l.add(projectDO);
            }else
                l.update(projectDO);
        }

        return man;
    }
    
    public void validate(SampleProjectManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private SampleProjectLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleProjectLocal)ctx.lookup("openelis/SampleProjectBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}