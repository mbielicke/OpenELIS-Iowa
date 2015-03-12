package org.openelis.portal.messages;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/nfalat/workspace/OpenELIS-Portal/war/WEB-INF/classes/org/openelis/portal/messages/PortalMessages.properties'.
 */
public interface PortalMessages extends org.openelis.ui.messages.UIMessages {
  
  /**
   * Translated "Completed Date".
   * 
   * @return translated "Completed Date"
   */
  @DefaultMessage("Completed Date")
  @Key("analysis.completedDate")
  String analysis_completedDate();

  /**
   * Translated "Method".
   * 
   * @return translated "Method"
   */
  @DefaultMessage("Method")
  @Key("analysis.method")
  String analysis_method();

  /**
   * Translated "QA Event".
   * 
   * @return translated "QA Event"
   */
  @DefaultMessage("QA Event")
  @Key("analysis.qaEvent")
  String analysis_qaEvent();

  /**
   * Translated "Revision".
   * 
   * @return translated "Revision"
   */
  @DefaultMessage("Revision")
  @Key("analysis.revision")
  String analysis_revision();

  /**
   * Translated "Started Date".
   * 
   * @return translated "Started Date"
   */
  @DefaultMessage("Started Date")
  @Key("analysis.startedDate")
  String analysis_startedDate();

  /**
   * Translated "Test".
   * 
   * @return translated "Test"
   */
  @DefaultMessage("Test")
  @Key("analysis.test")
  String analysis_test();

  /**
   * Translated "Unit".
   * 
   * @return translated "Unit"
   */
  @DefaultMessage("Unit")
  @Key("analysis.unit")
  String analysis_unit();

  /**
   * Translated "Select Analysis Fields For Output".
   * 
   * @return translated "Select Analysis Fields For Output"
   */
  @DefaultMessage("Select Analysis Fields For Output")
  @Key("dataView.analysisFields")
  String dataView_analysisFields();

  /**
   * Translated "Test Analyte".
   * 
   * @return translated "Test Analyte"
   */
  @DefaultMessage("Test Analyte")
  @Key("dataView.analyte")
  String dataView_analyte();

  /**
   * Translated "Auxiliary Data".
   * 
   * @return translated "Auxiliary Data"
   */
  @DefaultMessage("Auxiliary Data")
  @Key("dataView.aux")
  String dataView_aux();

  /**
   * Translated "Continue to Report Fields".
   * 
   * @return translated "Continue to Report Fields"
   */
  @DefaultMessage("Continue to Report Fields")
  @Key("dataView.continue")
  String dataView_continue();

  /**
   * Translated "Select Environmental Fields For Output".
   * 
   * @return translated "Select Environmental Fields For Output"
   */
  @DefaultMessage("Select Environmental Fields For Output")
  @Key("dataView.environmentalFields")
  String dataView_environmentalFields();

  /**
   * Translated "no results were found".
   * 
   * @return translated "no results were found"
   */
  @DefaultMessage("no results were found")
  @Key("dataView.error.noResults")
  String dataView_error_noResults();

  /**
   * Translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.".
   * 
   * @return translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples."
   */
  @DefaultMessage("The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.")
  @Key("dataView.help.accession")
  String dataView_help_accession();

  /**
   * Translated "This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.".
   * 
   * @return translated "This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample."
   */
  @DefaultMessage("This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.")
  @Key("dataView.help.clientReference")
  String dataView_help_clientReference();

  /**
   * Translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.".
   * 
   * @return translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field."
   */
  @DefaultMessage("The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.")
  @Key("dataView.help.collected")
  String dataView_help_collected();

  /**
   * Translated "The name or ID of the collector of the environmental sample.".
   * 
   * @return translated "The name or ID of the collector of the environmental sample."
   */
  @DefaultMessage("The name or ID of the collector of the environmental sample.")
  @Key("dataView.help.envCollector")
  String dataView_help_envCollector();

  /**
   * Translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.".
   * 
   * @return translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates."
   */
  @DefaultMessage("The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.")
  @Key("dataView.help.patientBirth")
  String dataView_help_patientBirth();

  /**
   * Translated "The first name of the patient for a clinical sample.".
   * 
   * @return translated "The first name of the patient for a clinical sample."
   */
  @DefaultMessage("The first name of the patient for a clinical sample.")
  @Key("dataView.help.patientFirst")
  String dataView_help_patientFirst();

  /**
   * Translated "The last name of the patient for a clinical sample.".
   * 
   * @return translated "The last name of the patient for a clinical sample."
   */
  @DefaultMessage("The last name of the patient for a clinical sample.")
  @Key("dataView.help.patientLast")
  String dataView_help_patientLast();

  /**
   * Translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects.".
   * 
   * @return translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects."
   */
  @DefaultMessage("The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects.")
  @Key("dataView.help.project")
  String dataView_help_project();

  /**
   * Translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.".
   * 
   * @return translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number."
   */
  @DefaultMessage("The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.")
  @Key("dataView.help.pws")
  String dataView_help_pws();

  /**
   * Translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.".
   * 
   * @return translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field."
   */
  @DefaultMessage("The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.")
  @Key("dataView.help.released")
  String dataView_help_released();

  /**
   * Translated "<span class=\"helpText\"><span class=\"helpHeader\">Locating Your Results</span><p>The Spreadsheet View option will display your results in a spreadsheet that you can sort and filter. It could be used to compare results from the same sampling location or the same patient over time, among other things. The first step is to search for the results that you would like to be displayed in the spreadsheet.</p><p>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b>.</p><p>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.</p><p>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.</p><p>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. </p><p>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. </p><p>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.</p><p>Click the <b>Continue to Report Fields</b> button to select the data that you want to be displayed on your spreadsheet. The <b>Reset</b> button clears all of the search fields.</p><span class=\"helpHeader\">Disclaimer</span><p>Results from this report represent analytical values as of the date they are generated. Future revisions may affect these results and official final results should be reviewed from the Final Report option to assure their accuracy.</p></span>".
   * 
   * @return translated "<span class=\"helpText\"><span class=\"helpHeader\">Locating Your Results</span><p>The Spreadsheet View option will display your results in a spreadsheet that you can sort and filter. It could be used to compare results from the same sampling location or the same patient over time, among other things. The first step is to search for the results that you would like to be displayed in the spreadsheet.</p><p>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b>.</p><p>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.</p><p>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.</p><p>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. </p><p>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. </p><p>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.</p><p>Click the <b>Continue to Report Fields</b> button to select the data that you want to be displayed on your spreadsheet. The <b>Reset</b> button clears all of the search fields.</p><span class=\"helpHeader\">Disclaimer</span><p>Results from this report represent analytical values as of the date they are generated. Future revisions may affect these results and official final results should be reviewed from the Final Report option to assure their accuracy.</p></span>"
   */
  @DefaultMessage("<span class=\"helpText\"><span class=\"helpHeader\">Locating Your Results</span><p>The Spreadsheet View option will display your results in a spreadsheet that you can sort and filter. It could be used to compare results from the same sampling location or the same patient over time, among other things. The first step is to search for the results that you would like to be displayed in the spreadsheet.</p><p>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b>.</p><p>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.</p><p>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.</p><p>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. </p><p>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. </p><p>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.</p><p>Click the <b>Continue to Report Fields</b> button to select the data that you want to be displayed on your spreadsheet. The <b>Reset</b> button clears all of the search fields.</p><span class=\"helpHeader\">Disclaimer</span><p>Results from this report represent analytical values as of the date they are generated. Future revisions may affect these results and official final results should be reviewed from the Final Report option to assure their accuracy.</p></span>")
  @Key("dataView.help.screen")
  String dataView_help_screen();

  /**
   * Translated "The name or ID of the collector of the safe drinking water sample.".
   * 
   * @return translated "The name or ID of the collector of the safe drinking water sample."
   */
  @DefaultMessage("The name or ID of the collector of the safe drinking water sample.")
  @Key("dataView.help.sdwisCollector")
  String dataView_help_sdwisCollector();

  /**
   * Translated "<span class=\"helpText\"><span class=\"helpHeader\">How to Customize your Spreadsheet</span><p>Below are groups of fields containing sample, organization, analysis, patient, and other information which you can select to be displayed as columns in your spreadsheet. This screen also shows all of the test analytes and auxiliary data that matches your search criteria. You need to select at least one Test Analyte or Auxiliary Data field to run the report. You can use the <b>Select All</b> buttons if you want to view all of the available information.</p><p>Click the <b>Run Report</b> button to pop up the spreadsheet containing all of the data that you selected. You may view, save, or print your spreadsheet reports.</p><span class=\"helpHeader\">Disclaimer</span><p>Results from this report represent analytical values as of the date they are generated. Future revisions may affect these results and official final results should be reviewed from the Final Report option to assure their accuracy.</p></span>".
   * 
   * @return translated "<span class=\"helpText\"><span class=\"helpHeader\">How to Customize your Spreadsheet</span><p>Below are groups of fields containing sample, organization, analysis, patient, and other information which you can select to be displayed as columns in your spreadsheet. This screen also shows all of the test analytes and auxiliary data that matches your search criteria. You need to select at least one Test Analyte or Auxiliary Data field to run the report. You can use the <b>Select All</b> buttons if you want to view all of the available information.</p><p>Click the <b>Run Report</b> button to pop up the spreadsheet containing all of the data that you selected. You may view, save, or print your spreadsheet reports.</p><span class=\"helpHeader\">Disclaimer</span><p>Results from this report represent analytical values as of the date they are generated. Future revisions may affect these results and official final results should be reviewed from the Final Report option to assure their accuracy.</p></span>"
   */
  @DefaultMessage("<span class=\"helpText\"><span class=\"helpHeader\">How to Customize your Spreadsheet</span><p>Below are groups of fields containing sample, organization, analysis, patient, and other information which you can select to be displayed as columns in your spreadsheet. This screen also shows all of the test analytes and auxiliary data that matches your search criteria. You need to select at least one Test Analyte or Auxiliary Data field to run the report. You can use the <b>Select All</b> buttons if you want to view all of the available information.</p><p>Click the <b>Run Report</b> button to pop up the spreadsheet containing all of the data that you selected. You may view, save, or print your spreadsheet reports.</p><span class=\"helpHeader\">Disclaimer</span><p>Results from this report represent analytical values as of the date they are generated. Future revisions may affect these results and official final results should be reviewed from the Final Report option to assure their accuracy.</p></span>")
  @Key("dataView.help.selection")
  String dataView_help_selection();

  /**
   * Translated "Select Organization Fields For Output".
   * 
   * @return translated "Select Organization Fields For Output"
   */
  @DefaultMessage("Select Organization Fields For Output")
  @Key("dataView.orgFields")
  String dataView_orgFields();

  /**
   * Translated "Select Patient Fields For Output".
   * 
   * @return translated "Select Patient Fields For Output"
   */
  @DefaultMessage("Select Patient Fields For Output")
  @Key("dataView.patientFields")
  String dataView_patientFields();

  /**
   * Translated "Query".
   * 
   * @return translated "Query"
   */
  @DefaultMessage("Query")
  @Key("dataView.query")
  String dataView_query();

  /**
   * Translated "Select Sample Fields For Output".
   * 
   * @return translated "Select Sample Fields For Output"
   */
  @DefaultMessage("Select Sample Fields For Output")
  @Key("dataView.sampleFields")
  String dataView_sampleFields();

  /**
   * Translated "Select SDWIS Fields For Output".
   * 
   * @return translated "Select SDWIS Fields For Output"
   */
  @DefaultMessage("Select SDWIS Fields For Output")
  @Key("dataView.sdwisFields")
  String dataView_sdwisFields();

  /**
   * Translated "Please select at least one test analyte or aux data".
   * 
   * @return translated "Please select at least one test analyte or aux data"
   */
  @DefaultMessage("Please select at least one test analyte or aux data")
  @Key("dataView.selectOneAnaOrAux")
  String dataView_selectOneAnaOrAux();

  /**
   * Translated "Selected Analytes".
   * 
   * @return translated "Selected Analytes"
   */
  @DefaultMessage("Selected Analytes")
  @Key("dataView.selectedAnalytes")
  String dataView_selectedAnalytes();

  /**
   * Translated "Add".
   * 
   * @return translated "Add"
   */
  @DefaultMessage("Add")
  @Key("emailNotification.add")
  String emailNotification_add();

  /**
   * Translated "Email".
   * 
   * @return translated "Email"
   */
  @DefaultMessage("Email")
  @Key("emailNotification.email")
  String emailNotification_email();

  /**
   * Translated "This string cannot contain {0}".
   * 
   * @return translated "This string cannot contain {0}"
   */
  @DefaultMessage("This string cannot contain {0}")
  @Key("emailNotification.error.invalidChar")
  String emailNotification_error_invalidChar(String arg0);

  /**
   * Translated "Invalid email address".
   * 
   * @return translated "Invalid email address"
   */
  @DefaultMessage("Invalid email address")
  @Key("emailNotification.error.invalidEmail")
  String emailNotification_error_invalidEmail();

  /**
   * Translated "Must input email address".
   * 
   * @return translated "Must input email address"
   */
  @DefaultMessage("Must input email address")
  @Key("emailNotification.error.noEmail")
  String emailNotification_error_noEmail();

  /**
   * Translated "Must input organization".
   * 
   * @return translated "Must input organization"
   */
  @DefaultMessage("Must input organization")
  @Key("emailNotification.error.noOrganization")
  String emailNotification_error_noOrganization();

  /**
   * Translated "You don''t have the permission to add emails for any organization. Please contact the system administrator".
   * 
   * @return translated "You don''t have the permission to add emails for any organization. Please contact the system administrator"
   */
  @DefaultMessage("You don''t have the permission to add emails for any organization. Please contact the system administrator")
  @Key("emailNotification.error.noPermToAddEmail")
  String emailNotification_error_noPermToAddEmail();

  /**
   * Translated "Filter By".
   * 
   * @return translated "Filter By"
   */
  @DefaultMessage("Filter By")
  @Key("emailNotification.filter")
  String emailNotification_filter();

  /**
   * Translated "Filter Match".
   * 
   * @return translated "Filter Match"
   */
  @DefaultMessage("Filter Match")
  @Key("emailNotification.filterValue")
  String emailNotification_filterValue();

  /**
   * Translated "SHL can send an email to specified email addresses whenever a new sample is received or when a result is available. You can choose whether the notification emails are sent for either or both types of events. In the near future you will be able to add a filter on the Collector, Provider, or Client Reference fields of the sample in order to receive emails that only match the filter.".
   * 
   * @return translated "SHL can send an email to specified email addresses whenever a new sample is received or when a result is available. You can choose whether the notification emails are sent for either or both types of events. In the near future you will be able to add a filter on the Collector, Provider, or Client Reference fields of the sample in order to receive emails that only match the filter."
   */
  @DefaultMessage("SHL can send an email to specified email addresses whenever a new sample is received or when a result is available. You can choose whether the notification emails are sent for either or both types of events. In the near future you will be able to add a filter on the Collector, Provider, or Client Reference fields of the sample in order to receive emails that only match the filter.")
  @Key("emailNotification.help.description")
  String emailNotification_help_description();

  /**
   * Translated "<span class = \"helpText\"><span class = \"helpHeader\">To Add Your Email Address </span><br/><ul><li>Click on the <b>Add</b> button to add a new email address.</li><li>Select your organization from the dropdown list in the <b>Organization</b> field.</li><li>Enter your email address in the <b>Email</b> field.</li><li>Check <b>Received</b> or <b>Released</b> or both to receive notifications for each type of event.</li><li>Click the <b>Save Changes</b> button to complete the process.</li></ul><br/><span class = \"helpHeader\">To Edit an Existing Email Address or Filter</span><br/><ul><li>Click on any cell in the entry that you want to edit.</li><li>Type in the new email address or filter information.</li><li>Click the <b>Save Changes</b> button to complete the process.</li></ul><br/><span class = \"helpHeader\">To Remove an Email Notification</span><br/><ul><li>Select the entry that you want to remove. Click the <b>Remove</b> button.</li></ul></span>".
   * 
   * @return translated "<span class = \"helpText\"><span class = \"helpHeader\">To Add Your Email Address </span><br/><ul><li>Click on the <b>Add</b> button to add a new email address.</li><li>Select your organization from the dropdown list in the <b>Organization</b> field.</li><li>Enter your email address in the <b>Email</b> field.</li><li>Check <b>Received</b> or <b>Released</b> or both to receive notifications for each type of event.</li><li>Click the <b>Save Changes</b> button to complete the process.</li></ul><br/><span class = \"helpHeader\">To Edit an Existing Email Address or Filter</span><br/><ul><li>Click on any cell in the entry that you want to edit.</li><li>Type in the new email address or filter information.</li><li>Click the <b>Save Changes</b> button to complete the process.</li></ul><br/><span class = \"helpHeader\">To Remove an Email Notification</span><br/><ul><li>Select the entry that you want to remove. Click the <b>Remove</b> button.</li></ul></span>"
   */
  @DefaultMessage("<span class = \"helpText\"><span class = \"helpHeader\">To Add Your Email Address </span><br/><ul><li>Click on the <b>Add</b> button to add a new email address.</li><li>Select your organization from the dropdown list in the <b>Organization</b> field.</li><li>Enter your email address in the <b>Email</b> field.</li><li>Check <b>Received</b> or <b>Released</b> or both to receive notifications for each type of event.</li><li>Click the <b>Save Changes</b> button to complete the process.</li></ul><br/><span class = \"helpHeader\">To Edit an Existing Email Address or Filter</span><br/><ul><li>Click on any cell in the entry that you want to edit.</li><li>Type in the new email address or filter information.</li><li>Click the <b>Save Changes</b> button to complete the process.</li></ul><br/><span class = \"helpHeader\">To Remove an Email Notification</span><br/><ul><li>Select the entry that you want to remove. Click the <b>Remove</b> button.</li></ul></span>")
  @Key("emailNotification.help.screen")
  String emailNotification_help_screen();

  /**
   * Translated "Organization".
   * 
   * @return translated "Organization"
   */
  @DefaultMessage("Organization")
  @Key("emailNotification.org")
  String emailNotification_org();

  /**
   * Translated "Remove".
   * 
   * @return translated "Remove"
   */
  @DefaultMessage("Remove")
  @Key("emailNotification.remove")
  String emailNotification_remove();

  /**
   * Translated "Save Changes".
   * 
   * @return translated "Save Changes"
   */
  @DefaultMessage("Save Changes")
  @Key("emailNotification.save")
  String emailNotification_save();

  /**
   * Translated "The Message Of The Day file was not found".
   * 
   * @return translated "The Message Of The Day file was not found"
   */
  @DefaultMessage("The Message Of The Day file was not found")
  @Key("error.messageFileNotFound")
  String error_messageFileNotFound();

  /**
   * Translated "You do not have permission to access {0}".
   * 
   * @return translated "You do not have permission to access {0}"
   */
  @DefaultMessage("You do not have permission to access {0}")
  @Key("error.screenPerm")
  String error_screenPerm(String arg0);

  /**
   * Translated "This record is not available at this time for you to add/edit/remove. Please try at another time. (Locked)".
   * 
   * @return translated "This record is not available at this time for you to add/edit/remove. Please try at another time. (Locked)"
   */
  @DefaultMessage("This record is not available at this time for you to add/edit/remove. Please try at another time. (Locked)")
  @Key("exc.recordNotAvailableLock")
  String exc_recordNotAvailableLock();

  /**
   * Translated "Additional Information".
   * 
   * @return translated "Additional Information"
   */
  @DefaultMessage("Additional Information")
  @Key("finalReport.additionalInfo")
  String finalReport_additionalInfo();

  /**
   * Translated "ANY SAMPLES".
   * 
   * @return translated "ANY SAMPLES"
   */
  @DefaultMessage("ANY SAMPLES")
  @Key("finalReport.anySamples")
  String finalReport_anySamples();

  /**
   * Translated "CLINICAL SAMPLES ONLY".
   * 
   * @return translated "CLINICAL SAMPLES ONLY"
   */
  @DefaultMessage("CLINICAL SAMPLES ONLY")
  @Key("finalReport.clinicalOnly")
  String finalReport_clinicalOnly();

  /**
   * Translated "ENVIRONMENTAL SAMPLES ONLY".
   * 
   * @return translated "ENVIRONMENTAL SAMPLES ONLY"
   */
  @DefaultMessage("ENVIRONMENTAL SAMPLES ONLY")
  @Key("finalReport.environmentalOnly")
  String finalReport_environmentalOnly();

  /**
   * Translated "Please query for at least one field".
   * 
   * @return translated "Please query for at least one field"
   */
  @DefaultMessage("Please query for at least one field")
  @Key("finalReport.error.emptyQueryException")
  String finalReport_error_emptyQueryException();

  /**
   * Translated "Please select at least one sample, then press Run Report".
   * 
   * @return translated "Please select at least one sample, then press Run Report"
   */
  @DefaultMessage("Please select at least one sample, then press Run Report")
  @Key("finalReport.error.noSampleSelected")
  String finalReport_error_noSampleSelected();

  /**
   * Translated "No samples returned".
   * 
   * @return translated "No samples returned"
   */
  @DefaultMessage("No samples returned")
  @Key("finalReport.error.noSamples")
  String finalReport_error_noSamples();

  /**
   * Translated "Must enter start accession number with end accession number".
   * 
   * @return translated "Must enter start accession number with end accession number"
   */
  @DefaultMessage("Must enter start accession number with end accession number")
  @Key("finalReport.error.noStartAccession")
  String finalReport_error_noStartAccession();

  /**
   * Translated "Must enter start date with end date".
   * 
   * @return translated "Must enter start date with end date"
   */
  @DefaultMessage("Must enter start date with end date")
  @Key("finalReport.error.noStartDate")
  String finalReport_error_noStartDate();

  /**
   * Translated "Please query for only one of environmental/SDWIS/clinical at a time.".
   * 
   * @return translated "Please query for only one of environmental/SDWIS/clinical at a time."
   */
  @DefaultMessage("Please query for only one of environmental/SDWIS/clinical at a time.")
  @Key("finalReport.error.queryDomainException")
  String finalReport_error_queryDomainException();

  /**
   * Translated "Find Samples".
   * 
   * @return translated "Find Samples"
   */
  @DefaultMessage("Find Samples")
  @Key("finalReport.getSampleList")
  String finalReport_getSampleList();

  /**
   * Translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.".
   * 
   * @return translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples."
   */
  @DefaultMessage("The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.")
  @Key("finalReport.help.accession")
  String finalReport_help_accession();

  /**
   * Translated "This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.".
   * 
   * @return translated "This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample."
   */
  @DefaultMessage("This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.")
  @Key("finalReport.help.clientReference")
  String finalReport_help_clientReference();

  /**
   * Translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.".
   * 
   * @return translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field."
   */
  @DefaultMessage("The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.")
  @Key("finalReport.help.collected")
  String finalReport_help_collected();

  /**
   * Translated "The name or ID of the collector of the environmental sample.".
   * 
   * @return translated "The name or ID of the collector of the environmental sample."
   */
  @DefaultMessage("The name or ID of the collector of the environmental sample.")
  @Key("finalReport.help.envCollector")
  String finalReport_help_envCollector();

  /**
   * Translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.".
   * 
   * @return translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates."
   */
  @DefaultMessage("The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.")
  @Key("finalReport.help.patientBirth")
  String finalReport_help_patientBirth();

  /**
   * Translated "The first name of the patient for a clinical sample.".
   * 
   * @return translated "The first name of the patient for a clinical sample."
   */
  @DefaultMessage("The first name of the patient for a clinical sample.")
  @Key("finalReport.help.patientFirst")
  String finalReport_help_patientFirst();

  /**
   * Translated "The last name of the patient for a clinical sample.".
   * 
   * @return translated "The last name of the patient for a clinical sample."
   */
  @DefaultMessage("The last name of the patient for a clinical sample.")
  @Key("finalReport.help.patientLast")
  String finalReport_help_patientLast();

  /**
   * Translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects.".
   * 
   * @return translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects."
   */
  @DefaultMessage("The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects.")
  @Key("finalReport.help.project")
  String finalReport_help_project();

  /**
   * Translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.".
   * 
   * @return translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number."
   */
  @DefaultMessage("The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.")
  @Key("finalReport.help.pws")
  String finalReport_help_pws();

  /**
   * Translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.".
   * 
   * @return translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field."
   */
  @DefaultMessage("The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.")
  @Key("finalReport.help.released")
  String finalReport_help_released();

  /**
   * Translated "<span class=\"helpText\"><span class=\"helpHeader\">Locating Your Results</span><br/><p/>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b><p/>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.<p/>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.<p/>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. <p/>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/>Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.</span>".
   * 
   * @return translated "<span class=\"helpText\"><span class=\"helpHeader\">Locating Your Results</span><br/><p/>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b><p/>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.<p/>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.<p/>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. <p/>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/>Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.</span>"
   */
  @DefaultMessage("<span class=\"helpText\"><span class=\"helpHeader\">Locating Your Results</span><br/><p/>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b><p/>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.<p/>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.<p/>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. <p/>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. <p/>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.<p/>Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.</span>")
  @Key("finalReport.help.screen")
  String finalReport_help_screen();

  /**
   * Translated "The name or ID of the collector of the safe drinking water sample.".
   * 
   * @return translated "The name or ID of the collector of the safe drinking water sample."
   */
  @DefaultMessage("The name or ID of the collector of the safe drinking water sample.")
  @Key("finalReport.help.sdwisCollector")
  String finalReport_help_sdwisCollector();

  /**
   * Translated "<span class=\"helpText\"><span class=\"helpHeader\">To View the Final Reports</span><br/><p>To the left is a list of sample results that match your search criteria and are available for you to view. To view all reports of the listed samples, click on the <b>Select All</b> button, and then the <b>Run Report</b> button.</p><p>To view specific reports, check the boxes in front of their Accession Numbers, and then click the <b>Run Report</b> button.</p><p>The PDF reports of the selected samples will pop up. You may view, save, or print your PDF reports.</p><p>The following are descriptions of each column on this screen.</p><p>Accession Number: The lab number assigned to the sample by SHL.</p><p>Collected Date: The date and time that the sample was collected. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Reference Information:  This information can be helpful in identifying a sample. The patient''s last name is usually displayed for clinical samples. The collector''s name is usually displayed for environmental, safe drinking water, and private well samples.</p><p>Additional Information: This information can also be useful in identifying a sample. The collection location and city (if submitted) are displayed for environmental and private well samples. The collection location (if submitted) and PWS ID-PWS Name are displayed for safe drinking water samples. The health care provider''s name and submitting organization are displayed for clinical samples.</p><p>Status: The sample''s status is shown here. \"In Progress\" samples have one or more tests that are not yet complete and at least one test that is finished. The finished test''s results are currently available on the Final Report. \"Completed\" samples have finished testing and all of their results are available on the Final Report.</p><p>Project: The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.</p></span>".
   * 
   * @return translated "<span class=\"helpText\"><span class=\"helpHeader\">To View the Final Reports</span><br/><p>To the left is a list of sample results that match your search criteria and are available for you to view. To view all reports of the listed samples, click on the <b>Select All</b> button, and then the <b>Run Report</b> button.</p><p>To view specific reports, check the boxes in front of their Accession Numbers, and then click the <b>Run Report</b> button.</p><p>The PDF reports of the selected samples will pop up. You may view, save, or print your PDF reports.</p><p>The following are descriptions of each column on this screen.</p><p>Accession Number: The lab number assigned to the sample by SHL.</p><p>Collected Date: The date and time that the sample was collected. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Reference Information:  This information can be helpful in identifying a sample. The patient''s last name is usually displayed for clinical samples. The collector''s name is usually displayed for environmental, safe drinking water, and private well samples.</p><p>Additional Information: This information can also be useful in identifying a sample. The collection location and city (if submitted) are displayed for environmental and private well samples. The collection location (if submitted) and PWS ID-PWS Name are displayed for safe drinking water samples. The health care provider''s name and submitting organization are displayed for clinical samples.</p><p>Status: The sample''s status is shown here. \"In Progress\" samples have one or more tests that are not yet complete and at least one test that is finished. The finished test''s results are currently available on the Final Report. \"Completed\" samples have finished testing and all of their results are available on the Final Report.</p><p>Project: The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.</p></span>"
   */
  @DefaultMessage("<span class=\"helpText\"><span class=\"helpHeader\">To View the Final Reports</span><br/><p>To the left is a list of sample results that match your search criteria and are available for you to view. To view all reports of the listed samples, click on the <b>Select All</b> button, and then the <b>Run Report</b> button.</p><p>To view specific reports, check the boxes in front of their Accession Numbers, and then click the <b>Run Report</b> button.</p><p>The PDF reports of the selected samples will pop up. You may view, save, or print your PDF reports.</p><p>The following are descriptions of each column on this screen.</p><p>Accession Number: The lab number assigned to the sample by SHL.</p><p>Collected Date: The date and time that the sample was collected. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Reference Information:  This information can be helpful in identifying a sample. The patient''s last name is usually displayed for clinical samples. The collector''s name is usually displayed for environmental, safe drinking water, and private well samples.</p><p>Additional Information: This information can also be useful in identifying a sample. The collection location and city (if submitted) are displayed for environmental and private well samples. The collection location (if submitted) and PWS ID-PWS Name are displayed for safe drinking water samples. The health care provider''s name and submitting organization are displayed for clinical samples.</p><p>Status: The sample''s status is shown here. \"In Progress\" samples have one or more tests that are not yet complete and at least one test that is finished. The finished test''s results are currently available on the Final Report. \"Completed\" samples have finished testing and all of their results are available on the Final Report.</p><p>Project: The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose.</p></span>")
  @Key("finalReport.help.selection")
  String finalReport_help_selection();

  /**
   * Translated "Patient''s Date of Birth".
   * 
   * @return translated "Patient''s Date of Birth"
   */
  @DefaultMessage("Patient''s Date of Birth")
  @Key("finalReport.patientBirth")
  String finalReport_patientBirth();

  /**
   * Translated "Patient''s First Name".
   * 
   * @return translated "Patient''s First Name"
   */
  @DefaultMessage("Patient''s First Name")
  @Key("finalReport.patientFirst")
  String finalReport_patientFirst();

  /**
   * Translated "Patient''s Last Name".
   * 
   * @return translated "Patient''s Last Name"
   */
  @DefaultMessage("Patient''s Last Name")
  @Key("finalReport.patientLast")
  String finalReport_patientLast();

  /**
   * Translated "Reference Information".
   * 
   * @return translated "Reference Information"
   */
  @DefaultMessage("Reference Information")
  @Key("finalReport.referenceInfo")
  String finalReport_referenceInfo();

  /**
   * Translated "Reset".
   * 
   * @return translated "Reset"
   */
  @DefaultMessage("Reset")
  @Key("finalReport.reset")
  String finalReport_reset();

  /**
   * Translated "Run Report".
   * 
   * @return translated "Run Report"
   */
  @DefaultMessage("Run Report")
  @Key("finalReport.run")
  String finalReport_run();

  /**
   * Translated "SDWIS SAMPLES ONLY".
   * 
   * @return translated "SDWIS SAMPLES ONLY"
   */
  @DefaultMessage("SDWIS SAMPLES ONLY")
  @Key("finalReport.sdwisOnly")
  String finalReport_sdwisOnly();

  /**
   * Translated "Select".
   * 
   * @return translated "Select"
   */
  @DefaultMessage("Select")
  @Key("finalReport.select")
  String finalReport_select();

  /**
   * Translated "Accession #".
   * 
   * @return translated "Accession #"
   */
  @DefaultMessage("Accession #")
  @Key("finalReport.select.accession")
  String finalReport_select_accession();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultMessage("Status")
  @Key("finalReport.select.status")
  String finalReport_select_status();

  /**
   * Translated "Select All".
   * 
   * @return translated "Select All"
   */
  @DefaultMessage("Select All")
  @Key("finalReport.selectAll")
  String finalReport_selectAll();

  /**
   * Translated "Unselect All".
   * 
   * @return translated "Unselect All"
   */
  @DefaultMessage("Unselect All")
  @Key("finalReport.unselectAll")
  String finalReport_unselectAll();

  /**
   * Translated "Accession #".
   * 
   * @return translated "Accession #"
   */
  @DefaultMessage("Accession #")
  @Key("gen.accessionNumber")
  String gen_accessionNumber();

  /**
   * Translated "Back To Search".
   * 
   * @return translated "Back To Search"
   */
  @DefaultMessage("Back To Search")
  @Key("gen.back")
  String gen_back();

  /**
   * Translated "CANCEL".
   * 
   * @return translated "CANCEL"
   */
  @DefaultMessage("CANCEL")
  @Key("gen.cancel")
  String gen_cancel();

  /**
   * Translated "yyyy-MM-dd".
   * 
   * @return translated "yyyy-MM-dd"
   */
  @DefaultMessage("yyyy-MM-dd")
  @Key("gen.datePattern")
  String gen_datePattern();

  /**
   * Translated "yyyy-MM-dd HH:mm".
   * 
   * @return translated "yyyy-MM-dd HH:mm"
   */
  @DefaultMessage("yyyy-MM-dd HH:mm")
  @Key("gen.dateTimePattern")
  String gen_dateTimePattern();

  /**
   * Translated "Fetching samples....".
   * 
   * @return translated "Fetching samples...."
   */
  @DefaultMessage("Fetching samples....")
  @Key("gen.fetchingSamples")
  String gen_fetchingSamples();

  /**
   * Translated "Generating report....".
   * 
   * @return translated "Generating report...."
   */
  @DefaultMessage("Generating report....")
  @Key("gen.genReportMessage")
  String gen_genReportMessage();

  /**
   * Translated "NO".
   * 
   * @return translated "NO"
   */
  @DefaultMessage("NO")
  @Key("gen.no")
  String gen_no();

  /**
   * Translated "samples have been found.".
   * 
   * @return translated "samples have been found."
   */
  @DefaultMessage("samples have been found.")
  @Key("gen.samplesFound")
  String gen_samplesFound();

  /**
   * Translated "HH:mm".
   * 
   * @return translated "HH:mm"
   */
  @DefaultMessage("HH:mm")
  @Key("gen.timePattern")
  String gen_timePattern();

  /**
   * Translated "To".
   * 
   * @return translated "To"
   */
  @DefaultMessage("To")
  @Key("gen.to")
  String gen_to();

  /**
   * Translated "YES".
   * 
   * @return translated "YES"
   */
  @DefaultMessage("YES")
  @Key("gen.yes")
  String gen_yes();

  /**
   * Translated "ACCOUNT INFORMATION".
   * 
   * @return translated "ACCOUNT INFORMATION"
   */
  @DefaultMessage("ACCOUNT INFORMATION")
  @Key("main.accountInfo")
  String main_accountInfo();

  /**
   * Translated "Change Password".
   * 
   * @return translated "Change Password"
   */
  @DefaultMessage("Change Password")
  @Key("main.changePassword")
  String main_changePassword();

  /**
   * Translated "Spreadsheet View".
   * 
   * @return translated "Spreadsheet View"
   */
  @DefaultMessage("Spreadsheet View")
  @Key("main.dataView")
  String main_dataView();

  /**
   * Translated "Email Notification".
   * 
   * @return translated "Email Notification"
   */
  @DefaultMessage("Email Notification")
  @Key("main.emailNotification")
  String main_emailNotification();

  /**
   * Translated "Final Report".
   * 
   * @return translated "Final Report"
   */
  @DefaultMessage("Final Report")
  @Key("main.finalReport")
  String main_finalReport();

  /**
   * Translated "Logout".
   * 
   * @return translated "Logout"
   */
  @DefaultMessage("Logout")
  @Key("main.logout")
  String main_logout();

  /**
   * Translated "Main".
   * 
   * @return translated "Main"
   */
  @DefaultMessage("Main")
  @Key("main.main")
  String main_main();

  /**
   * Translated "Spreadsheet\nView".
   * 
   * @return translated "Spreadsheet\nView"
   */
  @DefaultMessage("Spreadsheet\nView")
  @Key("main.menu.dataView")
  String main_menu_dataView();

  /**
   * Translated "Email\nNotification".
   * 
   * @return translated "Email\nNotification"
   */
  @DefaultMessage("Email\nNotification")
  @Key("main.menu.emailNotification")
  String main_menu_emailNotification();

  /**
   * Translated "Final\nReport".
   * 
   * @return translated "Final\nReport"
   */
  @DefaultMessage("Final\nReport")
  @Key("main.menu.finalReport")
  String main_menu_finalReport();

  /**
   * Translated "Test\nStatus".
   * 
   * @return translated "Test\nStatus"
   */
  @DefaultMessage("Test\nStatus")
  @Key("main.menu.sampleStatus")
  String main_menu_sampleStatus();

  /**
   * Translated "Clinical Test\nRequest Form".
   * 
   * @return translated "Clinical Test\nRequest Form"
   */
  @DefaultMessage("Clinical Test\nRequest Form")
  @Key("main.menu.testRequest")
  String main_menu_testRequest();

  /**
   * Translated "Was your experience using the website satisfactory?".
   * 
   * @return translated "Was your experience using the website satisfactory?"
   */
  @DefaultMessage("Was your experience using the website satisfactory?")
  @Key("main.question")
  String main_question();

  /**
   * Translated "REPORTS".
   * 
   * @return translated "REPORTS"
   */
  @DefaultMessage("REPORTS")
  @Key("main.reports")
  String main_reports();

  /**
   * Translated "Test Status".
   * 
   * @return translated "Test Status"
   */
  @DefaultMessage("Test Status")
  @Key("main.sampleStatus")
  String main_sampleStatus();

  /**
   * Translated "SHORT TERM FOLLOW UP".
   * 
   * @return translated "SHORT TERM FOLLOW UP"
   */
  @DefaultMessage("SHORT TERM FOLLOW UP")
  @Key("main.stfu")
  String main_stfu();

  /**
   * Translated "Clinical Test Request Form".
   * 
   * @return translated "Clinical Test Request Form"
   */
  @DefaultMessage("Clinical Test Request Form")
  @Key("main.testRequest")
  String main_testRequest();

  /**
   * Translated "Address".
   * 
   * @return translated "Address"
   */
  @DefaultMessage("Address")
  @Key("org.address")
  String org_address();

  /**
   * Translated "Apt/Suite #".
   * 
   * @return translated "Apt/Suite #"
   */
  @DefaultMessage("Apt/Suite #")
  @Key("org.apt")
  String org_apt();

  /**
   * Translated "City".
   * 
   * @return translated "City"
   */
  @DefaultMessage("City")
  @Key("org.city")
  String org_city();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
   */
  @DefaultMessage("Name")
  @Key("org.name")
  String org_name();

  /**
   * Translated "State".
   * 
   * @return translated "State"
   */
  @DefaultMessage("State")
  @Key("org.state")
  String org_state();

  /**
   * Translated "Zip Code".
   * 
   * @return translated "Zip Code"
   */
  @DefaultMessage("Zip Code")
  @Key("org.zip")
  String org_zip();

  /**
   * Translated "Birth Date".
   * 
   * @return translated "Birth Date"
   */
  @DefaultMessage("Birth Date")
  @Key("patient.birthDate")
  String patient_birthDate();

  /**
   * Translated "Ethnicity".
   * 
   * @return translated "Ethnicity"
   */
  @DefaultMessage("Ethnicity")
  @Key("patient.ethnicity")
  String patient_ethnicity();

  /**
   * Translated "First Name".
   * 
   * @return translated "First Name"
   */
  @DefaultMessage("First Name")
  @Key("patient.firstName")
  String patient_firstName();

  /**
   * Translated "Gender".
   * 
   * @return translated "Gender"
   */
  @DefaultMessage("Gender")
  @Key("patient.gender")
  String patient_gender();

  /**
   * Translated "Last Name".
   * 
   * @return translated "Last Name"
   */
  @DefaultMessage("Last Name")
  @Key("patient.lastName")
  String patient_lastName();

  /**
   * Translated "Race".
   * 
   * @return translated "Race"
   */
  @DefaultMessage("Race")
  @Key("patient.race")
  String patient_race();

  /**
   * Translated "PWS ID".
   * 
   * @return translated "PWS ID"
   */
  @DefaultMessage("PWS ID")
  @Key("pws.id")
  String pws_id();

  /**
   * Translated "PWS Name".
   * 
   * @return translated "PWS Name"
   */
  @DefaultMessage("PWS Name")
  @Key("pws.name")
  String pws_name();

  /**
   * Translated "Accession Number".
   * 
   * @return translated "Accession Number"
   */
  @DefaultMessage("Accession Number")
  @Key("sample.accessionNumber")
  String sample_accessionNumber();

  /**
   * Translated "Client Reference".
   * 
   * @return translated "Client Reference"
   */
  @DefaultMessage("Client Reference")
  @Key("sample.clientReference")
  String sample_clientReference();

  /**
   * Translated "Collected Date".
   * 
   * @return translated "Collected Date"
   */
  @DefaultMessage("Collected Date")
  @Key("sample.collectedDate")
  String sample_collectedDate();

  /**
   * Translated "Location".
   * 
   * @return translated "Location"
   */
  @DefaultMessage("Location")
  @Key("sample.collectionSite")
  String sample_collectionSite();

  /**
   * Translated "Collection Town".
   * 
   * @return translated "Collection Town"
   */
  @DefaultMessage("Collection Town")
  @Key("sample.collectionTown")
  String sample_collectionTown();

  /**
   * Translated "Collector".
   * 
   * @return translated "Collector"
   */
  @DefaultMessage("Collector")
  @Key("sample.collector")
  String sample_collector();

  /**
   * Translated "Sample Description".
   * 
   * @return translated "Sample Description"
   */
  @DefaultMessage("Sample Description")
  @Key("sample.description")
  String sample_description();

  /**
   * Translated "Location City".
   * 
   * @return translated "Location City"
   */
  @DefaultMessage("Location City")
  @Key("sample.locationCity")
  String sample_locationCity();

  /**
   * Translated "Phone #".
   * 
   * @return translated "Phone #"
   */
  @DefaultMessage("Phone #")
  @Key("sample.phone")
  String sample_phone();

  /**
   * Translated "Project".
   * 
   * @return translated "Project"
   */
  @DefaultMessage("Project")
  @Key("sample.project")
  String sample_project();

  /**
   * Translated "Received".
   * 
   * @return translated "Received"
   */
  @DefaultMessage("Received")
  @Key("sample.received")
  String sample_received();

  /**
   * Translated "Received Date".
   * 
   * @return translated "Received Date"
   */
  @DefaultMessage("Received Date")
  @Key("sample.receivedDate")
  String sample_receivedDate();

  /**
   * Translated "Released".
   * 
   * @return translated "Released"
   */
  @DefaultMessage("Released")
  @Key("sample.released")
  String sample_released();

  /**
   * Translated "Released Date".
   * 
   * @return translated "Released Date"
   */
  @DefaultMessage("Released Date")
  @Key("sample.releasedDate")
  String sample_releasedDate();

  /**
   * Translated "Source".
   * 
   * @return translated "Source"
   */
  @DefaultMessage("Source")
  @Key("sample.source")
  String sample_source();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultMessage("Status")
  @Key("sample.status")
  String sample_status();

  /**
   * Translated "Sample Type".
   * 
   * @return translated "Sample Type"
   */
  @DefaultMessage("Sample Type")
  @Key("sample.type")
  String sample_type();

  /**
   * Translated "Completed".
   * 
   * @return translated "Completed"
   */
  @DefaultMessage("Completed")
  @Key("sampleStatus.completed")
  String sampleStatus_completed();

  /**
   * Translated "Date Received".
   * 
   * @return translated "Date Received"
   */
  @DefaultMessage("Date Received")
  @Key("sampleStatus.dateReceived")
  String sampleStatus_dateReceived();

  /**
   * Translated "Sample/Test Description".
   * 
   * @return translated "Sample/Test Description"
   */
  @DefaultMessage("Sample/Test Description")
  @Key("sampleStatus.description")
  String sampleStatus_description();

  /**
   * Translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.".
   * 
   * @return translated "The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples."
   */
  @DefaultMessage("The lab number assigned to the sample by SHL. Enter the accession number in this field to search for a single sample or enter the beginning number in the Accession Number field and the ending number in the To field to search for a series of samples.")
  @Key("sampleStatus.help.accession")
  String sampleStatus_help_accession();

  /**
   * Translated "This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.".
   * 
   * @return translated "This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample."
   */
  @DefaultMessage("This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.")
  @Key("sampleStatus.help.clientReference")
  String sampleStatus_help_clientReference();

  /**
   * Translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.".
   * 
   * @return translated "The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field."
   */
  @DefaultMessage("The date that the sample was collected. You must enter a beginning date in the Collected Date field and an ending date in the To field.")
  @Key("sampleStatus.help.collected")
  String sampleStatus_help_collected();

  /**
   * Translated "The name or ID of the collector of the environmental sample.".
   * 
   * @return translated "The name or ID of the collector of the environmental sample."
   */
  @DefaultMessage("The name or ID of the collector of the environmental sample.")
  @Key("sampleStatus.help.envCollector")
  String sampleStatus_help_envCollector();

  /**
   * Translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.".
   * 
   * @return translated "The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates."
   */
  @DefaultMessage("The patient''s date of birth for a clinical sample. You must enter an ending date in the To field to search for a range of birth dates.")
  @Key("sampleStatus.help.patientBirth")
  String sampleStatus_help_patientBirth();

  /**
   * Translated "The first name of the patient for a clinical sample.".
   * 
   * @return translated "The first name of the patient for a clinical sample."
   */
  @DefaultMessage("The first name of the patient for a clinical sample.")
  @Key("sampleStatus.help.patientFirst")
  String sampleStatus_help_patientFirst();

  /**
   * Translated "The last name of the patient for a clinical sample.".
   * 
   * @return translated "The last name of the patient for a clinical sample."
   */
  @DefaultMessage("The last name of the patient for a clinical sample.")
  @Key("sampleStatus.help.patientLast")
  String sampleStatus_help_patientLast();

  /**
   * Translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects.".
   * 
   * @return translated "The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects."
   */
  @DefaultMessage("The name of the project that has been assigned to the sample. A project can be used to group samples with a similar purpose. Multiple projects can be selected at a time. Use the Check All button to select all of your projects.")
  @Key("sampleStatus.help.project")
  String sampleStatus_help_project();

  /**
   * Translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.".
   * 
   * @return translated "The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number."
   */
  @DefaultMessage("The Public Water Supply ID is a unique identifier assigned by the state to all public water supplies. The PWS Id begins with a two letter state abbreviation followed by a unique seven digit number.")
  @Key("sampleStatus.help.pws")
  String sampleStatus_help_pws();

  /**
   * Translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.".
   * 
   * @return translated "The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field."
   */
  @DefaultMessage("The date and time that the sample was released (finalized). You must enter a beginning date and time in the Released Date field and an ending date and time in the To field.")
  @Key("sampleStatus.help.released")
  String sampleStatus_help_released();

  /**
   * Translated "<span class=\"helpText\"><span class=\"helpHeader\">Viewing the Test Status of Your Samples</span><p>The Test Status option will display the status of each test that is being performed on your samples. The Collected Date, Received Date, Client Reference, and any QA Events will also be shown.</p><p>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b>.</p><p>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.</p><p>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.</p><p>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. </p><p>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. </p><p>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.</p><p>Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.</p></span>".
   * 
   * @return translated "<span class=\"helpText\"><span class=\"helpHeader\">Viewing the Test Status of Your Samples</span><p>The Test Status option will display the status of each test that is being performed on your samples. The Collected Date, Received Date, Client Reference, and any QA Events will also be shown.</p><p>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b>.</p><p>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.</p><p>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.</p><p>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. </p><p>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. </p><p>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.</p><p>Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.</p></span>"
   */
  @DefaultMessage("<span class=\"helpText\"><span class=\"helpHeader\">Viewing the Test Status of Your Samples</span><p>The Test Status option will display the status of each test that is being performed on your samples. The Collected Date, Received Date, Client Reference, and any QA Events will also be shown.</p><p>You only need to enter information in one field to conduct a search for your results. Example fields include: <b>Collected Date</b>, <b>Accession Number</b>, <b>Collector</b>, or <b>Patient''s Last Name</b>.</p><p>It is recommended that you use the calendar to the right of each date field when entering a date to eliminate any formatting errors. You must click out of the calendar to enter the selected date. All dates have the format of YYYY-MM-DD. Released Date also includes the time in the format of YYYY-MM-DD HH:MM.</p><p>You may also narrow your search by entering information in multiple fields. For example, to find all of the samples that you collected and sent to the laboratory in June of 2014, enter your name (as it appeared on the collection form) in the <b>Collector</b> field and select (using the calendars provided) 2014-06-01 in the <b>Collected Date</b> field and 2014-06-30 in the <b>To</b> field.</p><p>The system supports wild card searches in the following fields: <b>Client Reference</b>, <b>Collector</b>, <b>PWS ID</b>, <b>Patient''s First Name</b>, and <b>Patient''s Last Name</b>. By using this search method you would add an * at the beginning and/or end of your search term. </p><p>For example, to see a list of patient results for a patient whose last name starts with the letters <i>schm</i>, enter <i>schm*</i> in the <b>Patient''s Last Name</b> field. Matches would include: <i>schmidt</i>, <i>schmitt</i>, <i>schmitz</i>, etc. </p><p>In another example, to see a list of results for samples with a collector name of <i>buck</i>, enter <i>*buck*</i> in the <b>Collector</b> field. Matches would include: <i>joseph buck</i>, <i>joe buck</i>, <i>j buck</i>, <i>buck joseph</i>, <i>buck joe</i>, <i>buck j</i>, etc.</p><p>Click the <b>Find Samples</b> button to display a list of samples that match your search criteria. The <b>Reset</b> button clears all of the search fields.</p></span>")
  @Key("sampleStatus.help.screen")
  String sampleStatus_help_screen();

  /**
   * Translated "The name or ID of the collector of the safe drinking water sample.".
   * 
   * @return translated "The name or ID of the collector of the safe drinking water sample."
   */
  @DefaultMessage("The name or ID of the collector of the safe drinking water sample.")
  @Key("sampleStatus.help.sdwisCollector")
  String sampleStatus_help_sdwisCollector();

  /**
   * Translated "<span class=\"helpText\"><span class=\"helpHeader\">Viewing the Test Status of Your Samples</span><p>Below is a list of samples matching your search criteria that shows the status of each test that is being performed.</p><p>The following are descriptions of each column on this screen.</p><p>Accession Number: The lab number assigned to the sample by SHL.</p><p>Sample/Test Description: The Sample Description is displayed in the first row for each Accession #. The collector''s name is normally displayed for environmental and SDWIS samples; whereas, the patient''s name is displayed for clinical samples. Test Descriptions are displayed in the remaining rows for each Accession #. There may be a footnote at the end of a Sample or Test Description which is explained in the QA Event column. If a footnote is after the Sample Description, it applies to the entire sample. If a footnote is after a Test Description, it applies to only that test.</p><p>Test Status: The test''s status is shown here. \"In Progress\" tests have not been finished. \"Completed\" tests have finished testing and their results are available on the Final Report or through the Spreadsheet View.</p><p>Collected Date: The date and time that the sample was collected. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Date Received: The date and time that the sample was received at SHL. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Client Reference: This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.</p><p>QA Event: Any quality assurance issue that applies to your entire sample or an individual test is displayed in this column.</p></span>".
   * 
   * @return translated "<span class=\"helpText\"><span class=\"helpHeader\">Viewing the Test Status of Your Samples</span><p>Below is a list of samples matching your search criteria that shows the status of each test that is being performed.</p><p>The following are descriptions of each column on this screen.</p><p>Accession Number: The lab number assigned to the sample by SHL.</p><p>Sample/Test Description: The Sample Description is displayed in the first row for each Accession #. The collector''s name is normally displayed for environmental and SDWIS samples; whereas, the patient''s name is displayed for clinical samples. Test Descriptions are displayed in the remaining rows for each Accession #. There may be a footnote at the end of a Sample or Test Description which is explained in the QA Event column. If a footnote is after the Sample Description, it applies to the entire sample. If a footnote is after a Test Description, it applies to only that test.</p><p>Test Status: The test''s status is shown here. \"In Progress\" tests have not been finished. \"Completed\" tests have finished testing and their results are available on the Final Report or through the Spreadsheet View.</p><p>Collected Date: The date and time that the sample was collected. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Date Received: The date and time that the sample was received at SHL. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Client Reference: This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.</p><p>QA Event: Any quality assurance issue that applies to your entire sample or an individual test is displayed in this column.</p></span>"
   */
  @DefaultMessage("<span class=\"helpText\"><span class=\"helpHeader\">Viewing the Test Status of Your Samples</span><p>Below is a list of samples matching your search criteria that shows the status of each test that is being performed.</p><p>The following are descriptions of each column on this screen.</p><p>Accession Number: The lab number assigned to the sample by SHL.</p><p>Sample/Test Description: The Sample Description is displayed in the first row for each Accession #. The collector''s name is normally displayed for environmental and SDWIS samples; whereas, the patient''s name is displayed for clinical samples. Test Descriptions are displayed in the remaining rows for each Accession #. There may be a footnote at the end of a Sample or Test Description which is explained in the QA Event column. If a footnote is after the Sample Description, it applies to the entire sample. If a footnote is after a Test Description, it applies to only that test.</p><p>Test Status: The test''s status is shown here. \"In Progress\" tests have not been finished. \"Completed\" tests have finished testing and their results are available on the Final Report or through the Spreadsheet View.</p><p>Collected Date: The date and time that the sample was collected. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Date Received: The date and time that the sample was received at SHL. All dates and times have the format of YYYY-MM-DD HH:MM.</p><p>Client Reference: This value is a piece of information that your organization provided on the sample paperwork. It could be your organization''s lab number, a patient ID/Chart ID/Medical Record Number, or another unique identifier for a sample.</p><p>QA Event: Any quality assurance issue that applies to your entire sample or an individual test is displayed in this column.</p></span>")
  @Key("sampleStatus.help.viewScreen")
  String sampleStatus_help_viewScreen();

  /**
   * Translated "In Progress".
   * 
   * @return translated "In Progress"
   */
  @DefaultMessage("In Progress")
  @Key("sampleStatus.inProgress")
  String sampleStatus_inProgress();

  /**
   * Translated "QA Event".
   * 
   * @return translated "QA Event"
   */
  @DefaultMessage("QA Event")
  @Key("sampleStatus.qaEvent")
  String sampleStatus_qaEvent();

  /**
   * Translated "Test Status".
   * 
   * @return translated "Test Status"
   */
  @DefaultMessage("Test Status")
  @Key("sampleStatus.testStatus")
  String sampleStatus_testStatus();

  /**
   * Translated "Facility ID".
   * 
   * @return translated "Facility ID"
   */
  @DefaultMessage("Facility ID")
  @Key("sdwis.facilityId")
  String sdwis_facilityId();

  /**
   * Translated "Sample Category".
   * 
   * @return translated "Sample Category"
   */
  @DefaultMessage("Sample Category")
  @Key("sdwis.sampleCategory")
  String sdwis_sampleCategory();

  /**
   * Translated "Sample Point ID".
   * 
   * @return translated "Sample Point ID"
   */
  @DefaultMessage("Sample Point ID")
  @Key("sdwis.samplePtId")
  String sdwis_samplePtId();

  /**
   * Translated "SDWIS Sample Type".
   * 
   * @return translated "SDWIS Sample Type"
   */
  @DefaultMessage("SDWIS Sample Type")
  @Key("sdwis.sampleType")
  String sdwis_sampleType();
}
