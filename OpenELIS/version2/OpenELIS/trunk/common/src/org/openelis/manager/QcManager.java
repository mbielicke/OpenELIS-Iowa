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
import org.openelis.domain.QcViewDO;
import org.openelis.ui.common.NotFoundException;

public class QcManager implements Serializable {

    private static final long                 serialVersionUID = 1L;

    protected QcViewDO                        qc;
    protected QcAnalyteManager                analytes;
    protected QcLotManager                    lots;
    protected NoteManager                     internalNotes;

    protected transient static QcManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected QcManager() {
        qc = null;
        analytes = null;
        lots = null;
        internalNotes = null;
    }

    /**
     * Creates a new instance of this object. A default QC object is also
     * created.
     */
    public static QcManager getInstance() {
        QcManager manager;

        manager = new QcManager();
        manager.qc = new QcViewDO();

        return manager;
    }

    public QcViewDO getQc() {
        return qc;
    }

    public void setQc(QcViewDO qc) {
        this.qc = qc;
    }

    // service methods
    public static QcManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static QcManager fetchWithAnalytes(Integer id) throws Exception {
        return proxy().fetchWithAnalytes(id);
    }
    
    public static QcManager fetchWithLots(Integer id) throws Exception {
        return proxy().fetchWithLots(id);
    }
    
    public static QcManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public QcManager add() throws Exception {
        return proxy().add(this);
    }

    public QcManager update() throws Exception {
        return proxy().update(this);
    }

    public QcManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(qc.getId());
    }

    public QcManager abortUpdate() throws Exception {
        return proxy().abortUpdate(qc.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public QcAnalyteManager getAnalytes() throws Exception {
        if (analytes == null) {
            if (qc.getId() != null) {
                try {
                    analytes = QcAnalyteManager.fetchByQcId(qc.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (analytes == null)
                analytes = QcAnalyteManager.getInstance();
        }
        return analytes;
    }
    
    public QcLotManager getLots() throws Exception {
        if (lots == null) {
            if (qc.getId() != null) {
                try {
                    lots = QcLotManager.fetchByQcId(qc.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (lots == null)
                lots = QcLotManager.getInstance();
        }
        return lots;
    }
    
    public NoteManager getInternalNotes() throws Exception {
        if (internalNotes == null) {
            if (qc.getId() != null) {
                try {
                    internalNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().QC, qc.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (internalNotes == null) {
                internalNotes = NoteManager.getInstance();
                internalNotes.setIsExternal(false);
            }
        }

        return internalNotes;
    }

    private static QcManagerProxy proxy() {
        if (proxy == null)
            proxy = new QcManagerProxy();

        return proxy;
    }
}