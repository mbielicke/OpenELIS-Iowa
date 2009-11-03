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

import org.openelis.domain.QcAnalyteDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.gwt.common.RPC;

public class QcAnalyteManager implements RPC {

    private static final long                        serialVersionUID = 1L;

    protected Integer                                qcId;
    protected ArrayList<QcAnalyteViewDO>             analytes, deleted;

    protected transient static QcAnalyteManagerProxy proxy;

    protected QcAnalyteManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static QcAnalyteManager getInstance() {
        return new QcAnalyteManager();
    }

    public int count() {
        if (analytes == null)
            return 0;
        return analytes().size();
    }

    public QcAnalyteViewDO getAnalyteAt(int i) {
        return analytes.get(i);
    }

    public void setAnalyteAt(QcAnalyteViewDO qcAnalyte, int i) {
        analytes().set(i, qcAnalyte);
    }

    public void addAnalyte(QcAnalyteViewDO qcAnalyte) {
        analytes().add(qcAnalyte);
    }

    public void addAnalyteAt(QcAnalyteViewDO qcAnalyte, int i) {
        analytes().add(i, qcAnalyte);
    }

    public void removeAnalyteAt(int i) {
        QcAnalyteViewDO tmp;

        if (analytes == null || i >= analytes().size())
            return;

        tmp = analytes.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<QcAnalyteViewDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static QcAnalyteManager fetchByQcId(Integer id) throws Exception {
        return proxy().fetchByQcId(id);
    }

    public QcAnalyteManager add() throws Exception {
        return proxy().add(this);
    }

    public QcAnalyteManager update() throws Exception {
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

    ArrayList<QcAnalyteViewDO> getAnalytes() {
        return analytes;
    }

    void setAnalytes(ArrayList<QcAnalyteViewDO> analytes) {
        this.analytes = analytes;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    QcAnalyteDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static QcAnalyteManagerProxy proxy() {
        if (proxy == null)
            proxy = new QcAnalyteManagerProxy();
        return proxy;
    }
    
    private ArrayList<QcAnalyteViewDO> analytes() {
        if (analytes == null)
            analytes = new ArrayList<QcAnalyteViewDO>();
        return analytes;
    }
}
