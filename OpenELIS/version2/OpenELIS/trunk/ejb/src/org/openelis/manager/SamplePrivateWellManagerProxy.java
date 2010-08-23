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

import javax.naming.InitialContext;

import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SamplePrivateWellLocal;

public class SamplePrivateWellManagerProxy {
    public SamplePrivateWellManager fetch(Integer sampleId) throws Exception {
        SamplePrivateWellManager man;
        SamplePrivateWellViewDO data;        
        
        data = local().fetchBySampleId(sampleId);
        
        man = SamplePrivateWellManager.getInstance();
        man.setPrivateWell(data);       
        
        return man;
    }

    public SamplePrivateWellManager add(SamplePrivateWellManager man) throws Exception {     
        man.getPrivateWell().setSampleId(man.getSampleId());
        local().add(man.getPrivateWell());
        
        return man;
    }

    public SamplePrivateWellManager update(SamplePrivateWellManager man) throws Exception {
        SamplePrivateWellViewDO data;
        
        data = man.getPrivateWell();                       
        if(data.getId() == null) {
            data.setSampleId(man.getSampleId());
            local().add(data);
        } else {
            local().update(data);
        }
        
        return man;
    }
    
    public void validate(SamplePrivateWellManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private SamplePrivateWellLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (SamplePrivateWellLocal)ctx.lookup("openelis/SamplePrivateWellBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
}
