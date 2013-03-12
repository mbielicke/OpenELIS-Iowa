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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.entity.OrderRecurrence;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.OrderMeta;

@Stateless
@SecurityDomain("openelis")

public class OrderRecurrenceBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
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
        int bday, bmon, byr, nday, nmon, nyr, emon, eyr, nmons, iter;
        Integer freq, unit;
        Datetime bdt, edt;

        freq = data.getFrequency();
        unit = data.getUnitId();
        bdt = data.getActiveBegin();
        edt = data.getActiveEnd();

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth()+1;
        byr = bdt.getDate().getYear();
        emon = edt.getDate().getMonth()+1;
        eyr = edt.getDate().getYear();

        nyr = byr;
        nday = bday;
        nmon = bmon;
        if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit)) {
            /*
             * We calculate the number of months (nmons) between the one that
             * begin date is in and the one that end date is in, inclusive of
             * the latter.
             */
            nmons = (emon - bmon) + ((eyr - nyr) * 12);
            iter = freq;
            while (iter < nmons) {
                /*
                 * Here, "iter" is used to keep track of how close to end date's
                 * month we are, which is "nmons" months after begin date's
                 * month. We can't use "nmon", which is the month that the
                 * currently created date is in for this purpose, because its
                 * value is always betweeen 0 and 11, whereas "nmons" can be
                 * more than 11.
                 */
                nmon += freq;
                if (nmon > 11) {
                    /*
                     * we use 12 and not 11 to calculate the remainder because
                     * otherwise when "nmon" is 12 the remainder is 1 i.e. the
                     * 2nd month and not 0 or the 1st month
                     */
                    nmon %= 12;
                    nyr++ ;
                }
                /*
                 * we have to check to make sure than any month that we generate
                 * a date for has the number of days as specified in the begin
                 * date, otherwise the dates created won't conform to the
                 * frequency
                 */
                switch (nmon) {
                    case 2:
                        if (nday > 29 || ( (nyr % 4 != 0) && nday > 28)) {
                            return false;
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        if (nday > 30) {
                            return false;
                        }
                        break;
                }
                iter += freq;
            }
        } else if (Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS.equals(unit)) {
            if (nmon != 1 || nday <= 28)
                return true;
            while (nyr < eyr) {
                nyr += freq;
                if (nyr % 4 != 0) {
                    return false;
                }
            }
        }

        return true;       
    }
}
