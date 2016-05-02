package org.openelis.scriptlet.ms.quad;

import org.openelis.constants.Messages;
import org.openelis.scriptlet.ms.Util;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores constants such as correction factors for MoM per test
 * and truncation limits for a specific maternal screen such as AFP; it also
 * stores other values such as the result generated by the intrument and
 * p-values etc.; it also computes the initial and current MoMs
 */
public abstract class Test {
    private double  race, insuline, ntd[], downs[], t18[], slos[], momInit, momCurr;
    private int     minDay, maxDay;
    private Double  result, p1, p2, p3;
    private boolean didCmpMoM;
    private String analyteName;

    protected Test(double race, double insuline, double ntd[], double downs[], double t18[],
                   double slos[], int minDay, int maxDay, Double p1, Double p2, Double p3,
                   Double result, String analyteName) {
        this.race = race;
        this.insuline = insuline;
        this.ntd = ntd;
        this.downs = downs;
        this.t18 = t18;
        this.slos = slos;
        this.minDay = minDay;
        this.maxDay = maxDay;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.result = result;
        this.analyteName = analyteName;
    }

    /**
     * Returns race correction factor for MoM
     */
    public double getRace() {
        return race;
    }

    /**
     * Returns insulin correction factor for MoM
     */
    public double getInsuline() {
        return insuline;
    }

    /**
     * Returns truncation limits for NTD
     */
    public double[] getNTD() {
        return ntd;
    }

    /**
     * Returns truncation limits for Downs
     */
    public double[] getDowns() {
        return downs;
    }

    /**
     * Returns truncation limits for T18
     */
    public double[] getT18() {
        return t18;
    }

    /**
     * Returns truncation limits for SLOS
     */
    public double[] getSLOS() {
        return slos;
    }

    /**
     * Returns min gestational age
     */
    public int getMinDay() {
        return minDay;
    }

    /**
     * Returns max gestational age
     */
    public int getMaxDay() {
        return maxDay;
    }

    /**
     * Returns the value from the intrument for this test
     */
    public Double getResult() {
        return result;
    }

    /**
     * Returns p1
     */
    public Double getP1() {
        return p1;
    }

    /**
     * Returns p2
     */
    public Double getP2() {
        return p2;
    }

    /**
     * Returns p3
     */
    public Double getP3() {
        return p3;
    }

    /**
     * Returns MoM computed using initial gestational age
     */
    public double getMomInit() {
        return momInit;
    }

    /**
     * Sets MoM computed using initial gestational age
     */
    public void setMomInit(double momInit) {
        this.momInit = momInit;
    }

    /**
     * Returns MoM computed using current gestational age
     */
    public double getMomCurr() {
        return momCurr;
    }

    /**
     * Sets MoM computed using current gestational age
     */
    public void setMomCurr(double momCurr) {
        this.momCurr = momCurr;
    }

    /**
     * Returns true if MoMs were computed; returns false otherwise
     */
    public boolean getDidCmpMoM() {
        return didCmpMoM;
    }

    /**
     * Sets true if MoMs were computed; sets false otherwise
     */
    public void setDidCmpMoM(boolean didCmpMoM) {
        this.didCmpMoM = didCmpMoM;
    }
   
    /**
     * Computes Multiple of Medians for the test
     */
    public void computeMoms(int gestAgeInit, int gestAgeCurr, Datetime entered, Double weight,
                            boolean isRaceBlack, boolean isDiabetic) throws Exception {
        double mom;

        if (getResult() != null) {
            try {
                mom = getMoM(gestAgeInit, entered, weight, isRaceBlack, isDiabetic);
                setMomInit(Util.trunc(mom + 0.005, 2));
                mom = getMoM(gestAgeCurr, entered, weight, isRaceBlack, isDiabetic);
                setMomCurr(Util.trunc(mom + 0.005, 2));
                setDidCmpMoM(true);
            } catch (Exception indE) {
                setMomInit(0.0);
                setMomCurr(0.0);
                throw indE;
            }
        }
    }

    /**
     * Returns the weight adjustment factor for MoM
     */
    protected abstract double getAdjWeight(double w);

    /**
     * Returns the median computed based on the passed gestational age
     */
    protected abstract double getMedianforAge(int ga) throws Exception;    

    /**
     * Returns the error shown to the user when some of the p-values required
     * to compute the median for this test are missing
     */
    protected String getPValueError(String... pvals) {
        String pmsg;

        pmsg = null;
        for (String p : pvals)
            pmsg = DataBaseUtil.concatWithSeparator(pmsg, ", ", p);

        return Messages.get().result_pvaluesNotFoundException(pmsg, analyteName);
    }

    /**
     * Returns the computed MoM value after applying corrections
     */
    protected double getMoM(int ga, Datetime entered, Double weight, boolean isRaceBlack,
                          boolean isDiabetic) throws Exception {
        double mom, median, adjWeight, w;

        if (entered == null)
            throw new InconsistencyException(Messages.get().result_missingEnteredDateException());

        /*
         * the weight adjustment factor
         */
        if (weight == null) {
            adjWeight = 0.0;
        } else {
            w = Math.max(90.0, Math.min(350.0, weight.doubleValue()));
            adjWeight = getAdjWeight(w);
        }

        /*
         * the median for gestational age
         */
        if (ga < getMinDay() || ga > getMaxDay())
            median = 0.0;
        else
            median = getMedianforAge(ga);

        if (getResult() == 0.0)
            throw new InconsistencyException(Messages.get().result_invalidResultOfZeroException());
        if (adjWeight == 0.0)
            throw new InconsistencyException(Messages.get().result_invalidAdjWeightOfZeroException());
        if (median == 0.0)
            throw new InconsistencyException(Messages.get().result_noMedianForGestAgeException(ga));

        mom = getResult() / median / adjWeight;
        if (isRaceBlack)
            mom /= getRace();
        if (isDiabetic)
            mom /= getInsuline();
        
        return mom;
    }
}