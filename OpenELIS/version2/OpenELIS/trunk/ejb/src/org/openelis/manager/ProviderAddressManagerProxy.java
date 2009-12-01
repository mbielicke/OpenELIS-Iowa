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

import org.openelis.domain.ProviderAddressDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ProviderAddressLocal;
import org.openelis.manager.ProviderAddressManager;
import org.openelis.utilcommon.DataBaseUtil;

public class ProviderAddressManagerProxy {

    public ProviderAddressManager fetchByProviderId(Integer id) throws Exception {
        ProviderAddressManager pm;
        ArrayList<ProviderAddressDO> address;

        address = local().fetchByProviderId(id);
        pm = ProviderAddressManager.getInstance();
        pm.setProviderId(id);
        pm.setAddresses(address);

        return pm;
    }

    public ProviderAddressManager add(ProviderAddressManager man) throws Exception {
        ProviderAddressLocal pl;
        ProviderAddressDO address;

        pl = local();
        for (int i = 0; i < man.count(); i++ ) {
            address = man.getAddressAt(i);
            address.setProviderId(man.getProviderId());
            pl.add(address);
        }

        return man;
    }

    public ProviderAddressManager update(ProviderAddressManager man) throws Exception {
        ProviderAddressLocal pl;
        ProviderAddressDO address;

        pl = local();
        for (int j = 0; j < man.deleteCount(); j++)
            pl.delete(man.getDeletedAt(j));
        
        for (int i = 0; i < man.count(); i++ ) {
            address = man.getAddressAt(i);

            if (address.getId() == null) {
                address.setProviderId(man.getProviderId());
                pl.add(address);
            } else {
                pl.update(address);
            }
        }

        return man;
    }

    public void validate(ProviderAddressManager man) throws Exception {
        ValidationErrorsList list;
        ProviderAddressLocal cl;

        cl = local();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getAddressAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "providerAddressTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private ProviderAddressLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ProviderAddressLocal)ctx.lookup("openelis/ProviderAddressBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
