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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.entity.InstrumentLog;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.meta.InstrumentMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("instrument-select")
public class InstrumentLogBean implements InstrumentLogLocal {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;

    @SuppressWarnings("unchecked")
    public ArrayList<InstrumentLogDO> fetchByInstrumentId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InstrumentLog.FetchByInstrumentId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public InstrumentLogDO add(InstrumentLogDO data) throws Exception {
        InstrumentLog entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new InstrumentLog();
        entity.setInstrumentId(data.getInstrumentId());
        entity.setTypeId(data.getTypeId());
        entity.setWorksheetId(data.getWorksheetId());
        entity.setEventBegin(data.getEventBegin());
        entity.setEventEnd(data.getEventEnd());
        entity.setText(data.getText());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }
    
    public InstrumentLogDO update(InstrumentLogDO data) throws Exception {
        InstrumentLog entity;
        
        if(!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(InstrumentLog.class, data.getId());
        entity.setInstrumentId(data.getInstrumentId());
        entity.setTypeId(data.getTypeId());
        entity.setWorksheetId(data.getWorksheetId());
        entity.setEventBegin(data.getEventBegin());
        entity.setEventEnd(data.getEventEnd());
        entity.setText(data.getText());
                
        return data;
    }

    public void delete(InstrumentLogDO data) throws Exception {
        InstrumentLog entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(InstrumentLog.class, data.getId());
        if(entity != null)
            manager.remove(entity);        
    }

    public void validate(InstrumentLogDO data) throws Exception {
        Datetime eb,ee;
        Integer wsId;
        Query query;
        ValidationErrorsList list;
                       
        list = new ValidationErrorsList();
        
        if(DataBaseUtil.isEmpty(data.getTypeId())) 
            list.add(new FieldErrorException("fieldRequiredException",
                                             InstrumentMeta.getLogTypeId()));        
        
        wsId = data.getWorksheetId();
        if(!DataBaseUtil.isEmpty(wsId)) {
            query = manager.createNamedQuery("Worksheet.FetchById");
            query.setParameter("id",wsId );
            if(query.getResultList().size() == 0)
                list.add(new FieldErrorException("illegalWorksheetIdException",
                                                 InstrumentMeta.getLogWorksheetId()));           
        }
         
        eb = data.getEventBegin();
        ee = data.getEventEnd();
        
        if(DataBaseUtil.isEmpty(eb)) 
            list.add(new FieldErrorException("fieldRequiredException",
                                             InstrumentMeta.getLogEventBegin()));
        else if(!DataBaseUtil.isEmpty(ee) && DataBaseUtil.isAfter(eb,ee)) 
            list.add(new FieldErrorException("endDateAfterBeginDateException",
                                             InstrumentMeta.getLogEventEnd()));
                  
        if (list.size() > 0)
            throw list;
    }

}
