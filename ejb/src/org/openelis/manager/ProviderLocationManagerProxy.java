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

import org.openelis.bean.ProviderLocationBean;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ProviderLocationManagerProxy {

    public ProviderLocationManager fetchByProviderId(Integer id) throws Exception {
        ProviderLocationManager pm;
        ArrayList<ProviderLocationDO> location;

        location = EJBFactory.getProviderLocation().fetchByProviderId(id);
        pm = ProviderLocationManager.getInstance();
        pm.setProviderId(id);
        pm.setLocations(location);

        return pm;
    }

    public ProviderLocationManager add(ProviderLocationManager man) throws Exception {
        ProviderLocationBean pl;
        ProviderLocationDO location;

        pl = EJBFactory.getProviderLocation();
        for (int i = 0; i < man.count(); i++ ) {
            location = man.getLocationAt(i);
            location.setProviderId(man.getProviderId());
            pl.add(location);
        }

        return man;
    }

    public ProviderLocationManager update(ProviderLocationManager man) throws Exception {
        ProviderLocationBean pl;
        ProviderLocationDO location;

        pl = EJBFactory.getProviderLocation();
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
        ProviderLocationBean cl;

        cl = EJBFactory.getProviderLocation();
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
}
