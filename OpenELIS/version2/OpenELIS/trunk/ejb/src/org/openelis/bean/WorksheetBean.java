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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.WorksheetDO;
import org.openelis.entity.Worksheet;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.WorksheetLocal;
import org.openelis.metamap.WorksheetMetaMap;
import org.openelis.remote.WorksheetRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetBean implements WorksheetRemote, WorksheetLocal {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
    private static final WorksheetMetaMap meta = new WorksheetMetaMap();
    
	public WorksheetDO fetchById(Integer id) throws Exception {		
		Query       query;
		WorksheetDO data;
		
		query = manager.createNamedQuery("Worksheet.FetchById");
		query.setParameter("id", id);
		try {
		    data = (WorksheetDO) query.getSingleResult();
		} catch (NoResultException e) {
		    throw new NotFoundException();
		} catch (Exception e) {
		    throw new DatabaseException(e);
		}
        return data;
	}

    public ArrayList<WorksheetDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.WorksheetDO("+meta.getId()+", "+
                          meta.getCreatedDate()+", "+
                          meta.getSystemUserId()+", "+
                          meta.getStatusId()+", "+
                          meta.getFormatId()+") ");
        builder.constructWhere(fields);
        builder.setOrderBy(meta.getId());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<WorksheetDO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<WorksheetDO>)list;
    }

	public WorksheetDO add(WorksheetDO data){
        Worksheet entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Worksheet();
        entity.setCreatedDate(data.getCreatedDate());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setStatusId(data.getStatusId());
        entity.setFormatId(data.getFormatId());

        manager.persist(data);
        data.setId(entity.getId());
        
        return data;
    }
    
    public WorksheetDO update(WorksheetDO data) throws Exception {
        Worksheet entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Worksheet.class, data.getId());
        entity.setCreatedDate(data.getCreatedDate());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setStatusId(data.getStatusId());
        entity.setFormatId(data.getFormatId());

        return data;
    }

    public void validate(WorksheetDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getCreatedDate()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getCreatedDate()));

        if (DataBaseUtil.isEmpty(data.getSystemUserId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getSystemUserId()));

        if (DataBaseUtil.isEmpty(data.getStatusId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getStatusId()));

        if (DataBaseUtil.isEmpty(data.getFormatId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getFormatId()));

        if (list.size() > 0)
            throw list;
    }
}