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

import java.util.ArrayList;

import org.openelis.domain.QcLotViewDO;
import org.openelis.gwt.common.RPC;

public class QcLotManager implements RPC {

    private static final long        serialVersionUID = 1L;

    protected Integer                qcId;
    protected ArrayList<QcLotViewDO> lots, deleted;

    protected transient static QcLotManagerProxy proxy;

    protected QcLotManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static QcLotManager getInstance() {
        return new QcLotManager();
    }

    public int count() {
        if (lots == null)
            return 0;
        return lots().size();
    }

    public QcLotViewDO getLotAt(int i) {
        return lots.get(i);
    }

    public void setLotAt(QcLotViewDO qcLot, int i) {
        lots().set(i, qcLot);
    }

    public void addLot(QcLotViewDO qcLot) {
        lots().add(qcLot);
    }

    public void addLotAt(QcLotViewDO qcLot, int i) {
        lots().add(i, qcLot);
    }

    public void removeLotAt(int i) {
        QcLotViewDO tmp;

        if (lots == null || i >= lots().size())
            return;

        tmp = lots.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<QcLotViewDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static QcLotManager fetchByQcId(Integer id) throws Exception {
        return proxy().fetchByQcId(id);
    }

    public QcLotManager add() throws Exception {
        return proxy().add(this);
    }

    public QcLotManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getQcId() {
        return qcId;
    }

    void setQcId(Integer id) {
        qcId = id;
    }

    ArrayList<QcLotViewDO> getLots() {
        return lots;
    }

    void setLots(ArrayList<QcLotViewDO> lots) {
        this.lots = lots;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    QcLotViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static QcLotManagerProxy proxy() {
        if (proxy == null)
            proxy = new QcLotManagerProxy();
        return proxy;
    }
    
    private ArrayList<QcLotViewDO> lots() {
        if (lots == null)
            lots = new ArrayList<QcLotViewDO>();
        return lots;
    }
}
