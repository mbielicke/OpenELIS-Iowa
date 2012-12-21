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

import org.openelis.bean.ProjectParameterBean;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class ProjectParameterManagerProxy {

    public ProjectParameterManager fetchByProjectId(Integer id) throws Exception {
        ProjectParameterManager pm;
        ArrayList<ProjectParameterDO> parameters;

        parameters = EJBFactory.getProjectParameter().fetchByProjectId(id);
        pm = ProjectParameterManager.getInstance();
        pm.setProjectId(id);
        pm.setParameters(parameters);

        return pm;
    }

    public ProjectParameterManager add(ProjectParameterManager man) throws Exception {
        ProjectParameterBean pm;
        ProjectParameterDO parameter;

        pm = EJBFactory.getProjectParameter();
        for (int i = 0; i < man.count(); i++ ) {
            parameter = man.getParameterAt(i);
            parameter.setProjectId(man.getProjectId());
            pm.add(parameter);
        }

        return man;
    }

    public ProjectParameterManager update(ProjectParameterManager man) throws Exception {
        ProjectParameterBean pm;
        ProjectParameterDO parameter;

        pm = EJBFactory.getProjectParameter();
        for (int j = 0; j < man.deleteCount(); j++ )
            pm.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            parameter = man.getParameterAt(i);

            if (parameter.getId() == null) {
                parameter.setProjectId(man.getProjectId());
                pm.add(parameter);
            } else {
                pm.update(parameter);
            }
        }

        return man;
    }
    
    public void validate(ProjectParameterManager man) throws Exception {
        ValidationErrorsList list;
        ProjectParameterBean pm;

        pm = EJBFactory.getProjectParameter();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                pm.validate(man.getParameterAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "parameterTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
