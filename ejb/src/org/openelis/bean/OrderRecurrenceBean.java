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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.entity.OrderRecurrence;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.OrderRecurrenceLocal;
import org.openelis.meta.OrderMeta;

@Stateless
@SecurityDomain("openelis")

public class OrderRecurrenceBean implements OrderRecurrenceLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
    @EJB
    private DictionaryLocal dictionary;
    
    private static Integer monthId, yearId;
    
    @PostConstruct
    public void init() {
        try {
            if (monthId == null) {
                monthId = dictionary.fetchBySystemName("order_recurrence_unit_months").getId();
                yearId = dictionary.fetchBySystemName("order_recurrence_unit_years").getId();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public OrderRecurrenceDO fetchByOrderId(Integer orderId) throws Exception {
        Query query;
        OrderRecurrenceDO data;
        
        query = manager.createNamedQuery("OrderRecurrence.FetchByOrderId");
        query.setParameter("orderId", orderId);
        try {
            data = (OrderRecurrenceDO)query.getSingleResult();          
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<OrderRecurrenceDO> fetchActiveList() throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("OrderRecurrence.FetchActiveList"); 
        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }
    
    public OrderRecurrenceDO add(OrderRecurrenceDO data) throws Exception {
        OrderRecurrence entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new OrderRecurrence();
        entity.setOrderId(data.getOrderId());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setFrequency(data.getFrequency());
        entity.setUnitId(data.getUnitId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }
    
    public OrderRecurrenceDO update(OrderRecurrenceDO data) throws Exception {
        OrderRecurrence entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(OrderRecurrence.class, data.getId());
        entity.setOrderId(data.getOrderId());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setFrequency(data.getFrequency());
        entity.setUnitId(data.getUnitId());

        return data;
    }
    
    public void validate(OrderRecurrenceDO data) throws Exception {
        boolean validateFreq;
        ValidationErrorsList list;        
        
        list = new ValidationErrorsList();
        validateFreq = true;
                
        if (DataBaseUtil.isEmpty(data.getActiveBegin())) {
            list.add(new FieldErrorException("fieldRequiredException", OrderMeta.getRecurrenceActiveBegin()));
            validateFreq = false;
        }
        
        if (DataBaseUtil.isEmpty(data.getActiveEnd())) {
            list.add(new FieldErrorException("fieldRequiredException", OrderMeta.getRecurrenceActiveEnd()));
            validateFreq = false;
        }
        
        if (DataBaseUtil.isEmpty(data.getFrequency())) { 
            list.add(new FieldErrorException("fieldRequiredException", OrderMeta.getRecurrenceFrequency()));
            validateFreq = false;
        } else if (data.getFrequency() < 1) {
            list.add(new FieldErrorException("freqInvalidException", OrderMeta.getRecurrenceFrequency()));
            validateFreq = false;
        }
        
        if (DataBaseUtil.isEmpty(data.getUnitId())) { 
            list.add(new FieldErrorException("fieldRequiredException", OrderMeta.getRecurrenceUnitId()));
            validateFreq = false;
        }

        if (DataBaseUtil.isAfter(data.getActiveBegin(), data.getActiveEnd())) {
            list.add(new FieldErrorException("endDateAfterBeginDateException", OrderMeta.getRecurrenceActiveEnd()));
            validateFreq = false;
        }
        
        if (validateFreq && !frequencyValid(data)) 
            list.add(new FieldErrorException("notAllDatesValid", OrderMeta.getRecurrenceFrequency()));
        
        if (list.size() > 0)
            throw list;
    }
    
    public boolean isEmpty(OrderRecurrenceDO data) {
        if (data == null)
            return true;
        
        if (DataBaseUtil.isEmpty(data.getId()) && DataBaseUtil.isEmpty(data.getOrderId())
           && DataBaseUtil.isEmpty(data.getIsActive()) && DataBaseUtil.isEmpty(data.getActiveBegin())
           && DataBaseUtil.isEmpty(data.getActiveEnd()) && DataBaseUtil.isEmpty(data.getFrequency())
           && DataBaseUtil.isEmpty(data.getUnitId()))
           return true;
        
       return false; 
    }                                                                                                                                                                                              
                        
    private boolean frequencyValid(OrderRecurrenceDO data) {
        int bday, bmon, byr, nday, nmon, nyr, emon, eyr, nmons, dfyr, iter;
        Integer freq, unit;
        Datetime bdt, edt;
        
        freq = data.getFrequency();
        unit = data.getUnitId();
        bdt = data.getActiveBegin();
        edt = data.getActiveEnd();
        
        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth();
        byr = bdt.getDate().getYear();                                   
        emon = edt.getDate().getMonth();
        eyr = edt.getDate().getYear();
        
        dfyr = eyr-byr; 
        nyr = byr;
        nday = bday;                    
        nmon = bmon;        
        if (monthId.equals(unit)) {             
            if (dfyr > 0) 
                nmons = bmon + (dfyr-1)*11 + emon;
            else 
                nmons = emon - bmon;             
            iter = freq;                    
            while (iter < nmons) {                
                nmon += freq;
                if (nmon > 11) {
                    nmon %= 12;
                    nyr++;      
                }
                switch (nmon) {
                    case 1:       
                        if (nday > 29 || ((nyr % 4 != 0) && nday > 28))
                            return false;                                    
                        break;
                    case 3:
                    case 5:
                    case 8:
                    case 10:
                        if (nday > 30)
                            return false;
                        break;
                }
                
                iter += freq;
            }
        } else if (yearId.equals(unit)) {
            if (nmon != 1 || nday <= 28) {
                return true;
            } else {
                while (nyr < eyr) {
                    nyr += freq;
                    if (nyr % 4 != 0) 
                        return false;                    
                }
            }
        }
        
        return true;        
    }
}
