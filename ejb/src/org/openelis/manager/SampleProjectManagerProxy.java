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

import org.openelis.bean.SampleProjectBean;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class SampleProjectManagerProxy {
    public SampleProjectManager fetchBySampleId(Integer sampleId) throws Exception {
        ArrayList<SampleProjectViewDO> list;
        SampleProjectManager spm;
        
        list = EJBFactory.getSampleProject().fetchBySampleId(sampleId);
        
        spm = SampleProjectManager.getInstance();
        spm.setProjects(list);
        spm.setSampleId(sampleId);
        
        return spm;
    }
    
    public SampleProjectManager add(SampleProjectManager man) throws Exception {
        SampleProjectViewDO data;
        SampleProjectBean l;
        
        l = EJBFactory.getSampleProject();
        for(int i=0; i<man.count(); i++){
            data = man.getProjectAt(i);
            data.setSampleId(man.getSampleId());
            
            l.add(data);
        }
        
        return man;
    }
    
    public SampleProjectManager update(SampleProjectManager man) throws Exception {
        int i;
        SampleProjectViewDO data;
        SampleProjectBean l;
        
        l = EJBFactory.getSampleProject();
        for(i=0; i<man.deleteCount(); i++){
            l.delete(man.getDeletedAt(i));
        }
        
        for(i=0; i<man.count(); i++){
            data = man.getProjectAt(i);
            
            if(data.getId() == null){
                data.setSampleId(man.getSampleId());
                l.add(data);
            }else
                l.update(data);
        }

        return man;
    }
    
    public void validate(SampleProjectManager man, ValidationErrorsList errorsList) throws Exception {       
    }
}
