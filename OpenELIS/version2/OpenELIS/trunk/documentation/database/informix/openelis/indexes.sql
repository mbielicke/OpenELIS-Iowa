-------------------------------------------------------------------------------
--
-- indicies
--
-------------------------------------------------------------------------------

create unique index address_1_idx on address(id);

create unique index analysis_1_idx on analysis(id);
create        index analysis_2_idx on analysis(sample_item_id);
create        index analysis_3_idx on analysis(test_id);
create        index analysis_4_idx on analysis(released_date);

create unique index analysis_qaevent_1_idx on analysis_qaevent(id);
create        index analysis_qaevent_2_idx on analysis_qaevent(analysis_id);

create unique index analysis_report_flags_1_idx on analysis_report_flags(analysis_id);

create unique index analysis_user_1_idx on analysis_user(id);
create        index analysis_user_2_idx on analysis_user(analysis_id);

create unique index analyte_1_idx on analyte(id);
create unique index analyte_2_idx on analyte(name);

create unique index analyte_parameter_1_idx on analyte_parameter(id);
create        index analyte_parameter_2_idx on analyte_parameter(reference_id, reference_table_id);
create        index analyte_parameter_3_idx on analyte_parameter(analyte_id);

create unique index attachment_1_idx on attachment(id);

create unique index attachment_issue_1_idx on attachment_issue(id);
create unique index attachment_issue_2_idx on attachment_issue(attachment_id);

create unique index attachment_item_1_idx on attachment_item(id);
create        index attachment_item_2_idx on attachment_item(reference_id, reference_table_id);

create unique index aux_data_1_idx on aux_data(id);
create        index aux_data_2_idx on aux_data(reference_id, reference_table_id);

create unique index aux_field_1_idx on aux_field(id);
create        index aux_field_2_idx on aux_field(aux_field_group_id);

create unique index aux_field_group_1_idx on aux_field_group(id);
create        index aux_field_group_2_idx on aux_field_group(name);

create unique index aux_field_value_1_idx on aux_field_value(id);
create        index aux_field_value_2_idx on aux_field_value(aux_field_id);

create unique index category_1_idx on category(id);
create unique index category_2_idx on category(system_name);

create unique index cron_1_idx on cron(id);

create unique index dictionary_1_idx on dictionary(id);
create        index dictionary_2_idx on dictionary(category_id);

create unique index eorder_1_idx on eorder(id);
create        index eorder_2_idx on eorder(paper_order_validator);

create unique index eorder_body_1_idx on eorder_body(id);
create        index eorder_body_2_idx on eorder_body(eorder_id);

create unique index eorder_link_1_idx on eorder_link(id);
create        index eorder_link_2_idx on eorder_link(eorder_id);

create unique index event_log_1_idx on event_log(id);
create        index event_log_2_idx on event_log(reference_id, reference_table_id);

create unique index exchange_criteria_1_idx on exchange_criteria(id);
create unique index exchange_criteria_2_idx on exchange_criteria(name);

create unique index exchange_external_term_1_idx on exchange_external_term(id);
create        index exchange_external_term_2_idx on exchange_external_term(exchange_local_term_id);

create unique index exchange_local_term_1_idx on exchange_local_term(id);
create        index exchange_local_term_2_idx on exchange_local_term(reference_id, reference_table_id);

create unique index exchange_profile_1_idx on exchange_profile(id);
create        index exchange_profile_2_idx on exchange_profile(exchange_criteria_id);

create unique index history_1_idx on history(id);
create        index history_2_idx on history(reference_id, reference_table_id);

create unique index instrument_1_idx on instrument(id);
create        index instrument_2_idx on instrument(name);

create unique index instrument_log_1_idx on instrument_log(id);
create        index instrument_log_2_idx on instrument_log(instrument_id);

create unique index inventory_adjustment_1_idx on inventory_adjustment(id);

create unique index inventory_component_1_idx on inventory_component(id);
create        index inventory_component_2_idx on inventory_component(inventory_item_id);

create unique index inventory_item_1_idx on inventory_item(id);
create        index inventory_item_2_idx on inventory_item(name);

create unique index inventory_location_1_idx on inventory_location(id);
create        index inventory_location_2_idx on inventory_location(inventory_item_id);

create unique index inventory_receipt_1_idx on inventory_receipt(id);
create        index inventory_receipt_2_idx on inventory_receipt(inventory_item_id);
create        index inventory_receipt_3_idx on inventory_receipt(iorder_item_id);

create unique index inventory_receipt_iorder_item_1_idx on inventory_receipt_iorder_item(id);
create        index inventory_receipt_iorder_item_2_idx on inventory_receipt_iorder_item(inventory_receipt_id);
create        index inventory_receipt_iorder_item_3_idx on inventory_receipt_iorder_item(iorder_item_id);

create unique index inventory_x_adjust_1_idx on inventory_x_adjust(id);
create        index inventory_x_adjust_2_idx on inventory_x_adjust(inventory_adjustment_id);
create        index inventory_x_adjust_3_idx on inventory_x_adjust(inventory_location_id);

create unique index inventory_x_put_1_idx on inventory_x_put(id);
create        index inventory_x_put_2_idx on inventory_x_put(inventory_receipt_id);
create        index inventory_x_put_3_idx on inventory_x_put(inventory_location_id);

create unique index inventory_x_use_1_idx on inventory_x_use(id);
create        index inventory_x_use_2_idx on inventory_x_use(inventory_location_id);
create        index inventory_x_use_3_idx on inventory_x_use(iorder_item_id);

create unique index iorder_1_idx on iorder(id);
create        index iorder_2_idx on iorder(organization_id);

create unique index iorder_container_1_idx on iorder_container(id);
create        index iorder_container_2_idx on iorder_container(iorder_id);

create unique index iorder_item_1_idx on iorder_item(id);
create        index iorder_item_2_idx on iorder_item(iorder_id);

create unique index iorder_organization_1_idx on iorder_organization(id);
create        index iorder_organization_2_idx on iorder_organization(iorder_id);

create unique index iorder_recurrence_1_idx on iorder_recurrence(id);
create unique index iorder_recurrence_2_idx on iorder_recurrence(iorder_id);

create unique index iorder_test_1_idx on iorder_test(id);
create        index iorder_test_2_idx on iorder_test(iorder_id);

create unique index iorder_test_analyte_1_idx on iorder_test_analyte(id);
create        index iorder_test_analyte_2_idx on iorder_test_analyte(iorder_test_id);

create unique index label_1_idx on label(id);
create unique index label_2_idx on label(name);

create unique index lock_1_idx on lock(reference_table_id, reference_id);

create unique index method_1_idx on method(id);
create        index method_2_idx on method(name);

create unique index note_1_idx on note(id);
create        index note_2_idx on note(reference_id, reference_table_id);

create unique index organization_1_idx on organization(id);
create        index organization_2_idx on organization(name);

create unique index organization_contact_1_idx on organization_contact(id);
create        index organization_contact_2_idx on organization_contact(organization_id);

create unique index organization_parameter_1_idx on organization_parameter(id);
create        index organization_parameter_2_idx on organization_parameter(organization_id);

create unique index panel_1_idx on panel(id);
create unique index panel_2_idx on panel(name);

create unique index panel_item_1_idx on panel_item(id);
create        index panel_item_2_idx on panel_item(panel_id);

create unique index patient_1_idx on patient(id);
create        index patient_2_idx on patient(last_name);
create        index patient_3_idx on patient(national_id);

create unique index patient_relation_1_idx on patient_relation(id);
create        index patient_relation_2_idx on patient_relation(patient_id);
create        index patient_relation_3_idx on patient_relation(related_patient_id);

create unique index preferences_1_idx on preferences(system_user_id);

create unique index project_1_idx on project(id);
create        index project_2_idx on project(name);

create unique index project_parameter_1_idx on project_parameter(id);
create        index project_parameter_2_idx on project_parameter(project_id);

create unique index provider_1_idx on provider(id);
create        index provider_2_idx on provider(last_name);
create        index provider_3_idx on provider(npi);

create unique index provider_analyte_1_idx on provider_analyte(id);
create        index provider_analyte_2_idx on provider_analyte(provider_id);

create unique index provider_location_1_idx on provider_location(id);
create        index provider_location_2_idx on provider_location(provider_id);

create unique index pws_1_idx on pws(id);
create unique index pws_2_idx on pws(tinwsys_is_number);
create unique index pws_3_idx on pws(number0);

create unique index pws_address_1_idx on pws_address(tinwslec_is_number, tinlgent_is_number);
create        index pws_address_2_idx on pws_address(tinwsys_is_number);

create unique index pws_facility_1_idx on pws_facility(tinwsf_is_number, tsasmppt_is_number);
create        index pws_facility_2_idx on pws_facility(tinwsys_is_number);

create unique index pws_monitor_1_idx on pws_monitor(tiamrtask_is_number);
create        index pws_monitor_2_idx on pws_monitor(tinwsys_is_number);

create unique index pws_violation_1_idx on pws_violation(id);
create        index pws_violation_2_idx on pws_violation(tinwsys_is_number);

create unique index qaevent_1_idx on qaevent(id);
create        index qaevent_2_idx on qaevent(name);

create unique index qc_1_idx on qc(id);
create        index qc_2_idx on qc(name);

create unique index qc_analyte_1_idx on qc_analyte(id);
create        index qc_analyte_2_idx on qc_analyte(qc_id);

create unique index qc_lot_1_idx on qc_lot(id);
create        index qc_lot_2_idx on qc_lot(qc_id);

create unique index result_1_idx on result(id);
create        index result_2_idx on result(analysis_id);

create unique index sample_1_idx on sample(id);
create unique index sample_2_idx on sample(accession_number);
create        index sample_3_idx on sample(entered_date);
create        index sample_4_idx on sample(received_date);
create        index sample_5_idx on sample(released_date);

create unique index sample_animal_1_idx on sample_animal(id);
create unique index sample_animal_2_idx on sample_animal(sample_id);

create unique index sample_clinical_1_idx on sample_clinical(id);
create unique index sample_clinical_2_idx on sample_clinical(sample_id);
create        index sample_clinical_3_idx on sample_clinical(patient_id);

create unique index sample_environmental_1_idx on sample_environmental(id);
create unique index sample_environmental_2_idx on sample_environmental(sample_id);

create unique index sample_item_1_idx on sample_item(id);
create        index sample_item_2_idx on sample_item(sample_id);

create unique index sample_neonatal_1_idx on sample_neonatal(id);
create unique index sample_neonatal_2_idx on sample_neonatal(sample_id);
create        index sample_neonatal_3_idx on sample_neonatal(patient_id);

create unique index sample_organization_1_idx on sample_organization(id);
create        index sample_organization_2_idx on sample_organization(sample_id);
create        index sample_organization_3_idx on sample_organization(organization_id);

create unique index sample_project_1_idx on sample_project(id);
create        index sample_project_2_idx on sample_project(sample_id);
create        index sample_project_3_idx on sample_project(project_id);

create unique index sample_pt_1_idx on sample_pt(id);
create        index sample_pt_2_idx on sample_pt(sample_id);

create unique index sample_qaevent_1_idx on sample_qaevent(id);
create        index sample_qaevent_2_idx on sample_qaevent(sample_id);
create        index sample_qaevent_3_idx on sample_qaevent(qaevent_id);

create unique index sample_sdwis_1_idx on sample_sdwis(id);
create unique index sample_sdwis_2_idx on sample_sdwis(sample_id);
create        index sample_sdwis_3_idx on sample_sdwis(pws_id);

create unique index scriptlet_1_idx on scriptlet(id);

create unique index section_1_idx on section(id);

create unique index section_parameter_1_idx on section_parameter(id);
create        index section_parameter_2_idx on section_parameter(section_id);

create unique index shipping_1_idx on shipping(id);
create        index shipping_2_idx on shipping(shipped_from_id);
create        index shipping_3_idx on shipping(shipped_to_id);

create unique index shipping_item_1_idx on shipping_item(id);
create        index shipping_item_2_idx on shipping_item(shipping_id);
create        index shipping_item_3_idx on shipping_item(reference_id, reference_table_id);

create unique index shipping_tracking_1_idx on shipping_tracking(id);
create        index shipping_tracking_2_idx on shipping_tracking(shipping_id);

create unique index standard_note_1_idx on standard_note(id);
create        index standard_note_2_idx on standard_note(name);

create unique index storage_1_idx on storage(id);
create        index storage_2_idx on storage(reference_id, reference_table_id);

create unique index storage_location_1_idx on storage_location(id);
create        index storage_location_2_idx on storage_location(name);

create unique index storage_unit_1_idx on storage_unit(id);

create unique index system_variable_1_idx on system_variable(id);
create unique index system_variable_2_idx on system_variable(name);

create unique index test_1_idx on test(id);
create        index test_2_idx on test(name);

create unique index test_analyte_1_idx on test_analyte(id);
create        index test_analyte_2_idx on test_analyte(test_id);

create unique index test_prep_1_idx on test_prep(id);
create        index test_prep_2_idx on test_prep(test_id);

create unique index test_reflex_1_idx on test_reflex(id);
create        index test_reflex_2_idx on test_reflex(test_id);

create unique index test_result_1_idx on test_result(id);
create        index test_result_2_idx on test_result(test_id);

create unique index test_section_1_idx on test_section(id);
create        index test_section_2_idx on test_section(test_id);
create        index test_section_3_idx on test_section(section_id);

create unique index test_trailer_1_idx on test_trailer(id);
create unique index test_trailer_2_idx on test_trailer(name);

create unique index test_type_of_sample_1_idx on test_type_of_sample(id);
create        index test_type_of_sample_2_idx on test_type_of_sample(test_id);

create unique index test_worksheet_1_idx on test_worksheet(id);
create unique index test_worksheet_2_idx on test_worksheet(test_id);

create unique index test_worksheet_analyte_1_idx on test_worksheet_analyte(id);
create        index test_worksheet_analyte_2_idx on test_worksheet_analyte(test_id);

create unique index test_worksheet_item_1_idx on test_worksheet_item(id);
create        index test_worksheet_item_2_idx on test_worksheet_item(test_worksheet_id);

create unique index worksheet_1_idx on worksheet(id);
create        index worksheet_2_idx on worksheet(created_date);
create        index worksheet_3_idx on worksheet(system_user_id);

create unique index worksheet_analysis_1_idx on worksheet_analysis(id);
create        index worksheet_analysis_2_idx on worksheet_analysis(worksheet_item_id);
create        index worksheet_analysis_3_idx on worksheet_analysis(analysis_id);
create        index worksheet_analysis_4_idx on worksheet_analysis(qc_lot_id);

create unique index worksheet_item_1_idx on worksheet_item(id);
create        index worksheet_item_2_idx on worksheet_item(worksheet_id);

create unique index worksheet_qc_result_1_idx on worksheet_qc_result(id);
create        index worksheet_qc_result_2_idx on worksheet_qc_result(worksheet_analysis_id);

create unique index worksheet_reagent_1_idx on worksheet_reagent(id);
create        index worksheet_reagent_2_idx on worksheet_reagent(worksheet_id);
create        index worksheet_reagent_3_idx on worksheet_reagent(qc_lot_id);

create unique index worksheet_result_1_idx on worksheet_result(id);
create        index worksheet_result_2_idx on worksheet_result(worksheet_analysis_id);

-------------------------------------------------------------------------------
--
-- primary and foreign key
--
-------------------------------------------------------------------------------

alter table address add constraint primary key(id) constraint address_pk;
alter table analysis add constraint primary key(id) constraint analysis_pk;
alter table analysis_qaevent add constraint primary key(id) constraint analysis_qaevent_pk;
alter table analysis_user add constraint primary key(id) constraint analysis_user_pk;
alter table analyte add constraint primary key(id) constraint analyte_pk;
alter table analyte_parameter add constraint primary key(id) constraint analyte_parameter_pk;
alter table attachment add constraint primary key(id) constraint attachment_pk;
alter table attachment_issue add constraint primary key(id) constraint attachment_issue_pk;
alter table attachment_item add constraint primary key(id) constraint attachment_item_pk;
alter table aux_data add constraint primary key(id) constraint aux_data_pk;
alter table aux_field add constraint primary key(id) constraint aux_field_pk;
alter table aux_field_group add constraint primary key(id) constraint aux_field_group_pk;
alter table aux_field_value add constraint primary key(id) constraint aux_field_value_pk;
alter table category add constraint primary key(id) constraint category_pk;
alter table cron add constraint primary key(id) constraint cron_pk;
alter table dictionary add constraint primary key(id) constraint dictionary_pk;
alter table eorder add constraint primary key(id) constraint eorder_pk;
alter table eorder_body add constraint primary key(id) constraint eorder_body_pk;
alter table eorder_link add constraint primary key(id) constraint eorder_link_pk;
alter table event_log add constraint primary key(id) constraint event_log_pk;
alter table exchange_criteria add constraint primary key(id) constraint exchange_criteria_pk;
alter table exchange_external_term add constraint primary key(id) constraint exchange_external_term_pk;
alter table exchange_local_term add constraint primary key(id) constraint exchange_local_term_pk;
alter table exchange_profile add constraint primary key(id) constraint exchange_profile_pk;
alter table history add constraint primary key(id) constraint history_pk;
alter table instrument add constraint primary key(id) constraint instrument_pk;
alter table instrument_log add constraint primary key(id) constraint instrument_log_pk;
alter table inventory_adjustment add constraint primary key(id) constraint inventory_adjustment_pk;
alter table inventory_component add constraint primary key(id) constraint inventory_component_pk;
alter table inventory_item add constraint primary key(id) constraint inventory_item_pk;
alter table inventory_location add constraint primary key(id) constraint inventory_location_pk;
alter table inventory_receipt add constraint primary key(id) constraint inventory_receipt_pk;
alter table inventory_receipt_iorder_item add constraint primary key(id) constraint inventory_receipt_iorder_item_pk;
alter table inventory_x_adjust add constraint primary key(id) constraint inventory_x_adjust_pk;
alter table inventory_x_put add constraint primary key(id) constraint inventory_x_put_pk;
alter table inventory_x_use add constraint primary key(id) constraint inventory_x_use_pk;
alter table iorder add constraint primary key(id) constraint iorder_pk;
alter table iorder_container add constraint primary key(id) constraint iorder_container_pk;
alter table iorder_item add constraint primary key(id) constraint iorder_item_pk;
alter table iorder_organization add constraint primary key(id) constraint iorder_organization_pk;
alter table iorder_recurrence add constraint primary key(id) constraint iorder_recurrence_pk;
alter table iorder_test add constraint primary key(id) constraint iorder_test_pk;
alter table iorder_test_analyte add constraint primary key(id) constraint iorder_test_analyte_pk;
alter table label add constraint primary key(id) constraint label_pk;
alter table lock add constraint primary key(reference_id, reference_table_id) constraint lock_pk;
alter table method add constraint primary key(id) constraint method_pk;
alter table note add constraint primary key(id) constraint note_pk;
alter table organization add constraint primary key(id) constraint organization_pk;
alter table organization_contact add constraint primary key(id) constraint organization_contact_pk;
alter table organization_parameter add constraint primary key(id) constraint organization_parameter_pk;
alter table panel add constraint primary key(id) constraint panel_pk;
alter table panel_item add constraint primary key(id) constraint panel_item_pk;
alter table patient add constraint primary key(id) constraint patient_pk;
alter table patient_relation add constraint primary key(id) constraint patient_relation_pk;
alter table project add constraint primary key(id) constraint project_pk;
alter table project_parameter add constraint primary key(id) constraint project_parameter_pk;
alter table provider add constraint primary key(id) constraint provider_pk;
alter table provider_analyte add constraint primary key(id) constraint provider_analyte_pk;
alter table provider_location add constraint primary key(id) constraint provider_location_pk;
alter table pws add constraint primary key(id) constraint pws_pk;
alter table pws_address add constraint primary key(tinwslec_is_number, tinlgent_is_number) constraint pws_address_pk;
alter table pws_facility add constraint primary key(tinwsf_is_number, tsasmppt_is_number) constraint pws_facility_pk;
alter table pws_monitor add constraint primary key(tiamrtask_is_number) constraint pws_monitor_pk;
alter table pws_violation add constraint primary key(id) constraint pws_violation_pk;
alter table qaevent add constraint primary key(id) constraint qaevent_pk;
alter table qc add constraint primary key(id) constraint qc_pk;
alter table qc_analyte add constraint primary key(id) constraint qc_analyte_pk;
alter table qc_lot add constraint primary key(id) constraint qc_lot_pk;
alter table result add constraint primary key(id) constraint result_pk;
alter table sample add constraint primary key(id) constraint sample_pk;
alter table sample_animal add constraint primary key(id) constraint sample_animal_pk;
alter table sample_clinical add constraint primary key(id) constraint sample_clinical_pk;
alter table sample_environmental add constraint primary key(id) constraint sample_environmental_pk;
alter table sample_item add constraint primary key(id) constraint sample_item_pk;
alter table sample_neonatal add constraint primary key(id) constraint sample_neonatal_pk;
alter table sample_organization add constraint primary key(id) constraint sample_organization_pk;
alter table sample_project add constraint primary key(id) constraint sample_project_pk;
alter table sample_pt add constraint primary key(id) constraint sample_pt_pk;
alter table sample_qaevent add constraint primary key(id) constraint sample_qaevent_pk;
alter table sample_sdwis add constraint primary key(id) constraint sample_sdwis_pk;
alter table scriptlet add constraint primary key(id) constraint scriptlet_pk;
alter table section add constraint primary key(id) constraint section_pk;
alter table section_parameter add constraint primary key(id) constraint section_parameter_pk;
alter table shipping add constraint primary key(id) constraint shipping_pk;
alter table shipping_item add constraint primary key(id) constraint shipping_item_pk;
alter table shipping_tracking add constraint primary key(id) constraint shipping_tracking_pk;
alter table standard_note add constraint primary key(id) constraint standard_note_pk;
alter table storage add constraint primary key(id) constraint storage_pk;
alter table storage_location add constraint primary key(id) constraint storage_location_pk;
alter table storage_unit add constraint primary key(id) constraint storage_unit_pk;
alter table system_variable add constraint primary key(id) constraint system_variable_pk;
alter table test add constraint primary key(id) constraint test_pk;
alter table test_analyte add constraint primary key(id) constraint test_analyte_pk;
alter table test_prep add constraint primary key(id) constraint test_prep_pk;
alter table test_reflex add constraint primary key(id) constraint test_reflex_pk;
alter table test_result add constraint primary key(id) constraint test_result_pk;
alter table test_section add constraint primary key(id) constraint test_section_pk;
alter table test_trailer add constraint primary key(id) constraint test_trailer_pk;
alter table test_type_of_sample add constraint primary key(id) constraint test_type_of_sample_pk;
alter table test_worksheet add constraint primary key(id) constraint test_worksheet_pk;
alter table test_worksheet_analyte add constraint primary key(id) constraint test_worksheet_analyte_pk;
alter table test_worksheet_item add constraint primary key(id) constraint test_worksheet_item_pk;
alter table worksheet add constraint primary key(id) constraint worksheet_pk;
alter table worksheet_analysis add constraint primary key(id) constraint worksheet_analysis_pk;
alter table worksheet_item add constraint primary key(id) constraint worksheet_item_pk;
alter table worksheet_qc_result add constraint primary key(id) constraint worksheet_qc_result_pk;
alter table worksheet_reagent add constraint primary key(id) constraint worksheet_reagent_pk;
alter table worksheet_result add constraint primary key(id) constraint worksheet_result_pk;

alter table analysis add constraint foreign key(sample_item_id) references sample_item(id) constraint analysis_1_fk;
alter table analysis add constraint foreign key(test_id) references test(id) constraint analysis_2_fk;
alter table analysis add constraint foreign key(section_id) references section(id) constraint analysis_3_fk;
alter table analysis add constraint foreign key(panel_id) references panel(id) constraint analysis_4_fk;
alter table analysis add constraint foreign key(pre_analysis_id) references analysis(id) constraint analysis_5_fk;
alter table analysis add constraint foreign key(parent_analysis_id) references analysis(id) constraint analysis_6_fk;
alter table analysis add constraint foreign key(parent_result_id) references result(id) constraint analysis_7_fk;
alter table analysis add constraint foreign key(type_id) references dictionary(id) constraint analysis_8_fk;
alter table analysis add constraint foreign key(unit_of_measure_id) references dictionary(id) constraint analysis_9_fk;
alter table analysis add constraint foreign key(status_id) references dictionary(id) constraint analysis_10_fk;

alter table analysis_qaevent add constraint foreign key(analysis_id) references analysis(id) constraint analysis_qaevent_1_fk;
alter table analysis_qaevent add constraint foreign key(qaevent_id) references qaevent(id) constraint analysis_qaevent_2_fk;
alter table analysis_qaevent add constraint foreign key(type_id) references dictionary(id) constraint analysis_qaevent_3_fk;

alter table analysis_report_flags add constraint foreign key(analysis_id) references analysis(id) constraint analysis_report_flags_1_fk;

alter table analysis_user add constraint foreign key(analysis_id) references analysis(id) constraint analysis_user_1_fk;
alter table analysis_user add constraint foreign key(action_id) references dictionary(id) constraint analysis_user_2_fk;

alter table analyte add constraint foreign key(parent_analyte_id) references analyte(id) constraint analyte_1_fk;

alter table analyte_parameter add constraint foreign key(analyte_id) references analyte(id) constraint analyte_parameter_1_fk;
alter table analyte_parameter add constraint foreign key(type_of_sample_id) references dictionary(id) constraint analyte_parameter_2_fk;
alter table analyte_parameter add constraint foreign key(unit_of_measure_id) references dictionary(id) constraint analyte_parameter_3_fk;

alter table attachment add constraint foreign key(type_id) references dictionary(id) constraint attachment_1_fk;
alter table attachment add constraint foreign key(section_id) references section(id) constraint attachment_2_fk;

alter table attachment_issue add constraint foreign key(attachment_id) references attachment(id) constraint attachment_issue_1_fk;

alter table attachment_item add constraint foreign key(attachment_id) references attachment(id) constraint attachment_item_1_fk;

alter table aux_data add constraint foreign key(aux_field_id) references aux_field(id) constraint aux_data_1_fk;
alter table aux_data add constraint foreign key(type_id) references dictionary(id) constraint aux_data_2_fk;

alter table aux_field add constraint foreign key(aux_field_group_id) references aux_field_group(id) constraint aux_field_1_fk;
alter table aux_field add constraint foreign key(analyte_id) references analyte(id) constraint aux_field_2_fk;
alter table aux_field add constraint foreign key(method_id) references method(id) constraint aux_field_3_fk;
alter table aux_field add constraint foreign key(unit_of_measure_id) references dictionary(id) constraint aux_field_4_fk;
alter table aux_field add constraint foreign key(scriptlet_id) references dictionary(id) constraint aux_field_5_fk;

alter table aux_field_value add constraint foreign key(aux_field_id) references aux_field(id) constraint aux_field_value_1_fk;
alter table aux_field_value add constraint foreign key(type_id) references dictionary(id) constraint aux_field_value_2_fk;

alter table category add constraint foreign key(section_id) references section(id) constraint category_1_fk;

alter table dictionary add constraint foreign key(category_id) references category(id) constraint dictionary_1_fk;
alter table dictionary add constraint foreign key(related_entry_id) references dictionary(id) constraint dictionary_2_fk;

alter table eorder_body add constraint foreign key(eorder_id) references eorder(id) constraint eorder_body_1_fk;

alter table eorder_link add constraint foreign key(eorder_id) references eorder(id) constraint eorder_link_1_fk;

alter table event_log add constraint foreign key(type_id) references dictionary(id) constraint event_log_1_fk;
alter table event_log add constraint foreign key(level_id) references dictionary(id) constraint event_log_2_fk;

alter table exchange_criteria add constraint foreign key(environment_id) references dictionary(id) constraint exchange_criteria_1_fk;

alter table exchange_external_term add constraint foreign key(exchange_local_term_id) references exchange_local_term(id) constraint exchange_external_term_1_fk;
alter table exchange_external_term add constraint foreign key(profile_id) references dictionary(id) constraint exchange_external_term_2_fk;

alter table exchange_profile add constraint foreign key(exchange_criteria_id) references exchange_criteria(id) constraint exchange_profile_1_fk;
alter table exchange_profile add constraint foreign key(profile_id) references dictionary(id) constraint exchange_profile_2_fk;

alter table history add constraint foreign key(activity_id) references dictionary(id) constraint history_1_fk;

alter table instrument add constraint foreign key(type_id) references dictionary(id) constraint instrument_1_fk;
alter table instrument add constraint foreign key(scriptlet_id) references dictionary(id) constraint instrument_2_fk;

alter table instrument_log add constraint foreign key(instrument_id) references instrument(id) constraint instrument_log_1_fk;
alter table instrument_log add constraint foreign key(type_id) references dictionary(id) constraint instrument_log_2_fk;
alter table instrument_log add constraint foreign key(worksheet_id) references worksheet(id) constraint instrument_log_3_fk;

alter table inventory_component add constraint foreign key(inventory_item_id) references inventory_item(id) constraint inventory_component_1_fk;
alter table inventory_component add constraint foreign key(component_id) references inventory_item(id) constraint inventory_component_2_fk;

alter table inventory_item add constraint foreign key(category_id) references dictionary(id) constraint inventory_item_1_fk;
alter table inventory_item add constraint foreign key(store_id) references dictionary(id) constraint inventory_item_2_fk;
alter table inventory_item add constraint foreign key(dispensed_units_id) references dictionary(id) constraint inventory_item_3_fk;
alter table inventory_item add constraint foreign key(parent_inventory_item_id) references inventory_item(id) constraint inventory_item_4_fk;

alter table inventory_location add constraint foreign key(inventory_item_id) references inventory_item(id) constraint inventory_location_1_fk;
alter table inventory_location add constraint foreign key(storage_location_id) references storage_location(id) constraint inventory_location_2_fk;

alter table inventory_receipt add constraint foreign key(inventory_item_id) references inventory_item(id) constraint inventory_receipt_1_fk;
alter table inventory_receipt add constraint foreign key(iorder_item_id) references iorder_item(id) constraint inventory_receipt_2_fk;
alter table inventory_receipt add constraint foreign key(organization_id) references organization(id) constraint inventory_receipt_3_fk;

alter table inventory_receipt_iorder_item add constraint foreign key(inventory_receipt_id) references inventory_receipt(id) constraint inventory_receipt_iorder_item_1_fk;
alter table inventory_receipt_iorder_item add constraint foreign key(iorder_item_id) references iorder_item(id) constraint inventory_receipt_iorder_item_2_fk;

alter table inventory_x_adjust add constraint foreign key(inventory_adjustment_id) references inventory_adjustment(id) constraint inventory_x_adjust_1_fk;
alter table inventory_x_adjust add constraint foreign key(inventory_location_id) references inventory_location(id) constraint inventory_x_adjust_2_fk;

alter table inventory_x_put add constraint foreign key(inventory_receipt_id) references inventory_receipt(id) constraint inventory_x_put_1_fk;
alter table inventory_x_put add constraint foreign key(inventory_location_id) references inventory_location(id) constraint inventory_x_put_2_fk;

alter table inventory_x_use add constraint foreign key(inventory_location_id) references inventory_location(id) constraint inventory_x_use_1_fk;
alter table inventory_x_use add constraint foreign key(iorder_item_id) references iorder_item(id) constraint inventory_x_use_2_fk;

alter table label add constraint foreign key(printer_type_id) references dictionary(id) constraint label_1_fk;
alter table label add constraint foreign key(scriptlet_id) references dictionary(id) constraint label_2_fk;

alter table iorder add constraint foreign key(parent_iorder_id) references iorder(id) constraint iorder_1_fk;
alter table iorder add constraint foreign key(status_id) references dictionary(id) constraint iorder_2_fk;
alter table iorder add constraint foreign key(cost_center_id) references dictionary(id) constraint iorder_3_fk;
alter table iorder add constraint foreign key(organization_id) references organization(id) constraint iorder_4_fk;
alter table iorder add constraint foreign key(ship_from_id) references dictionary(id) constraint iorder_5_fk;

alter table iorder_container add constraint foreign key(iorder_id) references iorder(id) constraint iorder_container_1_fk;
alter table iorder_container add constraint foreign key(container_id) references dictionary(id) constraint iorder_container_2_fk;
alter table iorder_container add constraint foreign key(type_of_sample_id) references dictionary(id) constraint iorder_container_3_fk;

alter table iorder_item add constraint foreign key(iorder_id) references iorder(id) constraint iorder_item_1_fk;
alter table iorder_item add constraint foreign key(inventory_item_id) references inventory_item(id) constraint iorder_item_2_fk;

alter table iorder_organization add constraint foreign key(iorder_id) references iorder(id) constraint iorder_organization_1_fk;
alter table iorder_organization add constraint foreign key(organization_id) references organization(id) constraint iorder_organization_2_fk;
alter table iorder_organization add constraint foreign key(type_id) references dictionary(id) constraint iorder_organization_3_fk;

alter table iorder_recurrence add constraint foreign key(iorder_id) references iorder(id) constraint iorder_recurrence_1_fk;
alter table iorder_recurrence add constraint foreign key(unit_id) references dictionary(id) constraint iorder_recurrence_2_fk;

alter table iorder_test add constraint foreign key(iorder_id) references iorder(id) constraint iorder_test_1_fk;
alter table iorder_test add constraint foreign key(test_id) references test(id) constraint iorder_test_2_fk;

alter table iorder_test_analyte add constraint foreign key(iorder_test_id) references iorder_test(id) constraint iorder_test_analyte_1_fk;
alter table iorder_test_analyte add constraint foreign key(analyte_id) references analyte(id) constraint iorder_test_analyte_2_fk;

alter table organization add constraint foreign key(parent_organization_id) references organization(id) constraint organization_1_fk;
alter table organization add constraint foreign key(address_id) references address(id) constraint organization_2_fk;

alter table organization_contact add constraint foreign key(organization_id) references organization(id) constraint organization_contact_1_fk;
alter table organization_contact add constraint foreign key(contact_type_id) references dictionary(id) constraint organization_contact_2_fk;
alter table organization_contact add constraint foreign key(address_id) references address(id) constraint organization_contact_3_fk;

alter table organization_parameter add constraint foreign key(organization_id) references organization(id) constraint organization_parameter_1_fk;
alter table organization_parameter add constraint foreign key(type_id) references dictionary(id) constraint organization_parameter_2_fk;

alter table panel_item add constraint foreign key(panel_id) references panel(id) constraint panel_item_1_fk;

alter table patient add constraint foreign key(address_id) references address(id) constraint patient_1_fk;
alter table patient add constraint foreign key(gender_id) references dictionary(id) constraint patient_2_fk;
alter table patient add constraint foreign key(race_id) references dictionary(id) constraint patient_3_fk;
alter table patient add constraint foreign key(ethnicity_id) references dictionary(id) constraint patient_4_fk;

alter table patient_relation add constraint foreign key(relation_id) references dictionary(id) constraint patient_relation_1_fk;
alter table patient_relation add constraint foreign key(patient_id) references patient(id) constraint patient_relation_2_fk;
alter table patient_relation add constraint foreign key(related_patient_id) references patient(id) constraint patient_relation_3_fk;

alter table project add constraint foreign key(scriptlet_id) references dictionary(id) constraint project_1_fk;

alter table project_parameter add constraint foreign key(project_id) references project(id) constraint project_parameter_1_fk;
alter table project_parameter add constraint foreign key(operation_id) references dictionary(id) constraint project_parameter_2_fk;

alter table provider add constraint foreign key(type_id) references dictionary(id) constraint provider_1_fk;

alter table provider_analyte add constraint foreign key(provider_id) references provider(id) constraint provider_analyte_1_fk;
alter table provider_analyte add constraint foreign key(analyte_id) references analyte(id) constraint provider_analyte_2_fk;

alter table provider_location add constraint foreign key(provider_id) references provider(id) constraint provider_location_1_fk;
alter table provider_location add constraint foreign key(address_id) references address(id) constraint provider_location_2_fk;

alter table pws_violation add constraint foreign key(sample_id) references sample(id) constraint pws_violation_2_fk;

alter table qaevent add constraint foreign key(test_id) references test(id) constraint qaevent_1_fk;
alter table qaevent add constraint foreign key(type_id) references dictionary(id) constraint qaevent_2_fk;

alter table qc add constraint foreign key(type_id) references dictionary(id) constraint qc_1_fk;
alter table qc add constraint foreign key(inventory_item_id) references inventory_item(id) constraint qc_2_fk;

alter table qc_analyte add constraint foreign key(qc_id) references qc(id) constraint qc_analyte_1_fk;
alter table qc_analyte add constraint foreign key(analyte_id) references analyte(id) constraint qc_analyte_2_fk;
alter table qc_analyte add constraint foreign key(type_id) references dictionary(id) constraint qc_analyte_3_fk;

alter table qc_lot add constraint foreign key(qc_id) references qc(id) constraint qc_lot_1_fk;
alter table qc_lot add constraint foreign key(location_id) references dictionary(id) constraint qc_lot_2_fk;
alter table qc_lot add constraint foreign key(prepared_unit_id) references dictionary(id) constraint qc_lot_3_fk;

alter table result add constraint foreign key(analysis_id) references analysis(id) constraint result_1_fk;
alter table result add constraint foreign key(test_analyte_id) references test_analyte(id) constraint result_2_fk;
alter table result add constraint foreign key(test_result_id) references test_result(id) constraint result_3_fk;
alter table result add constraint foreign key(analyte_id) references analyte(id) constraint result_4_fk;
alter table result add constraint foreign key(type_id) references dictionary(id) constraint result_5_fk;

alter table sample add constraint foreign key(status_id) references dictionary(id) constraint sample_1_fk;

alter table sample_animal add constraint foreign key(sample_id) references sample(id) constraint sample_animal_1_fk;
alter table sample_animal add constraint foreign key(animal_common_name_id) references dictionary(id) constraint sample_animal_2_fk;
alter table sample_animal add constraint foreign key(animal_scientific_name_id) references dictionary(id) constraint sample_animal_3_fk;
alter table sample_animal add constraint foreign key(location_address_id) references address(id) constraint sample_animal_4_fk;
alter table sample_animal add constraint foreign key(provider_id) references provider(id) constraint sample_animal_5_fk;

alter table sample_clinical add constraint foreign key(sample_id) references sample(id) constraint sample_clinical_1_fk;
alter table sample_clinical add constraint foreign key(patient_id) references patient(id) constraint sample_clinical_2_fk;
alter table sample_clinical add constraint foreign key(provider_id) references provider(id) constraint sample_clinical_3_fk;

alter table sample_environmental add constraint foreign key(sample_id) references sample(id) constraint sample_environmental_1_fk;
alter table sample_environmental add constraint foreign key(location_address_id) references address(id) constraint sample_environmental_2_fk;

alter table sample_item add constraint foreign key(sample_id) references sample(id) constraint sample_item_1_fk;
alter table sample_item add constraint foreign key(sample_item_id) references sample_item(id) constraint sample_item_2_fk;
alter table sample_item add constraint foreign key(type_of_sample_id) references dictionary(id) constraint sample_item_3_fk;
alter table sample_item add constraint foreign key(source_of_sample_id) references dictionary(id) constraint sample_item_4_fk;
alter table sample_item add constraint foreign key(container_id) references dictionary(id) constraint sample_item_5_fk;
alter table sample_item add constraint foreign key(unit_of_measure_id) references dictionary(id) constraint sample_item_6_fk;

alter table sample_neonatal add constraint foreign key(sample_id) references sample(id) constraint sample_neonatal_1_fk;
alter table sample_neonatal add constraint foreign key(patient_id) references patient(id) constraint sample_neonatal_2_fk;
alter table sample_neonatal add constraint foreign key(next_of_kin_id) references patient(id) constraint sample_neonatal_3_fk;
alter table sample_neonatal add constraint foreign key(next_of_kin_relation_id) references dictionary(id) constraint sample_neonatal_4_fk;
alter table sample_neonatal add constraint foreign key(provider_id) references provider(id) constraint sample_neonatal_5_fk;

alter table sample_organization add constraint foreign key(sample_id) references sample(id) constraint sample_organization_1_fk;
alter table sample_organization add constraint foreign key(organization_id) references organization(id) constraint sample_organization_2_fk;
alter table sample_organization add constraint foreign key(type_id) references dictionary(id) constraint sample_organization_3_fk;

alter table sample_project add constraint foreign key(sample_id) references sample(id) constraint sample_project_1_fk;
alter table sample_project add constraint foreign key(project_id) references project(id) constraint sample_project_2_fk;

alter table sample_pt add constraint foreign key(sample_id) references sample(id) constraint sample_pt_1_fk;
alter table sample_pt add constraint foreign key(pt_provider_id) references dictionary(id) constraint sample_pt_2_fk;

alter table sample_qaevent add constraint foreign key(sample_id) references sample(id) constraint sample_qaevent_1_fk;
alter table sample_qaevent add constraint foreign key(qaevent_id) references qaevent(id) constraint sample_qaevent_2_fk;
alter table sample_qaevent add constraint foreign key(type_id) references dictionary(id) constraint sample_qaevent_3_fk;

alter table sample_sdwis add constraint foreign key(sample_id) references sample(id) constraint sample_sdwis_1_fk;
alter table sample_sdwis add constraint foreign key(pws_id) references pws(id) constraint sample_sdwis_2_fk;
alter table sample_sdwis add constraint foreign key(sample_type_id) references dictionary(id) constraint sample_sdwis_3_fk;
alter table sample_sdwis add constraint foreign key(sample_category_id) references dictionary(id) constraint sample_sdwis_4_fk;

alter table section add constraint foreign key(parent_section_id) references section(id) constraint section_1_fk;
alter table section add constraint foreign key(organization_id) references organization(id) constraint section_2_fk;

alter table section_parameter add constraint foreign key(section_id) references section(id) constraint section_parameter_1_fk;
alter table section_parameter add constraint foreign key(type_id) references dictionary(id) constraint section_parameter_2_fk;

alter table shipping add constraint foreign key(status_id) references dictionary(id) constraint shipping_1_fk;
alter table shipping add constraint foreign key(shipped_from_id) references dictionary(id) constraint shipping_2_fk;
alter table shipping add constraint foreign key(shipped_to_id) references organization(id) constraint shipping_3_fk;
alter table shipping add constraint foreign key(shipped_method_id) references dictionary(id) constraint shipping_4_fk;

alter table shipping_item add constraint foreign key(shipping_id) references shipping(id) constraint shipping_item_1_fk;

alter table shipping_tracking add constraint foreign key(shipping_id) references shipping(id) constraint shipping_tracking_1_fk;

alter table standard_note add constraint foreign key(type_id) references dictionary(id) constraint standard_note_1_fk;

alter table storage add constraint foreign key(storage_location_id) references storage_location(id) constraint storage_1_fk;

alter table storage_location add constraint foreign key(parent_storage_location_id) references storage_location(id) constraint storage_location_1_fk;
alter table storage_location add constraint foreign key(storage_unit_id) references storage_unit(id) constraint storage_location_2_fk;

alter table storage_unit add constraint foreign key(category_id) references dictionary(id) constraint storage_unit_1_fk;

alter table test add constraint foreign key(method_id) references method(id) constraint test_1_fk;
alter table test add constraint foreign key(label_id) references label(id) constraint test_2_fk;
alter table test add constraint foreign key(test_trailer_id) references test_trailer(id) constraint test_3_fk;
alter table test add constraint foreign key(scriptlet_id) references dictionary(id) constraint test_4_fk;
alter table test add constraint foreign key(test_format_id) references dictionary(id) constraint test_5_fk;
alter table test add constraint foreign key(revision_method_id) references dictionary(id) constraint test_6_fk;
alter table test add constraint foreign key(reporting_method_id) references dictionary(id) constraint test_7_fk;
alter table test add constraint foreign key(sorting_method_id) references dictionary(id) constraint test_8_fk;

alter table test_analyte add constraint foreign key(test_id) references test(id) constraint test_analyte_1_fk;
alter table test_analyte add constraint foreign key(analyte_id) references analyte(id) constraint test_analyte_2_fk;
alter table test_analyte add constraint foreign key(type_id) references dictionary(id) constraint test_analyte_3_fk;
alter table test_analyte add constraint foreign key(scriptlet_id) references dictionary(id) constraint test_analyte_4_fk;

alter table test_prep add constraint foreign key(test_id) references test(id) constraint test_prep_1_fk;
alter table test_prep add constraint foreign key(prep_test_id) references test(id) constraint test_prep_2_fk;

alter table test_reflex add constraint foreign key(test_id) references test(id) constraint test_reflex_1_fk;
alter table test_reflex add constraint foreign key(test_analyte_id) references test_analyte(id) constraint test_reflex_2_fk;
alter table test_reflex add constraint foreign key(test_result_id) references test_result(id) constraint test_reflex_3_fk;
alter table test_reflex add constraint foreign key(flags_id) references dictionary(id) constraint test_reflex_4_fk;
alter table test_reflex add constraint foreign key(add_test_id) references test(id) constraint test_reflex_5_fk;

alter table test_result add constraint foreign key(test_id) references test(id) constraint test_result_1_fk;
alter table test_result add constraint foreign key(unit_of_measure_id) references dictionary(id) constraint test_result_2_fk;
alter table test_result add constraint foreign key(type_id) references dictionary(id) constraint test_result_3_fk;
alter table test_result add constraint foreign key(rounding_method_id) references dictionary(id) constraint test_result_4_fk;
alter table test_result add constraint foreign key(flags_id) references dictionary(id) constraint test_result_5_fk;

alter table test_section add constraint foreign key(test_id) references test(id) constraint test_section_1_fk;
alter table test_section add constraint foreign key(section_id) references section(id) constraint test_section_2_fk;
alter table test_section add constraint foreign key(flag_id) references dictionary(id) constraint test_section_3_fk;

alter table test_type_of_sample add constraint foreign key(test_id) references test(id) constraint test_type_of_sample_1_fk;
alter table test_type_of_sample add constraint foreign key(type_of_sample_id) references dictionary(id) constraint test_type_of_sample_2_fk;
alter table test_type_of_sample add constraint foreign key(unit_of_measure_id) references dictionary(id) constraint test_type_of_sample_3_fk;

alter table test_worksheet add constraint foreign key(test_id) references test(id) constraint test_worksheet_1_fk;
alter table test_worksheet add constraint foreign key(format_id) references dictionary(id) constraint test_worksheet_2_fk;
alter table test_worksheet add constraint foreign key(scriptlet_id) references dictionary(id) constraint test_worksheet_3_fk;

alter table test_worksheet_analyte add constraint foreign key(test_id) references test(id) constraint test_worksheet_analyte_1_fk;
alter table test_worksheet_analyte add constraint foreign key(test_analyte_id) references test_analyte(id) constraint test_worksheet_analyte_2_fk;
alter table test_worksheet_analyte add constraint foreign key(flag_id) references dictionary(id) constraint test_worksheet_analyte_3_fk;

alter table test_worksheet_item add constraint foreign key(test_worksheet_id) references test_worksheet(id) constraint test_worksheet_item_1_fk;
alter table test_worksheet_item add constraint foreign key(type_id) references dictionary(id) constraint test_worksheet_item_2_fk;

alter table worksheet add constraint foreign key(status_id) references dictionary(id) constraint worksheet_1_fk;
alter table worksheet add constraint foreign key(format_id) references dictionary(id) constraint worksheet_2_fk;
alter table worksheet add constraint foreign key(related_worksheet_id) references worksheet(id) constraint worksheet_3_fk;
alter table worksheet add constraint foreign key(instrument_id) references instrument(id) constraint worksheet_4_fk;

alter table worksheet_analysis add constraint foreign key(worksheet_item_id) references worksheet_item(id) constraint worksheet_analysis_1_fk;
alter table worksheet_analysis add constraint foreign key(analysis_id) references analysis(id) constraint worksheet_analysis_2_fk;
alter table worksheet_analysis add constraint foreign key(qc_lot_id) references qc_lot(id) constraint worksheet_analysis_3_fk;
alter table worksheet_analysis add constraint foreign key(worksheet_analysis_id) references worksheet_analysis(id) constraint worksheet_analysis_4_fk;
alter table worksheet_analysis add constraint foreign key(change_flags_id) references dictionary(id) constraint worksheet_analysis_5_fk;

alter table worksheet_item add constraint foreign key(worksheet_id) references worksheet(id) constraint worksheet_item_1_fk;

alter table worksheet_qc_result add constraint foreign key(worksheet_analysis_id) references worksheet_analysis(id) constraint worksheet_qc_result_1_fk;
alter table worksheet_qc_result add constraint foreign key(qc_analyte_id) references qc_analyte(id) constraint worksheet_qc_result_2_fk;

alter table worksheet_reagent add constraint foreign key(worksheet_id) references worksheet(id) constraint worksheet_reagent_1_fk;
alter table worksheet_reagent add constraint foreign key(qc_lot_id) references qc_lot(id) constraint worksheet_reagent_2_fk;

alter table worksheet_result add constraint foreign key(worksheet_analysis_id) references worksheet_analysis(id) constraint worksheet_result_1_fk;
alter table worksheet_result add constraint foreign key(test_analyte_id) references test_analyte(id) constraint worksheet_result_2_fk;
alter table worksheet_result add constraint foreign key(analyte_id) references analyte(id) constraint worksheet_result_3_fk;