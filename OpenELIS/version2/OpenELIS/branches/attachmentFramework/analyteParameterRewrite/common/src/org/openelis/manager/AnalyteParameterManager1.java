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
import java.util.HashMap;

import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;

/**
 * This class encapsulates the list of analyte parameters for a test or qc.
 * Although the class provides some basic functions internally, it is designed
 * to interact with EJB methods to provide majority of the operations needed to
 * manage an order.
 */
public class AnalyteParameterManager1 implements Serializable {
    private static final long                   serialVersionUID = 1L;

    protected ArrayList<AnalyteParameterViewDO> parameters, removed;

    protected Integer                           referenceId, referenceTableId;

    protected String                            referenceName;

    protected int                               nextUID          = -1;

    transient public final AnalyteParameter     analyteParameter = new AnalyteParameter();
    transient private HashMap<String, AnalyteParameterViewDO> uidMap;

    /**
     * Initialize an empty analyte parameter manager
     */
    public AnalyteParameterManager1() {
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public String getReferenceName() {
        return referenceName;
    }

    /**
     * Returns the next negative Id for newly created and as yet uncommitted
     * parameters
     */
    public int getNextUID() {
        return --nextUID;
    }
    
    /**
     * Returns the parameter using its Uid.
     */
    public AnalyteParameterViewDO getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, AnalyteParameterViewDO>();
        
            if (parameters != null) {
                for (AnalyteParameterViewDO data : parameters) 
                    uidMap.put(Constants.uid().get(data), data);
            }
        }
        return uidMap.get(uid);
    }

    /**
     * Class to manage analyte parameter information
     */
    public class AnalyteParameter {
        /**
         * Returns the parameter at specified index.
         */
        public AnalyteParameterViewDO get(int i) {
            return parameters.get(i);
        }
        
        /**
         * Returns the index of the specified parameter.
         */
        public int getIndex(AnalyteParameterViewDO data) {
            return parameters.indexOf(data);
        }

        /**
         * Adds a parameter to the list
         */
        public AnalyteParameterViewDO add() {
            AnalyteParameterViewDO data;

            data = new AnalyteParameterViewDO();
            data.setId(getNextUID());
            if (parameters == null)
                parameters = new ArrayList<AnalyteParameterViewDO>();
            parameters.add(data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        public AnalyteParameterViewDO add(int i) {
            AnalyteParameterViewDO data;

            data = new AnalyteParameterViewDO();
            data.setId(getNextUID());
            if (parameters == null)
                parameters = new ArrayList<AnalyteParameterViewDO>();
            parameters.add(i, data);
            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Removes a parameter from the list
         */
        public void remove(int i) {
            AnalyteParameterViewDO data;

            data = parameters.remove(i);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        public void remove(AnalyteParameterViewDO data) {
            parameters.remove(data);
            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        /**
         * Returns the number of parameters associated with this record
         */
        public int count() {
            if (parameters != null)
                return parameters.size();
            return 0;
        }
        
        /**
         * adds an object to uid map
         */
        private void uidMapAdd(String uid, AnalyteParameterViewDO data) {
            if (uidMap != null)
                uidMap.put(uid, data);
        }
        
        /**
         * removes the object from uid map
         */
        private void uidMapRemove(String uid) {
            if (uidMap != null)
                uidMap.remove(uid);
        }

        /**
         * adds the parameter to the list of parameters that should be removed
         * from the database
         */
        private void dataObjectRemove(Integer id, AnalyteParameterViewDO data) {
            if (removed == null)
                removed = new ArrayList<AnalyteParameterViewDO>();
            if (id > 0)
                removed.add(data);
        }
    }
}