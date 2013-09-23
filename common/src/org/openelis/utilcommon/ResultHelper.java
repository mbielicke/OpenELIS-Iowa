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
     * validation succeeds, otherwise throws an exception.
     */
    public static void formatValue(ResultViewDO data, String value, Integer unitId,
                                   ResultFormatter rf) throws Exception {
        FormattedValue fv;

        fv = rf.format(data.getResultGroup(), unitId, value);
        if (fv != null) {
            if (DataBaseUtil.isDifferent(data.getValue(), fv.getDisplay()))
                data.setValue(fv.getDisplay());            
            if (DataBaseUtil.isDifferent(data.getTestResultId(), fv.getId()))
                data.setTestResultId(fv.getId());
            if (DataBaseUtil.isDifferent(data.getTypeId(), fv.getType()))
                data.setTypeId(fv.getType());
            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(data.getTypeId()))
                data.setDictionary(value);
        } else {
            throw new ParseException(Messages.get().gen_invalidValueException());
        }
    }

    /**
     * Validates and formats the value as per the aux data's field. Sets the
     * formatted value and type in the aux data if validation succeeds,
     * otherwise throws an exception.
     */
    public static void formatValue(AuxDataViewDO data, String value, ResultFormatter rf) throws Exception {
        FormattedValue fv;

        fv = rf.format(data.getAuxFieldId(), null, value);
        if (fv != null) {
            if (DataBaseUtil.isDifferent(data.getValue(), fv.getDisplay()))
                data.setValue(fv.getDisplay());
            if (DataBaseUtil.isDifferent(data.getTypeId(), fv.getType()))
                data.setTypeId(fv.getType());
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                data.setDictionary(value);
        } else {
            throw new ParseException(Messages.get().gen_invalidValueException());
        }
    }
}