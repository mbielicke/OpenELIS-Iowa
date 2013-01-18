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

import org.openelis.domain.QcViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class QcManagerProxy {

    public QcManager fetchById(Integer id) throws Exception {
        QcViewDO data;
        QcManager m;

        data = EJBFactory.getQc().fetchById(id);
        m = QcManager.getInstance();

        m.setQc(data);

        return m;
    }

    public QcManager fetchWithAnalytes(Integer id) throws Exception {
        QcManager m;

        m = fetchById(id);
        m.getAnalytes();

        return m;
    }
    
    public QcManager fetchWithLots(Integer id) throws Exception {
        QcManager m;

        m = fetchById(id);
        m.getLots();

        return m;
    }

    public QcManager add(QcManager man) throws Exception {
        Integer id;

        EJBFactory.getQc().add(man.getQc());
        id = man.getQc().getId();

        if (man.analytes != null) {
            man.getAnalytes().setQcId(id);
            man.getAnalytes().add();
        }
        
        if (man.lots != null) {
            man.getLots().setQcId(id);
            man.getLots().add();
        }

        return man;
    }

    public QcManager update(QcManager man) throws Exception {
        Integer id;

        EJBFactory.getQc().update(man.getQc());
        id = man.getQc().getId();

        if (man.analytes != null) {
            man.getAnalytes().setQcId(id);
            man.getAnalytes().update();
        }
        
        if (man.lots != null) {
            man.getLots().setQcId(id);
            man.getLots().update();
        }

        return man;
    }

    @SuppressWarnings("unused")
    public QcManager fetchForUpdate(QcManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    @SuppressWarnings("unused")
    public QcManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(QcManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        try {
            EJBFactory.getQc().validate(man.getQc());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.analytes != null)
                man.getAnalytes().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.lots != null)
                man.getLots().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }
}