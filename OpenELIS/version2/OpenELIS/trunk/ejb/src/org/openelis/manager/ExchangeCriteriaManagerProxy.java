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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.bean.EventLogBean;
import org.openelis.domain.EventLogDO;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ExchangeCriteriaManagerProxy {

    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        ExchangeCriteriaViewDO data;
        ExchangeCriteriaManager m;

        data = EJBFactory.getExchangeCriteria().fetchById(id);
        m = ExchangeCriteriaManager.getInstance();

        m.setExchangeCriteria(data);

        return m;
    }

    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        ExchangeCriteriaViewDO data;
        ExchangeCriteriaManager m;

        data = EJBFactory.getExchangeCriteria().fetchByName(name);
        m = ExchangeCriteriaManager.getInstance();

        m.setExchangeCriteria(data);

        return m;
    }

    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        ExchangeCriteriaManager m;

        m = fetchById(id);
        m.getProfiles();

        return m;
    }

    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        ExchangeCriteriaManager m;

        m = fetchByName(name);
        m.getProfiles();

        return m;
    }

    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        Integer id;

        EJBFactory.getExchangeCriteria().add(man.getExchangeCriteria());
        id = man.getExchangeCriteria().getId();

        if (man.profiles != null) {
            man.getProfiles().setExchangeCriteriaId(id);
            man.getProfiles().add();
        }

        return man;
    }

    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        Integer id;

        EJBFactory.getExchangeCriteria().update(man.getExchangeCriteria());
        id = man.getExchangeCriteria().getId();

        if (man.profiles != null) {
            man.getProfiles().setExchangeCriteriaId(id);
            man.getProfiles().update();
        }

        return man;
    }

    public void delete(ExchangeCriteriaManager man) throws Exception {
        ExchangeCriteriaViewDO data;
        EventLogBean el;
        ArrayList<EventLogDO> logs;

        man.getProfiles().delete();

        data = man.getExchangeCriteria();
        try {
            el = EJBFactory.getEventLog();
            logs = el.fetchByRefTableIdRefId(Constants.table().EXCHANGE_CRITERIA,
                                             data.getId());
            for (EventLogDO log : logs)
                el.delete(log);
        } catch (NotFoundException e) {
            // ignore
        }

        EJBFactory.getExchangeCriteria().delete(data);
    }

    public ExchangeCriteriaManager fetchForUpdate(ExchangeCriteriaManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(ExchangeCriteriaManager man) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        try {
            EJBFactory.getExchangeCriteria().validate(man.getExchangeCriteria());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.profiles != null)
                man.getProfiles().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        if (list.size() > 0)
            throw list;
    }
}