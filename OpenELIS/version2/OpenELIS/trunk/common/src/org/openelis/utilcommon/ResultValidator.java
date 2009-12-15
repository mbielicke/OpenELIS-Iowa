package org.openelis.utilcommon;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;

public class ResultValidator implements RPC {
    private static final long serialVersionUID = 1L;

    public enum Type {DATE, DATE_TIME, TIME,
        DICTIONARY, NUMERIC_RANGE, NUMERIC_TITER};
        
        protected HashMap<Integer, ArrayList<Result>> resultGroupHash;
        
        public ResultValidator(){
            resultGroupHash = new HashMap<Integer, ArrayList<Result>>();
        }
        
        public Type validate(Integer groupId, String value) throws ParseException {
            ArrayList<Result> results;
            Result result;
            
            result = null;
            results = resultGroupHash.get(groupId);
            
            if(results != null){
                for(int i=0; i<results.size(); i++){
                    try{
                        result = results.get(i);
                        result.validate(value);
                        
                        break;
                    }catch(Exception e){
                        result = null;
                    }
                }
            }
            
            if(result != null)
                return result.getType();
            else
                throw new ParseException("illegalResultValueException");
        }
        
        public void addResultGroup(Integer groupId, ArrayList<Result> results){
            resultGroupHash.put(groupId, results);
        }
        
        public void clear(){
            resultGroupHash.clear();
        }
}
