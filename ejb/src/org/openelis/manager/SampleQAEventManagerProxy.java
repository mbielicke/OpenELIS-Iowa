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

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.utils.EJBFactory;

public class SampleQAEventManagerProxy {
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        ArrayList<SampleQaEventViewDO> list;
        SampleQaEventManager sqm;
        
        list = EJBFactory.getSampleQAEvent().fetchBySampleId(sampleId);
        
        sqm = SampleQaEventManager.getInstance();
        sqm.setSampleQAEvents(list);
        sqm.setSampleId(sampleId);
        
        return sqm;
    }
    
    public SampleQaEventManager add(SampleQaEventManager man) throws Exception {
        SampleQaEventViewDO data;
        SampleQAEventLocal l;
        
        l = EJBFactory.getSampleQAEvent();
        for(int i=0; i<man.count(); i++){
            data = man.getSampleQAAt(i);
            data.setSampleId(man.getSampleId());
            
            l.add(data);
        }
        
        return man;
    }
    
    public SampleQaEventManager update(SampleQaEventManager man) throws Exception {
        int i;
        SampleQaEventViewDO data;
        SampleQAEventLocal l;
        
        l = EJBFactory.getSampleQAEvent();
        for(i=0; i<man.deleteCount(); i++)
            l.delete(man.getDeletedAt(i));
        
        for(i=0; i<man.count(); i++){
            data = man.getSampleQAAt(i);
            
            if(data.getId() == null){
                data.setSampleId(man.getSampleId());
                l.add(data);
            }else
                l.update(data);
        }

        return man;
    }
    
    public void validate(SampleQaEventManager man, ValidationErrorsList errorsList) throws Exception {        
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception{
        DictionaryDO data;
        
        data = EJBFactory.getDictionary().fetchBySystemName(systemName);
        return data.getId();
    }
}
