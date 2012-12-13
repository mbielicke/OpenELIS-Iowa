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
import org.openelis.domain.TestSectionViewDO;
import org.openelis.entity.TestSection;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestSectionLocal;
import org.openelis.meta.TestMeta;

@Stateless
@SecurityDomain("openelis")

public class TestSectionBean implements TestSectionLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    public ArrayList<TestSectionViewDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        List<TestSectionViewDO> list;
        
        query = manager.createNamedQuery("TestSection.FetchByTestId");
        query.setParameter("testId", testId);
        list = query.getResultList();

        if (list.isEmpty())
            throw new NotFoundException("Test has no sections assigned to it.");

        return DataBaseUtil.toArrayList(list);
    }
        
    public TestSectionViewDO add(TestSectionViewDO data) throws Exception {
        TestSection ts;

        manager.setFlushMode(FlushModeType.COMMIT);

        ts = new TestSection();

        ts.setFlagId(data.getFlagId());
        ts.setSectionId(data.getSectionId());
        ts.setTestId(data.getTestId());

        manager.persist(ts);
        data.setId(ts.getId());
        
        return data;
    }

    public TestSectionViewDO update(TestSectionViewDO data) throws Exception {
        TestSection ts;

        if(!data.isChanged())
            return data;            
        
        manager.setFlushMode(FlushModeType.COMMIT);

        ts = manager.find(TestSection.class, data.getId());

        ts.setFlagId(data.getFlagId());
        ts.setSectionId(data.getSectionId());
        ts.setTestId(data.getTestId());

        return data;
    }
    
    public void delete(TestSectionViewDO data) throws Exception {
        TestSection ts;

        manager.setFlushMode(FlushModeType.COMMIT);

        ts = manager.find(TestSection.class, data.getId());

        if (ts != null)
            manager.remove(ts);

    }

    public void validate(TestSectionViewDO data) throws Exception {        
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (data.getSectionId() == null) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getSectionSectionId()));
        } 
        
        if (list.size() > 0)
            throw list;
    }

}
