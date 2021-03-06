package org.openelis.scriptlet.ms.firstTri;

import org.openelis.constants.Messages;
import org.openelis.scriptlet.ms.Util;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores constants such as correction factors for MoM per test
 * and truncation limits for a specific maternal screen such as NT; it also
 * stores other values such as the result generated by the intrument and
 * p-values etc.; it also computes the MoM
 */
public abstract class Test {
    private double downs[], t18[];
    private Double mom, result, p1, p2, p3;
    private int    minDay, maxDay;
    private String analyteName;

    protected Test(double downs[], double t18[], int minDay, int maxDay, String analyteName) {
        this.downs = downs;
        this.t18 = t18;
        this.minDay = minDay;
        this.maxDay = maxDay;
        this.analyteName = analyteName;
    }

    /**
     * Returns truncation limits for Downs
     */
    protected double[] getDowns() {
        return downs;
    }

    /**
     * Returns truncation limits for T18
     */
    protected double[] getT18() {
        return t18;
    }

    /**
     * Returns min gestational age
     */
    protected int getMinDay() {
        return minDay;
    }

    /**
     * Returns max gestational age
     */
    protected int getMaxDay() {
        return maxDay;
    }

    /**
     * Returns the value from the intrument for this test
     */
    protected Double getResult() {
        return result;
    }
    
    /**
     * Sets the value from the intrument for this test
     */
    public void setResult(Double result) {
        this.result = result;
    }

    /**
     * Returns p1
     */
    protected Double getP1() {
        return p1;
    }
    
    /**
     * Sets p1
     */
    public void setP1(Double p1) {
        this.p1 = p1;
    }

    /**
     * Returns p2
     */
    protected Double getP2() {
        return p2;
    }
    
    /**
     * Sets p2
     */
    public void setP2(Double p2) {
        this.p2 = p2;
    }

    /**
     * Returns p3
     */
    protected Double getP3() {
        return p3;
    }
    
    /**
     * Sets p3
     */
    public void setP3(Double p3) {
        this.p3 = p3;
    }

    /**
     * Sets MoM computed using initial gestational age
     */
    protected void setMom(Double mom) {
        this.mom = mom;
    }

    /**
     * Returns the final computed MoM
     */
    public Double getMom() {
        return mom;
    }

    /**
     * Computes Multiple of Medians for the test
     */
    protected void computeMoms(Integer crl, int gestAge, Datetime entered, Double weight,
                            boolean isIntegrated) throws Exception {
        double mom;

        if (getResult() != null) {
            try {
                mom = getMoM(entered, weight, getMeasurement(crl, gestAge), isIntegrated);
                setMom(Util.trunc(mom + 0.005, 2));
            } catch (Exception indE) {
                setMom(null);
                throw indE;
            }
        }
    }
    
    /**
     * Validates the instrument result for this test based on the passed flag;
     * throws an exception if the result is invalid
     */
    protected abstract void validateResult(boolean isIntegrated) throws Exception;

    /**
     * Returns the weight adjustment factor for MoM
     */
    protected abstract double getAdjWeight(double w);

    /**
     * Returns the median computed based on the passed measurement
     */
    protected abstract double getMedian(int measurement) throws Exception;

    /**
     * Returns the value to be used as the measurement for calculating MoM
     */
    protected abstract int getMeasurement(Integer crl, int gestAge);

    /**
     * Returns the error shown to the user when some of the p-values required to
     * compute the median for this test are missing
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
    protected double getMoM(Datetime entered, Double weight, int measurement, boolean isIntegrated) throws Exception {
        double median, adjWeight, w;

        if (entered == null)
            throw new InconsistencyException(Messages.get().result_missingEnteredDateException());

        validateResult(isIntegrated);
        
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
        if (measurement < getMinDay() || measurement > getMaxDay())
            median = 0.0;
        else
            median = getMedian(measurement);

        if (getResult() == null || getResult() == 0.0)
            throw new InconsistencyException(Messages.get().result_missingInvalidResultException(analyteName));
        if (adjWeight == 0.0)
            throw new InconsistencyException(Messages.get()
                                                     .result_missingMaternalWeightException());
        if (median == 0.0)
            throw new InconsistencyException(Messages.get()
                                                     .result_noMedianForGestAgeException(measurement));

        return getResult() / median / adjWeight;
    }
    
    /**
     * Returns true if the MoM was computed; returns false otherwise
     */
    public boolean didCmpMoM() {
        return mom != null;
    }
}