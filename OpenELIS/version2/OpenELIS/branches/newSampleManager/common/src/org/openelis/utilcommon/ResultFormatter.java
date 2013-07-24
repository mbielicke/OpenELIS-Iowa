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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.exception.ParseException;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

public class ResultFormatter implements Serializable {
    private static final long                 serialVersionUID = 1L;

    protected ArrayList<Item>                 items;
    protected transient HashMap<String, Unit> map;

    /**
     * Type of rule for checking/formating
     */
    public enum Type {
        NUMERIC, DICTIONARY, DEFAULT, ALPHA_MIXED, ALPHA_UPPER, ALPHA_LOWER, TITER, DATE,
        DATE_TIME, TIME
    };

    /**
     * Type of rounding
     */
    public enum Rounding {
        SIG_FIG, SIG_FIG_NOE, INT, INT_SIG_FIG, INT_SIG_FIG_NOE
    };

    /**
     * Add a new formating/validation rule for given group and unit.
     * 
     * @param id
     *        specifies the rule/format id (test_result.id) to return when this
     *        rule in matched.
     * @param group
     *        specifies the is the rule/format group (test_result.result_group).
     * @param unitId
     *        specifies the unit of measure for this rule/format.
     * @param type
     *        is the type of rule/format
     * @param sigDigits
     *        number of significant digits for rounding
     * @param rounding
     *        specified the rounding format for this rule/format
     * @param value
     *        depending on type, this could be the min,max for number, titer,
     *        dictionary id, ...
     * @param entry
     *        for type dictionary, this is the text
     * @throws Exception
     */
    public void add(Integer id, Integer group, Integer unitId, Type type, Integer sigDigits,
                    Rounding rounding, String value, String entry) throws Exception {
        Item item;

        switch (type) {
            case NUMERIC:
                item = new NumericItem(value);
                ((NumericItem)item).rounding = rounding;
                ((NumericItem)item).sigDigits = sigDigits == null ? 0 : sigDigits.byteValue();
                break;
            case DICTIONARY:
                item = new DictionaryItem(value, entry);
                break;
            case TITER:
                item = new TiterItem(value);
                break;
            case DATE:
            case TIME:
            case DATE_TIME:
                item = new DateTimeItem();
                break;
            case DEFAULT:
                item = new DefaultItem(value);
                break;
            default:
                item = new Item();
                break;
        }

        item.type = type;
        item.id = (id == null) ? 0 : id;
        item.group = (group == null) ? 0 : group;
        item.unitId = (unitId == null) ? 0 : unitId;

        if (items == null)
            items = new ArrayList<ResultFormatter.Item>();
        items.add(item);

        map = null;
    }

    /**
     * This method is used to format/validate an input against the list of rules
     * in the specified group, by unit. If the specified unit exists in the
     * defined rules, the passed value is formated/validated against that unit's
     * rule list. Otherwise the general rules (null unit) are used to
     * format/validate the value.
     */
    public IdNameVO format(Integer group, Integer unitId, String value) {
        Unit u;
        IdNameVO pair;

        if (value == null || value.length() == 0 || value.trim().length() == 0)
            return null;

        /*
         * match the first range
         */
        u = getMap(group, unitId);
        if (u != null && u.items != null) {
            pair = new IdNameVO();
            for (Item item : u.items) {
                pair.setId(item.id);
                try {
                    switch (item.type) {
                        case NUMERIC:
                            pair.setName( ((NumericItem)item).format(value));
                            return pair;
                        case DICTIONARY:
                            pair.setName( ((DictionaryItem)item).format(value));
                            if (pair.getName() != null)
                                return pair;
                            break;
                        case ALPHA_MIXED:
                            pair.setName(value);
                            return pair;
                        case ALPHA_UPPER:
                            pair.setName(value.toUpperCase());
                            return pair;
                        case ALPHA_LOWER:
                            pair.setName(value.toLowerCase());
                            return pair;
                        case TITER:
                            pair.setName( ((NumericItem)item).format(value));
                            return pair;
                        case DATE:
                        case TIME:
                        case DATE_TIME:
                            pair.setName( ((DateTimeItem)item).format(value));
                            return pair;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return null;
    }

    /**
     * Returns the "default" for specified group and unit. If no unit default
     * has been defined, the general rules (null unit) default is returned if
     * any.
     */
    public String getDefault(Integer group, Integer unitId) {
        Unit u;

        u = getMap(group, unitId);
        if (u != null && u.def != null)
            return u.def.value;
        return null;
    }

    /**
     * 
     */
    public ArrayList<IdNameVO> getDictionaryValues(Integer group, Integer unitId) {
        Unit u;
        ArrayList<IdNameVO> l;

        l = null;
        u = getMap(group, unitId);
        if (u != null) {
            l = new ArrayList<IdNameVO>();
            for (Item item : u.items) {
                if (item.type == Type.DICTIONARY)
                    l.add(new IdNameVO( ((DictionaryItem)item).dictId, ((DictionaryItem)item).text));
            }
        }
        return l;
    }

    /**
     * Returns true if the specified group and unit can be used to format a
     * value
     */
    public boolean canFormat(Integer group, Integer unitId) {
        Unit u;

        u = getMap(group, unitId);
        return u != null;
    }

    /**
     * Returns true if all the results for this group and unit are of type
     * dictionary, false otherwise
     */
    public boolean hasAllDictionary(Integer group, Integer unitId) {
        Unit u;

        u = getMap(group, unitId);
        return (u != null) ? u.onlyDictionary : false;
    }

    /*
     * Builds a map using group and unitId
     */
    private void mapBuild() {
        Unit u;
        String uid;

        if (map == null && items != null) {
            map = new HashMap<String, Unit>();
            for (Item item : items) {
                uid = getUid(item.group, item.unitId);
                u = map.get(uid);
                if (u == null) {
                    u = new Unit();
                    u.items = new ArrayList<Item>();
                    map.put(uid, u);
                }
                if (item.type == Type.DEFAULT) {
                    u.def = (DefaultItem)item;
                } else {
                    u.items.add(item);
                    if (item.type != Type.DICTIONARY)
                        u.onlyDictionary = false;
                }
            }
        }
    }

    /*
     * Returns the map entry for given group and and unitId. If the specified
     * unitId does not exist, the method returns the map for unit 0 (null unit)
     * if any.
     */
    private Unit getMap(Integer group, Integer unitId) {
        Unit u;

        u = null;
        /*
         * prefer unit rules over general
         */
        if (group == null)
            group = 0;
        if (unitId == null)
            unitId = 0;

        mapBuild();
        if (map != null) {
            u = map.get(getUid(group, unitId));
            if (u == null && unitId != 0) {
                unitId = 0;
                u = map.get(getUid(group, unitId));
            }
        }

        return u;
    }
    
    /*
     * Returns a simple unique identifier for group, unit, and default
     */
    private String getUid(int group, int unitId) {
        return group + "-" + unitId;
    }

    /*
     * Base item for all the rules
     */
    static class Item implements Serializable {
        private static final long serialVersionUID = 1L;

        int                       id, group, unitId;
        Type                      type;
    }

    /*
     * Numeric Item.
     */
    static class NumericItem extends Item {
        private static final long serialVersionUID = 1L;

        Rounding                  rounding;
        byte                      sigDigits;
        double                    min, max;

        /**
         * Range is in format min,max such as 1.03,1.09. The min range is
         * inclusive while the max range is not.
         */
        public NumericItem(String range) throws ParseException {
            String st[];

            if (range == null)
                throw new ParseException(Messages.get().illegalNumericFormatException());
            st = range.split(",");
            if (st.length != 2)
                throw new ParseException(Messages.get().illegalNumericFormatException());

            try {
                min = Double.parseDouble(st[0]);
                max = Double.parseDouble(st[1]);
            } catch (NumberFormatException ex) {
                throw new ParseException(Messages.get().illegalNumericFormatException());
            }
            if (min >= max)
                throw new ParseException(Messages.get().illegalNumericRangeException());
        }

        /*
         * The format checks to see if the value is valid and in range and
         * applies rounding rules if any. Numbers can begin with '<' or '>' sign
         */
        public String format(String value) throws ParseException {
            double d;
            boolean err;
            String sign, fmt;

            if (value == null)
                return null;

            err = false;
            sign = null;
            fmt = null;
            if (value.startsWith(">") || value.startsWith("<")) {
                sign = value.substring(0, 1);
                value = value.substring(1);
            }
            try {
                d = Double.parseDouble(value);
                if (d < min || d >= max) {
                    err = true;
                } else {
                    switch (rounding) {
                        case SIG_FIG:
                            fmt = SignificantFigures.format(value, sigDigits);
                            break;
                        case SIG_FIG_NOE:
                            fmt = SignificantFiguresNoE.format(value, sigDigits);
                            break;
                        case INT:
                            fmt = String.valueOf(Math.round(d));
                            break;
                        case INT_SIG_FIG:
                            fmt = SignificantFigures.format(value, sigDigits);
                            fmt = String.valueOf(Math.round(Double.valueOf(fmt)));
                            break;
                        case INT_SIG_FIG_NOE:
                            fmt = SignificantFiguresNoE.format(value, sigDigits);
                            fmt = String.valueOf(Math.round(Double.valueOf(fmt)));
                            break;
                        default:
                            fmt = value.trim();
                            break;
                    }
                    if (sign != null)
                        fmt = sign + fmt;
                }
            } catch (Exception e) {
                err = true;
            }
            if (err)
                throw new ParseException(Messages.get()
                                                 .illegalNumericValueException(String.valueOf(min),
                                                                               String.valueOf(max)));
            return fmt;
        }
    }

    /*
     * Titer Item
     */
    static class TiterItem extends Item {
        private static final long serialVersionUID = 1L;

        int                       min, max;

        /**
         * Range is in format min:max for the denominator, e.g, 32:256 means any
         * titer value 1:16 is not valid while 1:32 & 1:64 ... (denominators are
         * between 32 and 256) are valid.
         */
        public TiterItem(String range) throws ParseException {
            String st[];

            if (range == null)
                throw new ParseException(Messages.get().illegalTiterFormatException());
            st = range.split(":");
            if (st.length != 2)
                throw new ParseException(Messages.get().illegalTiterFormatException());

            try {
                min = Integer.parseInt(st[0]);
                max = Integer.parseInt(st[1]);
            } catch (NumberFormatException ex) {
                throw new ParseException(Messages.get().illegalTiterFormatException());
            }
            if (min <= 0 || max <= 0)
                throw new ParseException(Messages.get().illegalTiterFormatException());
            if (min > max)
                throw new ParseException(Messages.get().illegalTiterRangeException());
        }

        /**
         * Formats the titer to [<,>,<=, >=]1:NNN.
         */
        public String format(String value) throws ParseException {
            int d;
            boolean err;
            String fmt;

            if (value == null)
                return null;

            fmt = "";
            err = false;
            if (value.startsWith(">") || value.startsWith("<")) {
                fmt = value.substring(0, 1);
                value = value.substring(1);
            }
            if (value.startsWith("=")) {
                fmt += value.substring(0, 1);
                value = value.substring(1);
            }
            if (value.startsWith("1:"))
                value = value.substring(2);
            else
                fmt += "1:";
            try {
                d = Integer.parseInt(value);
                if (d < min || d > max)
                    err = true;
                else
                    fmt += d;
            } catch (Exception e) {
                err = true;
            }
            if (err)
                throw new ParseException(Messages.get().illegalTiterFormatException());

            return fmt;
        }
    }

    /*
     * Dictionary Item
     */
    static class DictionaryItem extends Item {
        private static final long serialVersionUID = 1L;

        int                       dictId;
        String                    text;

        /**
         * Dictionary has dictionary.id and dictionary.text; Text is used for
         * matching while the id is stored in value
         */
        public DictionaryItem(String id, String text) {
            this.id = Integer.valueOf(id);
            this.text = text;
        }

        /**
         * Returns the id if this value matches the dictionary text
         */
        public String format(String value) {
            if (text.equals(value))
                return String.valueOf(id);
            return null;
        }
    }

    /*
     * Date/Time/DateTime Item
     */
    static class DateTimeItem extends Item {
        private static final long serialVersionUID = 1L;

        /**
         * Parses and format date and time
         */
        public String format(String value) throws ParseException {
            Date d;
            String format;
            DateTimeFormat dtf;

            format = null;
            switch (type) {
                case DATE:
                    format = Messages.get().datePattern();
                    break;
                case TIME:
                    format = Messages.get().timePattern();
                    break;
                case DATE_TIME:
                    format = Messages.get().dateTimePattern();
                    break;
            }
            /*
             * should work both in server and client GWT
             */
            dtf = new DateTimeFormat(format, new DefaultDateTimeFormatInfo()) {
            };
            try {
                d = dtf.parseStrict(value);
                return dtf.format(d);
            } catch (Exception e) {
                throw new ParseException(Messages.get().illegalDateTimeValueException());
            }
        }
    }

    /*
     * Dictionary Item
     */
    static class DefaultItem extends Item {
        private static final long serialVersionUID = 1L;

        String                    value;

        public DefaultItem(String value) {
            this.value = value;
        }
    }

    /*
     * Characteristics of each unit
     */
    static class Unit implements Serializable {
        private static final long serialVersionUID = 1L;

        DefaultItem               def;
        ArrayList<Item>           items;
        boolean                   onlyDictionary;
    }
}