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

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.ShippingTrackingDO;

public class ShippingTrackingManager implements Serializable {

    private static final long                          serialVersionUID = 1L;

    protected Integer                                  shippingId;
    protected ArrayList<ShippingTrackingDO>            trackings, deleted;

    protected transient static ShippingTrackingManagerProxy proxy;

    protected ShippingTrackingManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static ShippingTrackingManager getInstance() {
        return new ShippingTrackingManager();
    }

    public ShippingTrackingDO getTrackingAt(int i) {
        return trackings.get(i);
    }

    public void setTrackingAt(ShippingTrackingDO tracking, int i) {
        if (trackings == null)
            trackings = new ArrayList<ShippingTrackingDO>();
        trackings.set(i, tracking);
    }

    public int addTracking() {
        if (trackings == null)
            trackings = new ArrayList<ShippingTrackingDO>();
        trackings.add(new ShippingTrackingDO());

        return count() - 1;
    }

    public int addTrackingAt(int i) {
        if (trackings == null)
            trackings = new ArrayList<ShippingTrackingDO>();
        trackings.add(i, new ShippingTrackingDO());

        return i;
    }

    public void removeTrackingAt(int i) {
        ShippingTrackingDO tmp;

        if (trackings == null || i >= trackings.size())
            return;

        tmp = trackings.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<ShippingTrackingDO>();
            deleted.add(tmp);
        }
    }

    public int count() {
        if (trackings == null)
            return 0;

        return trackings.size();
    }

    // service methods
    public static ShippingTrackingManager fetchByShippingId(Integer id) throws Exception {
        return proxy().fetchByShippingId(id);
    }

    public ShippingTrackingManager add() throws Exception {
        return proxy().add(this);
    }

    public ShippingTrackingManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getShippingId() {
        return shippingId;
    }

    void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    ArrayList<ShippingTrackingDO> getTrackings() {
        return trackings;
    }

    void setTrackings(ArrayList<ShippingTrackingDO> trackings) {
        this.trackings = trackings;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    ShippingTrackingDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ShippingTrackingManagerProxy proxy() {
        if (proxy == null)
            proxy = new ShippingTrackingManagerProxy();
        return proxy;
    }
}
