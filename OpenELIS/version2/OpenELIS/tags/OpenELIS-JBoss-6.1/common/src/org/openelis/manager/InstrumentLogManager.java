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

import org.openelis.domain.InstrumentLogDO;
import org.openelis.gwt.common.RPC;

public class InstrumentLogManager implements RPC {

    private static final long                            serialVersionUID = 1L;

    protected Integer                                    instrumentId;
    protected ArrayList<InstrumentLogDO>                 logs, deleted;

    protected transient static InstrumentLogManagerProxy proxy;

    public static InstrumentLogManager getInstance() {
        return new InstrumentLogManager();
    }

    public int count() {
        if (logs == null)
            return 0;

        return logs.size();
    }

    public InstrumentLogDO getLogAt(int i) {
        return logs.get(i);
    }

    public void setLogAt(InstrumentLogDO log, int i) {
        if (logs == null)
            logs = new ArrayList<InstrumentLogDO>();
        logs.set(i, log);
    }

    public void addLog(InstrumentLogDO log) {
        if (logs == null)
            logs = new ArrayList<InstrumentLogDO>();
        logs.add(log);
    }

    public void addLogAt(InstrumentLogDO log, int i) {
        if (logs == null)
            logs = new ArrayList<InstrumentLogDO>();
        logs.add(i, log);
    }

    public void removeLogAt(int i) {
        InstrumentLogDO tmp;

        if (logs == null || i >= logs.size())
            return;

        tmp = logs.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<InstrumentLogDO>();
            deleted.add(tmp);
        }
    }

    public static InstrumentLogManager fetchByInstrumentId(Integer id) throws Exception {
        return proxy().fetchByInstrumentId(id);
    }

    public InstrumentLogManager add() throws Exception {
        return proxy().add(this);
    }

    public InstrumentLogManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getInstrumentId() {
        return instrumentId;
    }

    void setInstrumentId(Integer instrumentId) {
        this.instrumentId = instrumentId;
    }
    
    ArrayList<InstrumentLogDO> getLogs() {
        return logs;
    }
    
    void setLogs(ArrayList<InstrumentLogDO> logs) {
        this.logs = logs;        
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    InstrumentLogDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static InstrumentLogManagerProxy proxy() {
        if (proxy == null)
            proxy = new InstrumentLogManagerProxy();
        return proxy;
    }

}
