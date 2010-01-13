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

import org.openelis.domain.AddressDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.local.AddressLocal;
import org.openelis.local.SamplePrivateWellLocal;

public class SamplePrivateWellManagerProxy {
    public SamplePrivateWellManager add(SamplePrivateWellManager man) throws Exception {
        AddressDO adDO;
        SamplePrivateWellLocal wl = getWellLocal();
        AddressLocal al = getAddressLocal();
        
        //add the report to address if necessary
        if(man.getReportToAddress() != null){
            adDO = al.add(man.getReportToAddress());
            man.getPrivateWell().setReportToAddressId(adDO.getId());
        }
        
        man.getPrivateWell().setSampleId(man.getSampleId());
        wl.add(man.getPrivateWell());
        
        return man;
    }

    public SamplePrivateWellManager update(SamplePrivateWellManager man) throws Exception {
        AddressDO adDO;
        SamplePrivateWellLocal wl = getWellLocal();
        AddressLocal al = getAddressLocal();
        
        //delete the report to address if necessary
        if(man.getDeletedAddress() != null)
            al.delete(man.getDeletedAddress());
        
        //add the report to address if necessary
        if(man.getReportToAddress() != null && man.getReportToAddress().getId() == null){
            adDO = al.add(man.getReportToAddress());
            man.getPrivateWell().setReportToAddressId(adDO.getId());
        }
        
        man.getPrivateWell().setSampleId(man.getSampleId());
        wl.update(man.getPrivateWell());
        
        return man;
    }
    
    public SamplePrivateWellManager fetch(Integer sampleId) throws Exception {
        SamplePrivateWellViewDO wellDO;
        AddressDO addressDO;
        SamplePrivateWellLocal wl = getWellLocal();
        AddressLocal al = getAddressLocal();
        
        wellDO = wl.fetchBySampleId(sampleId);
        
        SamplePrivateWellManager pwm = SamplePrivateWellManager.getInstance();
        pwm.setPrivateWell(wellDO);
        
        if(wellDO.getOrganizationId() != null){
            addressDO = al.fetchById(wellDO.getOrgAddressId());
            pwm.setOrganizationAddress(addressDO);
        }else{
            addressDO = al.fetchById(wellDO.getReportToAddressId());
            pwm.setReportToAddress(addressDO);
        }
        
        return pwm;
    }
    
    private SamplePrivateWellLocal getWellLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SamplePrivateWellLocal)ctx.lookup("openelis/SamplePrivateWellBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private AddressLocal getAddressLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (AddressLocal)ctx.lookup("openelis/AddressBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
