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

import java.util.ArrayList;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.gwt.common.RPC;

public class AnalyteParameterManager implements RPC {

    private static final long                               serialVersionUID = 1L;
    
    protected Integer                                       referenceId, referenceTableId;
    protected String                                        referenceName;
    protected ArrayList<AnalyteParameterViewDO>             parameters, deleted;        

    protected transient static AnalyteParameterManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected AnalyteParameterManager() {
    }
    
    /**
     * Creates a new instance of this object
     */
    public static AnalyteParameterManager getInstance() {
        return new AnalyteParameterManager();
    }
    
    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getReferenceName() {
        return referenceName;
    }
    
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    } 

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }       
    
    public static AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        return proxy().fetchActiveByReferenceIdReferenceTableId(referenceId, referenceTableId);
    }
    
    public AnalyteParameterManager add() throws Exception {
        return proxy().add(this);
    }
    
    public AnalyteParameterManager update() throws Exception {
        return proxy().update(this);
    }
    
    public AnalyteParameterManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(this);
    }
    
    public AnalyteParameterManager abortUpdate() throws Exception {
        return proxy().abortUpdate(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }   
    
    public AnalyteParameterViewDO getParameterAt(int i) {
        return parameters.get(i);
    } 
    
    public void setParameterAt(AnalyteParameterViewDO parameter, int i) {
        if (parameters == null )
            parameters = new ArrayList<AnalyteParameterViewDO>();
        parameters.set(i, parameter);
    }
    
    public int addParamater(AnalyteParameterViewDO parameter) {
        if (parameters == null )
            parameters = new ArrayList<AnalyteParameterViewDO>();
        parameters.add(parameter);
        
        return count() - 1;
    }
    
    public int addParamaterAt(int i) {
        if (parameters == null )
            parameters = new ArrayList<AnalyteParameterViewDO>();
        parameters.add(i, new AnalyteParameterViewDO());
        
        return count() - 1;
    }
    
    public int addParamaterAt(AnalyteParameterViewDO data, int i) {
        if (parameters == null )
            parameters = new ArrayList<AnalyteParameterViewDO>();
        parameters.add(i, data);
        
        return count() - 1;
    }
    
    public void removeParameterAt(int i) {
        AnalyteParameterViewDO tmp;
        
        if (parameters == null || i >= parameters.size()) 
            return;
        
        tmp = parameters.remove(i);
        if(tmp.getId() != null) {
            if(deleted == null)
                deleted = new ArrayList<AnalyteParameterViewDO>();
            deleted.add(tmp);
        }
    }
    
    public int count() {
        if (parameters == null)
            return 0;

        return parameters.size();
    }
    
    public int indexOf(AnalyteParameterViewDO data) {
        if (parameters == null)
            return -1;
        return parameters.indexOf(data);
    }
    
    ArrayList<AnalyteParameterViewDO> getParameters() {
        return parameters;
    }
    
    void setParameters(ArrayList<AnalyteParameterViewDO> parameters) {
        this.parameters = parameters;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    AnalyteParameterViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static AnalyteParameterManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalyteParameterManagerProxy();
        
        return proxy;
    }
}
