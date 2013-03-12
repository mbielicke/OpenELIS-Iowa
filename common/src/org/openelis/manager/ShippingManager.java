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

import org.openelis.domain.Constants;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.NotFoundException;

public class ShippingManager implements Serializable, HasNotesInt {

    private static final long serialVersionUID = 1L;

    protected ShippingViewDO          shipping;
    protected ShippingItemManager     items;
    protected ShippingTrackingManager trackings;
    protected NoteManager             shipNotes;
    
    protected transient static ShippingManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected ShippingManager() {
        shipping = null;
    }
    
    /**
     * Creates a new instance of this object. A default shipping object is
     * also created.
     */
    public static ShippingManager getInstance() {
        ShippingManager manager;
        
        manager = new ShippingManager();
        manager.shipping = new ShippingViewDO();
        
        return manager;
    }

    public ShippingViewDO getShipping() {
        return shipping;
    }

    public void setShipping(ShippingViewDO shipping) {
        this.shipping = shipping;
    }
    
    // service methods
    public static ShippingManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    public static ShippingManager fetchWithItemsAndTracking(Integer id) throws Exception {       
        return proxy().fetchWithItemsAndTrackings(id);
    } 
    
    public static ShippingManager fetchWithNotes(Integer id) throws Exception {       
        return proxy().fetchWithNotes(id);
    } 
    
    public ShippingManager add() throws Exception {
        return proxy().add(this);
    }
    
    public ShippingManager update() throws Exception {
        return proxy().update(this);
    }
    
    public ShippingManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(shipping.getId());
    }
    
    public ShippingManager abortUpdate() throws Exception {
        return proxy().abortUpdate(shipping.getId());
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    //
    // other managers
    //
    public ShippingItemManager getItems() throws Exception {
        if (items == null) {
            if (shipping.getId() != null) {
                try {
                    items = ShippingItemManager.fetchByShippingId(shipping.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (items == null)
                items = ShippingItemManager.getInstance();
        }
        return items;
    }
    
    public ShippingTrackingManager getTrackings() throws Exception {
        if (trackings == null) {
            if (shipping.getId() != null) {
                try {
                    trackings = ShippingTrackingManager.fetchByShippingId(shipping.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (trackings == null)
                trackings = ShippingTrackingManager.getInstance();
        }
        return trackings;
    }
    
    public NoteManager getNotes() throws Exception {
        if (shipNotes == null) {
            if (shipping.getId() != null) {
                try {
                    shipNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().SHIPPING, shipping.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (shipNotes == null){
                shipNotes = NoteManager.getInstance();
                shipNotes.setIsExternal(false);
            }
        }
        return shipNotes;
    }
    
    private static ShippingManagerProxy proxy() {
        if (proxy == null)
            proxy = new ShippingManagerProxy();

        return proxy;
    }

}
 