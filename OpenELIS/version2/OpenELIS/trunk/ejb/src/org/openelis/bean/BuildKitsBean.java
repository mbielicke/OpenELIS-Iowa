/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.BuildKitComponentDO;
import org.openelis.domain.BuildKitDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.remote.BuildKitsRemote;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
})
@SecurityDomain("openelis")
@RolesAllowed("buildkits-select")
public class BuildKitsBean implements BuildKitsRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    @PostConstruct
    private void init() 
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }

    private static final InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    
    @RolesAllowed("buildkits-update")
    public Integer updateBuildKits(BuildKitDO buildKitDO, List<BuildKitComponentDO> components) throws Exception {
        validateForAdd(buildKitDO, components);
        return null;
    }

    public void validateForAdd(BuildKitDO buildKitDO, List<BuildKitComponentDO> components) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();
        validateKit(buildKitDO, list);
        boolean setNumRequestedError = false;
        for(int i=0; i<components.size(); i++){
            if(validateKitComponent(components.get(i), i, list))
                setNumRequestedError = true;
        }
        
        if(setNumRequestedError)
            list.add(new FieldErrorException("numRequestedIsToHigh", "numRequested"));
        
        if(list.size() > 0)
            throw list;
    }
    
    private void validateKit(BuildKitDO buildKitDO, ValidationErrorsList list) {
        //name required 
        if(buildKitDO.getKitId() == null){
            list.add(new FieldErrorException("fieldRequiredException",InventoryItemMeta.getName()));
        }
          
        //num requested required
        if(buildKitDO.getNumberRequested() == null || buildKitDO.getNumberRequested().compareTo(new Integer(0)) <= 0){
            list.add(new FieldErrorException("fieldRequiredException","numRequested"));
        }
          
        //kit location required
        if(buildKitDO.getLocationId() == null){
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()));
        }
    }
    
    //returns true if we need to set an error on number requested
    private boolean validateKitComponent(BuildKitComponentDO componentDO, int rowIndex, ValidationErrorsList list) {
        boolean setNumRequestedError = false;
        //componentname required
        if(componentDO.getComponent() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.getComponentId()));
        }
        
        //location required
        if(componentDO.getLocationId() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.INVENTORY_COMPONENT_ITEM.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()));
        }
        
        //unit required
        if(componentDO.getUnit() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.getQuantity()));
        }
        
        //total required
        if(componentDO.getTotal() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, "total"));
        }
        
        //on hand required
        if(componentDO.getQtyOnHand() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.INVENTORY_COMPONENT_ITEM.INVENTORY_LOCATION.getQuantityOnhand()));
        }
        
        //total cant be more than qty on hand
        if(componentDO.getTotal() != null && componentDO.getQtyOnHand() != null && componentDO.getTotal().compareTo(componentDO.getQtyOnHand()) > 0){
            list.add(new TableFieldErrorException("totalIsGreaterThanOnHandException", rowIndex, "total"));
            setNumRequestedError = true;
        }
        
        //TODO need a lot number check
        
        //TODO need an expiration date check
        
        return setNumRequestedError;
    }

}
