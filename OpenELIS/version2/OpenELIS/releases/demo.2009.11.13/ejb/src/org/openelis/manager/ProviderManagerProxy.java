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

import javax.naming.InitialContext;

import org.openelis.domain.ProviderDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ProviderLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class ProviderManagerProxy {

    public ProviderManager fetchById(Integer id) throws Exception {
        ProviderLocal ol;
        ProviderDO data;
        ProviderManager m;

        ol = local();
        data = ol.fetchById(id);
        m = ProviderManager.getInstance();

        m.setProvider(data);

        return m;
    }

    public ProviderManager fetchWithAddresses(Integer id) throws Exception {
        ProviderManager m;

        m = fetchById(id);
        m.getAddresses();

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
        ProviderLocal ol;

        ol = local();
        ol.add(man.getProvider());
        id = man.getProvider().getId();

        man.getAddresses().setProviderId(id);
        man.getAddresses().add();

        man.getNotes().setReferenceId(id);
        man.getNotes().setReferenceTableId(ReferenceTable.PROVIDER);
        man.getNotes().add();

        return man;
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        Integer id;
        ProviderLocal ol;

        ol = local();
        ol.update(man.getProvider());
        id = man.getProvider().getId();

        man.getAddresses().setProviderId(id);
        man.getAddresses().update();

        man.getNotes().setReferenceId(id);
        man.getNotes().setReferenceTableId(ReferenceTable.PROVIDER);
        man.getNotes().update();

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
            local().validate(man.getProvider());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            man.getAddresses().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            man.getNotes().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }

    private ProviderLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ProviderLocal)ctx.lookup("openelis/ProviderBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}