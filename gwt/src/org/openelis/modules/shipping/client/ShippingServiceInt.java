package org.openelis.modules.shipping.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("shipping")
public interface ShippingServiceInt extends XsrfProtectedService {

    ShippingManager fetchById(Integer id) throws Exception;

    ShippingViewDO fetchByOrderId(Integer id) throws Exception;

    ShippingManager fetchWithItemsAndTrackings(Integer id) throws Exception;

    ShippingManager fetchWithNotes(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ShippingManager add(ShippingManager man) throws Exception;

    ShippingManager update(ShippingManager man) throws Exception;

    ShippingManager fetchForUpdate(Integer id) throws Exception;

    ShippingManager abortUpdate(Integer id) throws Exception;

    ShippingItemManager fetchItemByShippingId(Integer id) throws Exception;

    ShippingTrackingManager fetchTrackingByShippingId(Integer id) throws Exception;

}