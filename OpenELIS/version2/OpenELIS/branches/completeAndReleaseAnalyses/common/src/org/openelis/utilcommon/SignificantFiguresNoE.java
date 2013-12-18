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

public class SignificantFiguresNoE extends SignificantFigures {
    private static final long serialVersionUID = 1L;
    
    public SignificantFiguresNoE(String number) {
        super(number);
    }
    
    public SignificantFiguresNoE(byte number) {
        super(number);
    }
    
    public SignificantFiguresNoE(short number) {
        super(number);
    }
    
    public SignificantFiguresNoE(int number) {
        super(number);
    }
    
    public SignificantFiguresNoE(long number) {
        super(number);
    }
    
    public SignificantFiguresNoE(float number) {
        super(number);
    }
    
    public SignificantFiguresNoE(double number) {
        super(number);
    }
    
    public SignificantFiguresNoE(Number number) {
        super(number);
    }
    
    /**
     * Overridden to not add the scientific notation. It is understood that 
     * the resulting value is not correct. 
     */
    public String toString() {
        if (digits == null)
            return original;
        StringBuffer digits = new StringBuffer(this.digits.toString());
        int length = digits.length();
        /*if (mantissa <= -8 || mantissa >= 7 ||
            (mantissa >= length && digits.charAt(digits.length() - 1) == '0') ||
            (isZero && mantissa != 0)) {
             //use scientific notation.
            if (length > 1) {
                digits.insert(1, '.');
            }
            if (mantissa != 0) {
                digits.append("E" + mantissa);
            }
        } else */ if (mantissa <= -1) {
            digits.insert(0, "0.");
            for (int i = mantissa; i < -1; i++ ) {
                digits.insert(2, '0');
            }
        } else if (mantissa + 1 == length) {
            if (length > 1 && digits.charAt(digits.length() - 1) == '0') {
                digits.append('.');
            }
        } else if (mantissa < length) {
            digits.insert(mantissa + 1, '.');
        } else {
            for (int i = length; i <= mantissa; i++ ) {
                digits.append('0');
            }
        }
        if ( !sign) {
            digits.insert(0, '-');
        }
        return digits.toString();
    }
    
    public static String format(byte number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(double number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(float number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(int number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(long number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(Number number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(short number, int significantFigures) {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }

    public static String format(String number, int significantFigures) throws NumberFormatException {
        SignificantFiguresNoE sf = new SignificantFiguresNoE(number);
        sf.setNumberSignificantFigures(significantFigures);
        return sf.toString();
    }
}