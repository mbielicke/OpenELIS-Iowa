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
package org.openelis.scriptlet.ms.firstTri;

import org.openelis.constants.Messages;
import org.openelis.scriptlet.ms.Constants.Apr_Risk;
import org.openelis.scriptlet.ms.Constants.Interpretation;
import org.openelis.scriptlet.ms.Util;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class for computing various risks and moms for the ms 1st tri (Maternal
 * First Trimester Screen) and ms 1st integ (Maternal Integrated Screen) tests
 */
public class Compute {

    protected Datetime birthDate, // date of birth
                        collectedDate, // date sample collected
                        enteredDate, // date sample record was entered
                        usDate; // date of ultrasound
    
    protected boolean  isOverridden,        // test been rejected by QA, default false
                        isIntegrated,    // do not show risks/interpretations
                         didCmpRisks; //  did compute  all the risks
    
    protected Integer  crl, // child's crown rump length
                    gestAge, // gest age (days) for serum comp
                    gestAgeNT;   // gest age (days) for NT comp
    
    protected Double   weight,          // patient's weight
                        dueAge;         // mother's due age using gestAgeInit
    
    protected Test[]    tests;

    protected Risk      downs, t18;

    protected Exception       lastException;   // last error if any
    
    public Compute() {
        /*
         * instantiate tests and risks
         */
        tests = new Test[3];
        tests[0] = new NT();
        tests[1] = new PAPPA();
        tests[2] = new HCG();
        
        downs = new Downs();
        t18 = new Trisomy18();        
    }
    
    /**
     * Sets whether the sample and/or analysis has been overridden
     */
    public void setIsOverridden(boolean isOverridden) {
        this.isOverridden = isOverridden;
    }
    
    /** 
     * Sets whether this is the integrated test
     */
    public void setIsIntegrated(boolean isIntegrated) {
        this.isIntegrated = isIntegrated;
    }
    
    /**
     * Sets the patient's birth date
     */
    public void setBirthDate(Datetime birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Sets the sample's collection date
     */
    public void setCollectionDate(Datetime collectionDate) {
        this.collectedDate = collectionDate;
    }
    
    /**
     * Sets the sample's entered date
     */
    public void setEnteredDate(Datetime enteredDate) {
        this.enteredDate = enteredDate;
    }
    
    /**
     * Sets the ultrasound date
     */
    public void setUltrasoundDate(Datetime usDate) {
        this.usDate = usDate;
    }

    /**
     * Sets the patient's weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Sets the crown rump length
     */
    public void setCRL(Integer crl) {
        this.crl = crl;
    }
    
    /**
     * Returns the object for NT
     */
    public Test getNT() {
        return tests[0];
    }
    
    /**
     * Returns the object for PAPP-A
     */
    public Test getPAPPA() {
        return tests[1];
    }
    
    /**
     * Returns the object for HCG
     */
    public Test getHCG() {
        return tests[2];
    }
    
    /**
     * Computes various values such as gestational and due ages, MoMs and risks
     */
    public void compute() {
        cmpGestationalAges();
        cmpMotherDueAge();
        if ( !isOverridden) {
            cmpMoMs();
            cmpDowns();
            cmpTrisomy18();
        }
    }

    /**
     * Returns the most recent exception thrown while computing various values
     */
    public Exception getLastException() {
        return lastException;
    }
    
    /**
     * Returns the gestational age 
     */
    public Integer getGestAge() {
        return gestAge;
    }
    
    /**
     * Returns the mother's due age 
     */
    public Double getMothersDueAge() {
        return dueAge;
    }
    
    /**
     * Returns the interpretation for NT
     */
    public Interpretation getInterpretationNT() {
        double limit;

        if ( !didCmpRisks)
            return null;

        limit = getLimitNT().doubleValue();
        if (tests[0].getResult().doubleValue() < limit)
            return Interpretation.NORMAL;
        else
            return Interpretation.PRES_POS;
    }
    
    /**
     * Returns the limit (cutoff) for Downs
     */
    public Integer getLimitDowns() {
        if (! didCmpRisks)
            return null;

        return 220;
    }
    
    /**
     * Returns the risk of Downs
     */
    public Integer getRiskDowns() {
        if (! didCmpRisks || dueAge < 15.0 || isIntegrated)
            return null;

        return (int)Util.setPrecision(Math.max(10.0,Math.min(99000.0, downs.getRisk())), 2);
    }

    /**
     * Returns the risk sign for Downs
     */
    public String getRiskSignDowns() {
        int risk;

        if (! didCmpRisks || dueAge < 15.0 || isIntegrated)
            return null;

        risk = downs.getRisk();
        if (risk > 99000)
            return "<";
        else if (risk < 10)
            return ">";
        return null;
    }
    
    /**
     * Returns the interpretation for Downs
     */
    public Interpretation getInterpretationDowns() {
        int risk, limit;

        if (! didCmpRisks || isIntegrated)
            return null;
        else if (dueAge < 15.0)
            return Interpretation.UNKNOWN;

        limit = getLimitDowns().intValue();
        risk  = downs.getRisk();

        if (risk > limit)
            return Interpretation.NORMAL;
        else
            return Interpretation.PRES_POS;
    }
    
    /**
     * Returns the limit (cutoff) for Trisomy 18
     */
    public Integer getLimitTrisomy18() {
        if (! didCmpRisks)
            return null;

        return 100;
    }

    /**
     * Returns the risk of Trisomy 18
     */
    public Integer getRiskTrisomy18() {
        if ( !didCmpRisks || dueAge < 15.0 || isIntegrated)
            return null;

        return (int)Util.setPrecision(Math.max(10.0, Math.min(99000.0, (double)t18.getRisk())), 2);
    }
    
    /**
     * Returns the risk sign for Trisomy 18
     */
    public String getRiskSignTrisomy18() {
        if (! didCmpRisks || dueAge < 15.0 || isIntegrated)
            return null;

        if (t18.getRisk() > 99000)
            return "<";
        else if (t18.getRisk() < 10)
            return ">";
        return null;
    }
    
    /**
     * Returns the interpretation for Trisomy 18
     */
    public Interpretation getInterpretationTrisomy18() {
        int limit;

        if (! didCmpRisks || isIntegrated)
            return null;
        else if (dueAge < 15.0)
            return Interpretation.UNKNOWN;

        limit = getLimitTrisomy18().intValue();
        if (t18.getRisk() > limit)
            return Interpretation.NORMAL;
        else
            return Interpretation.PRES_POS;
    }
    
    /**
     * Returns downs risk by maternal age
     */
    public Integer getRiskDownsByAge() {
        double risk;

        if (dueAge == null || isIntegrated)
            return null;

        risk = Util.getAPrioriRisk(dueAge, Apr_Risk.T1);
        return (int) Util.setPrecision((double)risk, 2);
    }
    
    /**
     * Returns the limit (cutoff) for NT
     */
    private Double getLimitNT() {
        return 3.0;
    }
    
    /**
     * Computes Gestinational age
     */
    private void cmpGestationalAges() {
        int  days;

        /*
         * check for required vars
         */
        if (collectedDate == null) {
            lastException = new InconsistencyException(Messages.get()
                                                               .result_missingCollectedDateException());
            return;
        }
        days = 0;
        if (usDate != null) {
            days = Util.getAgeforCRL(crl);
            gestAgeNT = days;
            if (days != 0)
                days += Util.getIntervalDays(collectedDate.getDate(), usDate.getDate());
        }
        if (days == 0) {
            lastException = new InconsistencyException(Messages.get()
                                                       .result_missingUSGAException());
            return;
        }
        gestAge = days;
    }
    
    /**
     * Calculates mother's age at delivery in year.1/10 fraction.
     */
    private void cmpMotherDueAge() {
        Datetime due;
        
        dueAge = null;

        // check for required vars
        if (gestAge == null)
            return;
        if (birthDate == null) {
            lastException = new InconsistencyException(Messages.get()
                                                               .result_missingBirthDateException());
            return;
        }
        if (collectedDate == null) {
            lastException = new InconsistencyException(Messages.get()
                                                       .result_missingCollectedDateException());
            return;
        }

        /*
         * compute mothers age at delivery using gestational age
         */
        due = collectedDate.add(40 * 7 - gestAge); // 40 weeks max gest
        dueAge = (due.getDate().getTime() - birthDate.getDate().getTime()) / 31536000000.0d;
        dueAge = Util.trunc(dueAge + 0.05, 1);
    }
    
    /**
     * Computes Multiple of Medians for each result
     */
    private void cmpMoMs() {
        if (gestAge == null)
            return;
        
        try {
            for (Test t : tests)
                t.computeMoms(crl, gestAge, enteredDate, weight, isIntegrated);
        } catch (Exception indE) {
            lastException = indE;
        }
    }

   /**
    * Computes downs risk
    */
    private void cmpDowns() {
        int i;
        Test t;
        double tmpMoM[], apr;

        if (!didCmpMoMs())
            return;
        
        apr = Util.getAPrioriRisk(dueAge, Apr_Risk.T1);
        /*
         * truncate all the test moms for DOWNS
         */
        tmpMoM = new double[4];
        for (i = 0; i < tests.length; i++ ) {
            t = tests[i];
            tmpMoM[i] = Math.max(t.getDowns()[0], Math.min(t.getDowns()[1], t.getMom()));
        }
        downs.computeRisk(tmpMoM, apr, gestAgeNT, gestAge);
        didCmpRisks = true;
    }
    
    /**
     * Computes Trisomy 18 risk
     */
    private void cmpTrisomy18() {
        int i;
        Test t;
        double tmpMoM[], apr;

        if (!didCmpMoMs())
            return;
        
        apr = Util.getAPrioriRisk(dueAge, Apr_Risk.TERM) * 10.0;
        /*
         * truncate all the test moms for T18
         */
        tmpMoM = new double[4];
        for (i = 0; i < tests.length; i++ ) {
            t = tests[i];
            tmpMoM[i] = Math.max(t.getT18()[0], Math.min(t.getT18()[1], t.getMom()));
        }
        t18.computeRisk(tmpMoM, apr, crl, gestAge);
        didCmpRisks = true;
    }

    /**
     * Returns true if MoMs for all tests have been computed; returns false
     * otherwise
     */
    private boolean didCmpMoMs() {
        for (Test t : tests) {
            if (!t.didCmpMoM())
                return false;
        }

        return true;
    }
}