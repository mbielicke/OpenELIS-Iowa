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

import javax.naming.InitialContext;

import org.openelis.domain.ProviderLocationDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ProviderLocationLocal;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.utilcommon.DataBaseUtil;

public class ProviderLocationManagerProxy {

    public ProviderLocationManager fetchByProviderId(Integer id) throws Exception {
        ProviderLocationManager pm;
        ArrayList<ProviderLocationDO> location;

        location = local().fetchByProviderId(id);
        pm = ProviderLocationManager.getInstance();
        pm.setProviderId(id);
        pm.setLocations(location);

        return pm;
    }

    public ProviderLocationManager add(ProviderLocationManager man) throws Exception {
        ProviderLocationLocal pl;
        ProviderLocationDO location;

        pl = local();
        for (int i = 0; i < man.count(); i++ ) {
            location = man.getLocationAt(i);
            location.setProviderId(man.getProviderId());
            pl.add(location);
        }

        return man;
    }

    public ProviderLocationManager update(ProviderLocationManager man) throws Exception {
        ProviderLocationLocal pl;
        ProviderLocationDO location;

        pl = local();
        for (int j = 0; j < man.deleteCount(); j++)
            pl.delete(man.getDeletedAt(j));
        
        for (int i = 0; i < man.count(); i++ ) {
            location = man.getLocationAt(i);

            if (location.getId() == null) {
                location.setProviderId(man.getProviderId());
                pl.add(location);
            } else {
                pl.update(location);
            }
        }

        return man;
    }

    public void validate(ProviderLocationManager man) throws Exception {
        ValidationErrorsList list;
        ProviderLocationLocal cl;

        cl = local();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getLocationAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "locationTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private ProviderLocationLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ProviderLocationLocal)ctx.lookup("openelis/ProviderLocationBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
