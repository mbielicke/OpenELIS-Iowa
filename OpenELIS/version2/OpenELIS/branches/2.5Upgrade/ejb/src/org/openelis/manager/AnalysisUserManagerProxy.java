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

import org.openelis.bean.AnalysisUserBean;
import org.openelis.bean.DictionaryBean;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class AnalysisUserManagerProxy {

    public AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception {
        AnalysisUserViewDO data;
        ArrayList<AnalysisUserViewDO> list;
        AnalysisUserManager man;

        list = EJBFactory.getAnalysisUser().fetchByAnalysisId(analysisId);
        man = AnalysisUserManager.getInstance();

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            man.addAnalysisUser(data);
        }

        man.setAnalysisId(analysisId);

        return man;
    }

    public AnalysisUserManager add(AnalysisUserManager man) throws Exception {
        AnalysisUserViewDO data;
        AnalysisUserBean l;

        l = EJBFactory.getAnalysisUser();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getAnalysisUserAt(i);
            data.setAnalysisId(man.getAnalysisId());

            l.add(data);
        }

        return man;
    }

    public AnalysisUserManager update(AnalysisUserManager man) throws Exception {
        int i;
        AnalysisUserViewDO data;
        AnalysisUserBean l;

        l = EJBFactory.getAnalysisUser();
        for (i = 0; i < man.deleteCount(); i++ ) {
            l.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.count(); i++ ) {
            data = man.getAnalysisUserAt(i);

            if (data.getId() == null) {
                data.setAnalysisId(man.getAnalysisId());
                l.add(data);
            } else
                l.update(data);
        }

        return man;
    }

    public void validate(AnalysisUserManager man, ValidationErrorsList errorsList) throws Exception {
    }

    protected SystemUserVO getSystemUser() {
        try {
            return EJBFactory.getUserCache().getSystemUser();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
