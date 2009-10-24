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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.entity.TestTypeOfSample;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestTypeOfSampleLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestTypeOfSampleBean implements TestTypeOfSampleLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    private static final TestMetaMap meta = new TestMetaMap();
    
    public ArrayList<TestTypeOfSampleDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        ArrayList<TestTypeOfSampleDO> sampleTypeList;

        query = manager.createNamedQuery("TestTypeOfSample.FetchByTestId");
        query.setParameter("id", testId);
        sampleTypeList = (ArrayList<TestTypeOfSampleDO>)query.getResultList();

        if (sampleTypeList.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(sampleTypeList);
    }

    public TestTypeOfSampleDO add(TestTypeOfSampleDO data) throws Exception {
        TestTypeOfSample sampleType;
        manager.setFlushMode(FlushModeType.COMMIT);

        sampleType = new TestTypeOfSample();

        sampleType.setTestId(data.getTestId());
        sampleType.setTypeOfSampleId(data.getTypeOfSampleId());
        sampleType.setUnitOfMeasureId(data.getUnitOfMeasureId());

        manager.persist(sampleType);
        data.setId(sampleType.getId());
        
        return data;

    }

    public TestTypeOfSampleDO update(TestTypeOfSampleDO data) throws Exception {
        TestTypeOfSample sampleType;
        
        if(!data.isChanged())
            return data;       
        
        manager.setFlushMode(FlushModeType.COMMIT);
    
        sampleType = manager.find(TestTypeOfSample.class, data.getId());

        sampleType.setTestId(data.getTestId());
        sampleType.setTypeOfSampleId(data.getTypeOfSampleId());
        sampleType.setUnitOfMeasureId(data.getUnitOfMeasureId());
        
        return data;
    }
    
    public void delete(TestTypeOfSampleDO sampleTypeDO) throws Exception {
        TestTypeOfSample sampleType;
        manager.setFlushMode(FlushModeType.COMMIT);

        sampleType = manager.find(TestTypeOfSample.class, sampleTypeDO.getId());
        if (sampleType != null)
            manager.remove(sampleType);

    }

    public void validate(TestTypeOfSampleDO sampleType) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        if (sampleType.getTypeOfSampleId() == null) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.TEST_TYPE_OF_SAMPLE.getTypeOfSampleId()));
        }
        
        if(list.size() > 0)
            throw list;
    }

}
