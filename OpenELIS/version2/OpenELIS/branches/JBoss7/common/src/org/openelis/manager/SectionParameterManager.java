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

import org.openelis.domain.SectionParameterDO;

public class SectionParameterManager implements Serializable {

    private static final long                        serialVersionUID = 1L;

    protected Integer                                sectionId;
    protected ArrayList<SectionParameterDO>          parameters, deleted;

    protected transient static SectionParameterManagerProxy proxy;

    protected SectionParameterManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static SectionParameterManager getInstance() {
        return new SectionParameterManager();
    }

    public int count() {
        if (parameters == null)
            return 0;
        return parameters().size();
    }

    public SectionParameterDO getParameterAt(int i) {
        return parameters.get(i);
    }

    public void setParameterAt(SectionParameterDO parameter, int i) {
        parameters().set(i, parameter);
    }

    public void addParameter(SectionParameterDO parameter) {
        parameters().add(parameter);
    }

    public void addParameterAt(SectionParameterDO parameter, int i) {
        parameters().add(i, parameter);
    }

    public void removeParameterAt(int i) {
        SectionParameterDO tmp;

        if (parameters == null || i >= parameters().size())
            return;

        tmp = parameters.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<SectionParameterDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static SectionParameterManager fetchBySectionId(Integer id) throws Exception {
        return proxy().fetchBySectionId(id);
    }

    public SectionParameterManager add() throws Exception {
        return proxy().add(this);
    }

    public SectionParameterManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getSectionId() {
        return sectionId;
    }

    void setSectionId(Integer id) {
        sectionId = id;
    }

    ArrayList<SectionParameterDO> getParameters() {
        return parameters;
    }

    void setParameters(ArrayList<SectionParameterDO> parameters) {
        this.parameters = parameters;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    SectionParameterDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static SectionParameterManagerProxy proxy() {
        if (proxy == null)
            proxy = new SectionParameterManagerProxy();
        return proxy;
    }
    
    private ArrayList<SectionParameterDO> parameters() {
        if (parameters == null)
            parameters = new ArrayList<SectionParameterDO>();
        return parameters;
    }
}
