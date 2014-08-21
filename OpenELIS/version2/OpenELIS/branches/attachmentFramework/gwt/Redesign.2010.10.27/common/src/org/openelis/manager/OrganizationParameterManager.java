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

import org.openelis.domain.OrganizationParameterDO;
import org.openelis.gwt.common.RPC;

public class OrganizationParameterManager implements RPC {

    private static final long                                  serialVersionUID = 1L;

    protected Integer                                          organizationId;
    protected ArrayList<OrganizationParameterDO>               parameters, deleted;

    protected transient static OrganizationParameterManagerProxy proxy;

    protected OrganizationParameterManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static OrganizationParameterManager getInstance() {
        return new OrganizationParameterManager();
    }

    public int count() {
        if (parameters == null)
            return 0;
        return parameters.size();
    }

    public OrganizationParameterDO getParameterAt(int i) {
        return parameters.get(i);
    }

    public void setParameterAt(OrganizationParameterDO parameter, int i) {
        if (parameters == null)
            parameters = new ArrayList<OrganizationParameterDO>();
        parameters.set(i, parameter);
    }

    public void addParameter(OrganizationParameterDO parameter) {
        if (parameters == null)
            parameters = new ArrayList<OrganizationParameterDO>();
        parameters.add(parameter);
    }

    public void addParameterAt(OrganizationParameterDO parameter, int i) {
        if (parameters == null)
            parameters = new ArrayList<OrganizationParameterDO>();
        parameters.add(i, parameter);
    }

    public void removeParameterAt(int i) {
        OrganizationParameterDO tmp;

        if (parameters == null || i >= parameters.size())
            return;

        tmp = parameters.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<OrganizationParameterDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static OrganizationParameterManager fetchByOrganizationId(Integer id) throws Exception {
        return proxy().fetchByOrganizationId(id);
    }

    public OrganizationParameterManager add() throws Exception {
        return proxy().add(this);
    }

    public OrganizationParameterManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getOrganizationId() {
        return organizationId;
    }

    void setOrganizationId(Integer id) {
        organizationId = id;
    }

    ArrayList<OrganizationParameterDO> getParameters() {
        return parameters;
    }

    void setParameters(ArrayList<OrganizationParameterDO> parameters) {
        this.parameters = parameters;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    OrganizationParameterDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static OrganizationParameterManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrganizationParameterManagerProxy();
        return proxy;
    }
}
