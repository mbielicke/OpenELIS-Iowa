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

import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;

public class IOrderTestAnalyteManager implements Serializable {

    private static final long                   serialVersionUID = 1L;

    protected Integer                           iorderTestId;
    protected ArrayList<IOrderTestAnalyteViewDO> analytes, deleted;

    protected transient static IOrderTestAnalyteManagerProxy proxy;

    protected IOrderTestAnalyteManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static IOrderTestAnalyteManager getInstance() {
        return new IOrderTestAnalyteManager();
    }

    public int count() {
        if (analytes == null)
            return 0;
        return analytes().size();
    }

    public IOrderTestAnalyteViewDO getAnalyteAt(int i) {
        return analytes.get(i);
    }

    public void setAnalyteAt(IOrderTestAnalyteViewDO orderTestAnalyte, int i) {
        analytes().set(i, orderTestAnalyte);
    }

    public void addAnalyte(IOrderTestAnalyteViewDO orderTestAnalyte) {
        analytes().add(orderTestAnalyte);
    }

    public void addAnalyteAt(IOrderTestAnalyteViewDO orderTestAnalyte, int i) {
        analytes().add(i, orderTestAnalyte);
    }

    public void removeAnalyteAt(int i) {
        if (analytes == null || i >= analytes().size())
            return;

        addToDeleted(analytes.remove(i));
    }
    
    // service methods
    public static IOrderTestAnalyteManager fetchByIorderTestId(Integer id) throws Exception {
        return proxy().fetchByIorderTestId(id);
    }
    
    public static IOrderTestAnalyteManager fetchMergedByIorderTestId(Integer id) throws Exception {
        return proxy().fetchMergedByIorderTestId(id);
    }
    
    public static IOrderTestAnalyteManager fetchByTestId(Integer id) throws Exception {
        return proxy().fetchByTestId(id);
    }
    
    public IOrderTestAnalyteManager add() throws Exception {
        return proxy().add(this);
    }

    public IOrderTestAnalyteManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate(IOrderTestViewDO test, int index) throws Exception {
        proxy().validate(this, test, index);
    }

    // friendly methods used by managers and proxies
    Integer getIorderTestId() {
        return iorderTestId;
    }

    void setIorderTestId(Integer id) {
        iorderTestId = id;
    }

    ArrayList<IOrderTestAnalyteViewDO> getAnalytes() {
        return analytes;
    }

    void setAnalytes(ArrayList<IOrderTestAnalyteViewDO> analytes) {
        this.analytes = analytes;
    }
    
    void mergeAnalytes(IOrderTestAnalyteManager man) {
        int i;
        
        if (man == null)
            return;
        
        for (i = 0; i < man.count(); i++)
            addToDeleted(man.getAnalyteAt(i));        
        
        for (i = 0; i < man.deleteCount(); i++)
            addToDeleted(man.getDeletedAt(i));        
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    IOrderTestAnalyteViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }        

    private static IOrderTestAnalyteManagerProxy proxy() {
        if (proxy == null)
            proxy = new IOrderTestAnalyteManagerProxy();
        return proxy;
    }
    
    private ArrayList<IOrderTestAnalyteViewDO> analytes() {
        if (analytes == null)
            analytes = new ArrayList<IOrderTestAnalyteViewDO>();
        return analytes;
    }
    
    private void addToDeleted(IOrderTestAnalyteViewDO tmp) {
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<IOrderTestAnalyteViewDO>();
            deleted.add(tmp);
        }
    }
}