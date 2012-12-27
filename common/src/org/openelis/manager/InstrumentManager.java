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
package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.NotFoundException;


public class InstrumentManager implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected InstrumentViewDO instrument;
    protected InstrumentLogManager logs;
    
    protected transient static InstrumentManagerProxy proxy;
    
    protected InstrumentManager() {
        instrument = null;
        logs = null;
    }
    
    public static InstrumentManager getInstance() {
        InstrumentManager manager;
        
        manager = new InstrumentManager();
        manager.instrument = new InstrumentViewDO();
        
        return manager;
    }
    
    public InstrumentViewDO getInstrument() {
        return instrument;
    }
    
    public void setInstrument(InstrumentViewDO instrument) {
        this.instrument = instrument;
    }
    
    // service methods
    public static InstrumentManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    public static InstrumentManager fetchWithLogs(Integer id) throws Exception {
        return proxy().fetchWithLogs(id);
    }
    
    public InstrumentManager add() throws Exception {
        return proxy().add(this);
    }
    
    public InstrumentManager update() throws Exception {
        return proxy().update(this);
    }
    
    public InstrumentManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(instrument.getId());
    }
    
    public InstrumentManager abortUpdate() throws Exception {
        return proxy().abortUpdate(instrument.getId());
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    //
    // other managers
    //
    public InstrumentLogManager getLogs() throws Exception {
        if (logs == null) {
            if (instrument.getId() != null) {
                try {
                    logs = InstrumentLogManager.fetchByInstrumentId(instrument.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (logs == null)
                logs = InstrumentLogManager.getInstance();
        }
        return logs;
    }
    
    private static InstrumentManagerProxy proxy() {
        if (proxy == null)
            proxy = new InstrumentManagerProxy();
        
        return proxy;
    }

}
