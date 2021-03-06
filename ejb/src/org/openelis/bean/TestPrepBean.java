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

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.entity.TestPrep;
import org.openelis.meta.TestMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class TestPrepBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
    public ArrayList<TestPrepViewDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        List<TestPrepViewDO> testPrepDOList;
        
        query = manager.createNamedQuery("TestPrep.FetchByTestId");
        query.setParameter("id", testId);
        testPrepDOList = (ArrayList<TestPrepViewDO>)query.getResultList();                
        
        if (testPrepDOList.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(testPrepDOList);
    }
    
    public TestPrepDO add(TestPrepDO data) throws Exception {
        TestPrep prepTest;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        prepTest = new TestPrep();

        prepTest.setIsOptional(data.getIsOptional());
        prepTest.setPrepTestId(data.getPrepTestId());
        prepTest.setTestId(data.getTestId());

        manager.persist(prepTest);

        data.setId(prepTest.getId());
        
        return data;

    }
    
    public TestPrepDO update(TestPrepDO data) throws Exception {
        TestPrep prepTest;

        if(!data.isChanged())
            return data;               
                
        manager.setFlushMode(FlushModeType.COMMIT);

        prepTest = manager.find(TestPrep.class, data.getId());

        prepTest.setIsOptional(data.getIsOptional());
        prepTest.setPrepTestId(data.getPrepTestId());
        prepTest.setTestId(data.getTestId());
        
        return data;
    }

    public void delete(TestPrepDO data) throws Exception {
        TestPrep entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestPrep.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void validate(TestPrepDO data) throws Exception {
        ValidationErrorsList list;                

        list = new ValidationErrorsList();        
        if (data.getPrepTestId() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getPrepPrepTestName()));                        
        }

        if (list.size() > 0)
            throw list;
    }

}
