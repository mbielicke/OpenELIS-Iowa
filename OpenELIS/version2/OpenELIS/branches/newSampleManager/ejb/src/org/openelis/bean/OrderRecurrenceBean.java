/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.entity.OrderRecurrence;
import org.openelis.meta.OrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class OrderRecurrenceBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

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

    public ArrayList<OrderRecurrenceDO> fetchByOrderIds(ArrayList<Integer> orderIds) {
        Query query;
        OrderRecurrenceDO data;

        query = manager.createNamedQuery("OrderRecurrence.FetchByOrderIds");
        query.setParameter("orderIds", orderIds);

        return DataBaseUtil.toArrayList(query.getResultList());
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

        if ( !data.isChanged())
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
            list.add(new FormErrorException(Messages.get()
                                                    .order_recurrenceActiveBeginRequiredException(DataBaseUtil.asString(data.getOrderId()))));
            validateFreq = false;
        }

        if (DataBaseUtil.isEmpty(data.getActiveEnd())) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_recurrenceActiveEndRequiredException(DataBaseUtil.asString(data.getOrderId()))));
            validateFreq = false;
        }

        if (DataBaseUtil.isEmpty(data.getFrequency())) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_recurrenceFrequencyRequiredException(DataBaseUtil.asString(data.getOrderId()))));
            validateFreq = false;
        } else if (data.getFrequency() < 1) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_freqInvalidException(DataBaseUtil.asString(data.getOrderId()))));
            validateFreq = false;
        }

        if (DataBaseUtil.isEmpty(data.getUnitId())) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_recurrenceUnitRequiredException(DataBaseUtil.asString(data.getOrderId()))));
            validateFreq = false;
        }

        if (DataBaseUtil.isAfter(data.getActiveBegin(), data.getActiveEnd())) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_endDateAfterBeginDateException(DataBaseUtil.asString(data.getOrderId()))));
            validateFreq = false;
        }

        if (validateFreq && !isFrequencyValid(data))
            list.add(new FormErrorException(Messages.get()
                                                    .order_notAllDatesValid(DataBaseUtil.asString(data.getOrderId()))));

        if (list.size() > 0)
            throw list;
    }

    public boolean isEmpty(OrderRecurrenceDO data) {
        if (data == null)
            return true;

        if (DataBaseUtil.isEmpty(data.getId()) && DataBaseUtil.isEmpty(data.getOrderId()) &&
            DataBaseUtil.isEmpty(data.getIsActive()) &&
            DataBaseUtil.isEmpty(data.getActiveBegin()) &&
            DataBaseUtil.isEmpty(data.getActiveEnd()) &&
            DataBaseUtil.isEmpty(data.getFrequency()) && DataBaseUtil.isEmpty(data.getUnitId()))
            return true;

        return false;
    }

    private boolean isFrequencyValid(OrderRecurrenceDO data) {
        int bday, bmon, byr, nday, nmon, nyr, eyr;
        Integer freq, unit;
        Datetime ndt, bdt, edt, now;
        Date nd;

        freq = data.getFrequency();
        unit = data.getUnitId();
        bdt = data.getActiveBegin();
        edt = data.getActiveEnd();
        now = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);

        bday = bdt.getDate().getDate();
        bmon = bdt.getDate().getMonth();
        byr = bdt.getDate().getYear();
        eyr = edt.getDate().getYear();

        nyr = byr;
        nday = bday;
        nmon = bmon;
        if (Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS.equals(unit)) {
            nd = new Date(nyr, nmon, nday);
            ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
            while ( !DataBaseUtil.isAfter(ndt, edt)) {
                if ( !now.after(ndt)) {
                    /*
                     * we have to check to make sure than any month that we
                     * generate a date for has the number of days as specified
                     * in the begin date, otherwise the dates created won't
                     * conform to the frequency
                     */
                    switch (nmon) {
                        case 1:
                            if (nday > 29 || ( (nyr % 4 != 0) && nday > 28)) 
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
                }
                nmon += freq;
                if (nmon > 11) {
                    /*
                     * we use 12 and not 11 to calculate the remainder because
                     * otherwise when "nmon" is 12 the remainder is 1 i.e. the
                     * 2nd month and not 0 or the 1st month
                     */
                    nyr += nmon / 12;
                    nmon %= 12;
                }

                nd.setDate(nday);
                nd.setMonth(nmon);
                nd.setYear(nyr);
                ndt = Datetime.getInstance(Datetime.YEAR, Datetime.DAY, nd);
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
