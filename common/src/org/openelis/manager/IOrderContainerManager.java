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

import org.openelis.domain.IOrderContainerDO;

public class IOrderContainerManager implements Serializable {

    private static final long                             serialVersionUID = 1L;

    protected Integer                                     iorderId;
    protected ArrayList<IOrderContainerDO>                containers, deleted;

    protected transient static IOrderContainerManagerProxy proxy;

    protected IOrderContainerManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static IOrderContainerManager getInstance() {
        return new IOrderContainerManager();
    }

    public IOrderContainerDO getContainerAt(int i) {
        return containers.get(i);
    }

    public void setContainerAt(IOrderContainerDO container, int i) {
        if (containers == null)
            containers = new ArrayList<IOrderContainerDO>();
        containers.set(i, container);
    }

    public int addContainer() {
        if (containers == null)
            containers = new ArrayList<IOrderContainerDO>();
        containers.add(new IOrderContainerDO());   
        
        return count() - 1;
    }
    
    public int addContainerAt(int i) {
        if (containers == null)
            containers = new ArrayList<IOrderContainerDO>();
        containers.add(i, new IOrderContainerDO()); 
        
        return count() - 1;
    }
    
    public int addContainer(IOrderContainerDO container) {
        if (containers == null)
            containers = new ArrayList<IOrderContainerDO>();
        containers.add(container);   
        
        return count() - 1;
    }
    
    public int addContainerAt(IOrderContainerDO container, int i) {
        if (containers == null)
            containers = new ArrayList<IOrderContainerDO>();
        containers.add(i, container);   
        
        return count() - 1;
    }

    public void removeContainerAt(int i) {
        IOrderContainerDO tmp;

        if (containers == null || i >= containers.size())
            return;

        tmp = containers.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<IOrderContainerDO>();
            deleted.add(tmp);
        }
    }
    
    public void moveContainer(int oldIndex, int newIndex) {
        IOrderContainerDO entry;

        if (containers == null || oldIndex == newIndex)
            return;

        entry = containers.remove(oldIndex);
        if (newIndex > oldIndex)
            newIndex-- ;

        if (newIndex >= count())
            addContainer(entry);
        else
            addContainerAt(entry, newIndex);
    }

    public int count() {
        if (containers == null)
            return 0;

        return containers.size();
    }

    // service methods
    public static IOrderContainerManager fetchByIorderId(Integer id) throws Exception {
        return proxy().fetchByIorderId(id);
    }

    public IOrderContainerManager add() throws Exception {
        return proxy().add(this);
    }

    public IOrderContainerManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getOrderId() {
        return iorderId;
    }

    void setOrderId(Integer id) {
        iorderId = id;
    }

    ArrayList<IOrderContainerDO> getContainers() {
        return containers;
    }

    void setContainers(ArrayList<IOrderContainerDO> containers) {
        this.containers = containers;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    IOrderContainerDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static IOrderContainerManagerProxy proxy() {
        if (proxy == null)
            proxy = new IOrderContainerManagerProxy();
        return proxy;
    }

}
