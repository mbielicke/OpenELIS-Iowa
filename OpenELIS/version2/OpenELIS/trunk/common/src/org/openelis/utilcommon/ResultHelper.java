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
package org.openelis.utilcommon;

import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.exception.ParseException;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;

/**
 * This class is used to provide various functionalities related to the results
 * or aux data e.g. formatting and validating the value
 */
public class ResultHelper {

    /**
     * Validates and formats the value as per the unit and the result's result
     * group. Sets the formatted value, type and test result in the result if
     * validation succeeds, otherwise throws an exception. Returns true if the
     * result was changed, false otherwise.
     */
    public static boolean formatValue(ResultViewDO data, String value, Integer unitId,
                                      ResultFormatter rf) throws Exception {
        boolean changed;
        FormattedValue fv;

        if (value == null) {
            /*
             * ResultFormatter's format() is not called for a null value because
             * in that case it returns null, which means an invalid value. That
             * would be erroneous behavior for the current method because it can
             * be called from the front-end when a user blanks a result's value.
             */
            if (data.getValue() != null) {
                data.setValue(null);
                data.setTypeId(null);
                data.setTestResultId(null);
                data.setDictionary(null);
                return true;
            }
            return false;
        }

        fv = rf.format(data.getResultGroup(), unitId, value);
        changed = false;
        if (fv != null) {
            if (DataBaseUtil.isDifferent(data.getValue(), fv.getDisplay())) {
                data.setValue(fv.getDisplay());
                changed = true;
            }
            if (DataBaseUtil.isDifferent(data.getTestResultId(), fv.getId())) {
                data.setTestResultId(fv.getId());
                changed = true;
            }
            if (DataBaseUtil.isDifferent(data.getTypeId(), fv.getType())) {
                data.setTypeId(fv.getType());
                changed = true;
            }
            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(data.getTypeId()) &&
                DataBaseUtil.isDifferent(data.getDictionary(), value)) {
                data.setDictionary(value);
                changed = true;
            }
        } else {
            throw new ParseException(Messages.get().gen_invalidValueException());
        }

        return changed;
    }

    /**
     * Validates and formats the value as per the aux data's field. Sets the
     * formatted value and type in the aux data if validation succeeds,
     * otherwise throws an exception. Returns true if the aux data was changed,
     * false otherwise.
     */
    public static boolean formatValue(AuxDataViewDO data, String value, ResultFormatter rf) throws Exception {
        boolean changed;
        FormattedValue fv;

        if (value == null) {
            /*
             * ResultFormatter's format() is not called for a null value because
             * in that case it returns null, which means an invalid value. That
             * would be erroneous behavior for the current method because it can
             * be called from the front-end when a user blanks an aux data's
             * value.
             */
            if (data.getValue() != null) {
                data.setValue(null);
                data.setTypeId(null);
                data.setDictionary(null);
                return true;
            }
            return false;
        }

        fv = rf.format(data.getAuxFieldId(), null, value);
        changed = false;
        if (fv != null) {
            if (DataBaseUtil.isDifferent(data.getValue(), fv.getDisplay())) {
                data.setValue(fv.getDisplay());
                changed = true;
            }
            if (DataBaseUtil.isDifferent(data.getTypeId(), fv.getType())) {
                data.setTypeId(fv.getType());
                changed = true;
            }
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()) &&
                DataBaseUtil.isDifferent(data.getDictionary(), value)) {
                data.setDictionary(value);
                changed = true;
            }
        } else {
            throw new ParseException(Messages.get().gen_invalidValueException());
        }

        return changed;
    }
}