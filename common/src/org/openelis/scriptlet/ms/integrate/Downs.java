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

/**
 * The class that stores data such as means and constant multipliers for Downs
 * for 2nd trimester integrate test; it also computes the disorder's risk
 */
public class Downs extends Risk {
    
    private double constUA10[], constUA11[], constUA12[], constUANoNT[], constDSNT[],
                    constDSNoNT[];
    
    protected Downs() {
        super(new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        constUA10 = new double[] {56.7927, -14.4620, -2.6836, -7.1287, -2.5938,
                                   0.1256, -14.4620, 82.3742,  1.1063,  5.4385,                                                                                                                         
                                   -3.8827,  -2.6702, -2.6836,  1.1063, 23.9102,                                                                                                                          
                                   -10.7565,  -1.0036,  1.0741, -7.1287,  5.4385,                                                                                                                        
                                   -10.7565,  29.4642,  0.2067,  0.4808, -2.5938,                                                                                                                                                                                                                              
                                   -3.8827,  -1.0036,  0.2067, 16.5334,  1.0698,                                                                                                                        
                                   0.1256,  -2.6702,  1.0741,  0.4808,  1.0698,                                                                                                                                                                                                                              
                                   33.5908};
        constUA11 = new double[] {56.7929, -14.4666, -2.6818, -7.1279, -2.5920,                                                                                                                         
                                   0.1829, -14.4666, 82.4707,  1.0674,  5.4213,                                                                                                                         
                                   -3.9212,  -3.8830, -2.6818,  1.0674, 23.9259,                                                                                                                          
                                   -10.7496,  -0.9881,  1.5631, -7.1279,  5.4213,                                                                                                                        
                                   -10.7496,  29.4672,  0.2136,  0.6964, -2.5920,                                                                                                                                                                                                                              
                                   -3.9212,  -0.9881,  0.2136, 16.5488,  1.5542,                                                                                                                        
                                   0.1829,  -3.8830,  1.5631,  0.6964,  1.5542,                                                                                                                                                                                                                              
                                   48.8304};
        constUA12 = new double[] {56.7930, -14.4692, -2.6807, -7.1274, -2.5910,                                                                                                                         
                                   0.2153, -14.4692, 82.5243,  1.0456,  5.4118,                                                                                                                         
                                   -3.9428,  -4.5594, -2.6807,  1.0456, 23.9348,                                                                                                                          
                                   -10.7457, -0.9793,  1.8378, -7.1274,  5.4118,                                                                                                                        
                                   -10.7457, 29.4689,  0.2174,  0.8161, -2.5910,                                                                                                                                                                                                                              
                                   -3.9428, -0.9793,  0.2174, 16.5575,  1.8263,                                                                                                                        
                                   0.2153, -4.5594,  1.8378,  0.8161,  1.8263,                                                                                                                                                                                                                              
                                   57.3584};
        constUANoNT = new double[] {56.7922, -14.4520, -2.6876, -7.1305, -2.5978,                                                                                                                         
                                     -14.4520,  82.1619,  1.1917,  5.4767, -3.7976,                                                                                                                         
                                     -2.6876,   1.1917, 23.8759,-10.7719, -1.0378,                                                                                                                          
                                     -7.1305,   5.4767,-10.7719, 29.4573,  0.1914,                                                                                                                        
                                     -2.5978,  -3.7976, -1.0378,  0.1914, 16.4993};
        constDSNT = new double[] {55.2443,  5.3612, -4.8704, -2.8740, -4.5171,                                                                                                                         
                                   -2.8726,  5.3612, 91.1545, 10.4334,  8.2722,                                                                                                                         
                                   -12.6473,-8.2401, -4.8704, 10.4334, 23.6077,                                                                                                                          
                                   -6.5208,  1.9908,  0.4744, -2.8740,  8.2722,                                                                                                                        
                                   -6.5208, 18.5536,  0.2552, -3.7901, -4.5171,    
                                   -12.6473, 1.9908,  0.2552, 15.8214,  3.4269,                                                                                                                         
                                   -2.8726, -8.2401,  0.4744, -3.7901,  3.4269,                                                                                                                          
                                   20.5549};
        constDSNoNT = new double[] {54.8428,  4.2096, -4.8041, -3.4037,  -4.0382,                                                                                                                         
                                     4.2096, 87.8512, 10.6236,  6.7528, -11.2735,                                                                                                                         
                                     -4.8041, 10.6236, 23.5968, -6.4334,   1.9117,                                                                                                                          
                                     -3.4037,  6.7528, -6.4334, 17.8547,   0.8871,                                                                                                                        
                                     -4.0382,-11.2735,  1.9117,  0.8871,  15.2500};
    }

    protected double[] getMeanDS(Integer gestAge1T, Integer gestAge1TNT) {
        double means[] = {-0.1308, -0.1549, 0.3118, 0.3384,
                          -1.1444 + 0.00965 * gestAge1T,
                          0.8554679 - 0.0064686 * gestAge1TNT};
       return means;
    }
    
    protected double[] getConstUA(Double ntMom, Integer gestAge1TNT) {
        if (ntMom != null) {
            if (gestAge1TNT < 77)         // 10th week
                return constUA10;
            else if (gestAge1TNT < 84)   // 11th week
                return constUA11;
            else                        // 12 & 13th week
                return constUA12;
        } else {
            return constUANoNT;
        }
    }

    protected double[] getConstDS(Double ntMom) {
        if (ntMom != null)
            return constDSNT;
        else
            return constDSNoNT;
    }
    
    protected double getDiscrUA(Double ntMom, Integer gestAge1TNT) {
        if (ntMom != null) {
            if (gestAge1TNT < 77)        // 10th week
                return 0.000000000755644;
            else if (gestAge1TNT < 84) // 11th week
                return 0.000000000519813;
            else                       // 12 & 13th week
                return 0.000000000442528;
        } else {
            return 0.0000000253827;
        }
    }

    protected double getDiscrDS(Double ntMom) {
        if (ntMom != null)
            return 0.00000000253129;
        else
            return 0.0000000520304;
    }

    @Override
    protected void setRisk(double apr, double ordinalUA, double ordinalDS) {
        setRisk((int) (apr / (ordinalDS / ordinalUA)));
    }
}