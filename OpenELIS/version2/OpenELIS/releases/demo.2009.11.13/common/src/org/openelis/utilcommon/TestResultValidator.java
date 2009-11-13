/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.utilcommon;

import java.util.Date;

import org.openelis.exception.ParseException;

public class TestResultValidator {
    
    public static void validateDate(String value) throws ParseException{                                
        if(value == null)
            return;        
        try {
            Date.parse(value.replaceAll("-", "/"));
        } catch (IllegalArgumentException ex){
            throw new ParseException("Invalid date");
        }       
    }
    
    public static void validateDateTime(String value) throws ParseException{
        String st[];
        String hhmm;

        if (value == null) {
            return;
        }
        try {
            st = value.split(" ");
            if (st.length != 2)
                throw new ParseException("Invalid date and time");

            hhmm = st[1];
            if (hhmm.split(":").length != 2)
                throw new ParseException("Invalid date and time");

            Date.parse(value.replaceAll("-", "/"));
        } catch (IllegalArgumentException ex) {
            throw new ParseException("Invalid date and time");
        }                                                                                                       
    }
    
    public static void validateTime(String value) throws ParseException{
        String st[];
        String dateStr, defDate;        
        
        defDate = "2000-01-01 ";
        
        if(value != null) {                           
            try{
                st = value.split(":");                             
                if(st.length != 2)
                    throw new ParseException("Invalid time");                                                       
                
                dateStr = defDate + value;                                
                Date.parse(dateStr.replaceAll("-", "/"));                                                            
            } catch (IllegalArgumentException ex) {
                throw new ParseException("Invalid time");
            }                                                                                                     
        }
    }
    
    public static void validateYesNoValue(String value) throws ParseException{
        if(value == null)
            return;
        
        if(!"Y".equals(value) && !"N".equals(value)) {
            throw new ParseException("Illegal Yes/No Value");
        }
    } 
    
    
}