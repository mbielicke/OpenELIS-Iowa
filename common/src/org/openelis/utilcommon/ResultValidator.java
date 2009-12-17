package org.openelis.utilcommon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;

public class ResultValidator implements RPC {
    private static final long serialVersionUID = 1L;

    public enum Type {
        DATE, DATE_TIME, TIME, DICTIONARY, NUMERIC, TITER
    };
    
    HashMap<Integer, ArrayList<Item>> units;
    HashMap<String, Integer> dictionary; 
    
    public ResultValidator() {
        units = new HashMap<Integer, ArrayList<Item>>();
    }
    
    public void addResult(Integer id, Integer unitId, Type type, String validRange) throws Exception {
        Item item;
        ArrayList<Item> list;
        
        if (unitId == null)
            unitId = 0;
        //
        // dictionary is special because we are going to add to the same list
        // rather than creating a new result range every time
        //
        if (type == Type.DICTIONARY && dictionary != null) {
            dictionary.put(unitId+"|"+validRange, id);
            return;
        }

        item = new Item();
        item.id = id;
        item.type = type;
        
        switch (type) {
            case DICTIONARY:
                dictionary = new HashMap<String, Integer>();
                dictionary.put(unitId+"|"+validRange, id);
                break;
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

    public Integer validate(Integer unitId, String value) throws ParseException {
        Integer id;
        ArrayList<Item> list;

        list = units.get(unitId);
        if (list == null)
            list = units.get(0);
        
        id = null;
        for (Item item : list) {
            if (item.type == Type.DICTIONARY) {
                id = dictionary.get(unitId+"|"+value);
                if (id == null)
                    id = dictionary.get("0|"+value);
            } else {
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
        else
            throw new ParseException("illegalResultValueException");
    }

    private static class Item implements Serializable {
        private static final long serialVersionUID = 1L;

        Integer id;
        Type type;
        ResultRange resultRange;
    }
}
