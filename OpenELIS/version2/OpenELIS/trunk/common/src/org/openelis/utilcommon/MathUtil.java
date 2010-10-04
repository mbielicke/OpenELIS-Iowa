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

public class MathUtil {
    /**
     * This functions returns true if the arguments satisfy the following
     * equation, value = base ^ n; such that n is a natural number.
     */
    public static boolean isPowerOf(int value, int base) {
        double dv, db, quotient;
        
        dv = Math.log(value);
        db = Math.log(base);
        //
        // we can't use the operator "%" here because for floating point 
        // numbers the result of the operation may not be zero even if the result
        // of using the operator "/" on the same numbers has significant digits
        // only to the left of the decimal point
        //
        quotient = dv/db;
        
        //
        // for a number like 5.34 or 5.0 the method Math.floor(double) returns 5,
        // so if the following test succeeds then we know that "dv" is a multiple  
        // of "db" 
        //
        if (Math.floor(quotient) == quotient)
            return true;
        
        return false;
    }
}