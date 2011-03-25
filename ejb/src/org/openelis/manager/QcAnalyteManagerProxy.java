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

import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.QcAnalyteLocal;
import org.openelis.utils.EJBFactory;

public class QcAnalyteManagerProxy {

    public QcAnalyteManager fetchByQcId(Integer id) throws Exception {
        QcAnalyteManager cm;
        ArrayList<QcAnalyteViewDO> analytes;

        analytes = EJBFactory.getQcAnalyte().fetchByQcId(id);
        cm = QcAnalyteManager.getInstance();
        cm.setQcId(id);
        cm.setAnalytes(analytes);

        return cm;
    }

    public QcAnalyteManager add(QcAnalyteManager man) throws Exception {
        QcAnalyteLocal cl;
        QcAnalyteViewDO analyte;

        cl = EJBFactory.getQcAnalyte();
        for (int i = 0; i < man.count(); i++ ) {
            analyte = man.getAnalyteAt(i);
            analyte.setQcId(man.getQcId());
            cl.add(analyte);
        }

        return man;
    }

    public QcAnalyteManager update(QcAnalyteManager man) throws Exception {
        QcAnalyteLocal cl;
        QcAnalyteViewDO analyte;

        cl = EJBFactory.getQcAnalyte();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            analyte = man.getAnalyteAt(i);

            if (analyte.getId() == null) {
                analyte.setQcId(man.getQcId());
                cl.add(analyte);
            } else {
                cl.update(analyte);
            }
        }

        return man;
    }

    public void validate(QcAnalyteManager man) throws Exception {
        ValidationErrorsList list;
        QcAnalyteLocal cl;

        cl = EJBFactory.getQcAnalyte();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getAnalyteAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "QcAnalyteTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
