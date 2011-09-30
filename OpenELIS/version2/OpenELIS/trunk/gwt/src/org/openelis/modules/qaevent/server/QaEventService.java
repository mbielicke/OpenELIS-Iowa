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
package org.openelis.modules.qaevent.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventVO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.QaEventRemote;

public class QaEventService {

    public QaEventViewDO fetchById(Integer id) throws Exception {
        return remote().fetchById(id);
    }

    public ArrayList<QaEventVO> fetchByTestId(Integer id) throws Exception {
        return remote().fetchByTestId(id);
    }

    public ArrayList<QaEventVO> fetchByCommon() throws Exception {
        return remote().fetchByCommon();
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public QaEventViewDO add(QaEventViewDO data) throws Exception {
        return remote().add(data);
    }

    public QaEventViewDO update(QaEventViewDO data) throws Exception {
        return remote().update(data);
    }

    public QaEventViewDO fetchForUpdate(Integer id) throws Exception {
        return remote().fetchForUpdate(id);
    }

    public QaEventViewDO abortUpdate(Integer id) throws Exception {
        return remote().abortUpdate(id);
    }

    private QaEventRemote remote() {
        return (QaEventRemote)EJBFactory.lookup("openelis/QaEventBean/remote");
    }
}
