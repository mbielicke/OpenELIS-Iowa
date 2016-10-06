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
package org.openelis.scriptlet.ms.integrate;

import org.openelis.constants.Messages;
import org.openelis.scriptlet.ms.Constants.Interpretation;
import org.openelis.scriptlet.ms.Util;
import org.openelis.scriptlet.ms.Constants.Apr_Risk;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class for computing various risks and moms for the ms integrate Maternal
 * First Trimester Screen) test
 */
public class Compute {

    protected Datetime        birthDate, // date of birth
                               collectedDate, // date sample collected
                               enteredDate, // date sample record was entered
                               lmpDate, // date of last menstrual period
                               us1TDate;  // date of ultrasound

    protected boolean         hasNTDHistory, // patient NTD history, default
                                             // false
                                isRaceBlack, // patient is black, default false
                                isDiabetic, // patient is diabetic, default false
                                isOverridden, // test been rejected by QA,
                                             // default
                                             // false
                                didCmpRisks; //  did compute  all the risks

    protected Integer         numFetus, // number of Fetuses 1, 2 or many
                               crl1T, // child's crown rump length 1st tri
                               gestAge1T, // gest age (days) for 1st tri comp 
                               gestAge1TNT, // gest age (days) for 1st tri NT comp
                               gestAge2T;  // gest age (days) for 2nd tri comp

    protected Double          weight, // patient's weight
                               dueAge; // mother's due age using gestAge2T

    protected Test[]          tests;

    protected Risk            ntd, downs, t18; // data specific to the risks

    protected Exception       lastException;   // last error if any

    public Compute() {
        /*
         * instantiate tests and risks
         */
        tests = new Test[6];
        tests[0] = new AFP();
        tests[1] = new EST();
        tests[2] = new HCG();
        tests[3] = new INH();
        tests[4] = new PAPPA();
        tests[5] = new NT();

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
     * Sets the patient's weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Sets whether the patient's race is black
     */
    public void setIsRaceBlack(boolean isRaceBlack) {
        this.isRaceBlack = isRaceBlack;
    }

    /**
     * Sets whether the patient is diabetic
     */
    public void setIsDiabetic(boolean isDiabetic) {
        this.isDiabetic = isDiabetic;
    }

    /**
     * Sets whether the patient is has a history of NTD
     */
    public void setHasHistoryOfNTD(boolean hasNTDHistory) {
        this.hasNTDHistory = hasNTDHistory;
    }

    /**
     * Sets the number of fetuses
     */
    public void setNumFetus(Integer numFetus) {
        this.numFetus = numFetus;
    }
    
    /**
     * Sets the first trimester gestational age
     */
    public void setFirstTriGestAge(Integer gestAge1T) {
        this.gestAge1T = gestAge1T;
    }

    /**
     * Sets the first trimester ultrasound date
     */
    public void setFirstTriUltrasoundDate(Datetime us1TDate) {
        this.us1TDate = us1TDate;
    }

    /**
     * Sets the first trimester child's crown rump length
     */
    public void setFirstTriCRL(Integer crl1T) {
        this.crl1T = crl1T;
    }
    
    
    /**
     * Returns the object for AFP
     */
    public Test getAFP() {
        return tests[0];
    }

    /**
     * Returns the object for Estriol
     */
    public Test getEST() {
        return tests[1];
    }

    /**
     * Returns the object for HCG
     */
    public Test getHCG() {
        return tests[2];
    }

    /**
     * Returns the object for Inhibin
     */
    public Test getInhibin() {
        return tests[3];
    }
    
    /**
     * Returns the object PAPP-A
     */
    public Test getPAPPA() {
        return tests[4];
    }
    
    /**
     * Returns the object for NT
     */
    public Test getNT() {
        return tests[5];
    }
    

    /**
     * Computes various values such as gestational and due ages, MoMs and risks
     */
    public void compute() {
        cmpGestationalAges();
        cmpMotherDueAge();
        if ( !isOverridden) {
            cmpMoMs();
            cmpNTD();
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
     * Returns the limit (cutoff) for NTD
     */
    public Double getLimitNTD() {
        double limit;

        if (! didCmpRisks)
            return null;

        limit = (hasNTDHistory) ? 2.0d : 2.2d;

        return new Double(limit);
    }

    /**
     * Returns the risk of NTD
     */
    public Integer getRiskNTD() {
        return null;
    }

    /**
     * Returns the interpretation for NTD
     */
    public Interpretation getInterpretationNTD() {
        double limit;

        if (! didCmpRisks)
            return null;

        limit = getLimitNTD().doubleValue();
        if (tests[0].getMom() < limit)
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

        return 150;
    }

    /**
     * Returns the risk of Downs
     */
    public Integer getRiskDowns() {
        if ( !didCmpRisks || dueAge < 15.0)
            return null;

        return (int)Util.setPrecision(Math.max(10.0, Math.min(99000.0, downs.getRisk())), 2);
    }

    /**
     * Returns the risk sign for Downs
     */
    public String getRiskSignDowns() {
        int risk;

        if (! didCmpRisks || dueAge < 15.0)
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
        int    risk, limit;

        if (! didCmpRisks)
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
     * Returns downs risk by maternal age
     */
    public Integer getRiskDownsByAge() {
        double risk;

        if (dueAge == null)
            return null;

        risk = Util.getAPrioriRisk(dueAge, Apr_Risk.T2);
        risk = (int)Util.setPrecision((double)risk, 2);
        return new Integer((int)risk);
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
        if ( !didCmpRisks || dueAge < 15.0)
            return null;

        return (int)Util.setPrecision(Math.max(10.0, Math.min(99000.0, (double)t18.getRisk())), 2);
    }

    /**
     * Returns the risk sign for Trisomy 18
     */
    public String getRiskSignTrisomy18() {
        if (! didCmpRisks || dueAge < 15.0)
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
        int    limit;

        if (! didCmpRisks)
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
     * Returns the 2nd trimester gestational age
     */
    public Integer getGestAge() {
        return gestAge2T;
    }

    /**
     * Returns the mother's due age
     */
    public Double getMothersDueAge() {
        return dueAge;
    }

    /**
     * Computes Gestational age
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
        if (gestAge1T == null) {
            lastException = new InconsistencyException(Messages.get().result_missing1stTriGAException());
            return;
        }
        days = 0;
        if (us1TDate != null) {
            days = Util.getAgeforCRL(crl1T);
            gestAge1TNT = days;
            if (days != 0)
                days += Util.getIntervalDays(collectedDate.getDate(), us1TDate.getDate());
        }
        if (days == 0) {
            lastException = new InconsistencyException(Messages.get().result_missingUSGAException());
            return;
        }
        gestAge2T = days;
    }
    
    /**
     * Calculates mother's age at delivery in year.1/10 fraction.
     */
    private void cmpMotherDueAge() {
        Datetime due;
        
        dueAge = null;
        /*
         * check for required vars
         */
        if (gestAge2T == null)
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
        due = collectedDate.add(40 * 7 - gestAge2T); // 40 weeks max gest
        dueAge = (due.getDate().getTime() - birthDate.getDate().getTime()) / 31536000000.0d;
        dueAge = Util.trunc(dueAge + 0.05, 1);
    }
    
    /**
     * Computes Multiple of Medians for each result
     */
    private void cmpMoMs() {
        if (gestAge2T == null)
            return;
        
        try {
            for (Test t : tests) {
                t.computeMoms(gestAge2T,
                              enteredDate,
                              weight,
                              isRaceBlack,
                              isDiabetic);
            }
        } catch (Exception indE) {
            lastException = indE;
        }
    }
    
    /**
     * Computes NTD -- NA
     */
    private void cmpNTD() {
        if (!didCmpMoMs(1))
            return;
        
        if (numFetus == null || numFetus.intValue() < 1 ||
            numFetus.intValue() > 2) {
            lastException = new InconsistencyException(Messages.get()
                                                       .result_invalidNumFetusException());
            return;
        }
        didCmpRisks = true;
    }

    /**
     * Computes downs risk
     */
    private void cmpDowns() {
        int i, markers;
        double tmpMoM[], apr;
        Test t, nt;

        markers = 5;
        nt = tests[5];
        if (nt.didCmpMoM())
            markers = 6;
        
        if (!didCmpMoMs(markers))
            return;

        apr = Util.getAPrioriRisk(dueAge, Apr_Risk.T2);
        /*
         * truncate all the test moms for DOWNS
         */
        tmpMoM = new double[6];
        for (i = 0; i < markers; i++ ) {
            t = tests[i];
            tmpMoM[i] = Math.max(t.getDowns()[0], Math.min(t.getDowns()[1], t.getMom()));
        }
        downs.computeRisk(tmpMoM, markers, apr, nt.getMom(), gestAge1T, gestAge1TNT);
        didCmpRisks = true;
    }

    /**
     * Computes trisomy 18 risk
     */
    private void cmpTrisomy18() {
        int i, markers;
        double tmpMoM[], apr;
        Test t;

        markers = 3;
        if (!didCmpMoMs(markers))
            return;

        apr = Util.getAPrioriRisk(dueAge, Apr_Risk.TERM) * 10.0;
        /*
         * truncate all the test moms for T18
         */
        tmpMoM = new double[4];      
        for (i = 0; i < markers; i++ ) {
            t = tests[i];
            tmpMoM[i] = Math.max(t.getT18()[0], Math.min(t.getT18()[1], t.getMom()));
        }
        t18.computeRisk(tmpMoM, markers, apr, tests[5].getMom(), gestAge1T, gestAge1TNT);
        didCmpRisks = true;
    }
    
    /**
     * Returns true if MoMs for all tests have been computed; returns false
     * otherwise
     */
    private boolean didCmpMoMs(int markers) {
        int i;
        Test t;
        
        for (i = 0; i < markers; i++) {
            t = tests[i];
            if (!t.didCmpMoM())
                return false;
        }
        return true;
    }
}