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
package org.openelis.domain;

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class BillToReportToDO implements RPC {

    private static final long serialVersionUID = 1L;
    protected Integer         billToId;
    protected String          billTo;
    public AddressDO          billToAddress    = new AddressDO();

    protected Integer         reportToId;
    protected String          reportTo;
    public AddressDO          reportToAddress  = new AddressDO();

    public BillToReportToDO() {

    }

    public BillToReportToDO(Integer billToId,
                            String billTo,
                            String billToMultUnit,
                            String billToStreetAddress,
                            String billToCity,
                            String billToState,
                            String billToZipcode,
                            Integer reportToId,
                            String reportTo,
                            String reportToMultUnit,
                            String reportToStreetAddress,
                            String reportToCity,
                            String reportToState,
                            String reportToZipcode) {
        setBillToId(billToId);
        setBillTo(billTo);
        billToAddress.setMultipleUnit(billToMultUnit);
        billToAddress.setStreetAddress(billToStreetAddress);
        billToAddress.setCity(billToCity);
        billToAddress.setState(billToState);
        billToAddress.setZipCode(billToZipcode);

        setReportToId(reportToId);
        setReportTo(reportTo);
        reportToAddress.setMultipleUnit(reportToMultUnit);
        reportToAddress.setStreetAddress(reportToStreetAddress);
        reportToAddress.setCity(reportToCity);
        reportToAddress.setState(reportToState);
        reportToAddress.setZipCode(reportToZipcode);
    }

    public Integer getBillToId() {
        return billToId;
    }

    public void setBillToId(Integer billToId) {
        this.billToId = billToId;
    }

    public Integer getReportToId() {
        return reportToId;
    }

    public void setReportToId(Integer reportToId) {
        this.reportToId = reportToId;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = DataBaseUtil.trim(billTo);
    }

    public String getReportTo() {
        return reportTo;
    }

    public void setReportTo(String reportTo) {
        this.reportTo = DataBaseUtil.trim(reportTo);
    }
}
