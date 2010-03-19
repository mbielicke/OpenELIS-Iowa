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

import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.meta.SampleMeta;

public class SampleOrganizationManagerProxy {
    public SampleOrganizationManager fetchBySampleId(Integer sampleId) throws Exception {
        ArrayList<SampleOrganizationViewDO> orgs;
        SampleOrganizationManager som;
        
        orgs = local().fetchBySampleId(sampleId);
        
        som = SampleOrganizationManager.getInstance();
        som.setOrganizations(orgs);
        som.setSampleId(sampleId);
        
        return som;
    }
    
    public SampleOrganizationManager add(SampleOrganizationManager man) throws Exception {
        SampleOrganizationViewDO orgDO;
        SampleOrganizationLocal l;
        
        l = local();
        for(int i=0; i<man.count(); i++){
            orgDO = man.getOrganizationAt(i);
            orgDO.setSampleId(man.getSampleId());
            
            l.add(orgDO);
        }
        
        return man;
    }
    
    public SampleOrganizationManager update(SampleOrganizationManager man) throws Exception {
        SampleOrganizationViewDO orgDO;
        SampleOrganizationLocal l;
        
        l = local();
        for(int j=0; j<man.deleteCount(); j++){
            l.delete(man.getDeletedAt(j));
        }
        
        for(int i=0; i<man.count(); i++){
            orgDO = man.getOrganizationAt(i);
            
            if(orgDO.getId() == null){
                orgDO.setSampleId(man.getSampleId());
                l.add(orgDO);
            }else
                l.update(orgDO);
        }

        return man;
    }
    
    public Integer getIdFromSystemName(String systemName){
        assert false : "not supported";
        return null;
    }
    
    public void validate(SampleOrganizationManager man, ValidationErrorsList errorsList) throws Exception {
        int numBillTo, numReportTo;
        Integer orgBillToId, orgReportToId;
        
        numBillTo = 0;
        numReportTo = 0;
        orgBillToId = dictionaryLocal().fetchBySystemName("org_bill_to").getId();
        orgReportToId = dictionaryLocal().fetchBySystemName("org_report_to").getId();
        
        for(int i=0; i<man.count(); i++){
            SampleOrganizationDO orgDO = man.getOrganizationAt(i);
            if(orgBillToId.equals(orgDO.getTypeId()))
                numBillTo++;
            
            if(orgReportToId.equals(orgDO.getTypeId()))
                numReportTo++;
        }
        
        if(numBillTo > 1)
            errorsList.add(new FormErrorException("multipleBillToException"));
        
        if(numReportTo > 1)
            errorsList.add(new FormErrorException("multipleReportToException"));
        
        if(numReportTo == 0)
            errorsList.add(new FieldErrorWarning("reportToMissingWarning", SampleMeta.getOrgName()));
            
        if(numBillTo == 0)
            errorsList.add(new FieldErrorWarning("billToMissingWarning", SampleMeta.getBillTo()));
    }
    
    private SampleOrganizationLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleOrganizationLocal)ctx.lookup("openelis/SampleOrganizationBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private static DictionaryLocal dictionaryLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
