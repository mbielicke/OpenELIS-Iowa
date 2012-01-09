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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.entity.TestWorksheetAnalyte;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestWorksheetAnalyteLocal;
import org.openelis.meta.TestMeta;

@Stateless
@SecurityDomain("openelis")

public class TestWorksheetAnalyteBean implements TestWorksheetAnalyteLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
    public ArrayList<TestWorksheetAnalyteViewDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("TestWorksheetAnalyte.FetchByTestId");
        query.setParameter("testId", testId);
        list = query.getResultList();       
        
        return DataBaseUtil.toArrayList(list);
    }
    
    public TestWorksheetAnalyteViewDO add(TestWorksheetAnalyteViewDO data) throws Exception {
        TestWorksheetAnalyte analyte;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        analyte = new TestWorksheetAnalyte();
        
        analyte.setFlagId(data.getFlagId());
        analyte.setRepeat(data.getRepeat());
        analyte.setTestAnalyteId(data.getTestAnalyteId());
        analyte.setTestId(data.getTestId());
        
        manager.persist(analyte);
        
        data.setId(analyte.getId());
        
        return data;
    }
    
    public TestWorksheetAnalyteViewDO update(TestWorksheetAnalyteViewDO data) throws Exception {
        TestWorksheetAnalyte analyte;
        
        if(!data.isChanged()) 
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        analyte = manager.find(TestWorksheetAnalyte.class, data.getId());
        
        analyte.setFlagId(data.getFlagId());
        analyte.setRepeat(data.getRepeat());
        analyte.setTestAnalyteId(data.getTestAnalyteId());
        analyte.setTestId(data.getTestId());
        
        return data;

    }

    public void delete(TestWorksheetAnalyteViewDO deletedAnalyteAt) throws Exception {
        TestWorksheetAnalyte analyte;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        analyte = manager.find(TestWorksheetAnalyte.class, deletedAnalyteAt.getId());
        
        if(analyte != null)
            manager.remove(analyte);

    }

    public void validate(TestWorksheetAnalyteViewDO analyte) throws Exception {
        ValidationErrorsList list;
        Integer repeat;
                
        repeat = analyte.getRepeat();
        list = new ValidationErrorsList();
        
        if (DataBaseUtil.isEmpty(repeat) || repeat < 1) {
            list.add(new FieldErrorException("repeatNullForAnalyteException",
                                             TestMeta.getWorksheetAnalyteRepeat()));
        }
        
        if(list.size() > 0)
            throw list;

    }


}
