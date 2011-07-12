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
import java.util.Iterator;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;

/**
 * This class implements a validator for a result group or auxiliary data.
 */
public class ResultValidator implements RPC {
    private static final long                 serialVersionUID = 1L;

    private HashMap<Integer, ArrayList<Item>> units;
    private HashMap<Integer, String>          defaults;
    private boolean                           hasOnlyDictionary;

    public enum Type {
        DATE, DATE_TIME, TIME, DICTIONARY, NUMERIC, TITER, ALPHA_LOWER, ALPHA_MIXED, ALPHA_UPPER,
        DEFAULT
    };

    public enum RoundingMethod {
        EPA_METHOD, INTEGER
    };

    public ResultValidator() {
        units = new HashMap<Integer, ArrayList<Item>>();
        defaults = new HashMap<Integer, String>();
        hasOnlyDictionary = true;
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
    public void addResult(Integer id, Integer unitId, Type type, RoundingMethod roundingMethod,
                          Integer significantDigits, String value, String dictEntry) throws Exception {
        Item item;
        ArrayList<Item> list;

        if (unitId == null)
            unitId = 0;

        //
        // default is special, there is just one per unit id
        //
        if (type == Type.DEFAULT) {
            defaults.put(unitId, value);
            return;
        }

        item = new Item();
        item.id = id;
        item.type = type;
        item.roundingMethod = roundingMethod;
        item.significantDigits = significantDigits;

        switch (type) {
            case DICTIONARY:
                ResultRangeDictionary d;
                d = new ResultRangeDictionary();
                d.setRange(dictEntry);
                d.setId(new Integer(value));
                item.value = d;
                break;
            case NUMERIC:
                item.value = new ResultRangeNumeric();
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case TITER:
                item.value = new ResultRangeTiter();
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case DATE:
                item.value = new ResultRangeDate();
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case DATE_TIME:
                item.value = new ResultRangeDateTime();
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case TIME:
                item.value = new ResultRangeTime();
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case ALPHA_LOWER:
                item.value = new ResultRangeAlpha(ResultRangeAlpha.Type.LOWER);
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case ALPHA_MIXED:
                item.value = new ResultRangeAlpha(ResultRangeAlpha.Type.MIXED);
                item.value.setRange(value);
                hasOnlyDictionary = false;
                break;
            case ALPHA_UPPER:
                item.value = new ResultRangeAlpha(ResultRangeAlpha.Type.UPPER);
                item.value.setRange(value);
                hasOnlyDictionary = false;
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

        id = null;
        list = getUnits(unitId);

        // match the first range
        if (list != null) {
            for (Item item : list) {
                try {
                    item.value.contains(value);
                    id = item.id;
                    break;
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
     * Formats the result based on rounding and significant digits
     */
    public String getValue(Integer unitId, Integer testResultId, String value) throws NumberFormatException {
        ArrayList<Item> list;
        String compOp;

        if (value == null)
            return value;

        list = getUnits(unitId);

        // match the first range
        if (list != null) {
            for (Item item : list) {
                if (item.id.equals(testResultId)) {
                    switch (item.type) {
                        case DICTIONARY:
                            return ((ResultRangeDictionary)item.value).getId().toString();
                        case ALPHA_UPPER:
                            return value.toUpperCase();
                        case ALPHA_LOWER:
                            return value.toLowerCase();
                        case NUMERIC:
                            compOp = null;
                            if (value.length() > 1 && (value.startsWith(">") || value.startsWith("<"))) {
                                compOp = value.substring(0, 1);
                                value = value.substring(1);
                            }
                            if (item.roundingMethod != null) {
                                switch (item.roundingMethod) {
                                    case EPA_METHOD:
                                        if (item.significantDigits != null)
                                            value = SignificantFigures.format(value,
                                                                              item.significantDigits);
                                        break;
                                    case INTEGER:
                                        value = String.valueOf(Math.round(Double.valueOf(value)));
                                        break;
                                }
                            }
                            if (compOp != null)
                                value = compOp + value;
                            return value;
                        default:
                            return value;
                    }
                }
            }
        }

        return value;
    }

    /**
     * This method returns a list of valid result ranges suitable for tooltip.
     */
    public ArrayList<OptionItem> getRanges(Integer unitId) {
        ArrayList<Item> list;
        ArrayList<OptionItem> opt;

        list = getUnits(unitId);
        opt = new ArrayList<OptionItem>();
        if (list != null) {
            for (Item item : list) {
                if (item.type == Type.DICTIONARY)
                    opt.add(new OptionItem("option" + item.type,
                                           item.value.toString(),
                                           ((ResultRangeDictionary)item.value).getId()));
                else
                    opt.add(new OptionItem("option" + item.type, item.value.toString(), null));
            }
        }

        return opt;
    }
    
    /**
     * This method returns a list of all dictionary result ranges suitable for tooltip 
     */
    public ArrayList<OptionItem> getDictionaryRanges() {
        ArrayList<Item> list;
        ArrayList<OptionItem> opt;
        Iterator<Integer> iter;
        Integer unitId;        
        
        opt = new ArrayList<OptionItem>();                
        iter = units.keySet().iterator();
        
        while (iter.hasNext()) {
            unitId = iter.next();            
            list = units.get(unitId);            
            for (Item item : list) {
                if (item.type == Type.DICTIONARY)
                    opt.add(new OptionItem("option" + item.type,
                                           item.value.toString(),
                                           ((ResultRangeDictionary)item.value).getId()));                
            }
        }                
        return opt;
    }

    /**
     * Returns the "default" for specified unit.
     */
    public String getDefault(Integer unitId) {
        if (unitId == null || !defaults.containsKey(unitId))
            return defaults.get(0);
        else
            return defaults.get(unitId);
    }

    /**
     * Returns true if no unit was specified for the result group, false otherwise
     */
    public boolean noUnitsSpecified() {
        int unitsSize, defaultsSize;

        unitsSize = units.size();
        defaultsSize = defaults.size();

        return (unitsSize == 0 || (unitsSize == 1 && units.containsKey(0))) &&
               (defaultsSize == 0 || (defaultsSize == 1 && defaults.containsKey(0)));
    }

    /**
     * Returns true if all the results for this result group are of type dictionary, false otherwise
     */
    public boolean hasOnlyDictionary() {
        return hasOnlyDictionary;
    }
    
    private ArrayList<Item> getUnits(Integer unitId) {
        ArrayList<Item> list;
        //
        // first check to see if we have unit specific validator; use
        // null units (0) for all the other units
        //
        list = units.get(unitId == null ? 0 : unitId);
        if (list == null)
            list = units.get(0);

        return list;
    }

    /**
     * Class to hold all our result validator objects
     */
    static class Item implements RPC {
        private static final long serialVersionUID = 1L;

        Integer                   id, significantDigits;
        Type                      type;
        RoundingMethod            roundingMethod;
        ResultRange               value;
    }

    /**
     * Simple class to carry suggestions and dropdown options for dictionary
     * entries.
     */
    public class OptionItem {
        private Integer id;
        private String  property, value;

        OptionItem(String property, String value, Integer id) {
            this.property = property;
            this.value = value;
            this.id = id;
        }

        public String getProperty() {
            return property;
        }

        public Integer getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }
}