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
import org.openelis.domain.IOrderTestAnalyteDO;
import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.entity.IOrderTestAnalyte;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class IOrderTestAnalyteBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;

    public ArrayList<IOrderTestAnalyteViewDO> fetchByIorderTestId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("IOrderTestAnalyte.FetchByIorderTestId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<IOrderTestAnalyteViewDO> fetchByIorderTestIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("IOrderTestAnalyte.FetchByIorderTestIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IOrderTestAnalyteViewDO> fetchRowAnalytesByIorderTestId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("IOrderTestAnalyte.FetchRowAnalytesByIorderTestId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<IOrderTestAnalyteViewDO> fetchRowAnalytesByTestId(Integer testId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("IOrderTestAnalyte.FetchRowAnalytesByTestId");
        query.setParameter("testId", testId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public IOrderTestAnalyteDO add(IOrderTestAnalyteDO data) throws Exception {
        IOrderTestAnalyte entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new IOrderTestAnalyte();       
        entity.setIorderTestId(data.getIorderTestId());
        entity.setAnalyteId(data.getAnalyteId());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public IOrderTestAnalyteDO update(IOrderTestAnalyteDO data) throws Exception {
        IOrderTestAnalyte entity;
        
        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderTestAnalyte.class, data.getId());       
        entity.setIorderTestId(data.getIorderTestId());
        entity.setAnalyteId(data.getAnalyteId());
        
        return data;
    }

    public void delete(IOrderTestAnalyteDO data) throws Exception {
        IOrderTestAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderTestAnalyte.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
    
    public void deleteByOrderTestId(Integer id) throws Exception {
        ArrayList<IOrderTestAnalyteViewDO> list;

        try {
            list = fetchByIorderTestId(id);
            for (IOrderTestAnalyteViewDO data : list)
                delete(data);
        } catch (NotFoundException e) {
            // the order test may not have any analytes linked to it
        }
    }

    public void validate(IOrderTestAnalyteViewDO data, int index) throws Exception {
        String indexStr;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        indexStr = String.valueOf(index);
        if ("N".equals(data.getTestAnalyteIsPresent()) && "Y".equals(data.getTestAnalyteIsReportable()))
            /*
             * this analyte is not present in the original test thus it needs
             * to be removed from this order test 
             */
            list.add(new FieldErrorException(Messages.get().analyteNotPresentInTestException(null, indexStr), data.getAnalyteName()));            
        
        if (list.size() > 0)
            throw list;
    }
}