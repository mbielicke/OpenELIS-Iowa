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
package org.openelis.modules.main.server.constants;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface OpenELISConstants extends ConstantsWithLookup {
    //common
    String greeting();
    String  favorites();
    String queryExpiredException();
    String lastPageException();
    String endingQueryException();
    String storageUnitDeleteException();
    String storageLocDeleteException();
    String firstPageException();
    String inventoryAdjLocAutoException();
    String fieldUniqueException();
    
    //button panel
    String abort();
    String add();
    String commit();
    String delete();
    String next();
    String previous();
    String query();
    String reload();
    String update();
    
    //status bar messages
    String addAborted();
    String adding();
    String addingFailed();
    String addingComplete();
    String loadCompleteMessage();
    String enterFieldsToQuery();
    String enterInformationPressCommit();
    String loadingMessage();
    String correctErrors();
    String queryAborted();
    String querying();
    String queryingComplete();
    String updateFailed();
    String updateFieldsPressCommit();
    String updating();
    String updateAborted();
    String updatingComplete();
    String mustCommitOrAbort();
    String deleteComplete();
    String deleteMessage();
    String deleteAborted();
    String deleting();
    
    //OpenELIS screen
    String file();
    String edit();
    String sampleManagement();
    String analysisManagement();
    String supplyManagement();
    String reports();
    String dataEntry();
    String utilities();
    String window();
    String help();
    String logout();
    String logoutDescription();
    String cut();
    String copy();
    String paste();
    String project();
    String projectDescription();
    String release();
    String releaseDescription();
    String sampleLookup();
    String sampleLookupDescription();
    String analyte();
    String analyteDescription();
    String method();
    String methodDescription();
    String methodPanel();
    String methodPanelDescription();
    String qaEvents();
    String qaEventsDescription();
    String results();
    String resultsDescription();
    String testManagement();
    String testManagementDescription();
    String testTrailer();
    String testTrailerDescription();
    String worksheets();
    String worksheetsDescription();
    String instrument();
    String instrumentDescription();
    String inventory();
    String inventoryDescription();
    String order();
    String orderDescription();
    String storage();
    String storageDescription();
    String storageUnit();
    String category1();
    String category1Description();
    String category2();
    String category2Description();
    String category3();
    String category3Description();
    String report1();
    String report1Description();
    String report2();
    String report2Description();
    String report3();
    String report3Description();
    String fastSampleLogin();
    String fastSampleLoginDescription();
    String organization();
    String organizationDescription();
    String patient();
    String patientDescription();
    String person();
    String personDescription();
    String provider();
    String providerDescription();
    String sampleLogin();
    String sampleLoginDescription();
    String auxiliary();
    String auxiliaryDescription();
    String dictionary();
    String dictionaryDescription();
    String label();
    String labelDescription();
    String referenceTable();
    String referenceTableDescription();
    String scriptlet();
    String scriptletDescription();
    String section();
    String sectionDescription();
    String standardNote();
    String standardNoteDescription(); 
    String systemVariable();
    String systemVariableDescription();
    String organizeFavorites();
    String viewFavoritesMenu();
    String viewFavoritesMenuDescription();
    String closeCurrentWindow();
    String closeAllWindows();
    String index();
    String indexDescription();
    
    //organization form
    String name();
    String organizations();
    String address();
    String phoneEmail();
    String aptSuite();
    String street();
    String city();
    String state();
    String zipcode();
    String country();
    String workNumber();
    String cellNumber();
    String faxNumber();
    String homeNumber();
    String email();
    String active();
    String parentOrganization();
    String contactsForThisOrganization();
    String removeRow();
    String contactName();
    String contact();
    String note();
    String subject();
    String newNote();
    
    //dictionary form
    String dictSystemNameError();
    String dictEntryError();
    String dictNoResults();
}