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

import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.NotFoundException;

public class SectionManager implements Serializable {

    private static final long                      serialVersionUID = 1L;

    protected SectionViewDO                        section;
    protected SectionParameterManager              parameters;

    protected transient static SectionManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected SectionManager() {
        section = null;
        parameters = null;
    }

    /**
     * Creates a new instance of this object. A default Section object is also
     * created.
     */
    public static SectionManager getInstance() {
        SectionManager manager;

        manager = new SectionManager();
        manager.section = new SectionViewDO();

        return manager;
    }

    public SectionViewDO getSection() {
        return section;
    }

    public void setSection(SectionViewDO section) {
        this.section = section;
    }

    // service methods
    public static SectionManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static SectionManager fetchWithParameters(Integer id) throws Exception {
        return proxy().fetchWithParameters(id);
    }

    public SectionManager add() throws Exception {
        return proxy().add(this);
    }

    public SectionManager update() throws Exception {
        return proxy().update(this);
    }

    public SectionManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(section.getId());
    }

    public SectionManager abortUpdate() throws Exception {
        return proxy().abortUpdate(section.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public SectionParameterManager getParameters() throws Exception {
        if (parameters == null) {
            if (section.getId() != null) {
                try {
                    parameters = SectionParameterManager.fetchBySectionId(section.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (parameters == null)
                parameters = SectionParameterManager.getInstance();
        }
        return parameters;
    }

    private static SectionManagerProxy proxy() {
        if (proxy == null)
            proxy = new SectionManagerProxy();

        return proxy;
    }
}