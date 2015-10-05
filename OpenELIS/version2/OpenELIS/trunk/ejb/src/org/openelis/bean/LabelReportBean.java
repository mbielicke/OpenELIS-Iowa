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
package org.openelis.bean;

import java.io.PrintStream;

import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.ui.common.DataBaseUtil;

/**
 * 
 * This class prints barcode labels for ZPL type printers
 *
 */
@Stateless
@SecurityDomain("openelis")
public class LabelReportBean {

    /*
     * Prints a barcode label for sample login
     */
    public void sampleLoginLabel(PrintStream f, int accession, int container, String received, String location) {
        String s;
        
        if (container == -1)
            s = String.valueOf(accession);
        else
            s = accession + "-" + container;
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO50,35^AE^B3N,N,80,N,N^FD"+s+"^FS");             // barcoded accession
        f.print("^FO50,120^AE^FD"+s+"   ("+location+")^FS");        // readable accession + location
        f.print("^FO50,160^AE^BCN,80,Y,N,N^FD"+received+"^FS");     // barcoded/readable received
        f.print("^PQ1,,1,^XZ");
    }
    
    /*
     * Prints a barcode label for shipping address
     */
    public void shippingAddressLabel(PrintStream f, String labName, String method, String costCenter, 
                                     String fromMultipleUnit, String shippingId, 
                                     String fromStreetAddress, String fromCity,
                                     String fromState, String fromZip, String attention, 
                                     String orgName, String toMultipleUnits, String toStreetAddress,
                                     String toCity, String toState, String toZip) {
        int p;
        String csz;
        
        f.print("^XA");
        p = 15;
        f.printf("^FO15,%d^A040,40^FD%s^FS",p, labName);                     // the lab's name     
        f.printf("^FO560,%d^A050,50^FD%s^FS",p, method);                     // shipping method like UPS Ground
        if (fromMultipleUnit != null)
            f.printf("^FO15,%d^A040,40^FD%s^FS",p+=50, fromMultipleUnit);    // apt/suite of the lab's address
        f.printf("^FO560,%d^AE^BY2^BCN,50,N,N,N^FD%s^FS",p+=40, shippingId); // barcode shipping id   
        f.printf("^FO15,%d^A040,40^FD%s^FS",p+=10, fromStreetAddress);       // the lab's street address
        csz = DataBaseUtil.concatWithSeparator(DataBaseUtil.concatWithSeparator(fromCity, ", ", fromState), " ", fromZip);
        f.printf("^FO15,%d^A040,40^FD%s^FS",p+=50, csz);                     // the lab's city, state, zip code
        f.printf("^FO560,%d^A040,40^FD%s^FS",p, shippingId);                 // readable shipping id
        if (costCenter != null)
            f.printf("^FO560,%d^A040,40^FD%s^FS",p+=50, costCenter);         // the iorder's cost center
        p = 500;
        if (attention != null)
            f.printf("^FO150,%d^A048,42^FD%s^FS",p, attention);                // the attention line for the receiver
        f.printf("^FO150,%d^A048,42^FD%s^FS",p+=50, orgName);                  // the receiver's name
        if (toMultipleUnits != null)
            f.printf("^FO150,%d^A048,42^FD%s^FS",p+=50, toMultipleUnits);      // the receiver's apt/suite
        f.printf("^FO150,%d^A048,42^FD%s^FS",p+=50, toStreetAddress);          // the receiver's street address
        csz = DataBaseUtil.concatWithSeparator(DataBaseUtil.concatWithSeparator(toCity, ", ", toState), " ", toZip);
        f.printf("^FO150,%d^A048,42^FD%s^FS",p+=50, csz);                      // the receiver's city, state, zip code 
        if (toZip != null)
            f.printf("^FO150,%d^BY4^BZN,50,N,N^FD%s^FS",p+=50, toZip);         // the receiver's barcoded zip code
        f.print("^XZ");
    }
    
    /*
     * Prints a barcode label for kit
     */
    public void kitLabel(PrintStream f, String name, String locPhone1, String locPhone2,
                         String lotNumber, String createdDate, String buildId,
                         String expiredDate, String kitDescription, String specialInstruction) {                        
        f.print("^XA");
        f.print("^CF040,50");
        f.print("^FO190,15^FD"+name+"^FS");                                               // the lab's name
        f.print("^CF030,40"); 
        f.print("^FO250,80^FD"+locPhone1+"^FS");                                          // the location like Iowa City and phone number  
        f.print("^FO250,130^FD"+locPhone2+"^FS");                                         // another location like Ankeny and phone number  
        f.print("^FO0,180^GB900,5,5^FS");
        f.print("^CF025,35");        
        f.print("^FO15,200^FDLot #: "+DataBaseUtil.toString(lotNumber)+"^FS");            // the lot # given to the kit, if any         
        f.print("^FO550,200^FDCreated: "+createdDate+"^FS");                              // the date on which the kit was created
        f.print("^FO15,250^FDBuild Id: ^FS^FO150,250^BY2^BCN,50,N,N,N^FD"+buildId+"^FS"); // the id of the internal iorder created when                                         
        f.print("^FO550,250^FDExpires: "+DataBaseUtil.toString(expiredDate)+"^FS");       // the date of expiration of the kit, if any
        f.print("^FO200,300^FD"+buildId+"^FS");                                           // the human-readable internal iorder number
        f.print("^FO15,350^FD"+kitDescription+"^FS");                                     // the descriptive text for the kit         
        f.print("^FO0,400^GB900,5,5^FS");
        f.print("^FO15,420^FB900,4,0,C,0");                                               // defines the region for printing the special instructions     
        f.print("^FD"+DataBaseUtil.toString(specialInstruction)+"^FS");                   // the instruction associated with this kit, if any  
        f.print("^FO15,600^FDFacility or Location: _________________________________^FS");
        f.print("^FO15,700^FDDate: ___________________ Time: ___________ AM / PM^FS");
        f.print("^FO15,800^FDCollector: __________________________________________^FS");
        f.print("^XZ");
    }
    
    
    /*
     * Print a test label showing accession number and received date;
     * quantity is the number of copies to be printed
     */
    public void accessionReceivedLabel(PrintStream f, int accession, String received, int quantity) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO60,35^AE^BCN,50,Y,N,N^FD"+accession+"^FS");     // barcoded accession
        f.print("^FO60,130^AE^BCN,50,Y,N,N^FD"+received+"^FS");     // barcoded received date
        f.print("^PQ"+quantity+",,1,^XZ");
    }
    
    /*
     * Print a test label showing accession number, received date and test name;
     * quantity is the number of copies to be printed
     */
    public void accessionReceivedTestLabel(PrintStream f, int accession, String received, String testName,
                                int quantity) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO60,35^AE^BCN,50,Y,N,N^FD"+accession+"^FS");     // barcoded accession
        f.print("^FO60,130^AE^FD"+received+"^FS");                  // readable received date
        f.print("^FO60,165^AE^FD"+testName+"^FS");                  // readable test name
        f.print("^PQ"+quantity+",,1,^XZ");
    }
    
    /*
     * Print a test label showing accession number, received date and test and method names;
     * quantity is the number of copies to be printed
     */
    public void accessionReceivedTestMethodLabel(PrintStream f, int accession, String received,
                                      String testName, String methodName, int quantity) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO60,35^AE^BCN,50,Y,N,N^FD"+accession+"^FS");       // barcoded accession
        f.print("^FO60,130^AE^FD"+received+"^FS");                    // readable received date
        f.print("^FO60,165^AD^FD"+testName+","+methodName+"^FS");     // readable test name + method name
        f.print("^PQ"+quantity+",,1,^XZ");
    }
    
    /*
     * Print a test label showing accession number, patient name, received date and test name;
     * quantity is the number of copies to be printed
     */
    public void accessionPatientReceivedTestLabel(PrintStream f, int accession, String patientName,
                                   String received, String testName, int quantity) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO60,35^AE^BCN,50,Y,N,N^FD"+accession+"^FS");     // barcoded accession
        f.print("^FO60,130^AE^FD"+patientName+"^FS");               // readable patient name
        f.print("^FO60,170^AE^FD"+received+" "+testName+"^FS");     // readable received date + test name  
        f.print("^PQ"+quantity+",,1,^XZ");
    }
    
    /*
     * Print a test label showing accession number, collection date, received
     * date, patient name, sample type, test name, and method name;
     * quantity is the number of copies to be printed
     */
    public void accessionCollectionReceivedPatientTypeTestLabel(PrintStream f, int accession,
                                                                String collection, String received,
                                                                String patientName,
                                                                String sampleType, String testName,
                                                                String methodName, int quantity) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO60,35^AE^BCN,50,Y,N,N^FD"+accession+"^FS");     // barcoded accession
        f.print("^FO60,130^AF^FD"+collection+"^FS");                // readable collection date
        f.print("^FO350,130^AF^FD"+received+"^FS");                 // readable received date
        f.print("^FO60,165^AF^FD"+patientName+"^FS");               // readable patient name
        f.print("^FO60,200^AF^FD"+sampleType+"^FS");                // readable sample type  
        f.print("^FO60,235^AF^FD"+testName+", "+methodName+"^FS");  // readable test name + method name  
        f.print("^PQ"+quantity+",,1,^XZ");
    }
    
    /*
     * Print a tube label for blood lead
     */
    public void tubeLabel(PrintStream f, String tubeNumber) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO30,60^AE^FDData Slip Label^FS");
        f.print("^FWB");
        f.print("^FO550,5^AE^FDTube Label^FS");
        f.print("^FO600,65^AE^FD"+tubeNumber+"^FS");                  //readable tube number
        f.print("^FWN");
        f.print("^FO30,165^BY3^BCN,90,Y,N,N^FD"+tubeNumber+"^FS");    //barcoded tube number
        f.print("^PQ1,,1,^XZ");
    }
    
    /*
     * Print a small worksheet analysis label
     */
    public void worksheetAnalysisSmallLabel(PrintStream f, String accession, String worksheetPosition,
                                            String name1, String name2, String started,
                                            String users, String qcLink) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO60,30^AQ^FD"+accession+"  ("+worksheetPosition+")^FS");
        f.print("^FO60,60^AP^FD"+name1+"^FS");
        f.print("^FO60,80^AP^FD"+name2+"^FS");
        f.print("^FO60,100^AP^FD"+started+"^FS");
        f.print("^FO60,120^AP^FD"+users+"^FS");
        f.print("^FO140,115^AQ^FDq:"+qcLink+"^FS");
        f.print("^PQ1,,1,^XZ");
    }
    
    /*
     * Print a large worksheet analysis label
     */
    public void worksheetAnalysisDilutionLabel(PrintStream f, String accession,
                                               String worksheetPosition, String dilution,
                                               String name1, String name2) {
        f.print("^XA");
        f.print("^LH0,0");
        f.print("^FO30,35^AT^BCN,50,Y,N,N^FD"+accession+"^FS");                                 // barcoded accession
        if (dilution.length() > 0)
            f.print("^FO30,140^AT^FDWS: "+worksheetPosition+" Dilution: "+dilution+"^FS");      // readable worksheet position and dilution
        else
            f.print("^FO30,140^AT^FDWS: "+worksheetPosition+"^FS");                             // readable worksheet position
        f.print("^FO30,190^AT^FD"+name1+"^FS");                                                 // readable test name or qc name
        f.print("^FO30,240^AT^FD"+name2+"^FS");                                                 // readable method name or qc lot number
        f.print("^PQ1,,1,^XZ");
    }
}