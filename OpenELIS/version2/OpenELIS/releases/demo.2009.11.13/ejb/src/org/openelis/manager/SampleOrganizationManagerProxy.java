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

import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SampleOrganizationLocal;

public class SampleOrganizationManagerProxy {
    public Integer getIdFromSystemName(String systemName){
        throw new UnsupportedOperationException();
    }
    
    public SampleOrganizationManager fetchBySampleId(Integer sampleId) throws Exception {
        SampleOrganizationLocal sol = getOrganizationLocal();
        ArrayList<SampleOrganizationViewDO> orgs = (ArrayList<SampleOrganizationViewDO>)sol.fetchBySampleId(sampleId);
        
        SampleOrganizationManager som = SampleOrganizationManager.getInstance();
        som.setOrganizations(orgs);
        som.setSampleId(sampleId);
        
        return som;
    }
    
    public SampleOrganizationManager add(SampleOrganizationManager man) throws Exception {
        SampleOrganizationLocal sol = getOrganizationLocal();
        SampleOrganizationViewDO orgDO;
        
        for(int i=0; i<man.count(); i++){
            orgDO = man.getOrganizationAt(i);
            orgDO.setSampleId(man.getSampleId());
            
            sol.add(orgDO);
        }
        
        return man;
    }
    
    public SampleOrganizationManager update(SampleOrganizationManager man) throws Exception {
        SampleOrganizationLocal sol = getOrganizationLocal();
        SampleOrganizationViewDO orgDO;
        
        for(int j=0; j<man.deleteCount(); j++){
            sol.delete(man.getDeletedAt(j));
        }
        
        for(int i=0; i<man.count(); i++){
            orgDO = man.getOrganizationAt(i);
            
            if(orgDO.getId() == null){
                orgDO.setSampleId(man.getSampleId());
                sol.add(orgDO);
            }else
                sol.update(orgDO);
        }

        return man;
    }
    
    public void validate(SampleOrganizationManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private SampleOrganizationLocal getOrganizationLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleOrganizationLocal)ctx.lookup("openelis/SampleOrganizationBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
