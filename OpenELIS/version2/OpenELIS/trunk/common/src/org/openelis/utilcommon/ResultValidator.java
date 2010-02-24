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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;

/**
 * This class implements a validator for a result group or auxiliary data.
 */
public class ResultValidator implements RPC {
    private static final long serialVersionUID = 1L;

    public enum Type {
        DATE, DATE_TIME, TIME, DICTIONARY, NUMERIC, TITER, DEFAULT
    };

    HashMap<Integer, ArrayList<Item>>          units;
    HashMap<Integer, HashMap<String, Integer>> dictionary;
    HashMap<Integer, String>                   defaults;

    public ResultValidator() {
        units = new HashMap<Integer, ArrayList<Item>>();
        dictionary = new HashMap<Integer, HashMap<String, Integer>>();
        defaults = new HashMap<Integer, String>();
    }

    /**
     * This method adds a new validator for given unit and result range.
     * 
     * @param id
     *        is a unique id for given result range
     * @param unitId
     *        is the unit identifier that groups the result ranges
     * @param type
     *        is the enumerated type for result range
     * @param validRange
     *        the string representation of valid range
     * @throws Exception
     */
    public void addResult(Integer id, Integer unitId, Type type, String validRange)
                                                                                   throws Exception {
        Item item;
        ArrayList<Item> list;
        HashMap<String, Integer> dictUnit;

        if (unitId == null)
            unitId = 0;
        //
        // dictionary is special because we are going to add to the same list
        // rather than creating a new result range every time
        //
        if (type == Type.DICTIONARY) {
            dictUnit = dictionary.get(unitId);
            if (dictUnit == null) {
                dictUnit = new HashMap<String, Integer>();
                dictionary.put(unitId, dictUnit);
            }
            dictUnit.put(validRange, id);
            return;
        } else if (type == Type.DEFAULT) {
            //
            // default is also special, there is just one per unit id
            //
            defaults.put(unitId, validRange);
            return;
        }

        item = new Item();
        item.id = id;
        item.type = type;

        switch (type) {
            case NUMERIC:
                item.resultRange = new ResultRangeNumeric();
                item.resultRange.setRange(validRange);
                break;
            case TITER:
                item.resultRange = new ResultRangeTiter();
                item.resultRange.setRange(validRange);
                break;
            case DATE:
                item.resultRange = new ResultRangeDate();
                item.resultRange.setRange(validRange);
                break;
            case DATE_TIME:
                item.resultRange = new ResultRangeDateTime();
                item.resultRange.setRange(validRange);
                break;
            case TIME:
                item.resultRange = new ResultRangeTime();
                item.resultRange.setRange(validRange);
                break;
        }
        list = units.get(unitId);
        if (list == null) {
            list = new ArrayList<Item>();
            units.put(unitId, list);
        }
        list.add(item);
    }

    /**
     * This method is used to validate an input against the list of result
     * ranges group by unit. If the specified unit exists in the defined result
     * range, the passed value is validated against that unit list. Otherwise
     * the null unit is used to validate the value.
     * 
     * @param unitId
     *        is the unit identifier
     * @param value
     *        to validate
     * @throws ParseException
     */
    public Integer validate(Integer unitId, String value) throws ParseException {
        Integer id;
        ArrayList<Item> list;
        HashMap<String, Integer> dictUnit;

        id = null;

        if (unitId == null)
            unitId = 0;

        //
        // first check to see if we have unit specific validator; use
        // null units (0) for all the other units
        //
        list = units.get(unitId);
        dictUnit = dictionary.get(unitId);
        if (list == null && dictUnit == null) {
            list = units.get(0);
            dictUnit = dictionary.get(0);
        }

        if (dictUnit != null) {
            id = dictUnit.get(value);
            if (id != null)
                return id;
        }

        if (list != null) {
            for (Item item : list) {
                try {
                    item.resultRange.contains(value);
                    id = item.id;
                } catch (Exception e) {
                    // ignore it
                }
            }
        }

        if (id != null)
            return id;

        throw new ParseException("illegalResultValueException");
    }

    /**
     * This method returns a list of valid result ranges suitable for tooltip.
     */
    public ArrayList<String> getRanges(Integer unitId) {
        ArrayList<Item> list;
        ArrayList<String> ranges;
        HashMap<String, Integer> dictUnit;

        if (unitId == null)
            unitId = 0;

        list = units.get(unitId);
        dictUnit = dictionary.get(unitId);
        if (list == null && dictUnit == null)
            list = units.get(0);

        ranges = new ArrayList<String>();
        if (list != null)
            for (Item i : list)
                ranges.add(i.resultRange.toString());

        return ranges;
    }

    /**
     * This method returns a list of valid result ranges suitable for tooltip.
     */
    public ArrayList<String> getDictionaryRanges(Integer unitId) {
        ArrayList<Item> list;
        ArrayList<String> ranges;
        HashMap<String, Integer> dictUnit;

        if (unitId == null)
            unitId = 0;

        list = units.get(unitId);
        dictUnit = dictionary.get(unitId);
        if (list == null && dictUnit == null)
            dictUnit = dictionary.get(0);

        ranges = new ArrayList<String>();
        if (dictUnit != null)
            for (String i : dictUnit.keySet())
                ranges.add(i);

        return ranges;
    }

    public String getDefault(Integer unitId) {
        String returnValue;

        if (unitId == null)
            unitId = 0;

        returnValue = defaults.get(unitId);

        if (returnValue == null)
            returnValue = defaults.get(0);

        return returnValue;
    }

    public boolean noUnitsSpecified() {
        int unitsSize, dictUnitsSize, defaultsSize;
        boolean unitsNoUnits, dictNoUnits, defaultsNoUnits;
        unitsSize = units.size();
        dictUnitsSize = dictionary.size();
        defaultsSize = defaults.size();

        unitsNoUnits = ((unitsSize == 1 && units.containsKey(0)) || unitsSize == 0);
        dictNoUnits = ((dictUnitsSize == 1 && dictionary.containsKey(0)) || dictUnitsSize == 0);
        defaultsNoUnits = ((defaultsSize == 1 && defaults.containsKey(0)) || defaultsSize == 0);

        return unitsNoUnits && dictNoUnits && defaultsNoUnits;
    }

    static class Item implements RPC {
        private static final long serialVersionUID = 1L;

        Integer                   id;
        Type                      type;
        ResultRange               resultRange;
    }
}
