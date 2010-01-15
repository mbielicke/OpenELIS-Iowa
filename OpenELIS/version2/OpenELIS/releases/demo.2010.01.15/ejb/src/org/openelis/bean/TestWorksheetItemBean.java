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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.entity.TestWorksheetItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestWorksheetItemLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestWorksheetItemBean implements TestWorksheetItemLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    @EJB
    private DictionaryLocal          dictionary;
    
    private static int               typeFixed, typeDupl; 
    
    private static final Logger      log  = Logger.getLogger(TestWorksheetItemBean.class.getName());
    
    @PostConstruct
    public void init() {
        DictionaryDO data;
        
        
        try {
            data = dictionary.fetchBySystemName("pos_fixed");
            typeFixed = data.getId();
        } catch (Throwable e) {
            typeFixed = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='pos_fixed'", e);
        }
        
        try {
            data = dictionary.fetchBySystemName("pos_duplicate");
            typeDupl = data.getId();
        } catch (Throwable e) {
            typeDupl = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='pos_duplicate'", e);
        }
    }
    
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
    
    public void delete(TestWorksheetItemDO data) throws Exception {
        TestWorksheetItem testWorksheetItem;       
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = manager.find(TestWorksheetItem.class, data.getId());
        
        if(testWorksheetItem != null)
            manager.remove(testWorksheetItem);

    }


    public void validate(TestWorksheetItemDO data) throws Exception {
        String name;
        ValidationErrorsList list;
        Integer position, typeId;

        list = new ValidationErrorsList();
        name = data.getQcName();
        position = data.getPosition();
        typeId = data.getTypeId();
        
        if (DataBaseUtil.isEmpty(name)) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetItemQcName()));
        }
        if (DataBaseUtil.isEmpty(data.getTypeId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetItemTypeId()));
            
        }
        
        if (position == null) {
            if (DataBaseUtil.isSame(typeDupl, typeId) || DataBaseUtil.isSame(typeFixed, typeId)) {
                list.add(new FieldErrorException("fixedDuplicatePosException",
                                                 TestMeta.getWorksheetItemPosition()));
            }
        } else {
            if (position == 1 && DataBaseUtil.isSame(typeDupl, typeId)) {
                list.add(new FieldErrorException("posOneDuplicateException",
                                                 TestMeta.getWorksheetItemTypeId()));
            } else if (DataBaseUtil.isDifferent(typeDupl, typeId) && DataBaseUtil.isDifferent(typeFixed, typeId)) {
                list.add(new FieldErrorException("posSpecifiedException",
                                                 TestMeta.getWorksheetItemPosition()));
            }
        }
        
        if(list.size() > 0)
            throw list;
    }


}
