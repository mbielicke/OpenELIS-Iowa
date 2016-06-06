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
package org.openelis.modules.order1.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IOrderReturnVO;
import org.openelis.manager.IOrderManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * ScreenServiceInt is a GWT RemoteService interface for the Screen Widget. GWT
 * RemoteServiceServlets that want to provide server side logic for Screens must
 * implement this interface.
 */
@RemoteServiceRelativePath("order1")
public interface OrderServiceInt1 extends XsrfProtectedService {

    public IOrderManager1 getInstance(String type) throws Exception;

    public IOrderManager1 fetchById(Integer orderId, IOrderManager1.Load... elements) throws Exception;

    public ArrayList<IOrderManager1> fetchByIds(ArrayList<Integer> orderIds,
                                               IOrderManager1.Load... elements) throws Exception;

    public ArrayList<IOrderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                 IOrderManager1.Load... elements) throws Exception;

    public IOrderManager1 fetchWith(IOrderManager1 om, IOrderManager1.Load... elements) throws Exception;

    public ArrayList<IdNameVO> query(Query query) throws Exception;

    public IOrderManager1 fetchForUpdate(Integer orderId) throws Exception;

    public IOrderManager1 fetchForUpdate(Integer orderId, IOrderManager1.Load... elements) throws Exception;

    public ArrayList<IOrderManager1> fetchForUpdate(ArrayList<Integer> orderIds,
                                                   IOrderManager1.Load... elements) throws Exception;

    public IOrderManager1 unlock(Integer orderId, IOrderManager1.Load... elements) throws Exception;

    public ArrayList<IOrderManager1> unlock(ArrayList<Integer> orderIds,
                                           IOrderManager1.Load... elements) throws Exception;

    public IOrderReturnVO duplicate(Integer id) throws Exception;

    public IOrderManager1 update(IOrderManager1 om, boolean ignoreWarnings) throws Exception;

    public ArrayList<IdNameVO> fetchByDescription(String search, int max) throws Exception;

    public IOrderReturnVO addAuxGroups(IOrderManager1 om, ArrayList<Integer> groupIds) throws Exception;

    public IOrderManager1 removeAuxGroups(IOrderManager1 om, ArrayList<Integer> groupIds) throws Exception;

    public IOrderReturnVO addTest(IOrderManager1 om, Integer id, boolean isTest, Integer index) throws Exception;

    public IOrderManager1 removeTests(IOrderManager1 om, ArrayList<Integer> ids) throws Exception;

}