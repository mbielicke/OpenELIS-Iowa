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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.entity.TestWorksheetItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestWorksheetItemBean implements TestWorksheetItemLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    private static final TestMetaMap meta = new TestMetaMap();
    
    public ArrayList<TestWorksheetItemDO> fetchByTestWorksheetId(Integer twsId) throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("TestWorksheetItem.FetchByTestWorksheetId");
        query.setParameter("testWorksheetId", twsId);
                
        list = query.getResultList();       
        
        return DataBaseUtil.toArrayList(list);
    }
    
    public TestWorksheetItemDO add(TestWorksheetItemDO data) throws Exception {
        TestWorksheetItem testWorksheetItem;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = new TestWorksheetItem();
        
        testWorksheetItem.setPosition(data.getPosition());
        testWorksheetItem.setQcName(data.getQcName());
        testWorksheetItem.setTestWorksheetId(data.getTestWorksheetId());
        testWorksheetItem.setTypeId(data.getTypeId()); 
        
        manager.persist(testWorksheetItem);
        
        data.setId(testWorksheetItem.getId()); 
        
        return data;
    }

    public TestWorksheetItemDO update(TestWorksheetItemDO data) throws Exception {
        TestWorksheetItem testWorksheetItem;
        
        if(!data.isChanged())
            return data;
            
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = manager.find(TestWorksheetItem.class, data.getId());
        
        testWorksheetItem.setPosition(data.getPosition());
        testWorksheetItem.setQcName(data.getQcName());
        testWorksheetItem.setTestWorksheetId(data.getTestWorksheetId());
        testWorksheetItem.setTypeId(data.getTypeId());
        
        return data;
    }
    
    public void delete(TestWorksheetItemDO deletedItemAt) throws Exception {
        TestWorksheetItem testWorksheetItem;       
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = manager.find(TestWorksheetItem.class, deletedItemAt.getId());
        
        if(testWorksheetItem != null)
            manager.remove(testWorksheetItem);

    }


    public void validate(TestWorksheetItemDO item) throws Exception {
        String name;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        name = item.getQcName();
        
        if (DataBaseUtil.isEmpty(name)) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.TEST_WORKSHEET_ITEM.getQcName()));
        }
        if (DataBaseUtil.isEmpty(item.getTypeId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                                  meta.TEST_WORKSHEET_ITEM.getTypeId()));
            
        }
        
        if(list.size() > 0)
            throw list;
    }


}
