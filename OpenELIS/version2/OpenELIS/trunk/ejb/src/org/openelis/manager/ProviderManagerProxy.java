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

import org.openelis.domain.Constants;
import org.openelis.bean.ProviderBean;
import org.openelis.domain.ProviderDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ProviderManagerProxy {

    public ProviderManager fetchById(Integer id) throws Exception {
        ProviderBean ol;
        ProviderDO data;
        ProviderManager m;

        ol = EJBFactory.getProvider();
        data = ol.fetchById(id);
        m = ProviderManager.getInstance();

        m.setProvider(data);

        return m;
    }

    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        ProviderManager m;

        m = fetchById(id);
        m.getLocations();

        return m;
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        ProviderManager m;

        m = fetchById(id);
        m.getNotes();

        return m;
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        Integer id;
        ProviderBean ol;

        ol = EJBFactory.getProvider();
        ol.add(man.getProvider());
        id = man.getProvider().getId();

        if (man.locations != null) {
            man.getLocations().setProviderId(id);
            man.getLocations().add();
        }
        if (man.notes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(Constants.table().PROVIDER);
            man.getNotes().add();
        }
        return man;
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        Integer id;
        ProviderBean ol;

        ol = EJBFactory.getProvider();
        ol.update(man.getProvider());
        id = man.getProvider().getId();

        if (man.locations != null) {
            man.getLocations().setProviderId(id);
            man.getLocations().update();
        }
        if (man.notes != null) {
            man.getNotes().setReferenceId(id);
            man.getNotes().setReferenceTableId(Constants.table().PROVIDER);
            man.getNotes().update();
        }
        return man;
    }

    public ProviderManager fetchForUpdate(ProviderManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(ProviderManager man) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        try {
            EJBFactory.getProvider().validate(man.getProvider());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.locations != null)
                man.getLocations().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.notes != null)
                man.getNotes().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        if (list.size() > 0)
            throw list;
    }
}