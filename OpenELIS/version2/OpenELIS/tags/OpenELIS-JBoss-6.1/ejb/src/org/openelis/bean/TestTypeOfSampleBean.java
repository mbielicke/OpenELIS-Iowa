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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.entity.TestTypeOfSample;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestTypeOfSampleLocal;
import org.openelis.meta.TestMeta;
import org.openelis.remote.TestTypeOfSampleRemote;

@Stateless
@SecurityDomain("openelis")

public class TestTypeOfSampleBean implements TestTypeOfSampleLocal, TestTypeOfSampleRemote {
    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
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

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Integer testId, Integer typeOfSampleId, String unitOfMeasure) throws Exception {
        Query query;
        ArrayList<IdNameVO> unitList;

        query = manager.createNamedQuery("TestTypeOfSample.FetchUnitsForWorksheetAutocomplete");
        query.setParameter("testId", testId);
        query.setParameter("typeOfSampleId", typeOfSampleId);
        query.setParameter("unitOfMeasure", unitOfMeasure);
        unitList = (ArrayList<IdNameVO>)query.getResultList();

        if (unitList.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(unitList);
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
    
    public void delete(TestTypeOfSampleDO data) throws Exception {
        TestTypeOfSample sampleType;
        manager.setFlushMode(FlushModeType.COMMIT);

        sampleType = manager.find(TestTypeOfSample.class, data.getId());
        if (sampleType != null)
            manager.remove(sampleType);

    }

    public void validate(TestTypeOfSampleDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        if (data.getTypeOfSampleId() == null) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getTypeOfSampleTypeOfSampleId()));
        }
        
        if(list.size() > 0)
            throw list;
    }

}
