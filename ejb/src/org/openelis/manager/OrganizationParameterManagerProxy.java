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

import org.openelis.bean.OrganizationParameterBean;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class OrganizationParameterManagerProxy {

    public OrganizationParameterManager fetchByOrganizationId(Integer id) throws Exception {
        OrganizationParameterManager cm;
        ArrayList<OrganizationParameterDO> parameters;

        parameters = EJBFactory.getOrganizationParameter().fetchByOrganizationId(id);
        cm = OrganizationParameterManager.getInstance();
        cm.setOrganizationId(id);
        cm.setParameters(parameters);

        return cm;
    }

    public OrganizationParameterManager add(OrganizationParameterManager man) throws Exception {
        OrganizationParameterBean cl;
        OrganizationParameterDO parameter;

        cl = EJBFactory.getOrganizationParameter();
        for (int i = 0; i < man.count(); i++ ) {
            parameter = man.getParameterAt(i);
            parameter.setOrganizationId(man.getOrganizationId());
            cl.add(parameter);
        }

        return man;
    }

    public OrganizationParameterManager update(OrganizationParameterManager man) throws Exception {
        OrganizationParameterBean cl;
        OrganizationParameterDO parameter;

        cl = EJBFactory.getOrganizationParameter();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            parameter = man.getParameterAt(i);

            if (parameter.getId() == null) {
                parameter.setOrganizationId(man.getOrganizationId());
                cl.add(parameter);
            } else {
                cl.update(parameter);
            }
        }

        return man;
    }
    
    public void validate(OrganizationParameterManager man) throws Exception {
        ValidationErrorsList list;
        OrganizationParameterBean cl;

        cl = EJBFactory.getOrganizationParameter();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getParameterAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "parameterTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
