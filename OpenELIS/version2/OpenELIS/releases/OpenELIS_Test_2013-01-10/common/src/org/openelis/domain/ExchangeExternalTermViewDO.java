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

/**
 * The class extends exchange external term DO and adds a reference table id and
 * reference id from the exchange local term to which the record represented by 
 * this DO is linked. The additional fields are used to  make it easier to establish
 * that link between the two records and do not get committed to the database. 
 * Note: isChanged will reflect any changes to these fields.
 */
public class ExchangeExternalTermViewDO extends ExchangeExternalTermDO {

    private static final long serialVersionUID = 1L;
    
    protected Integer         exchangeLocalTermReferenceTableId, exchangeLocalTermReferenceId;
    
    public ExchangeExternalTermViewDO() {
    }

    public ExchangeExternalTermViewDO(Integer id, Integer exchangeLocalTermId, Integer profileId,
                                  String isActive, String externalTerm, String externalDescription,
                                  String externalCodingSystem, String version,
                                  Integer exchangeLocalTermReferenceTableId,
                                  Integer exchangeLocalTermReferenceId) {
        super(id, exchangeLocalTermId, profileId, isActive, externalTerm, externalDescription,
                                   externalCodingSystem, version);
        setExchangeLocalTermReferenceTableId(exchangeLocalTermReferenceTableId);
        setExchangeLocalTermReferenceId(exchangeLocalTermReferenceId);
    }

    public Integer getExchangeLocalTermReferenceTableId() {
        return exchangeLocalTermReferenceTableId;
    }

    public void setExchangeLocalTermReferenceTableId(Integer exchangeLocalTermReferenceTableId) {
        this.exchangeLocalTermReferenceTableId = exchangeLocalTermReferenceTableId;
    }

    public Integer getExchangeLocalTermReferenceId() {
        return exchangeLocalTermReferenceId;
    }

    public void setExchangeLocalTermReferenceId(Integer exchangeLocalTermReferenceId) {
        this.exchangeLocalTermReferenceId = exchangeLocalTermReferenceId;
    }
}