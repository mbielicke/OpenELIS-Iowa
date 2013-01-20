package org.openelis.modules.shipping.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ShippingServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<ShippingManager> callback);

    void add(ShippingManager man, AsyncCallback<ShippingManager> callback);

    void fetchById(Integer id, AsyncCallback<ShippingManager> callback);

    void fetchByOrderId(Integer id, AsyncCallback<ShippingViewDO> callback);

    void fetchForUpdate(Integer id, AsyncCallback<ShippingManager> callback);

    void fetchItemByShippingId(Integer id, AsyncCallback<ShippingItemManager> callback);

    void fetchTrackingByShippingId(Integer id, AsyncCallback<ShippingTrackingManager> callback);

    void fetchWithItemsAndTrackings(Integer id, AsyncCallback<ShippingManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<ShippingManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(ShippingManager man, AsyncCallback<ShippingManager> callback);

}
