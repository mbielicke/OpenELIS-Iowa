-------------------------------------------------------------------------------
--
-- indicies
--
-------------------------------------------------------------------------------

create unique index address_1_idx on address(id)

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
create        index inventory_x_put_2_idx on inventory_x_put(inventory_location_id);
create        index inventory_x_put_3_idx on inventory_x_put(inventory_receipt_id);

create unique index inventory_x_use_1_idx on inventory_x_use(id);
create        index inventory_x_use_2_idx on inventory_x_use(inventory_location_id);
create        index inventory_x_use_3_idx on inventory_x_use(iorder_item_id);

create unique index label_1_idx on label(id);
create unique index label_2_idx on label(name);

create unique index lock_1_idx on lock(reference_table_id, reference_id);

create unique index method_1_idx on method(id);
create        index method_2_idx on method(name);

create unique index note_1_idx on note(id);
create        index note_2_idx on note(reference_id, reference_table_id);

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

create unique index sample_private_well_1_idx on sample_private_well(id);
create unique index sample_private_well_2_idx on sample_private_well(sample_id);
create        index sample_private_well_3_idx on sample_private_well(organization_id);

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
create        index test_type_of_sample_2_id_idx on test_type_of_sample(test_id);

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

alter table address add primary key(id);
alter table analysis add primary key(id);
alter table analysis_qaevent add primary key(id);
alter table analysis_user add primary key(id);
alter table analyte add primary key(id);
alter table analyte_parameter add primary key(id);
alter table attachment add primary key(id);
alter table attachment_item add primary key(id);
alter table aux_data add primary key(id);
alter table aux_field add primary key(id);
alter table aux_field_group add primary key(id);
alter table aux_field_value add primary key(id);
alter table category add primary key(id);
alter table cron add primary key(id);
alter table dictionary add primary key(id);
alter table eorder add primary key(id);
alter table eorder_body add primary key(id);
alter table eorder_link add primary key(id);
alter table event_log add primary key(id);
alter table exchange_criteria add primary key(id);
alter table exchange_external_term add primary key(id);
alter table exchange_local_term add primary key(id);
alter table exchange_profile add primary key(id);
alter table history add primary key(id);
alter table instrument add primary key(id);
alter table instrument_log add primary key(id);
alter table inventory_adjustment add primary key(id);
alter table inventory_component add primary key(id);
alter table inventory_item add primary key(id);
alter table inventory_location add primary key(id);
alter table inventory_receipt add primary key(id);
alter table inventory_receipt_iorder_item add primary key(id);
alter table inventory_x_adjust add primary key(id);
alter table inventory_x_put add primary key(id);
alter table inventory_x_use add primary key(id);
alter table label add primary key(id);
alter table lock add primary key(reference_id, reference_table_id);
alter table method add primary key(id);
alter table note add primary key(id);
alter table iorder add primary key(id);
alter table iorder_container add primary key(id);
alter table iorder_item add primary key(id);
alter table iorder_organization add primary key(id);
alter table iorder_recurrence add primary key(id);
alter table iorder_test add primary key(id);
alter table iorder_test_analyte add primary key(id);
alter table organization add primary key(id);
alter table organization_contact add primary key(id);
alter table organization_parameter add primary key(id);
alter table panel add primary key(id);
alter table panel_item add primary key(id);
alter table patient add primary key(id);
alter table patient_relation add primary key(id);
alter table project add primary key(id);
alter table project_parameter add primary key(id);
alter table provider add primary key(id);
alter table provider_location add primary key(id);
alter table pws add primary key(id);
alter table pws_address add primary key(tinwslec_is_number, tinlgent_is_number);
alter table pws_facility add primary key(tinwsf_is_number, tsasmppt_is_number);
alter table pws_monitor add primary key(tiamrtask_is_number);
alter table pws_violation add primary key(id);
alter table qaevent add primary key(id);
alter table qc add primary key(id);
alter table qc_analyte add primary key(id);
alter table qc_lot add primary key(id);
alter table result add primary key(id);
alter table sample add primary key(id);
alter table sample_animal add primary key(id);
alter table sample_clinical add primary key(id);
alter table sample_environmental add primary key(id);
alter table sample_item add primary key(id);
alter table sample_neonatal add primary key(id);
alter table sample_organization add primary key(id);
alter table sample_private_well add primary key(id);
alter table sample_project add primary key(id);
alter table sample_pt add primary key(id);
alter table sample_qaevent add primary key(id);
alter table sample_sdwis add primary key(id);
alter table scriptlet add primary key(id);
alter table section add primary key(id);
alter table section_parameter add primary key(id);
alter table shipping add primary key(id);
alter table shipping_item add primary key(id);
alter table shipping_tracking add primary key(id);
alter table standard_note add primary key(id);
alter table storage add primary key(id);
alter table storage_location add primary key(id);
alter table storage_unit add primary key(id);
alter table system_variable add primary key(id);
alter table test add primary key(id);
alter table test_analyte add primary key(id);
alter table test_prep add primary key(id);
alter table test_reflex add primary key(id);
alter table test_result add primary key(id);
alter table test_section add primary key(id);
alter table test_trailer add primary key(id);
alter table test_type_of_sample add primary key(id);
alter table test_worksheet add primary key(id);
alter table test_worksheet_analyte add primary key(id);
alter table test_worksheet_item add primary key(id);
alter table worksheet add primary key(id);
alter table worksheet_analysis add primary key(id);
alter table worksheet_item add primary key(id);
alter table worksheet_qc_result add primary key(id);
alter table worksheet_reagent add primary key(id);
alter table worksheet_result add primary key(id);

alter table analysis add foreign key(sample_item_id) references sample_item(id);
alter table analysis add foreign key(test_id) references test(id);
alter table analysis add foreign key(section_id) references section(id);
alter table analysis add foreign key(panel_id) references panel(id);
alter table analysis add foreign key(pre_analysis_id) references analysis(id);
alter table analysis add foreign key(parent_analysis_id) references analysis(id);
alter table analysis add foreign key(parent_result_id) references result(id);
alter table analysis add foreign key(type_id) references dictionary(id);
alter table analysis add foreign key(unit_of_measure_id) references dictionary(id);
alter table analysis add foreign key(status_id) references dictionary(id);

alter table analysis_qaevent add foreign key(analysis_id) references analysis(id);
alter table analysis_qaevent add foreign key(qaevent_id) references qaevent(id);
alter table analysis_qaevent add foreign key(type_id) references dictionary(id);

alter table analysis_report_flags add foreign key(analysis_id) references analysis(id);

alter table analysis_user add foreign key(analysis_id) references analysis(id);
alter table analysis_user add foreign key(action_id) references dictionary(id);

alter table analyte add foreign key(parent_analyte_id) references analyte(id);

alter table analyte_parameter add foreign key(analyte_id) references analyte(id);
alter table analyte_parameter add foreign key(type_of_sample_id) references dictionary(id);
alter table analyte_parameter add foreign key(unit_of_measure_id) references dictionary(id);

alter table attachment add foreign key(type_id) references dictionary(id);
alter table attachment add foreign key(section_id) references section(id);

alter table attachment_item add foreign key(attachment_id) references attachment(id);

alter table aux_data add foreign key(aux_field_id) references aux_field(id);
alter table aux_data add foreign key(type_id) references dictionary(id);

alter table aux_field add foreign key(aux_field_group_id) references aux_field_group(id);
alter table aux_field add foreign key(analyte_id) references analyte(id);
alter table aux_field add foreign key(method_id) references method(id);
alter table aux_field add foreign key(unit_of_measure_id) references dictionary(id);
alter table aux_field add foreign key(scriptlet_id) references dictionary(id);

alter table aux_field_value add foreign key(aux_field_id) references aux_field(id);
alter table aux_field_value add foreign key(type_id) references dictionary(id);

alter table category add foreign key(section_id) references section(id);

alter table dictionary add foreign key(category_id) references category(id);
alter table dictionary add foreign key(related_entry_id) references dictionary(id);

alter table eorder_body add foreign key(eorder_id) references eorder(id);

alter table eorder_link add foreign key(eorder_id) references eorder(id);

alter table event_log add foreign key(type_id) references dictionary(id);
alter table event_log add foreign key(level_id) references dictionary(id);

alter table exchange_criteria add foreign key(environment_id) references dictionary(id);

alter table exchange_external_term add foreign key(exchange_local_term_id) references exchange_local_term(id);
alter table exchange_external_term add foreign key(profile_id) references dictionary(id);

alter table exchange_profile add foreign key(exchange_criteria_id) references exchange_criteria(id);
alter table exchange_profile add foreign key(profile_id) references dictionary(id);

alter table history add foreign key(activity_id) references dictionary(id);

alter table instrument add foreign key(type_id) references dictionary(id);
alter table instrument add foreign key(scriptlet_id) references dictionary(id);

alter table instrument_log add foreign key(instrument_id) references instrument(id);
alter table instrument_log add foreign key(type_id) references dictionary(id);
alter table instrument_log add foreign key(worksheet_id) references worksheet(id);

alter table inventory_component add foreign key(inventory_item_id) references inventory_item(id);
alter table inventory_component add foreign key(component_id) references inventory_item(id);

alter table inventory_item add foreign key(category_id) references dictionary(id);
alter table inventory_item add foreign key(store_id) references dictionary(id);
alter table inventory_item add foreign key(dispensed_units_id) references dictionary(id);
alter table inventory_item add foreign key(parent_inventory_item_id) references inventory_item(id);

alter table inventory_location add foreign key(inventory_item_id) references inventory_item(id);
alter table inventory_location add foreign key(storage_location_id) references storage_location(id);

alter table inventory_receipt add foreign key(inventory_item_id) references inventory_item(id);
alter table inventory_receipt add foreign key(iorder_item_id) references iorder_item(id);
alter table inventory_receipt add foreign key(organization_id) references organization(id);

alter table inventory_receipt_iorder_item add foreign key(inventory_receipt_id) references inventory_receipt(id);
alter table inventory_receipt_iorder_item add foreign key(iorder_item_id) references iorder_item(id);

alter table inventory_x_adjust add foreign key(inventory_adjustment_id) references inventory_adjustment(id);
alter table inventory_x_adjust add foreign key(inventory_location_id) references inventory_location(id);

alter table inventory_x_put add foreign key(inventory_receipt_id) references inventory_receipt(id);
alter table inventory_x_put add foreign key(inventory_location_id) references inventory_location(id);

alter table inventory_x_use add foreign key(inventory_location_id) references inventory_location(id);
alter table inventory_x_use add foreign key(iorder_item_id) references iorder_item(id);

alter table label add foreign key(printer_type_id) references dictionary(id);
alter table label add foreign key(scriptlet_id) references dictionary(id);

alter table iorder add foreign key(parent_iorder_id) references iorder(id);
alter table iorder add foreign key(status_id) references dictionary(id);
alter table iorder add foreign key(cost_center_id) references dictionary(id);
alter table iorder add foreign key(organization_id) references organization(id);
alter table iorder add foreign key(ship_from_id) references dictionary(id);

alter table iorder_container add foreign key(iorder_id) references iorder(id);
alter table iorder_container add foreign key(container_id) references dictionary(id);
alter table iorder_container add foreign key(type_of_sample_id) references dictionary(id);

alter table iorder_item add foreign key(iorder_id) references iorder(id);
alter table iorder_item add foreign key(inventory_item_id) references inventory_item(id);

alter table iorder_organization add foreign key(iorder_id) references iorder(id);
alter table iorder_organization add foreign key(organization_id) references organization(id);
alter table iorder_organization add foreign key(type_id) references dictionary(id);

alter table iorder_recurrence add foreign key(iorder_id) references iorder(id);
alter table iorder_recurrence add foreign key(unit_id) references dictionary(id);

alter table iorder_test add foreign key(iorder_id) references iorder(id);
alter table iorder_test add foreign key(test_id) references test(id);

alter table iorder_test_analyte add foreign key(iorder_test_id) references iorder_test(id);
alter table iorder_test_analyte add foreign key(analyte_id) references analyte(id);

alter table organization add foreign key(parent_organization_id) references organization(id);
alter table organization add foreign key(address_id) references address(id);

alter table organization_contact add foreign key(organization_id) references organization(id);
alter table organization_contact add foreign key(contact_type_id) references dictionary(id);
alter table organization_contact add foreign key(address_id) references address(id);

alter table organization_parameter add foreign key(organization_id) references organization(id);
alter table organization_parameter add foreign key(type_id) references dictionary(id);

alter table panel_item add foreign key(panel_id) references panel(id);

alter table patient add foreign key(address_id) references address(id);
alter table patient add foreign key(gender_id) references dictionary(id);
alter table patient add foreign key(race_id) references dictionary(id);
alter table patient add foreign key(ethnicity_id) references dictionary(id);

alter table patient_relation add foreign key(relation_id) references dictionary(id);
alter table patient_relation add foreign key(patient_id) references patient(id);
alter table patient_relation add foreign key(related_patient_id) references patient(id);

alter table project add foreign key(scriptlet_id) references dictionary(id);

alter table project_parameter add foreign key(project_id) references project(id);
alter table project_parameter add foreign key(operation_id) references dictionary(id);

alter table provider add foreign key(type_id) references dictionary(id);

alter table provider_location add foreign key(provider_id) references provider(id);
alter table provider_location add foreign key(address_id) references address(id);

alter table pws_violation add foreign key(sample_id) references sample(id);

alter table qaevent add foreign key(test_id) references test(id);
alter table qaevent add foreign key(type_id) references dictionary(id);

alter table qc add foreign key(type_id) references dictionary(id);
alter table qc add foreign key(inventory_item_id) references inventory_item(id);

alter table qc_analyte add foreign key(qc_id) references qc(id);
alter table qc_analyte add foreign key(analyte_id) references analyte(id);
alter table qc_analyte add foreign key(type_id) references dictionary(id);

alter table qc_lot add foreign key(qc_id) references qc(id);
alter table qc_lot add foreign key(location_id) references dictionary(id);
alter table qc_lot add foreign key(prepared_unit_id) references dictionary(id);

alter table result add foreign key(analysis_id) references analysis(id);
alter table result add foreign key(test_analyte_id) references test_analyte(id);
alter table result add foreign key(test_result_id) references test_result(id);
alter table result add foreign key(analyte_id) references analyte(id);
alter table result add foreign key(type_id) references dictionary(id);

alter table sample add foreign key(status_id) references dictionary(id);

alter table sample_animal add foreign key(sample_id) references sample(id);
alter table sample_animal add foreign key(animal_common_name_id) references dictionary(id);
alter table sample_animal add foreign key(animal_scientific_name_id) references dictionary(id);
alter table sample_animal add foreign key(address_id) references address(id);

alter table sample_clinical add foreign key(sample_id) references sample(id);
alter table sample_clinical add foreign key(patient_id) references patient(id);
alter table sample_clinical add foreign key(provider_id) references provider(id);

alter table sample_environmental add foreign key(sample_id) references sample(id);
alter table sample_environmental add foreign key(location_address_id) references address(id);

alter table sample_item add foreign key(sample_id) references sample(id);
alter table sample_item add foreign key(sample_item_id) references sample_item(id);
alter table sample_item add foreign key(type_of_sample_id) references dictionary(id);
alter table sample_item add foreign key(source_of_sample_id) references dictionary(id);
alter table sample_item add foreign key(container_id) references dictionary(id);
alter table sample_item add foreign key(unit_of_measure_id) references dictionary(id);

alter table sample_neonatal add foreign key(sample_id) references sample(id);
alter table sample_neonatal add foreign key(patient_id) references patient(id);
alter table sample_neonatal add foreign key(next_of_kin_id) references patient(id);
alter table sample_neonatal add foreign key(next_of_kin_relation_id) references dictionary(id);
alter table sample_neonatal add foreign key(provider_id) references provider(id);

alter table sample_organization add foreign key(sample_id) references sample(id);
alter table sample_organization add foreign key(organization_id) references organization(id);
alter table sample_organization add foreign key(type_id) references dictionary(id);

alter table sample_private_well add foreign key(sample_id) references sample(id);
alter table sample_private_well add foreign key(report_to_address_id) references address(id);
alter table sample_private_well add foreign key(location_address_id) references address(id);

alter table sample_project add foreign key(sample_id) references sample(id);
alter table sample_project add foreign key(project_id) references project(id);

alter table sample_pt add foreign key(sample_id) references sample(id);
alter table sample_pt add foreign key(pt_provider_id) references dictionary(id);

alter table sample_qaevent add foreign key(sample_id) references sample(id);
alter table sample_qaevent add foreign key(qaevent_id) references qaevent(id);
alter table sample_qaevent add foreign key(type_id) references dictionary(id);

alter table sample_sdwis add foreign key(sample_id) references sample(id);
alter table sample_sdwis add foreign key(pws_id) references pws(id);
alter table sample_sdwis add foreign key(sample_type_id) references dictionary(id);
alter table sample_sdwis add foreign key(sample_category_id) references dictionary(id);

alter table section add foreign key(parent_section_id) references section(id);
alter table section add foreign key(organization_id) references organization(id);

alter table section_parameter add foreign key(section_id) references section(id);
alter table section_parameter add foreign key(type_id) references dictionary(id);

alter table shipping add foreign key(status_id) references dictionary(id);
alter table shipping add foreign key(shipped_from_id) references dictionary(id);
alter table shipping add foreign key(shipped_to_id) references organization(id);
alter table shipping add foreign key(shipped_method_id) references dictionary(id);

alter table shipping_item add foreign key(shipping_id) references shipping(id);

alter table shipping_tracking add foreign key(shipping_id) references shipping(id);

alter table standard_note add foreign key(type_id) references dictionary(id);

alter table storage add foreign key(storage_location_id) references storage_location(id);

alter table storage_location add foreign key(parent_storage_location_id) references storage_location(id);
alter table storage_location add foreign key(storage_unit_id) references storage_unit(id);

alter table storage_unit add foreign key(category_id) references dictionary(id);

alter table test add foreign key(method_id) references method(id);
alter table test add foreign key(label_id) references label(id);
alter table test add foreign key(test_trailer_id) references test_trailer(id);
alter table test add foreign key(scriptlet_id) references dictionary(id);
alter table test add foreign key(test_format_id) references dictionary(id);
alter table test add foreign key(revision_method_id) references dictionary(id);
alter table test add foreign key(reporting_method_id) references dictionary(id);
alter table test add foreign key(sorting_method_id) references dictionary(id);

alter table test_analyte add foreign key(test_id) references test(id);
alter table test_analyte add foreign key(analyte_id) references analyte(id);
alter table test_analyte add foreign key(type_id) references dictionary(id);
alter table test_analyte add foreign key(scriptlet_id) references dictionary(id);

alter table test_prep add foreign key(test_id) references test(id);
alter table test_prep add foreign key(prep_test_id) references test(id);

alter table test_reflex add foreign key(test_id) references test(id);
alter table test_reflex add foreign key(test_analyte_id) references test_analyte(id);
alter table test_reflex add foreign key(test_result_id) references test_result(id);
alter table test_reflex add foreign key(flags_id) references dictionary(id);
alter table test_reflex add foreign key(add_test_id) references test(id);

alter table test_result add foreign key(test_id) references test(id);
alter table test_result add foreign key(unit_of_measure_id) references dictionary(id);
alter table test_result add foreign key(type_id) references dictionary(id);
alter table test_result add foreign key(rounding_method_id) references dictionary(id);
alter table test_result add foreign key(flags_id) references dictionary(id);

alter table test_section add foreign key(test_id) references test(id);
alter table test_section add foreign key(section_id) references section(id);
alter table test_section add foreign key(flag_id) references dictionary(id);

alter table test_type_of_sample add foreign key(test_id) references test(id);
alter table test_type_of_sample add foreign key(type_of_sample_id) references dictionary(id);
alter table test_type_of_sample add foreign key(unit_of_measure_id) references dictionary(id);

alter table test_worksheet add foreign key(test_id) references test(id);
alter table test_worksheet add foreign key(format_id) references dictionary(id);
alter table test_worksheet add foreign key(scriptlet_id) references dictionary(id);

alter table test_worksheet_analyte add foreign key(test_id) references test(id);
alter table test_worksheet_analyte add foreign key(test_analyte_id) references test_analyte(id);
alter table test_worksheet_analyte add foreign key(flag_id) references dictionary(id);

alter table test_worksheet_item add foreign key(test_worksheet_id) references test_worksheet(id);
alter table test_worksheet_item add foreign key(type_id) references dictionary(id);

alter table worksheet add foreign key(status_id) references dictionary(id);
alter table worksheet add foreign key(format_id) references dictionary(id);
alter table worksheet add foreign key(related_worksheet_id) references worksheet(id);
alter table worksheet add foreign key(instrument_id) references instrument(id);

alter table worksheet_analysis add foreign key(worksheet_item_id) references worksheet_item(id);
alter table worksheet_analysis add foreign key(analysis_id) references analysis(id);
alter table worksheet_analysis add foreign key(qc_lot_id) references qc_lot(id);
alter table worksheet_analysis add foreign key(worksheet_analysis_id) references worksheet_analysis(id);
alter table worksheet_analysis add foreign key(change_flags_id) references dictionary(id);

alter table worksheet_item add foreign key(worksheet_id) references worksheet(id);

alter table worksheet_qc_result add foreign key(worksheet_analysis_id) references worksheet_analysis(id);
alter table worksheet_qc_result add foreign key(qc_analyte_id) references qc_analyte(id);

alter table worksheet_reagent add foreign key(worksheet_id) references worksheet(id);
alter table worksheet_reagent add foreign key(qc_lot_id) references qc_lot(id);

alter table worksheet_result add foreign key(worksheet_analysis_id) references worksheet_analysis(id);
alter table worksheet_result add foreign key(test_analyte_id) references test_analyte(id);
alter table worksheet_result add foreign key(analyte_id) references analyte(id);
