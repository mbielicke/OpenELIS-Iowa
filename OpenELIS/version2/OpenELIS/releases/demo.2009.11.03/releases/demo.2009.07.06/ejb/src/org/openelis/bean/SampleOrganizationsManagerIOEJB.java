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
package org.openelis.bean;

import java.util.List;

import javax.naming.InitialContext;

import org.openelis.local.CategoryLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.manager.SampleOrganizationsManager;
import org.openelis.manager.SampleOrganizationsManagerIOInt;

public class SampleOrganizationsManagerIOEJB implements SampleOrganizationsManagerIOInt {

public List fetch(Integer sampleId) {
        
    SampleOrganizationLocal local = getSampleOrganizationLocal();

        return local.getOrganizationsBySampleId(sampleId);
        
    }

    public void update(SampleOrganizationsManager sampleOrgs) {
        
        SampleOrganizationLocal local = getSampleOrganizationLocal();

        local.update(sampleOrgs);
        
    }
    
    public Integer getIdFromSystemName(String systemName) {
        CategoryLocal local = getCategoryLocal();

        return local.getEntryIdForSystemName(systemName);
    }
    
    private SampleOrganizationLocal getSampleOrganizationLocal(){
        
        try{
            InitialContext ctx = new InitialContext();
            return (SampleOrganizationLocal)ctx.lookup("openelis/SampleOrganizationBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private CategoryLocal getCategoryLocal(){
        
        try{
            InitialContext ctx = new InitialContext();
            return (CategoryLocal)ctx.lookup("openelis/CategoryBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }    
}
