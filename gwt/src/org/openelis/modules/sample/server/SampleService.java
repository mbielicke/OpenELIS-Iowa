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
package org.openelis.modules.sample.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleManagerRemote;
import org.openelis.remote.SampleQAEventManagerRemote;
import org.openelis.remote.SampleRemote;
import org.openelis.remote.SystemVariableRemote;

public class SampleService {
    private static final int rowPP = 12;
    
    public SampleManager fetchById(Integer sampleId) throws Exception {
        try{
            return managerRemote().fetchById(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        try{
            return managerRemote().fetchWithItemsAnalysis(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        try{
            return managerRemote().fetchByAccessionNumber(accessionNumber);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    //sample methods
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try{
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public SampleManager add(SampleManager man) throws Exception {
        try{
            return managerRemote().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public SampleManager update(SampleManager man) throws Exception {
        try{
            return managerRemote().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        try{
            return managerRemote().fetchForUpdate(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        try{
            return managerRemote().abortUpdate(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    //sample org methods
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId) throws Exception {
        try{
            return managerRemote().fetchSampleOrgsBySampleId(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    //sample project methods
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        try{
            return managerRemote().fetchSampleProjectsBySampleId(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    //sample item methods
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        try{
            return managerRemote().fetchSampleItemsBySampleId(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    //sample qa method
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        try{
            return qaRemote().fetchBySampleId(sampleId);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception {
        try{
            return managerRemote().validateAccessionNumber(sampleDO);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public Integer getNewAccessionNumber() throws Exception {
        SystemVariableDO  sysVarDO;
        int tries, i;
        Integer value;
        
        sysVarDO = null;
        tries = 5;
        value = null;
        i=0;
        while(i<tries && sysVarDO == null){
            try {
                sysVarDO = sysVarRemote().fetchForUpdateByName("last_accession_number");
                value = Integer.valueOf(sysVarDO.getValue());
                value = value+1;
                sysVarDO.setValue(value.toString());
                sysVarRemote().update(sysVarDO);
            } catch (Exception e) {
                Thread.sleep(50);
            }
            i++;
        }
        
        return value;
    }

    private SampleRemote remote(){
        return (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
    }
    
    private SampleManagerRemote managerRemote(){
        return (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
    }
    
    private SampleQAEventManagerRemote qaRemote(){
        return (SampleQAEventManagerRemote)EJBFactory.lookup("openelis/SampleQAEventManagerBean/remote");
    }
    
    private SystemVariableRemote sysVarRemote() {
        return (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
    }
}
