grant dba to "dba";
grant dba to "dshirazi";
grant connect to "akampoow";
grant connect to "bcook";
grant connect to "jboss_demo";
grant connect to "jsartain";
grant connect to "kakenned";
grant connect to "mbielick";
grant connect to "nfalat";
grant connect to "openelis";
grant connect to "wberger";
grant connect to "xfer";

revoke all on address from "public";
revoke all on analysis from "public";
revoke all on analysis_qaevent from "public";
revoke all on analysis_report_flags from "public";
revoke all on analysis_user from "public";
revoke all on analyte from "public";
revoke all on analyte_parameter from "public";
revoke all on attachment from "public";
revoke all on attachment_item from "public";
revoke all on aux_data from "public";
revoke all on aux_field from "public";
revoke all on aux_field_group from "public";
revoke all on aux_field_value from "public";
revoke all on category from "public";
revoke all on cron from "public";
revoke all on dictionary from "public";
revoke all on eorder from "public";
revoke all on eorder_body from "public";
revoke all on eorder_link from "public";
revoke all on event_log from "public";
revoke all on exchange_criteria from "public";
revoke all on exchange_external_term from "public";
revoke all on exchange_local_term from "public";
revoke all on exchange_profile from "public";
revoke all on history from "public";
revoke all on instrument from "public";
revoke all on instrument_log from "public";
revoke all on inventory_adjustment from "public";
revoke all on inventory_component from "public";
revoke all on inventory_item from "public";
revoke all on inventory_location from "public";
revoke all on inventory_receipt from "public";
revoke all on inventory_receipt_iorder_item from "public";
revoke all on inventory_x_adjust from "public";
revoke all on inventory_x_put from "public";
revoke all on inventory_x_use from "public";
revoke all on label from "public";
revoke all on lock from "public";
revoke all on method from "public";
revoke all on note from "public";
revoke all on iorder from "public";
revoke all on iorder_container from "public";
revoke all on iorder_item from "public";
revoke all on iorder_organization from "public";
revoke all on iorder_recurrence from "public";
revoke all on iorder_test from "public";
revoke all on iorder_test_analyte from "public";
revoke all on organization from "public";
revoke all on organization_contact from "public";
revoke all on organization_parameter from "public";
revoke all on panel from "public";
revoke all on panel_item from "public";
revoke all on patient from "public";
revoke all on patient_relation from "public";
revoke all on preferences from "public";
revoke all on project from "public";
revoke all on project_parameter from "public";
revoke all on provider from "public";
revoke all on provider_location from "public";
revoke all on pws from "public";
revoke all on pws_address from "public";
revoke all on pws_facility from "public";
revoke all on pws_monitor from "public";
revoke all on pws_violation from "public";
revoke all on qaevent from "public";
revoke all on qc from "public";
revoke all on qc_analyte from "public";
revoke all on qc_lot from "public";
revoke all on result from "public";
revoke all on sample from "public";
revoke all on sample_animal from "public";
revoke all on sample_clinical from "public";
revoke all on sample_environmental from "public";
revoke all on sample_item from "public";
revoke all on sample_neonatal from "public";
revoke all on sample_organization from "public";
revoke all on sample_private_well from "public";
revoke all on sample_project from "public";
revoke all on sample_pt from "public";
revoke all on sample_qaevent from "public";
revoke all on sample_sdwis from "public";
revoke all on scriptlet from "public";
revoke all on section from "public";
revoke all on section_parameter from "public";
revoke all on shipping from "public";
revoke all on shipping_item from "public";
revoke all on shipping_tracking from "public";
revoke all on standard_note from "public";
revoke all on storage from "public";
revoke all on storage_location from "public";
revoke all on storage_unit from "public";
revoke all on system_variable from "public";
revoke all on test from "public";
revoke all on test_analyte from "public";
revoke all on test_prep from "public";
revoke all on test_reflex from "public";
revoke all on test_result from "public";
revoke all on test_section from "public";
revoke all on test_trailer from "public";
revoke all on test_type_of_sample from "public";
revoke all on test_worksheet from "public";
revoke all on test_worksheet_analyte from "public";
revoke all on test_worksheet_item from "public";
revoke all on worksheet from "public";
revoke all on worksheet_analysis from "public";
revoke all on worksheet_item from "public";
revoke all on worksheet_qc_result from "public";
revoke all on worksheet_reagent from "public";
revoke all on worksheet_result from "public";

grant select,update,insert,delete,index on address to "jboss_demo";
grant select,index on address to "public";

grant select,update,insert,delete,index on analysis to "jboss_demo";
grant select,index on analysis to "public";

grant select,update,insert,delete,index on analysis_qaevent to "jboss_demo";
grant select,index on analysis_qaevent to "public";

grant select,update,insert,delete,index on analysis_report_flags to "jboss_demo";
grant select,index on analysis_report_flags to "public";

grant select,update,insert,delete,index on analysis_user to "jboss_demo";
grant select,index on analysis_user to "public";

grant select,update,insert,delete,index on analyte to "jboss_demo";
grant select,index on analyte to "public";

grant select,update,insert,delete,index on analyte_parameter to "jboss_demo";
grant select,index on analyte_parameter to "public";

grant select,update,insert,delete,index on attachment to "jboss_demo";
grant select,index on attachment to "public";

grant select,update,insert,delete,index on attachment_item to "jboss_demo";
grant select,index on attachment_item to "public";

grant select,update,insert,delete,index on aux_data to "jboss_demo";
grant select,index on aux_data to "public";

grant select,update,insert,delete,index on aux_field to "jboss_demo";
grant select,index on aux_field to "public";

grant select,update,insert,delete,index on aux_field_group to "jboss_demo";
grant select,index on aux_field_group to "public";

grant select,update,insert,delete,index on aux_field_value to "jboss_demo";
grant select,index on aux_field_value to "public";

grant select,update,insert,delete,index on category to "jboss_demo";
grant select,index on category to "public";

grant select,update,insert,delete,index on cron to "jboss_demo";
grant select,index on cron to "public";

grant select,update,insert,delete,index on dictionary to "jboss_demo";
grant select,index on dictionary to "public";

grant select,update,insert,delete,index on eorder to "jboss_demo";
grant select,index on eorder to "public";

grant select,update,insert,delete,index on eorder_body to "jboss_demo";
grant select,index on eorder_body to "public";

grant select,update,insert,delete,index on eorder_link to "jboss_demo";
grant select,index on eorder_link to "public";

grant select,update,insert,delete,index on event_log to "jboss_demo";
grant select,index on event_log to "public";

grant select,update,insert,delete,index on exchange_criteria to "jboss_demo";
grant select,index on exchange_criteria to "public";

grant select,update,insert,delete,index on exchange_external_term to "jboss_demo";
grant select,index on exchange_external_term to "public";

grant select,update,insert,delete,index on exchange_local_term to "jboss_demo";
grant select,index on exchange_local_term to "public";

grant select,update,insert,delete,index on exchange_profile to "jboss_demo";
grant select,index on exchange_profile to "public";

grant select,update,insert,delete,index on history to "jboss_demo";
grant select,index on history to "public";

grant select,update,insert,delete,index on instrument to "jboss_demo";
grant select,index on instrument to "public";

grant select,update,insert,delete,index on instrument_log to "jboss_demo";
grant select,index on instrument_log to "public";

grant select,update,insert,delete,index on inventory_adjustment to "jboss_demo";
grant select,index on inventory_adjustment to "public";

grant select,update,insert,delete,index on inventory_component to "jboss_demo";
grant select,index on inventory_component to "public";

grant select,update,insert,delete,index on inventory_item to "jboss_demo";
grant select,index on inventory_item to "public";

grant select,update,insert,delete,index on inventory_location to "jboss_demo";
grant select,index on inventory_location to "public";

grant select,update,insert,delete,index on inventory_receipt to "jboss_demo";
grant select,index on inventory_receipt to "public";

grant select,update,insert,delete,index on inventory_receipt_iorder_item to "jboss_demo";
grant select,index on inventory_receipt_iorder_item to "public";

grant select,update,insert,delete,index on inventory_x_adjust to "jboss_demo";
grant select,index on inventory_x_adjust to "public";

grant select,update,insert,delete,index on inventory_x_put to "jboss_demo";
grant select,index on inventory_x_put to "public";

grant select,update,insert,delete,index on inventory_x_use to "jboss_demo";
grant select,index on inventory_x_use to "public";

grant select,update,insert,delete,index on label to "jboss_demo";
grant select,index on label to "public";

grant select,update,insert,delete,index on lock to "jboss_demo";
grant select,index on lock to "public";

grant select,update,insert,delete,index on method to "jboss_demo";
grant select,index on method to "public";

grant select,update,insert,delete,index on note to "jboss_demo";
grant select,index on note to "public";

grant select,update,insert,delete,index on iorder to "jboss_demo";
grant select,index on iorder to "public";

grant select,update,insert,delete,index on iorder_container to "jboss_demo";
grant select,index on iorder_container to "public";

grant select,update,insert,delete,index on iorder_item to "jboss_demo";
grant select,index on iorder_item to "public";

grant select,update,insert,delete,index on iorder_organization to "jboss_demo";
grant select,index on iorder_organization to "public";

grant select,update,insert,delete,index on iorder_recurrence to "jboss_demo";
grant select,index on iorder_recurrence to "public";

grant select,update,insert,delete,index on iorder_test to "jboss_demo";
grant select,index on iorder_test to "public";

grant select,update,insert,delete,index on iorder_test_analyte to "jboss_demo";
grant select,index on iorder_test_analyte to "public";

grant select,update,insert,delete,index on organization to "jboss_demo";
grant select,index on organization to "public";

grant select,update,insert,delete,index on organization_contact to "jboss_demo";
grant select,index on organization_contact to "public";

grant select,update,insert,delete,index on organization_parameter to "jboss_demo";
grant select,index on organization_parameter to "public";

grant select,update,insert,delete,index on panel to "jboss_demo";
grant select,index on panel to "public";

grant select,update,insert,delete,index on panel_item to "jboss_demo";
grant select,index on panel_item to "public";

grant select,update,insert,delete,index on patient to "jboss_demo";
grant select,index on patient to "public";

grant select,update,insert,delete,index on patient_relation to "jboss_demo";
grant select,index on patient_relation to "public";

grant select,update,insert,delete,index on preferences to "jboss_demo";
grant select,index on preferences to "public";

grant select,update,insert,delete,index on project to "jboss_demo";
grant select,index on project to "public";

grant select,update,insert,delete,index on project_parameter to "jboss_demo";
grant select,index on project_parameter to "public";

grant select,update,insert,delete,index on provider to "jboss_demo";
grant select,index on provider to "public";

grant select,update,insert,delete,index on provider_location to "jboss_demo";
grant select,index on provider_location to "public";

grant select,update,insert,delete,index on pws to "jboss_demo";
grant select,index on pws to "public";

grant select,update,insert,delete,index on pws_address to "jboss_demo";
grant select,index on pws_address to "public";

grant select,update,insert,delete,index on pws_facility to "jboss_demo";
grant select,index on pws_facility to "public";

grant select,update,insert,delete,index on pws_monitor to "jboss_demo";
grant select,index on pws_monitor to "public";

grant select,update,insert,delete,index on pws_violation to "jboss_demo";
grant select,index on pws_violation to "public";

grant select,update,insert,delete,index on qaevent to "jboss_demo";
grant select,index on qaevent to "public";

grant select,update,insert,delete,index on qc to "jboss_demo";
grant select,index on qc to "public";

grant select,update,insert,delete,index on qc_analyte to "jboss_demo";
grant select,index on qc_analyte to "public";

grant select,update,insert,delete,index on qc_lot to "jboss_demo";
grant select,index on qc_lot to "public";

grant select,update,insert,delete,index on result to "jboss_demo";
grant select,index on result to "public";

grant select,update,insert,delete,index on sample to "jboss_demo";
grant select,index on sample to "public";

grant select,update,insert,delete,index on sample_animal to "jboss_demo";
grant select,index on sample_animal to "public";

grant select,update,insert,delete,index on sample_clinical to "jboss_demo";
grant select,index on sample_clinical to "public";

grant select,update,insert,delete,index on sample_environmental to "jboss_demo";
grant select,index on sample_environmental to "public";

grant select,update,insert,delete,index on sample_item to "jboss_demo";
grant select,index on sample_item to "public";

grant select,update,insert,delete,index on sample_neonatal to "jboss_demo";
grant select,index on sample_neonatal to "public";

grant select,update,insert,delete,index on sample_organization to "jboss_demo";
grant select,index on sample_organization to "public";

grant select,update,insert,delete,index on sample_private_well to "jboss_demo";
grant select,index on sample_private_well to "public";

grant select,update,insert,delete,index on sample_project to "jboss_demo";
grant select,index on sample_project to "public";

grant select,update,insert,delete,index on sample_pt to "jboss_demo";
grant select,index on sample_pt to "public";

grant select,update,insert,delete,index on sample_qaevent to "jboss_demo";
grant select,index on sample_qaevent to "public";

grant select,update,insert,delete,index on sample_sdwis to "jboss_demo";
grant select,index on sample_sdwis to "public";

grant select,update,insert,delete,index on scriptlet to "jboss_demo";
grant select,index on scriptlet to "public";

grant select,update,insert,delete,index on section to "jboss_demo";
grant select,index on section to "public";

grant select,update,insert,delete,index on section_parameter to "jboss_demo";
grant select,index on section_parameter to "public";

grant select,update,insert,delete,index on shipping to "jboss_demo";
grant select,index on shipping to "public";

grant select,update,insert,delete,index on shipping_item to "jboss_demo";
grant select,index on shipping_item to "public";

grant select,update,insert,delete,index on shipping_tracking to "jboss_demo";
grant select,index on shipping_tracking to "public";

grant select,update,insert,delete,index on standard_note to "jboss_demo";
grant select,index on standard_note to "public";

grant select,update,insert,delete,index on storage to "jboss_demo";
grant select,index on storage to "public";

grant select,update,insert,delete,index on storage_location to "jboss_demo";
grant select,index on storage_location to "public";

grant select,update,insert,delete,index on storage_unit to "jboss_demo";
grant select,index on storage_unit to "public";

grant select,update,insert,delete,index on system_variable to "jboss_demo";
grant select,index on system_variable to "public";

grant select,update,insert,delete,index on test to "jboss_demo";
grant select,index on test to "public";

grant select,update,insert,delete,index on test_analyte to "jboss_demo";
grant select,index on test_analyte to "public";

grant select,update,insert,delete,index on test_prep to "jboss_demo";
grant select,index on test_prep to "public";

grant select,update,insert,delete,index on test_reflex to "jboss_demo";
grant select,index on test_reflex to "public";

grant select,update,insert,delete,index on test_result to "jboss_demo";
grant select,index on test_result to "public";

grant select,update,insert,delete,index on test_section to "jboss_demo";
grant select,index on test_section to "public";

grant select,update,insert,delete,index on test_trailer to "jboss_demo";
grant select,index on test_trailer to "public";

grant select,update,insert,delete,index on test_type_of_sample to "jboss_demo";
grant select,index on test_type_of_sample to "public";

grant select,update,insert,delete,index on test_worksheet to "jboss_demo";
grant select,index on test_worksheet to "public";

grant select,update,insert,delete,index on test_worksheet_analyte to "jboss_demo";
grant select,index on test_worksheet_analyte to "public";

grant select,update,insert,delete,index on test_worksheet_item to "jboss_demo";
grant select,index on test_worksheet_item to "public";

grant select,update,insert,delete,index on worksheet to "jboss_demo";
grant select,index on worksheet to "public";

grant select,update,insert,delete,index on worksheet_analysis to "jboss_demo";
grant select,index on worksheet_analysis to "public";

grant select,update,insert,delete,index on worksheet_item to "jboss_demo";
grant select,index on worksheet_item to "public";

grant select,update,insert,delete,index on worksheet_qc_result to "jboss_demo";
grant select,index on worksheet_qc_result to "public";

grant select,update,insert,delete,index on worksheet_reagent to "jboss_demo";
grant select,index on worksheet_reagent to "public";

grant select,update,insert,delete,index on worksheet_result to "jboss_demo";
grant select,index on worksheet_result to "public";

grant select on analysis_view to "public";
grant select on sample_patient_view to "public";
grant select on sample_view to "public";
grant select on test_analyte_view to "public";
grant select on todo_sample_view to "public";
grant select on worksheet_analysis_view to "public";
grant select on worksheet_qc_result_view to "public";

grant execute on function instrument_get_analytes (integer,integer) to "public";