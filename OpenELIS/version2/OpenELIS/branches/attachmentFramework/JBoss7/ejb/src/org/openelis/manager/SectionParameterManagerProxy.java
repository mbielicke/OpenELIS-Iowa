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

import org.openelis.bean.SectionParameterBean;
import org.openelis.domain.SectionParameterDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class SectionParameterManagerProxy {

    public SectionParameterManager fetchBySectionId(Integer id) throws Exception {
        SectionParameterManager pm;
        ArrayList<SectionParameterDO> parameters;

        parameters = EJBFactory.getSectionParameter().fetchBySectionId(id);
        pm = SectionParameterManager.getInstance();
        pm.setSectionId(id);
        pm.setParameters(parameters);

        return pm;
    }

    public SectionParameterManager add(SectionParameterManager man) throws Exception {
        SectionParameterBean pl;
        SectionParameterDO data;

        pl = EJBFactory.getSectionParameter();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getParameterAt(i);
            data.setSectionId(man.getSectionId());
            pl.add(data);
        }

        return man;
    }

    public SectionParameterManager update(SectionParameterManager man) throws Exception {
        int i;
        SectionParameterBean pl;
        SectionParameterDO data;        

        pl = EJBFactory.getSectionParameter();
        for (i = 0; i < man.deleteCount(); i++ )
            pl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            data = man.getParameterAt(i);

            if (data.getId() == null) {
                data.setSectionId(man.getSectionId());
                pl.add(data);
            } else {
                pl.update(data);
            }
        }

        return man;
    }

    public void validate(SectionParameterManager man) throws Exception {
        ValidationErrorsList list;
        SectionParameterBean pl;

        pl = EJBFactory.getSectionParameter();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                pl.validate(man.getParameterAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "sectionParamTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
