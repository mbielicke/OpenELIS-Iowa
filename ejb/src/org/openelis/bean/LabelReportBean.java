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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.local.LabelReportLocal;

/**
 * 
 * This class prints barcode labels for ZPL type printers
 *
 */
@Stateless
@SecurityDomain("openelis")
public class LabelReportBean implements LabelReportLocal {

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
        f.print("^FO10,35^AE^BCN,50,N,N,N^FD"+s+"^FS");             // barcoded accession
        f.print("^FO10,85^AE^FD"+s+"   ("+location+")^FS");         // readable accession + location
        f.print("^FO10,130^AE^BCN,50,Y,N,N^FD"+received+"^FS");     // barcoded/readable received
        f.print("^PQ1,,1,^XZ");
    }
    
    /*
     * Prints a barcode label for shipping address
     */
    public void shippingAddressLabel(PrintStream f, String name, String method, String costCenter, 
                                     String fromStreetAddress1, String shippingId, 
                                     String fromStreetAddress2, String fromCity,
                                     String fromState, String fromZip, String attention, 
                                     String toStreetAddress1, String toStreetAddress2,
                                     String toCity, String toState, String toZip) {

        f.print("^XA");
        f.print("^FO15,15^A040,40^FD"+name+"^FS");                                 // the lab's name
        f.print("^FO600,15^A050,50^FD"+method+"^FS");                              // shipping method like UPS Ground
        f.print("^FO15,65^A040,40^FD"+fromStreetAddress1+"^FS");                   // 1st line of the lab's street address  
        f.print("^FO600,105^AE^BY2^BCN,50,Y,N,N^FD"+shippingId+"^FS");             // barcoded/readable shipping id
        f.print("^FO15,115^A040,40^FD"+fromStreetAddress2+"^FS");                  // 2nd line of the lab's street address
        f.print("^FO15,165^A040,40^FD"+fromCity+", "+fromState+" "+fromZip+"^FS"); // the lab's city, state, zip code
        if (costCenter != null)
            f.print("^FO600,215^A040,40^FD"+costCenter+"^FS");                     // the order's cost center
        if (attention != null)
            f.print("^FO150,500^A048,42^FD"+attention+"^FS");                      // the attention line for the receiver
        f.print("^FO150,550^A048,42^FD"+toStreetAddress1+"^FS");                   // the receiver's 1st line of street address 
        f.print("^FO150,600^A048,42^FD"+toStreetAddress2+"^FS");                   // the receiver's 2nd line of street address 
        f.print("^FO150,650^A048,42^FD"+toCity+", "+toState+" "+toZip+"^FS");      // the receiver's city, state, zip code
        f.print("^FO150,700^BY4^BZN,50,N,N^FD"+toZip+"^FS");                       // the receiver's barcoded zip code  
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
        f.print("^FO15,250^FDBuild Id: ^FS^FO150,250^BY2^BCN,50,Y,N,N^FD"+buildId+"^FS"); // the id of the internal order created when                                         
        f.print("^FO550,250^FDExpires: "+DataBaseUtil.toString(expiredDate)+"^FS");       // the date of expiration of the kit, if any
        f.print("^FO15,350^FD"+kitDescription+"^FS");                                     // the descriptive text for the kit         
        f.print("^FO0,400^GB900,5,5^FS");
        f.print("^FB900,1,0,C,0");
        f.print("^FO15,420^FD"+DataBaseUtil.toString(specialInstruction)+"^FS");           // the instruction associated with this kit, if any  
        f.print("^FO15,500^FDFacility or Location: _________________________________^FS");
        f.print("^FO15,600^FD__________________________________________________^FS");
        f.print("^FO15,700^FDDate: ___________________ Time: ___________ AM / PM^FS");
        f.print("^FO15,800^FDCollector: __________________________________________^FS");
        f.print("^XZ");
    }
}