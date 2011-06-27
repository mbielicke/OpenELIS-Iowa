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

import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class SectionManagerProxy {

    public SectionManager fetchById(Integer id) throws Exception {
        SectionViewDO data;
        SectionManager m;

        data = EJBFactory.getSection().fetchById(id);
        m = SectionManager.getInstance();

        m.setSection(data);

        return m;
    }

    public SectionManager fetchWithParameters(Integer id) throws Exception {
        SectionManager m;

        m = fetchById(id);
        m.getParameters();

        return m;
    }

    public SectionManager add(SectionManager man) throws Exception {
        Integer id;

        EJBFactory.getSection().add(man.getSection());
        id = man.getSection().getId();

        if (man.parameters != null) {
            man.getParameters().setSectionId(id);
            man.getParameters().add();
        }

        return man;
    }

    public SectionManager update(SectionManager man) throws Exception {
        Integer id;

        EJBFactory.getSection().update(man.getSection());
        id = man.getSection().getId();

        if (man.parameters != null) {
            man.getParameters().setSectionId(id);
            man.getParameters().update();
        }

        return man;
    }

    public SectionManager fetchForUpdate(SectionManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public SectionManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(SectionManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            EJBFactory.getSection().validate(man.getSection());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.parameters != null)
                man.getParameters().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }
}