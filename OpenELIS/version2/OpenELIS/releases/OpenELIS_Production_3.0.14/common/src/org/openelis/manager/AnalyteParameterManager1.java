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
 * manage analyte parameters.
 */
public class AnalyteParameterManager1 implements Serializable {
    private static final long                     serialVersionUID = 1L;

    protected ArrayList<AnalyteCombo>             analyteCombos;
    protected ArrayList<AnalyteParameterViewDO>   removed;
    protected Integer                             referenceId, referenceTableId;
    protected String                              referenceName;
    protected int                                 nextUID          = -1;

    transient public final Analyte                analyte          = new Analyte();
    transient public final AnalyteParameter       analyteParameter = new AnalyteParameter();
    transient private HashMap<String, DataObject> uidMap;

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
     * parameters and analyte combos.
     */
    public int getNextUID() {
        return --nextUID;
    }

    /**
     * Returns the parameter using its Uid.
     */
    public DataObject getObject(String uid) {
        if (uidMap == null) {
            uidMap = new HashMap<String, DataObject>();

            if (analyteCombos != null)
                for (AnalyteCombo c : analyteCombos) {
                    uidMap.put(Constants.uid().getAnalyteParameter(c.id), c);
                    if (c.parameters != null)
                        for (AnalyteParameterViewDO data : c.parameters)
                            uidMap.put(Constants.uid().get(data), data);
                }
        }
        return uidMap.get(uid);
    }

    /**
     * Class to manage analyte combos
     */
    public class Analyte {
        /**
         * Returns the analyte combo at the passed index.
         */
        public AnalyteCombo get(int i) {
            return analyteCombos.get(i);
        }

        /**
         * Returns the analyte combo with the passed id (it's not the analyte id
         * of the combo)
         */
        public AnalyteCombo get(Integer analyteId) {
            return (AnalyteCombo)getObject(Constants.uid().getAnalyteParameter(analyteId));
        }

        /**
         * Adds an analyte combo to the list after the combo with the passed id
         * (it's not the analyte id of the combo)
         */
        public AnalyteCombo add(Integer analyteId) {
            int i, index;
            AnalyteCombo ac;

            /*
             * using this loop, the index of the combo with the passed id can be
             * found without looking it up in the hash using its id and then
             * calling indexOf() on the list
             */
            index = -1;
            for (i = 0; i < analyteCombos.size(); i++ ) {
                ac = analyteCombos.get(i);
                if (analyteId.equals(ac.id)) {
                    index = i;
                    break;
                }
            }

            ac = new AnalyteCombo(getNextUID(), null, null, null, null, null);
            analyteCombos.add(index + 1, ac);
            uidMapAdd(Constants.uid().getAnalyteParameter(ac.id), ac);
            return ac;
        }

        /**
         * Removes the combo with the passed id (it's not the analyte id of the
         * combo)
         */
        public void remove(Integer analyteId) {
            String uid;
            AnalyteCombo ac;

            uid = Constants.uid().getAnalyteParameter(analyteId);
            ac = (AnalyteCombo)getObject(uid);
            analyteCombos.remove(ac);

            uidMapRemove(uid);
        }

        /**
         * Returns the number of combinations associated with this record
         */
        public int count() {
            return analyteCombos != null ? analyteCombos.size() : 0;
        }
    }

    /**
     * Class to manage analyte parameters
     */
    public class AnalyteParameter {
        /**
         * Returns the parameter at specified index the combo with the passed id
         * (it's not the analyte id of the combo)
         */
        public AnalyteParameterViewDO get(Integer analyteId, int i) {
            AnalyteCombo ac;

            ac = (AnalyteCombo)getObject(Constants.uid().getAnalyteParameter(analyteId));
            return ac.getParameters().get(i);
        }

        /**
         * Adds a parameter to the combo with the passed id at the passed index
         * (it's not the analyte id of the combo)
         */
        public AnalyteParameterViewDO add(Integer analyteId, int i) {
            AnalyteParameterViewDO data;
            AnalyteCombo ac;

            ac = (AnalyteCombo)getObject(Constants.uid().getAnalyteParameter(analyteId));
            if (ac.parameters == null)
                ac.parameters = new ArrayList<AnalyteParameterViewDO>();

            data = new AnalyteParameterViewDO();
            data.setId(getNextUID());
            ac.parameters.add(i, data);

            uidMapAdd(Constants.uid().get(data), data);

            return data;
        }

        /**
         * Removes a parameter from the combo with the passed id (it's not the
         * analyte id of the combo)
         */
        public void remove(Integer analyteId, AnalyteParameterViewDO data) {
            AnalyteCombo ac;

            ac = (AnalyteCombo)getObject(Constants.uid().getAnalyteParameter(analyteId));
            ac.parameters.remove(data);

            dataObjectRemove(data.getId(), data);
            uidMapRemove(Constants.uid().get(data));
        }

        /**
         * Returns the number of parameters associated with the combination with
         * this id (it's not the analyte id of the combo)
         */
        public int count(Integer analyteId) {
            AnalyteCombo ac;

            ac = (AnalyteCombo)getObject(Constants.uid().getAnalyteParameter(analyteId));
            return ac.getParameters() != null ? ac.getParameters().size() : 0;
        }
    }

    /**
     * adds an object to uid map
     */
    private void uidMapAdd(String uid, DataObject data) {
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
     * adds the parameter to the list of parameters that should be removed from
     * the database
     */
    private void dataObjectRemove(Integer id, AnalyteParameterViewDO data) {
        if (removed == null)
            removed = new ArrayList<AnalyteParameterViewDO>();
        if (id > 0)
            removed.add(data);
    }

    /**
     * This class represent a unique combination of analyte, sample type and
     * unit and contains all parameters belonging to that combination. The class
     * makes it easy and straightforward to perfom operations such as adding new
     * parameters in the front-end and validating them in the back-end for date
     * ranges etc, because the data in the manager stays consistent with the way
     * it's displayed to the user.
     */
    public static class AnalyteCombo extends DataObject {
        private static final long                   serialVersionUID = 1L;

        protected Integer                           id, analyteId, typeOfSampleId, unitOfMeasureId;
        protected String                            analyteName;
        protected ArrayList<AnalyteParameterViewDO> parameters;

        public AnalyteCombo() {
        }

        public AnalyteCombo(Integer id, Integer analyteId, String analyteName,
                            Integer typeOfSampleId, Integer unitOfMeasureId,
                            ArrayList<AnalyteParameterViewDO> parameters) {
            this.id = id;
            this.analyteId = analyteId;
            this.analyteName = analyteName;
            this.typeOfSampleId = typeOfSampleId;
            this.unitOfMeasureId = unitOfMeasureId;
            this.parameters = parameters;
        }

        public Integer getId() {
            return id;
        }

        public Integer getAnalyteId() {
            return analyteId;
        }

        public void setAnalyteId(Integer analyteId) {
            this.analyteId = analyteId;
        }

        public String getAnalyteName() {
            return analyteName;
        }

        public void setAnalyteName(String analyteName) {
            this.analyteName = analyteName;
        }

        public Integer getTypeOfSampleId() {
            return typeOfSampleId;
        }

        public void setTypeOfSampleId(Integer typeOfSampleId) {
            this.typeOfSampleId = typeOfSampleId;
        }

        public Integer getUnitOfMeasureId() {
            return unitOfMeasureId;
        }

        public void setUnitOfMeasureId(Integer unitOfMeasureId) {
            this.unitOfMeasureId = unitOfMeasureId;
        }

        /**
         * Making these methods protected makes sure that the parameters of a
         * combination can't be accessed in the front-end without the manager
         */
        protected ArrayList<AnalyteParameterViewDO> getParameters() {
            return parameters;
        }

        protected void setParameters(ArrayList<AnalyteParameterViewDO> parameters) {
            this.parameters = parameters;
        }
    }
}