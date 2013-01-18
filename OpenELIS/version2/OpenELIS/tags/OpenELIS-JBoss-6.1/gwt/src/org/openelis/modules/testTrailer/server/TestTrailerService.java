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
package org.openelis.modules.testTrailer.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.server.EJBFactory;

public class TestTrailerService {

    public TestTrailerDO fetchById(Integer id) throws Exception {
        return EJBFactory.getTestTrailer().fetchById(id);
    }

    public ArrayList<IdNameVO> fetchByName(String search) throws Exception {
        return EJBFactory.getTestTrailer().fetchByName(search + "%", 10);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getTestTrailer().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public TestTrailerDO add(TestTrailerDO data) throws Exception {
        return EJBFactory.getTestTrailer().add(data);
    }

    public TestTrailerDO update(TestTrailerDO data) throws Exception {
        return EJBFactory.getTestTrailer().update(data);
    }

    public TestTrailerDO fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getTestTrailer().fetchForUpdate(id);
    }

    public void delete(TestTrailerDO data) throws Exception {
        EJBFactory.getTestTrailer().delete(data);
    }

    public TestTrailerDO abortUpdate(Integer id) throws Exception {
        return EJBFactory.getTestTrailer().abortUpdate(id);
    }
}
