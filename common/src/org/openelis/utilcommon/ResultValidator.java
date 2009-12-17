package org.openelis.utilcommon;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;

public class ResultValidator implements RPC {
    private static final long serialVersionUID = 1L;

    public enum Type {DATE, DATE_TIME, TIME,
        DICTIONARY, NUMERIC_RANGE, NUMERIC_TITER};
        
        protected HashMap<Integer, ArrayList<ResultType>> resultGroupHash;
        
        public ResultValidator(){
            resultGroupHash = new HashMap<Integer, ArrayList<ResultType>>();
        }
        
        public Integer validate(Integer groupId, String value) throws ParseException {
            ArrayList<ResultType> results;
            ResultType result;
            
            result = null;
            results = resultGroupHash.get(groupId);
            
            if(results != null){
                for(int i=0; i<results.size(); i++){
                    try{
                        result = results.get(i);
                        result.contains(value);
                        
                        break;
                    }catch(Exception e){
                        result = null;
                    }
                }
            }
            
            if(result != null)
                return result.getId();
            else
                throw new ParseException("illegalResultValueException");
        }
        
        public void addResultGroup(Integer groupId, ArrayList<ResultType> results){
            resultGroupHash.put(groupId, results);
        }
        
        public void clear(){
            resultGroupHash.clear();
        }
}
