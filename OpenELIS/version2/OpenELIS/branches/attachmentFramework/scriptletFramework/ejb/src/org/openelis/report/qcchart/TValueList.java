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
package org.openelis.report.qcchart;

public class TValueList {
    
    protected static double tValues[] = {31.8193,6.9646,4.5407,3.7470,
                                         3.3650,3.1426,2.9980,2.8965,
                                         2.8214,2.7638,2.7181,2.6810,
                                         2.6503,2.6245,2.6025,2.5835,
                                         2.5669,2.5524,2.5395,2.5280,
                                         2.5176,2.5083,2.4998,2.4922,
                                         2.4851,2.4786,2.4727,2.4671,
                                         2.4620,2.4572,2.4528,2.4487,
                                         2.4448,2.4411,2.4377,2.4345,
                                         2.4315,2.4286,2.4258,2.4233,
                                         2.4208,2.4185,2.4162,2.4142,
                                         2.4121,2.4102,2.4083,2.4066,
                                         2.4049,2.4033,2.4017,2.4002,
                                         2.3988,2.3974,2.3961,2.3948,
                                         2.3936,2.3924,2.3912,2.3901,
                                         2.3890,2.3880,2.3870,2.3860,
                                         2.3851,2.3842,2.3833,2.3824,
                                         2.3816,2.3808,2.3800,2.3793,
                                         2.3785,2.3778,2.3771,2.3764,
                                         2.3758,2.3751,2.3745,2.3739,
                                         2.3733,2.3727,2.3721,2.3716,
                                         2.3710,2.3705,2.3700,2.3695,
                                         2.3690,2.3685,2.3680,2.3676,
                                         2.3671,2.3667,2.3662,2.3658,
                                         2.3654,2.3650,2.3646,2.3642,
                                         2.3638,2.3635,2.3631,2.3627,
                                         2.3624,2.3620,2.3617,2.3614,
                                         2.3611,2.3607,2.3604,2.3601,
                                         2.3598,2.3595,2.3592,2.3589,
                                         2.3586,2.3583,2.3581,2.3578,
                                         2.3576,2.3573,2.3571,2.3568,
                                         2.3565,2.3563,2.3561,2.3559,
                                         2.3556,2.3554,2.3552,2.3549,
                                         2.3547,2.3545,2.3543,2.3541,
                                         2.3539,2.3537,2.3535,2.3533,
                                         2.3531,2.3529,2.3527,2.3525,
                                         2.3523,2.3522,2.3520,2.3518,
                                         2.3516,2.3515,2.3513,2.3511,
                                         2.3510,2.3508,2.3507,2.3505,
                                         2.3503,2.3502,2.3500,2.3499,
                                         2.3497,2.3496,2.3495,2.3493,  
                                         2.3492,2.3490,2.3489,2.3487,
                                         2.3486,2.3485,2.3484,2.3482,
                                         2.3481,2.3480,2.3478,2.3477, 
                                         2.3476,2.3475,2.3474,2.3472,
                                         2.3471,2.3470,2.3469,2.3468,   
                                         2.3467,2.3466,2.3465,2.3463,    
                                         2.3463,2.3461,2.3460,2.3459,    
                                         2.3458,2.3457,2.3456,2.3455,
                                         2.3454,2.3453,2.3452,2.3451};

    public static double getTValue(int df, double sd) {        
        if (df <= 1)
            return 0.0;
        else if (df >= tValues.length - 1)
            return 2.326;
        
        return sd * tValues[df - 2] ;
    }
}
