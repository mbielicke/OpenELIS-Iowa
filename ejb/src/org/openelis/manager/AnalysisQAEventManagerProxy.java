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

import org.openelis.bean.AnalysisQAEventBean;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class AnalysisQAEventManagerProxy {
    public AnalysisQaEventManager fetchByAnalysisId(Integer analysisId) throws Exception {
        ArrayList<AnalysisQaEventViewDO> list;
        AnalysisQaEventManager aqm;
        
        list = EJBFactory.getAnalysisQAEvent().fetchByAnalysisId(analysisId);
        aqm = AnalysisQaEventManager.getInstance();
        aqm.setAnalysisQAEvents(list);
        aqm.setAnalysisId(analysisId);
        
        return aqm;
    }
    
    public AnalysisQaEventManager add(AnalysisQaEventManager man) throws Exception {
        AnalysisQaEventViewDO data;
        AnalysisQAEventBean l;
        
        l = EJBFactory.getAnalysisQAEvent();
        for(int i=0; i<man.count(); i++){
            data = man.getAnalysisQAAt(i);
            data.setAnalysisId(man.getAnalysisId());
            
            l.add(data);
        }
        
        return man;
    }
    
    public AnalysisQaEventManager update(AnalysisQaEventManager man) throws Exception {
        int i;
        AnalysisQaEventViewDO data;
        AnalysisQAEventBean l;
        
        l = EJBFactory.getAnalysisQAEvent();
        for(i=0; i<man.deleteCount(); i++)
            l.delete(man.getDeletedAt(i));
        
        for(i=0; i<man.count(); i++){
            data = man.getAnalysisQAAt(i);
            
            if(data.getId() == null){
                data.setAnalysisId(man.getAnalysisId());
                l.add(data);
            }else
                l.update(data);
        }

        return man;
    }
    
    public void validate(AnalysisQaEventManager man, ValidationErrorsList errorsList) throws Exception {      
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception{
        DictionaryDO data;
        
        data = EJBFactory.getDictionary().fetchBySystemName(systemName);
        return data.getId();
    }      
}
