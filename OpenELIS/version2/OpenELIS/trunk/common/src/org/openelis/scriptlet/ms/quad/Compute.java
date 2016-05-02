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
package org.openelis.scriptlet.ms.quad;

import java.util.Date;

import org.openelis.constants.Messages;
import org.openelis.scriptlet.ms.Util;
import org.openelis.scriptlet.ms.quad.Constants.Apr_Risk;
import org.openelis.scriptlet.ms.quad.Constants.Gest_Age_Method;
import org.openelis.scriptlet.ms.quad.Constants.Interpretation;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class for computing various risks and moms for the ms quad (Maternal Quad
 * Screen) test
 */
public class Compute {

    protected Datetime        birthDate, // date of birth
                    collected, // date sample collected
                    enteredDate, // date sample record was entered
                    lmpDate, // date of last menstrual period
                    usDate;               // date of ultrasound

    protected boolean       hasNTDHistory, // patient NTD history, default false
                    isRaceBlack, // patient is black, default false
                    isDiabetic, // patient is diabetic, default false
                    beenReleased, // test previously released, default false
                    isOverridden;         // test been rejected by QA, default
                                           // false

    protected Integer       numFetus, // number of Fetuses 1, 2 or many
                             crl, // child's length crown rump length
                             bpd;                  // biparietal diameter

    protected Double        weight, // patient's weight
                             weeksDays; // Gest Age in weeks.days format

    protected Test          afp, est, hcg, inh; // data specific to the tests
    
    protected Risk          ntd, downsUS, downsLMP, t18, slos;  // data specific to the risks

    protected Gest_Age_Method gestAgeInitBy, // init gest age computed by method
                    gestAgeCurrBy;        // current gest age computed by
                                           // method
    /*
     * internal
     */
    protected boolean       didCmpGa, // did compute gest-age
                    didCmpDueAge;         // "     " mothers due age

    protected int           gestAgeInit, // gest age (days) for initial comp
                    gestAgeCurr;          // gest age (days) for current comp

    protected double        dueAgeInit, // mother's due age using gestAgeInit
                    dueAgeCurr;           // "         " "    " gestAgeCurr

    protected Exception     lastException; // last error if any

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
     * Sets whether the sample and/or analysis has been overridden
     */
    public void setIsOverridden(boolean isOverridden) {
        this.isOverridden = isOverridden;
    }

    /**
     * Sets whether the sample and/or analysis has been released
     */
    public void setIsReleased(boolean isReleased) {
        this.beenReleased = isReleased;
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
        this.collected = collectionDate;
    }

    /**
     * Sets the sample's entered date
     */
    public void setEnteredDate(Datetime enteredDate) {
        this.enteredDate = enteredDate;
    }

    /**
     * Sets the last menstrual period date
     */
    public void setLMPDate(Datetime lmpDate) {
        this.lmpDate = lmpDate;
    }

    /**
     * Sets the ultrasound date 
     */
    public void setUltrasoundDate(Datetime usDate) {
        this.usDate = usDate;
    }

    /**
     * Sets the number of fetuses
     */
    public void setNumFetus(Integer numFetus) {
        this.numFetus = numFetus;
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
     * Sets the biparietal diameter
     */
    public void setBPD(Integer bpd) {
        this.bpd = bpd;
    }

    /**
     * Sets gestational age in weeks and days 
     */
    public void setWeeksDays(Double weeksDays) {
        this.weeksDays = weeksDays;
    }

    /**
     * Sets the method by which initial gestational age was determined
     */
    public void setGestAgeInitMethod(Gest_Age_Method gestAgeInitBy) {
        this.gestAgeInitBy = gestAgeInitBy;
    }

    /**
     * Sets p-values and intrument result for AFP
     */
    public void setResultAFP(Test afp) {
        this.afp = afp;
    }

    /**
     * Sets p-values and intrument result for estriol
     */
    public void setResultEST(Test est) {
        this.est = est;
    }

    /**
     * Sets p-values and intrument result for HCG
     */
    public void setResultHCG(Test hcg) {
        this.hcg = hcg;
    }

    /**
     * Sets p-values and intrument result for Inhibin
     */
    public void setResultInhibin(Test inh) {
        this.inh = inh;
    }
    
    /**
     * Computes various values such as gestational and due ages, MoMs and risks
     */
    public void compute() {
        cmpGestationalAges();
        cmpMotherDueAge();
        /*
         * these objects are created here because the scriptlet gets MoMs, risks
         * etc. from this compute engine even if results are overridden; the
         * engine gets those values from these objects, so they can't be null in
         * any case; objects for tests e.g. AFP are set by the scriptlet
         */
        ntd = new NTD();
        downsLMP = new DownsLMP();
        downsUS = new DownsUS();
        t18 = new Trisomy18();
        slos = new SLOS();
        if ( !isOverridden) {
            cmpMoMs();
            cmpNTD();
            cmpDowns();
            cmpTrisomy18();
            cmpSLOS();
        }
    }


    /**
     * Returns the most recent exception thrown while computing various values
     */
    public Exception getLastException() {
        return lastException;
    }

    /**
     * Returns the MoM for AFP
     */
    public Double getMoMAFP() {
        if ( !afp.getDidCmpMoM())
            return null;
        return new Double(afp.getMomCurr());
    }

    /**
     * Returns the MoM for Estriol
     */
    public Double getMoMEST() {
        if ( !est.getDidCmpMoM())
            return null;
        return new Double(est.getMomCurr());
    }

    /**
     * Returns the MoM for HCG
     */
    public Double getMoMHCG() {
        if ( !hcg.getDidCmpMoM())
            return null;
        return new Double(hcg.getMomCurr());
    }

    /**
     * Returns the MoM for Inhibin
     */
    public Double getMoMINH() {
        if ( !inh.getDidCmpMoM())
            return null;
        return new Double(inh.getMomCurr());
    }

    /**
     * Returns initial gestational age
     */
    public Integer getGestAgeInit() {
        if ( !didCmpGa)
            return null;

        return new Integer(gestAgeInit);
    }

    /**
     * Returns the method by which initial gestational age was determined
     */
    public Gest_Age_Method getGestAgeInitMethod() {
        if ( !didCmpGa)
            return null;

        return gestAgeInitBy;
    }

    /**
     * Returns the current gestational age
     */
    public Integer getGestAgeCurr() {
        if ( !didCmpGa)
            return null;

        return new Integer(gestAgeCurr);
    }

    /**
     * Returns the method by which current gestational age was determined
     */
    public Gest_Age_Method getGestAgeCurrMethod() {
        if ( !didCmpGa)
            return null;

        return gestAgeCurrBy;
    }

    /**
     * Returns the mother's due age
     */
    public Double getMothersDueAge() {
        if ( !didCmpDueAge)
            return null;

        return new Double(dueAgeCurr);
    }

    /**
     * Returns the limit (cutoff) for NTD
     */
    public Double getLimitNTD() {
        double limit;

        if ( !ntd.getDidCmpRisk())
            return null;

        limit = 0.0d;
        if (numFetus.intValue() == 1)
            limit = (hasNTDHistory) ? 2.0d : 2.2d;
        if (numFetus.intValue() == 2)
            limit = (hasNTDHistory) ? 4.0d : 4.4d;

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

        if ( !ntd.getDidCmpRisk())
            return null;

        limit = getLimitNTD().doubleValue();
        if (afp.getMomCurr() < limit) {
            return Interpretation.NORMAL;
        } else {
            if (usDate == null)
                return Interpretation.PRES_POS;
            else
                return Interpretation.PRES_POS_AS;
        }
    }

    /**
     * Returns the limit (cutoff) for Downs
     */
    public Integer getLimitDowns() {
        if ( !downsLMP.getDidCmpRisk() && !downsUS.getDidCmpRisk())
            return null;

        return new Integer(150);
    }

    /**
     * Returns the risk of Downs
     */
    public Integer getRiskDowns() {
        int risk;

        if ( ( !downsLMP.getDidCmpRisk() || !downsUS.getDidCmpRisk()) || dueAgeCurr < 15.0 ||
            (numFetus != null && numFetus.intValue() > 1))
            return null;
        risk = (Gest_Age_Method.LMP.equals(gestAgeCurrBy)) ? downsLMP.getRisk() : downsUS.getRisk();
        risk = (int)setPrecision(Math.max(10.0, Math.min(99000.0, (double)risk)), 2);
        return new Integer(risk);
    }

    /**
     * Returns the risk sign for Downs
     */
    public String getRiskSignDowns() {
        int risk;

        if ( ( !downsLMP.getDidCmpRisk() && !downsUS.getDidCmpRisk()) || dueAgeCurr < 15.0 ||
            (numFetus != null && numFetus.intValue() > 1))
            return null;

        risk = (Gest_Age_Method.LMP.equals(gestAgeCurrBy)) ? downsLMP.getRisk() : downsUS.getRisk();
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

        if ( !downsLMP.getDidCmpRisk() && !downsUS.getDidCmpRisk())
            return null;
        else if (dueAgeCurr < 15.0 || (numFetus != null && numFetus.intValue() > 1))
            return Interpretation.UNKNOWN;

        limit = getLimitDowns().intValue();
        risk = (Gest_Age_Method.LMP.equals(gestAgeCurrBy)) ? downsLMP.getRisk() : downsUS.getRisk();

        if (risk > limit) {
            return Interpretation.NORMAL;
        } else {
            if (usDate == null)
                return Interpretation.PRES_POS;
            else
                return Interpretation.PRES_POS_AS;
        }
    }

    /**
     * Returns the limit (cutoff) for Trisomy 18
     */
    public Integer getLimitTrisomy18() {
        if ( !t18.getDidCmpRisk())
            return null;

        return new Integer(100);
    }

    /**
     * Returns the risk of Trisomy 18
     */
    public Integer getRiskTrisomy18() {
        int risk0, risk1, limit0, limit1;

        if ( !t18.getDidCmpRisk() || !slos.getDidCmpRisk() || dueAgeInit < 15.0 ||
            (numFetus != null && numFetus.intValue() > 1))
            return null;

        risk0 = (int)setPrecision(Math.max(10.0, Math.min(99000.0, (double)slos.getRisk())), 2);
        risk1 = (int)setPrecision(Math.max(10.0, Math.min(99000.0, (double)t18.getRisk())), 2);
        /*
         * The SLOS computation is poor indicator for SLOS and no longer being
         * reported as SLOS risk. However, at the risk of 1 in 50 or greater a
         * large percentage of preg had significant preg related issues.
         * Therefore the program decided to include those women indentified by
         * SLOS risk in the positive T18 risk computation.
         */
        limit0 = 50; // SLOS limit
        limit1 = getLimitTrisomy18().intValue();
        if (risk0 <= limit0 && risk1 > limit1) {
            t18.setRisk(limit1);
            risk1 = limit1;
        }
        return new Integer(risk1);
    }

    /**
     * Returns the risk sign for Trisomy 18
     */
    public String getRiskSignTrisomy18() {
        if ( !t18.getDidCmpRisk() || dueAgeInit < 15.0 ||
            (numFetus != null && numFetus.intValue() > 1))
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

        if ( !t18.getDidCmpRisk())
            return null;
        else if (dueAgeInit < 15.0 || (numFetus != null && numFetus.intValue() > 1))
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

        if ( !didCmpDueAge)
            return null;

        risk = getAPrioriRisk(dueAgeCurr, Apr_Risk.T2);
        risk = (int)setPrecision(risk, 2);
        return new Integer((int)risk);
    }

    /**
     * Computes Gestinational age using the following model: 1) ultrasound if
     * available a) using CRL b) using BPD c) weeks and days 2) lmp
     */
    protected void cmpGestationalAges() {
        int days, usGA, lmpGA;

        /*
         * check for required vars
         */
        if (collected == null) {
            lastException = new InconsistencyException(Messages.get()
                                                               .result_missingCollectedDateException());
            return;
        }

        /*
         * compute gest age based on ultrasound
         */
        usGA = 0;
        if (usDate != null) {
            /*
             * base it first on CRL, second on BPD and last on weeks.days
             */
            days = getAgeforCRL();
            if (days == 0)
                days = getAgeforBPD();
            /*
             * weeksDays in a float with the fraction being number of days and
             * not fraction of a week. Thus 10.5 is 10w 5d and not 10w 3.5d.
             */
            if (days == 0 && weeksDays != null)
                days = (weeksDays.intValue() * 7) + (int) (weeksDays.doubleValue() * 10.0) % 10;
            /*
             * gest age = collected - (us - (baby's age_days))
             */
            if (days != 0)
                usGA = getIntervalDays(collected.getDate(), usDate.getDate()) + days;
        }

        /*
         * compute gest age based on LMP gest age = collected - lmp
         */
        lmpGA = 0;
        if (lmpDate != null)
            lmpGA = getIntervalDays(collected.getDate(), lmpDate.getDate());
        /*
         * 1) use most accurate method to compute gest age for unreleased. us
         * first, lmp next 2) use the original method to compute gest age for
         * released test if possible
         */
        if (usGA != 0 && lmpGA == 0) { // can do us only
            gestAgeCurrBy = Gest_Age_Method.US;
        } else if (usGA == 0 && lmpGA != 0) { // can do lmp only
            gestAgeCurrBy = Gest_Age_Method.LMP;
        } else if (usGA != 0 && lmpGA != 0) { // can do us and lmp
            if ( !beenReleased || gestAgeInitBy == null) {
                /*
                 * unreleased tests choose us over lmp
                 */
                gestAgeCurrBy = Gest_Age_Method.US;
            } else {
                /*
                 * lmp if original method was lmp and us +-12 days lmp us
                 * otherwise
                 */
                if (Gest_Age_Method.LMP.equals(gestAgeInitBy) && Math.abs(usGA - lmpGA) <= 12)
                    gestAgeCurrBy = Gest_Age_Method.LMP;
                else
                    gestAgeCurrBy = Gest_Age_Method.US;
            }
        } else {
            lastException = new InconsistencyException(Messages.get()
                                                               .result_missingUSLMPGAException());
            return;
        }

        if ( !beenReleased || gestAgeInitBy == null) {
            gestAgeInitBy = gestAgeCurrBy;
        } else {
            /*
             * check to see if we can compute based on the original method, U/L.
             * this makes it possible to force recompute when followup staff
             * remove us or lmp date based on doc's request.
             */
            if (Gest_Age_Method.LMP.equals(gestAgeInitBy) && lmpGA == 0)
                lmpGA = usGA;
            else if (Gest_Age_Method.US.equals(gestAgeInitBy) && usGA == 0)
                usGA = lmpGA;
        }

        gestAgeInit = Gest_Age_Method.LMP.equals(gestAgeInitBy) ? lmpGA : usGA;
        gestAgeCurr = Gest_Age_Method.LMP.equals(gestAgeCurrBy) ? lmpGA : usGA;

        didCmpGa = true;
    }

    /**
     * Computes mother's age at delivery in year.1/10 fraction.
     */
    protected void cmpMotherDueAge() {
        dueAgeInit = cmpMotherDueAge(gestAgeInit);
        if (dueAgeInit != 0.0) {
            dueAgeCurr = cmpMotherDueAge(gestAgeCurr);
            if (dueAgeCurr != 0.0)
                didCmpDueAge = true;
        }
    }

    /**
     * Computes mother's due age based on the passed gestational age, patient's
     * birth date and sample's collected date
     */
    protected double cmpMotherDueAge(int gestAge) {
        double dueAge;
        Datetime due;

        dueAge = 0.0;
        /*
         * check for required vars
         */
        if ( !didCmpGa)
            return dueAge;
        if (birthDate == null) {
            lastException = new InconsistencyException(Messages.get().result_missingBirthDateException());
            return dueAge;
        }
        if (collected == null) {
            lastException = new InconsistencyException(Messages.get().result_missingCollectedDateException());
            return dueAge;
        }

        /*
         * compute mothers age at delivery using gestational age
         */
        due = collected.add(40 * 7 - gestAge); // 40 weeks max gest
        dueAge = (due.getDate().getTime() - birthDate.getDate().getTime()) / 31536000000.0d;
        dueAge = Util.trunc(dueAge + 0.05, 1);
        return dueAge;
    }

    /**
     * Computes Multiple of Medians for each result
     */
    protected void cmpMoMs() {
        if ( !didCmpGa)
            return;

        try {
            afp.computeMoms(gestAgeInit, gestAgeCurr, enteredDate, weight, isRaceBlack, isDiabetic);
            est.computeMoms(gestAgeInit, gestAgeCurr, enteredDate, weight, isRaceBlack, isDiabetic);
            hcg.computeMoms(gestAgeInit, gestAgeCurr, enteredDate, weight, isRaceBlack, isDiabetic);
            inh.computeMoms(gestAgeInit, gestAgeCurr, enteredDate, weight, isRaceBlack, isDiabetic);
        } catch (Exception indE) {
            lastException = indE;
        }
    }

    /**
     * Computes NTD -- NA
     */
    protected void cmpNTD() {
        if ( !afp.getDidCmpMoM())
            return;
        if (numFetus == null || numFetus.intValue() < 1 || numFetus.intValue() > 2) {
            lastException = new InconsistencyException(Messages.get().result_invalidNumFetusException());
            return;
        }
        ntd.computeRisk(null, 0.0);
    }

    /**
     * Computes quad marker for lmp and us downs risk
     */
    protected void cmpDowns() {
        double tmpMoM[], apr;

        if ( !afp.getDidCmpMoM() || !est.getDidCmpMoM() || !hcg.getDidCmpMoM() ||
            !inh.getDidCmpMoM())
            return;

        apr = getAPrioriRisk(dueAgeCurr, Apr_Risk.T2);
        /*
         * truncate all the test moms for DOWNS
         */
        tmpMoM = new double[4];
        tmpMoM[0] = Math.max(afp.getDowns()[0], Math.min(afp.getDowns()[1], afp.getMomCurr()));
        tmpMoM[1] = Math.max(est.getDowns()[0], Math.min(est.getDowns()[1], est.getMomCurr()));
        tmpMoM[2] = Math.max(hcg.getDowns()[0], Math.min(hcg.getDowns()[1], hcg.getMomCurr()));
        tmpMoM[3] = Math.max(inh.getDowns()[0], Math.min(inh.getDowns()[1], inh.getMomCurr()));
        downsLMP.computeRisk(tmpMoM, apr);
        downsUS.computeRisk(tmpMoM, apr);
    }

    /**
     * Computes trisomy 18 risk
     */
    protected void cmpTrisomy18() {
        double tmpMoM[], apr;

        if ( !afp.getDidCmpMoM() || !est.getDidCmpMoM() || !hcg.getDidCmpMoM())
            return;
        /*
         * apr risk for t18 is 10 less than apr risk for downs. So 1 in 200
         * changes to 1 in 2000
         */
        apr = getAPrioriRisk(dueAgeInit, Apr_Risk.TERM) * 10.0;
        /*
         * truncate all the test moms for T18
         */
        tmpMoM = new double[4];
        tmpMoM[0] = Math.max(afp.getT18()[0], Math.min(afp.getT18()[1], afp.getMomInit()));
        tmpMoM[1] = Math.max(est.getT18()[0], Math.min(est.getT18()[1], est.getMomInit()));
        tmpMoM[2] = Math.max(hcg.getT18()[0], Math.min(hcg.getT18()[1], hcg.getMomInit()));
        tmpMoM[3] = Math.max(inh.getT18()[0], Math.min(inh.getT18()[1], inh.getMomInit()));
        t18.computeRisk(tmpMoM, apr);
    }
    
    /**
     * Computes SLOS risk
     */
    protected void cmpSLOS() {
        double tmpMoM[];

        if ( !afp.getDidCmpMoM() || !est.getDidCmpMoM() || !hcg.getDidCmpMoM())
            return;
        /*
         * truncate all the test moms for SLOS
         */
        tmpMoM = new double[4];
        tmpMoM[0] = Math.max(afp.getSLOS()[0], Math.min(afp.getSLOS()[1], afp.getMomInit()));
        tmpMoM[1] = Math.max(est.getSLOS()[0], Math.min(est.getSLOS()[1], est.getMomInit()));
        tmpMoM[2] = Math.max(hcg.getSLOS()[0], Math.min(hcg.getSLOS()[1], hcg.getMomInit()));
        tmpMoM[3] = Math.max(inh.getSLOS()[0], Math.min(inh.getSLOS()[1], inh.getMomInit()));
        slos.computeRisk(tmpMoM, 0.0);
    }

    /**
     * Returns mother's apriori risk based on maternal age.
     */
    private double getAPrioriRisk(double dueAge, Apr_Risk aprRisk) {
        double risk;

        dueAge = Math.min(50, Math.max(15, dueAge));
        // computes term risk
        risk = Math.exp(7.33 - 4.211 / (1.0 + Math.exp( -0.2815 * (dueAge - 0.5) + 10.480245)));
        switch (aprRisk) {
            case T1:
                risk *= 0.57;
                break;
            case T2:
                risk *= 0.77;
                break;
        }
        return risk;
    }

    /**
     * Truncates a double to given decimal digits
     */
    private double setPrecision(double value, int sd) {
        int mag, factor;

        mag = (int) (Math.floor(Math.log10(value)) - sd + 1.0);
        if (mag < 0)
            return value;
        factor = (int)Math.pow(10, mag);
        return Math.floor( (value / factor) + 0.9) * factor;
    }

    /**
     * Returns the interval in days between two date objects
     */
    private int getIntervalDays(Date a, Date b) {
        double days;

        days = (a.getTime() - b.getTime()) / 86400000.0;
        /*
         * add a fudge factor to cancel the spring/fall clock changes.
         */
        return (int)Math.floor(days + 0.25);
    }

    /**
     * Returns the infant age associated with Biparietal diameter or 0 if not
     * found.
     */
    private int getAgeforBPD() {
        double x;
        
        if (bpd == null)
            return 0;
        /*
         * change mm to cm
         */
        x = bpd.intValue() / 10.0;
        return (int) ( (9.54 + 1.482 * x + 0.1676 * (x * x)) * 7.0);
    }

    /**
     * Returns the gestational age in days associated with crown rump length
     */
    private int getAgeforCRL() {
        double x;
        
        if (crl == null)
            return 0;
        /*
         * change mm to cm
         */
        x = crl.intValue() / 10.0;
        return (int) (Math.exp(1.684969 + 0.315646 * x - 0.049306 * x * x + 0.004057 * x * x * x -
                              0.000120456 * x * x * x * x) * 7.0);
    }
}