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
package org.openelis.scriptlet.ms;

/**
 * The class for storing the constants used for calculating various risks for
 * the maternal screening tests
 */
public class Constants {
    
    public static final String QA_EST_MOM =  "estriol < 0.25 mom", QA_PAPP_A_MOM = "papp-a < 0.1 mom", 
                    QA_TWINS = "twins", QA_LESS_THAN_15 = "< 15 years old";
    public static final double EST_LIMIT = 0.25, PAPP_A_LIMIT = 0.1;

    /**
     * Risk interpretations
     */
    public enum Interpretation {
        NORMAL, PRES_POS, PRES_POS_AS, UNKNOWN
    }
    
    /**
     * Apriori risk types
     */
    public enum Apr_Risk {
        T1, T2, TERM
    }

    /**
     * Gestational age determined by method
     */
    public enum Gest_Age_Method {
        LMP, US
    }
    
    /**
     *  The sample type set for the analysis
     */
    public enum Sample_Type {
        SERUM, AMNIOTIC_FLUID
    }
}