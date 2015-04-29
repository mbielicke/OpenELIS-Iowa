create unique index "dba".ix161_1 on "dba".address (id) using btree;

create        index "dba".analysis_2 on "dba".analysis (sample_item_id) using btree;
create        index "dba".analysis_4 on "dba".analysis (test_id) using btree;

create        index "dba".analysis_qaevent_2 on "dba".analysis_qaevent (analysis_id) using btree;

create unique index "dba".ix218_1 on "dba".analysis_report_flags (analysis_id) using btree;

create        index "dba".analysis_user_2 on "dba".analysis_user (analysis_id) using btree;

create unique index "dba".analyte_2 on "dba".analyte (name) using btree;

create unique index "dba".ix216_1 on "dba".analyte_parameter (id) using btree;
create        index "dba".ix216_2 on "dba".analyte_parameter (reference_id) using btree;
create        index "dba".ix216_3 on "dba".analyte_parameter (reference_table_id) using btree;
create        index "dba".ix216_4 on "dba".analyte_parameter (analyte_id) using btree;
create        index "dba".ix216_5 on "dba".analyte_parameter (type_of_sample_id) using btree;

create        index "dba".attach_item_2 on "dba".attachment_item (reference_id) using btree;
create        index "dba".attach_item_3 on "dba".attachment_item (reference_table_id) using btree;

create        index "dba".aux_data_4 on "dba".aux_data (reference_id) using btree;
create        index "dba".aux_data_5 on "dba".aux_data (reference_table_id) using btree;

create        index "dba".ix159_2 on "dba".aux_field (aux_field_group_id) using btree;

create unique index "dba".ix180_1 on "dba".aux_field_group (id) using btree;
create        index "dba".ix180_2 on "dba".aux_field_group (name) using btree;

create        index "dba".aux_field_val_2 on "dba".aux_field_value (aux_field_id) using btree;

create unique index "dba".case_1_idx on "dba".case (id) using btree;
create unique index "dba".case_2_idx on "dba".case (created_date) using btree;

create unique index "dba".case_analysis_1_idx on "dba".case_analysis (id) using btree;
create        index "dba".case_analysis_2_idx on "dba".case_analysis (case_id) using btree;
create        index "dba".case_analysis_3_idx on "dba".case_analysis (test_id) using btree;
create        index "dba".case_analysis_4_idx on "dba".case_analysis (completed_date) using btree;

create unique index "dba".case_contact_1_idx on "dba".case_contact (id) using btree;
create        index "dba".case_contact_2_idx on "dba".case_contact (source_reference_id, source_reference) using btree;

create unique index "dba".case_contact_location_1_idx on "dba" .case_contact_location (id) using btree;
create        index "dba".case_contact_location_2_idx on "dba".case_contact_location (case_contact_id) using btree;

create unique index "dba".case_patient_1_idx on "dba".case_patient (id) using btree;
create        index "dba".case_patient_2_idx on "dba".case_patient (last_name) using btree;
create        index "dba".case_patient_3_idx on "dba".case_patient (national_id) using btree;

create unique index "dba".case_provider_1_idx on "dba".case_provider (id) using btree;
create        index "dba".case_provider_2_idx on "dba".case_provider (case_id) using btree;

create unique index "dba".case_result_1_idx on "dba".case_result (id) using btree;
create        index "dba".case_result_2_idx on "dba".case_result (case_analysis_id) using btree;

create unique index "dba".case_tag_1_idx on "dba".case_tag (id) using btree;
create        index "dba".case_tag_2_idx on "dba".case_tag (case_id) using btree;

create unique index "dba".case_user_1_idx on "dba".case_user (id) using btree;
create        index "dba".case_user_2_idx on "dba".case_user (case_id) using btree;

create unique index "dba".ix149_1 on "dba".category (id) using btree;
create unique index "dba".ix149_2 on "dba".category (system_name) using btree;

create unique index "dba".ix224_1 on "dba".cron (id) using btree;
create        index "dba".ix224_2 on "dba".cron (cron_tab) using btree;

create        index "dba".dictionary_2 on "dba".dictionary (category_id) using btree;

create        index "dba".eorder_body_2 on "dba".eorder_body (eorder_id) using btree;

create        index "dba".eorder_link_2 on "dba".eorder_link (eorder_id) using btree;

create unique index "dba".ix238_1 on "dba".event_log (id) using btree;
create        index "dba".ix238_2 on "dba".event_log (type_id) using btree;

create unique index "dba".ix236_1 on "dba".exchange_criteria (id) using btree;
create unique index "dba".ix236_2 on "dba".exchange_criteria (name) using btree;

create unique index "dba".ix235_1 on "dba".exchange_external_term (id) using btree;
create        index "dba".ix235_2 on "dba".exchange_external_term (exchange_local_term_id) using btree;

create unique index "dba".ix233_1 on "dba".exchange_local_term (id) using btree;
create        index "dba".ix233_2 on "dba".exchange_local_term (reference_table_id) using btree;
create        index "dba".ix233_3 on "dba".exchange_local_term (reference_id) using btree;

create unique index "dba".ix237_1 on "dba".exchange_profile (id) using btree;
create        index "dba".ix237_2 on "dba".exchange_profile (exchange_criteria_id) using btree;

create        index "dba".history_2 on "dba".history (reference_id) using btree;
create        index "dba".history_3 on "dba".history (reference_table_id) using btree;

create        index "dba".instrument_2 on "dba".instrument (name) using btree;

create        index "dba".instrument_log_2 on "dba".instrument_log (instrument_id) using btree;

create unique index "dba".ix171_1 on "dba".inventory_adjustment (id) using btree;

create        index "dba".inventcomp_2 on "dba".inventory_component (inventory_item_id) using btree;

create        index "dba".invent_item_2 on "dba".inventory_item (name) using btree;
create        index "dba".ix133_23 on "dba".inventory_item (parent_inventory_item_id) using btree;
create        index "dba".ix135_4 on "dba".inventory_item (category_id) using btree;
create        index "dba".ix135_5 on "dba".inventory_item (store_id) using btree;

create        index "dba".invent_loc_2 on "dba".inventory_location (inventory_item_id) using btree;

create        index "dba".invent_recpt_2 on "dba".inventory_receipt (inventory_item_id) using btree;
create        index "dba".ix136_3 on "dba".inventory_receipt (order_item_id) using btree;
create        index "dba".ix138_9 on "dba".inventory_receipt (upc) using btree;

create unique index "dba".invrecorditm_1 on "dba".inventory_receipt_order_item (id) using btree;
create        index "dba".invrecorditm_2 on "dba".inventory_receipt_order_item (inventory_receipt_id) using btree;
create        index "dba".invrecorditm_3 on "dba".inventory_receipt_order_item (order_item_id) using btree;

create unique index "dba".ix170_1 on "dba".inventory_x_adjust (id) using btree;
create        index "dba".ix170_2 on "dba".inventory_x_adjust (inventory_adjustment_id) using btree;
create        index "dba".ix170_3 on "dba".inventory_x_adjust (inventory_location_id) using btree;

create unique index "dba".ix168_1 on "dba".inventory_x_put (id) using btree;
create        index "dba".ix168_2 on "dba".inventory_x_put (inventory_receipt_id) using btree;
create        index "dba".ix168_3 on "dba".inventory_x_put (inventory_location_id) using btree;

create unique index "dba".ix169_1 on "dba".inventory_x_use (id) using btree;
create        index "dba".ix169_2 on "dba".inventory_x_use (inventory_location_id) using btree;
create        index "dba".ix169_3 on "dba".inventory_x_use (order_item_id) using btree;

create unique index "dba".label_2 on "dba".label (name) using btree;

create unique index "dba".lck_1 on "dba".lock (reference_table_id, reference_id) using btree;

create        index "dba".method_2 on "dba".method (name) using btree;

create        index "dba".note_2 on "dba".note (reference_id) using btree;
create        index "dba".note_3 on "dba".note (reference_table_id) using btree;

create        index "dba".ix139_7 on "dba".order (organization_id) using btree;

create unique index "dba".ix205_1 on "dba".order_container (id) using btree;
create        index "dba".ix205_2 on "dba".order_container (order_id) using btree;
create        index "dba".ix205_3 on "dba".order_container (container_id) using btree;

create        index "dba".order_item_2 on "dba".order_item (order_id) using btree;
create        index "dba".order_item_3 on "dba".order_item (inventory_item_id) using btree;

create unique index "dba".ix231_1 on "dba".order_organization (id) using btree;
create        index "dba".ix231_2 on "dba".order_organization (order_id) using btree;
create        index "dba".ix231_3 on "dba".order_organization (organization_id) using btree;

create unique index "dba".ix225_1 on "dba".order_recurrence (id) using btree;
create unique index "dba".ix225_2 on "dba".order_recurrence (order_id) using btree;

create unique index "dba".ix204_1 on "dba".order_test (id) using btree;
create        index "dba".ix204_2 on "dba".order_test (order_id) using btree;
create        index "dba".ix204_3 on "dba".order_test (test_id) using btree;

create unique index "dba".ix232_1 on "dba".order_test_analyte (id) using btree;
create        index "dba".ix232_2 on "dba".order_test_analyte (order_test_id) using btree;
create        index "dba".ix232_3 on "dba".order_test_analyte (analyte_id) using btree;

create        index "dba".organization_3 on "dba".organization (name) using btree;

create        index "dba".organization_contact_2 on "dba".organization_contact (organization_id) using btree;

create unique index "dba".ix194_1 on "dba".organization_parameter (id) using btree;
create        index "dba".ix194_2 on "dba".organization_parameter (organization_id) using btree;

create unique index "dba".panel_2 on "dba".panel (name) using btree;

create        index "dba".panel_item_2 on "dba".panel_item (panel_id) using btree;

create        index "dba".ix101_2 on "dba".patient (last_name) using btree;

create unique index "dba".ix197_1 on "dba".preferences (system_user_id) using btree;

create        index "dba".project_2 on "dba".project (name) using btree;

create        index "dba".project_param_2 on "dba".project_parameter (project_id) using btree;

create        index "dba".ix103_2 on "dba".provider (last_name) using btree;

create unique index "dba".ix200_2 on "dba".pws (tinwsys_is_number) using btree;
create unique index "dba".ix200_3 on "dba".pws (number0) using btree;

create        index "dba".ix202_2 on "dba".pws_address (tinwsys_is_number) using btree;
create unique index "dba".pws_address_1_2 on "dba".pws_address (tinwslec_is_number,tinlgent_is_number) using btree;

create        index "dba".ix201_3 on "dba".pws_facility (tinwsys_is_number) using btree;
create unique index "dba".pws_facility_1_2 on "dba".pws_facility (tinwsf_is_number,tsasmppt_is_number) using btree;

create unique index "dba".ix203_1 on "dba".pws_monitor (tiamrtask_is_number) using btree;
create        index "dba".ix203_2 on "dba".pws_monitor (tinwsys_is_number) using btree;

create unique index "dba".ix215_1 on "dba".pws_violation (id) using btree;
create        index "dba".ix215_2 on "dba".pws_violation (tinwsys_is_number) using btree;

create        index "dba".qaevent_2 on "dba".qaevent (name) using btree;

create        index "dba".qc_2 on "dba".qc (name) using btree;

create        index "dba".qc_analyte_2 on "dba".qc_analyte (qc_id) using btree;

create unique index "dba".ix240_1 on "dba".qc_lot (id) using btree;
create        index "dba".ix240_2 on "dba".qc_lot (qc_id) using btree;

create unique index "dba".ix187_1 on "dba".result (id) using btree;
create        index "dba".ix187_2 on "dba".result (analysis_id) using btree;

create        index "dba".sample_13 on "dba".sample (released_date) using btree;
create unique index "dba".sample_4 on "dba".sample (accession_number) using btree;
create        index "dba".sample_6 on "dba".sample (entered_date) using btree;
create        index "dba".sample_7 on "dba".sample (received_date) using btree;

create unique index "dba".sample_animal_2 on "dba".sample_animal (sample_id) using btree;

create unique index "dba".sample_human_2 on "dba".sample_clinical (sample_id) using btree;
create        index "dba".sample_human_3 on "dba".sample_clinical (patient_id) using btree;

create unique index "dba".sample_environ_2 on "dba".sample_environmental (sample_id) using btree;

create        index "dba".sample_item_2 on "dba".sample_item (sample_id) using btree;
create        index "dba".sample_item_3 on "dba".sample_item (sample_item_id) using btree;

create unique index "dba".ix261_1 on "dba".sample_neonatal (id) using btree;
create unique index "dba".ix261_2 on "dba".sample_neonatal (sample_id) using btree;
create        index "dba".ix261_3 on "dba".sample_neonatal (patient_id) using btree;

create        index "dba".sample_org_2 on "dba".sample_organization (sample_id) using btree;
create        index "dba".sample_org_3 on "dba".sample_organization (organization_id) using btree;

create unique index "dba".ix198_1 on "dba".sample_private_well (id) using btree;
create unique index "dba".ix198_2 on "dba".sample_private_well (sample_id) using btree;
create        index "dba".ix198_3 on "dba".sample_private_well (organization_id) using btree;

create        index "dba".sample_project_2 on "dba".sample_project (sample_id) using btree;
create        index "dba".sample_project_3 on "dba".sample_project (project_id) using btree;

create unique index "dba".ix256_1 on "dba".sample_pt (id) using btree;
create        index "dba".ix256_2 on "dba".sample_pt (sample_id) using btree;

create unique index "dba".ix188_1 on "dba".sample_qaevent (id) using btree;
create        index "dba".ix188_2 on "dba".sample_qaevent (sample_id) using btree;
create        index "dba".ix188_3 on "dba".sample_qaevent (qaevent_id) using btree;

create unique index "dba".ix207_1 on "dba".sample_sdwis (id) using btree;
create        index "dba".ix207_2 on "dba".sample_sdwis (sample_id) using btree;
create        index "dba".ix207_3 on "dba".sample_sdwis (pws_id) using btree;

create unique index "dba".ix265_1 on "dba".scriptlet (id) using btree;
create unique index "dba".ix265_2 on "dba".scriptlet (name) using btree;

create unique index "dba".section_3 on "dba".section (name) using btree;

create unique index "dba".ix223_1 on "dba".section_parameter (id) using btree;
create        index "dba".ix223_2 on "dba".section_parameter (section_id) using btree;

create unique index "dba".ix174_1 on "dba".shipping (id) using btree;
create        index "dba".ix174_3 on "dba".shipping (shipped_from_id) using btree;
create        index "dba".ix174_4 on "dba".shipping (shipped_to_id) using btree;

create unique index "dba".ix176_1 on "dba".shipping_item (id) using btree;
create        index "dba".ix176_2 on "dba".shipping_item (shipping_id) using btree;
create        index "dba".ix176_3 on "dba".shipping_item (reference_table_id) using btree;
create        index "dba".ix176_4 on "dba".shipping_item (reference_id) using btree;

create unique index "dba".ix175_1 on "dba".shipping_tracking (id) using btree;
create        index "dba".ix175_2 on "dba".shipping_tracking (shipping_id) using btree;

create unique index "dba".ix121_1 on "dba".standard_note (id) using btree;
create        index "dba".ix121_2 on "dba".standard_note (name) using btree;

create        index "dba".ix152_7 on "dba".storage (system_user_id) using btree;
create unique index "dba".ix155_1 on "dba".storage (id) using btree;
create        index "dba".storage_2 on "dba".storage (reference_id) using btree;
create        index "dba".storage_3 on "dba".storage (reference_table_id) using btree;

create        index "dba".storage_loc_3 on "dba".storage_location (name) using btree;

create unique index "dba".sys_var_2 on "dba".system_variable (name) using btree;

create        index "dba".test_2 on "dba".test (name) using btree;

create unique index "dba".ix185_1 on "dba".test_analyte (id) using btree;
create        index "dba".ix185_2 on "dba".test_analyte (test_id) using btree;

create unique index "dba".ix173_1 on "dba".test_prep (id) using btree;
create        index "dba".ix173_2 on "dba".test_prep (test_id) using btree;

create        index "dba".test_reflex_2 on "dba".test_reflex (test_id) using btree;

create unique index "dba".ix186_1 on "dba".test_result (id) using btree;
create        index "dba".ix186_2 on "dba".test_result (test_id) using btree;
create        index "dba".ix186_3 on "dba".test_result (result_group) using btree;

create unique index "dba".ix178_1 on "dba".test_section (id) using btree;
create        index "dba".ix178_2 on "dba".test_section (test_id) using btree;
create        index "dba".ix178_3 on "dba".test_section (section_id) using btree;

create unique index "dba".test_trailer_2 on "dba".test_trailer (name) using btree;

create unique index "dba".ix172_1 on "dba".test_type_of_sample (id) using btree;
create        index "dba".ix172_2 on "dba".test_type_of_sample (test_id) using btree;

create unique index "dba".test_worksheet_2 on "dba".test_worksheet (test_id) using btree;

create unique index "dba".ix179_1 on "dba".test_worksheet_analyte (id) using btree;
create        index "dba".ix179_2 on "dba".test_worksheet_analyte (test_id) using btree;
create        index "dba".ix179_3 on "dba".test_worksheet_analyte (test_analyte_id) using btree;

create        index "dba".tst_worksheet_itm_2 on "dba".test_worksheet_item (test_worksheet_id) using btree;

create unique index "dba".ix183_1 on "dba".worksheet (id) using btree;
create        index "dba".ix183_2 on "dba".worksheet (created_date) using btree;
create        index "dba".ix183_3 on "dba".worksheet (system_user_id) using btree;

create        index "dba".wsheet_analysis_2 on "dba".worksheet_analysis (worksheet_item_id) using btree;
create        index "dba".wsheet_analysis_4 on "dba".worksheet_analysis (analysis_id) using btree;
create        index "dba".wsheet_analysis_5 on "dba".worksheet_analysis (qc_lot_id) using btree;

create unique index "dba".ix190_1 on "dba".worksheet_item (id) using btree;
create        index "dba".ix190_2 on "dba".worksheet_item (worksheet_id) using btree;

create unique index "dba".ix193_1 on "dba".worksheet_qc_result (id) using btree;
create        index "dba".ix193_2 on "dba".worksheet_qc_result (worksheet_analysis_id) using btree;

create unique index "dba".ix228_1 on "dba".worksheet_reagent (id) using btree;
create        index "dba".ix228_2 on "dba".worksheet_reagent (worksheet_id) using btree;
create        index "dba".ix228_3 on "dba".worksheet_reagent (qc_lot_id) using btree;

create unique index "dba".ix192_1 on "dba".worksheet_result (id) using btree;
create        index "dba".ix192_2 on "dba".worksheet_result (worksheet_analysis_id) using btree;