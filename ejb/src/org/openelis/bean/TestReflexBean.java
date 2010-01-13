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
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.entity.TestReflex;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestReflexLocal;
import org.openelis.meta.TestMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestReflexBean implements TestReflexLocal {
    
    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    public ArrayList<TestReflexViewDO> fetchByTestId(Integer testId) throws Exception {
        Query query;
        List<TestReflexViewDO> list;
        Integer dictId;
        TestReflexViewDO refDO;
        String value;
        DictionaryViewDO dictDO;
        
        dictId = (dictLocal().fetchBySystemName("test_res_type_dictionary")).getId();
        
        query = manager.createNamedQuery("TestReflex.FetchByTestId");
        query.setParameter("testId", testId);
        list = query.getResultList();                
        
        for(int i = 0; i < list.size(); i++) {
            refDO = list.get(i);            
            if(!dictId.equals(refDO.getTestResultTypeId()))
                continue;
            
            value = refDO.getTestResultValue();            
            dictDO = dictLocal().fetchById(Integer.parseInt(value));
            refDO.setTestResultValue(dictDO.getEntry());
        }
        
        return DataBaseUtil.toArrayList(list);
    }

    public TestReflexViewDO add(TestReflexViewDO data) throws Exception {
        TestReflex testReflex;

        manager.setFlushMode(FlushModeType.COMMIT);

        testReflex = new TestReflex();

        testReflex.setTestId(data.getTestId());
        testReflex.setAddTestId(data.getAddTestId());
        testReflex.setTestAnalyteId(data.getTestAnalyteId());
        testReflex.setTestResultId(data.getTestResultId());
        testReflex.setFlagsId(data.getFlagsId());

        manager.persist(testReflex);

        data.setId(testReflex.getId());
        
        return data;
    }
    
    public TestReflexViewDO update(TestReflexViewDO data) throws Exception {
        TestReflex testReflex;

        if(!data.isChanged())
            return data;               
        
        manager.setFlushMode(FlushModeType.COMMIT);

        testReflex = manager.find(TestReflex.class, data.getId());

        testReflex.setTestId(data.getTestId());
        testReflex.setAddTestId(data.getAddTestId());        
        testReflex.setTestAnalyteId(data.getTestAnalyteId());
        testReflex.setTestResultId(data.getTestResultId());
        testReflex.setFlagsId(data.getFlagsId());
        
        return data;
    }

    public void delete(TestReflexViewDO data) throws Exception {
        TestReflex testReflex;

        manager.setFlushMode(FlushModeType.COMMIT);

        testReflex = manager.find(TestReflex.class, data.getId());

        if (testReflex != null)
            manager.remove(testReflex);
    }

    public void validate(TestReflexViewDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
                
        if (data.getAddTestId() == null) {           
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getReflexAddTestName()));
        }

        if (data.getTestAnalyteId() == null) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getReflexTestAnalyteName()));
        }

        if (data.getTestResultId() == null) {            
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getReflexTestResultValue()));
        }

        if (data.getFlagsId() == null) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getReflexFlagsId()));
        }
        
        if(list.size() > 0)
            throw list;

    }
    
    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
